package de.hpi.petrinet.pnml;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.TauTransition;
import de.hpi.petrinet.Transition;

public abstract class PetriNetPNMLImporter {
	
	public PetriNet loadPetriNet(Document doc) {
		Node netnode = getNetNode(doc);
		if (netnode == null)
			return null;
		
		PetriNet net = createPetriNet(netnode);
		
		Map map = new HashMap();
		
		for (Node node=netnode.getFirstChild(); node.getNextSibling() != null; node=node.getNextSibling()) {
			if (node.getNodeName().equals("place")) {
				addPlace(net, node, map);
			} else if (node.getNodeName().equals("transition")) {
				addTransition(net, node, map);
			} else if (node.getNodeName().equals("arc")) {
				addArc(net, node, map);
			}
		}
		
		return net;
	}
	
	protected PetriNet createPetriNet(Node nnode) {
		return PetriNetFactory.eINSTANCE.createPetriNet();
	}

	protected Place addPlace(PetriNet net, Node pnode, Map map) {
		Place p = createPlace(net, pnode);
		net.getPlaces().add(p);
		String id = pnode.getAttributes().getNamedItem("id").getNodeValue();
		p.setId(id);
		map.put(id, p);
		
		return p;
	}
	
	protected Place createPlace(PetriNet net, Node pnode) {
		return PetriNetFactory.eINSTANCE.createPlace();
	}

	protected Transition addTransition(PetriNet net, Node tnode, Map map) {
		String label = getContent(getChild(getChild(tnode, "name"), "value"));
		if (label == null)
			label = getContent(getChild(getChild(tnode, "name"), "text"));
		
		Transition t;
		if (label != null) {
			if ((t = createLabeledTransition(net, tnode)) != null)
				((LabeledTransition)t).setLabel(label);
			else
				t = createTauTransition(net, tnode);
			
		} else {
			t = createTauTransition(net, tnode);
		}
		
		net.getTransitions().add(t);
		String id = tnode.getAttributes().getNamedItem("id").getNodeValue();
		t.setId(id);
		map.put(id, t);
		
		return t;
	}
	
	protected LabeledTransition createLabeledTransition(PetriNet net, Node tnode) {
		return PetriNetFactory.eINSTANCE.createLabeledTransition();
	}
	
	protected TauTransition createTauTransition(PetriNet net, Node tnode) {
		return PetriNetFactory.eINSTANCE.createTauTransition();
	}

	protected FlowRelationship addArc(PetriNet net, Node anode, Map map) {
		FlowRelationship rel = createFlowRelationship(net, anode);
		net.getFlowRelationships().add(rel);
		
		Node src = anode.getAttributes().getNamedItem("source");
		Node trg = anode.getAttributes().getNamedItem("target");
		if (src != null && trg != null) {
			rel.setSource((de.hpi.petrinet.Node)map.get(src.getNodeValue()));
			rel.setTarget((de.hpi.petrinet.Node)map.get(trg.getNodeValue()));
		}
		
		return rel;
	}
	
	protected FlowRelationship createFlowRelationship(PetriNet net, Node anode) {
		return PetriNetFactory.eINSTANCE.createFlowRelationship();
	}

	protected String getContent(Node node) {
		if (node != null && node.hasChildNodes())
			return node.getFirstChild().getNodeValue();
		return null;
	}
	
	protected Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().equals(name)) 
				return node;
		return null;
	}

	private Node getNetNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("pnml"))
			return null;
		return getChild(node, "net");
	}

}
