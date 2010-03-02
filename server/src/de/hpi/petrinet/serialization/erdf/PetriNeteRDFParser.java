package de.hpi.petrinet.serialization.erdf;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.LabeledTransitionImpl;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.PlaceImpl;
import de.hpi.petrinet.SilentTransitionImpl;
import de.hpi.petrinet.Transition;

public class PetriNeteRDFParser {
	Document doc;
	
	PetriNet net;
	HashMap<Object, List<String>> connections;
	
	public PetriNeteRDFParser(Document doc){
		this.doc = doc;
	}
	
	public PetriNet parse(){
		connections = new HashMap<Object, List<String>>();
		net = new PTNet();

		addAllObjects();
		addConnections();
		
		return net;
	}
	
	/**
	 * @return List of all child nodes of body element
	 */
	private NodeList getChildNodesOfBody(){
		try {
			return XPathAPI.selectNodeList(doc, "//body/*");
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Iterates over document and adds nodes to net depending on type
	 * @param net
	 */
	private void addAllObjects(){
		List<Node> arcs = new LinkedList<Node>();
		NodeList divList = getChildNodesOfBody();
		
		for(int i=0; i < divList.getLength(); i++){
			try {
				Node divNode = divList.item(i);
				Object processObject = null;
				String type = XPathAPI.selectSingleNode(divNode, "span[@class='oryx-type']").getTextContent();

				if(type.equals("http://b3mn.org/stencilset/petrinet#Transition")){
					LabeledTransition trans = new LabeledTransitionImpl();
					trans.setResourceId(getResourceId(divNode));
					trans.setId(getId(divNode));
					trans.setLabel(getTitle(divNode));
					net.getTransitions().add(trans);
					processObject = trans;
				} else if(type.equals("http://b3mn.org/stencilset/petrinet#VerticalEmptyTransition")){
					SilentTransitionImpl trans = new SilentTransitionImpl();
					trans.setResourceId(getResourceId(divNode));
					trans.setId(getId(divNode));
					net.getTransitions().add(trans);
					processObject = trans;
				} else if(type.equals("http://b3mn.org/stencilset/petrinet#Place")){
					PlaceImpl place = new PlaceImpl();
					place.setResourceId(getResourceId(divNode));
					place.setId(getId(divNode));
					net.getPlaces().add(place);
					processObject = place;
				} else if(type.equals("http://b3mn.org/stencilset/petrinet#Arc")){
					FlowRelationship rel = new FlowRelationship();
					rel.setResourceId(getResourceId(divNode));
					rel.setId(getId(divNode));
					net.getFlowRelationships().add(rel);
					processObject = rel;
				}
				handleOutgoings(divNode, processObject);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getResourceId(Node divNode){
		try {
			return XPathAPI.selectSingleNode(divNode, "@id").getTextContent();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getId(Node divNode){
		try {
			String id = XPathAPI.selectSingleNode(divNode, "span[@class='oryx-id']").getTextContent();
			if( id != null && !id.equals("")) return id;
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		// Take resource id if no id given
		return getResourceId(divNode);
	}
	
	private String getTitle(Node divNode){
		try {
			return XPathAPI.selectSingleNode(divNode, "span[@class='oryx-title']").getTextContent();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void handleOutgoings(Node divNode, Object processObject){
		List<String> outgoingResourceIds = new LinkedList<String>();
		
		try {
			NodeList outgoings = XPathAPI.selectNodeList(divNode, "a[@rel='raziel-outgoing']/@href");
			for(int i = 0; i < outgoings.getLength(); i++){
				outgoingResourceIds.add(outgoings.item(i).getTextContent().replace("#", ""));
			}
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		connections.put(processObject, outgoingResourceIds);
	}
	
	private void addConnections(){
		for(Object processObject : connections.keySet()){
			for(String outgoingResourceId : connections.get(processObject)){
				de.hpi.petrinet.Node outgoingNode = findNode(outgoingResourceId);
				if(outgoingNode != null){// process object is a flow relationship
					((FlowRelationship)processObject).setTarget(outgoingNode);
				} else { // process object is a node
					findFlowRelationship(outgoingResourceId).setSource((de.hpi.petrinet.Node)processObject);
				}
			}
		}
	}
	
	private de.hpi.petrinet.Node findNode(String resourceId){
		for(Place place : net.getPlaces()){
			if(place.getResourceId().equals(resourceId)) return place;
		}
		for(Transition transition : net.getTransitions()){
			if(transition.getResourceId().equals(resourceId)) return transition;
		}
		return null;
	}
	
	private FlowRelationship findFlowRelationship(String resourceId){
		for(FlowRelationship rel : net.getFlowRelationships()){
			if(rel.getResourceId().equals(resourceId)) return rel;
		}
		return null;
	}
}
