package de.hpi.bpmn2pn.converter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.EndTerminateEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn2pn.model.ConversionContext;
import de.hpi.bpmn2pn.model.HighConversionContext;
import de.hpi.bpmn2pn.model.SubProcessPlaces;
import de.hpi.highpetrinet.HighFlowRelationship;
import de.hpi.highpetrinet.HighPetriNet;
import de.hpi.highpetrinet.HighPetriNetFactory;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;
import de.hpi.petrinet.serialization.PetriNetPNMLExporter;

public class HighConverter extends StandardConverter {

	public HighConverter(BPMNDiagram diagram) {
		super(diagram, new HighPetriNetFactory());
	}

	public HighConverter(BPMNDiagram diagram, PetriNetFactory pnfactory) {
		super(diagram, pnfactory);
	}

	public static void main(String[] args) {
		BPMNDiagram diag = loadRDFDiagram("bpmn.rdf"); 
		printBPMN(diag);
		/*HighPetriNet net = new STConverter(diag).convert();
		STMapper map = new STMapper(net);
		List<DiagramObject> l = map.getFireableObjects();
		map.clearChangedObjs();
		map.fireObject(l.get(0).getResourceId());
		l = map.getFireableObjects();
		String s = map.getChangedObjsAsString();*/
	}
	
	@Override
	public HighPetriNet convert() {
		return (HighPetriNet)super.convert();
	}
	
	public static BPMNDiagram loadRDFDiagram(String fileName) {
		try{
			RandomAccessFile file = new RandomAccessFile("C:\\Dokumente und Einstellungen\\Kai\\Eigene Dateien\\Downloads\\"+fileName, "r");
			String rdf = "";
			String zeile;
			while ( (zeile = file.readLine()) != null){
				rdf += zeile;
			}
			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes("UTF-8")));
			BPMNRDFImporter importer = new BPMNRDFImporter(document);
			BPMNDiagram diagram = (BPMNDiagram) importer.loadBPMN();
			return diagram;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void printBPMN(BPMNDiagram diagram){
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document pnmlDoc = builder.newDocument();
			PetriNet net = new STConverter(diagram).convert();
			PetriNetPNMLExporter exp = new PetriNetPNMLExporter();
			exp.savePetriNet(pnmlDoc, net);
			OutputFormat format = new OutputFormat(pnmlDoc);
			StringWriter stringOut = new StringWriter();
			XMLSerializer serial2 = new XMLSerializer(stringOut, format);
			serial2.asDOMSerializer();
			serial2.serialize(pnmlDoc.getDocumentElement());
			new File("C:\\Dokumente und Einstellungen\\Kai\\Eigene Dateien\\Downloads\\high.pnml").delete();
			new RandomAccessFile("C:\\Dokumente und Einstellungen\\Kai\\Eigene Dateien\\Downloads\\high.pnml", "rw").writeBytes(stringOut.toString());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected HighFlowRelationship addResetFlowRelationship(PetriNet net,
			de.hpi.petrinet.Place source, de.hpi.petrinet.Transition target) {	
		HighFlowRelationship rel = (HighFlowRelationship)addFlowRelationship(net, source, target);
		if (rel == null){
			return null;
		}
		rel.setType(HighFlowRelationship.ArcType.Reset);
		return rel;
	}
	
	// high Petri net has its own conversion context
	@Override
	protected ConversionContext setupConversionContext() {
		return new HighConversionContext();
	}
	
	/* 
	 * Begin Exc Handling Mapping 
	 */
	
	@Override
	protected void prepareExceptionHandling(PetriNet net, SubProcess process,
			Transition startT, Transition endT, ConversionContext c){
		//just add 1 ok place from start to end
		SubProcessPlaces pl = c.getSubprocessPlaces(process);
		pl.ok = addPlace(net, "ok" + process.getId());
		connectWithExcTransitions(net, pl.ok, c);
		addFlowRelationship(net, startT, pl.ok);
		addFlowRelationship(net, pl.ok, endT);
	}
	
	@Override
	protected void handleNodesRecursively(PetriNet net, Container container,
			ConversionContext c) {
		List<Transition> terminateTransitions = null;
		
		//handle terminate events for BPMNFDiagram and SubProcess
		if(container instanceof BPMNDiagram){
			//search for all Terminate Events and handle them
			for(Container process : diagram.getProcesses()){
				handleEndTerminateEvents(net, process, c);
			}
		} else if (container instanceof SubProcess){
			terminateTransitions = handleEndTerminateEvents(net, container, c);
		}
		
		super.handleNodesRecursively(net, container, c);
		
		//remove terminate transitions from context
		if (container instanceof SubProcess){
			for(Transition t : terminateTransitions){
				((HighConversionContext)c).removeAncestorExcpTransition(container,t);
			}
		}
	}

	@Override
	protected void handleAttachedIntermediateEventForSubProcess(PetriNet net,
			IntermediateEvent event, ConversionContext c) {
		Transition t = addLabeledTransition(net, event.getId(), event, 0, event.getLabel());
		handleMessageFlow(net, event, t, t, c);

		SubProcessPlaces pl = c.getSubprocessPlaces((SubProcess) event
				.getActivity());

		addFlowRelationship(net, pl.ok, t);
		addFlowRelationship(net, t, c.map.get(getOutgoingSequenceFlow(event)));
		((HighConversionContext)c).addAncestorExcpTransition(event.getProcess(), t);
	}

	@Override
	protected void handleSubProcess(PetriNet net, SubProcess process,
			ConversionContext c) {
		super.handleSubProcess(net, process, c);
		
		SubProcessPlaces pl = c.getSubprocessPlaces(process);
		connectWithExcTransitions(net,pl.startP, c);
		connectWithExcTransitions(net,pl.endP, c);
		
		((HighConversionContext)c).removeAncestorExcpTransition(process);
	}
	
	@Override
	protected void handleExceptions(PetriNet net, Node node, Transition t,
			ConversionContext c) {
		//no skip transitions anymore needed, but incoming and outgoing places should be connected
		//with exc activities
		//TODO ensure, that one place don't have several reset arcs to exc activity
		connectWithExcTransitions(net, c.map.get(getIncomingSequenceFlow(node)), c);
		connectWithExcTransitions(net, c.map.get(getOutgoingSequenceFlow(node)), c);
	}
	
	@Override
	protected void createStartPlaces(PetriNet net, ConversionContext c) {
		//TODO handle start places for termination events
		super.createStartPlaces(net, c);
	}
	
	@Override
	protected void handleEndEvent(PetriNet net, EndEvent event,	ConversionContext c) {
		// EndTerminateEvent should be handles before
		if (!(event instanceof EndTerminateEvent)){
			//All Events except of EndTerminateEvent and EndPlainEvent shouldn't be connected to regular end event
			//TODO trigger somehow (using node id) intermediate events
			if(!(event instanceof EndPlainEvent)){
				Transition t = addLabeledTransition(net, event.getId(), event, 0, event.getLabel());
				handleMessageFlow(net, event, t, t, c);
				addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(event)), t);
				if (c.ancestorHasExcpH)
					handleExceptions(net, event, t, c);
			} else {		
				super.handleEndEvent(net, event, c);
			}
		}
	}
	protected void handleXORDataBasedGateway(PetriNet net,
			XORDataBasedGateway gateway, ConversionContext c) {
		super.handleXORDataBasedGateway(net, gateway, c);
	}

	//@Override
	//protected void handleSequenceFlow(PetriNet net, SequenceFlow flow,
	//		ConversionContext c) {
	//	super.handleSequenceFlow(net, flow, c);
	//}
	
	@Override
	protected void handleMessageFlows(PetriNet net, ConversionContext c) {
		// TODO connect with exc transitions ==> don't do this, same as handleSequenceFlow
		// But: handleMessageFlow seems to be called for every activity
		super.handleMessageFlows(net, c);
	}
	
	/* Connects given place with all transitions representing exceptions */
	private void connectWithExcTransitions(PetriNet net, Place p, ConversionContext c){
		for(Transition t : ((HighConversionContext)c).getExcpTransitions()){
			//only add, if there is not already an reset arc
			//TODO this should be guaranteed by mapping!!
			boolean alreadyConnected = false;
			for (HighFlowRelationship rel : (List<HighFlowRelationship>)t.getIncomingFlowRelationships()){
				if ((Place)rel.getSource() == p){
					alreadyConnected = true;
				}
			}
			if(!alreadyConnected)	
				addResetFlowRelationship(net, p, t);
		}
	}
	
	protected Transition handleEndTerminateEvent(PetriNet net, EndTerminateEvent event, ConversionContext c){
		Transition t = addLabeledTransition(net, event.getId(), event, 0, event.getLabel());
		handleMessageFlow(net, event, t, t, c);
		addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(event)), t);
		Place p = c.getSubprocessPlaces(event.getProcess()).endP;
		//connect with end place from process
		addFlowRelationship(net, t, c.getSubprocessPlaces(event.getProcess()).endP);
		if (c.ancestorHasExcpH)
			handleExceptions(net, event, t, c);
		//TODO: connect terminaction with end event?
		
		((HighConversionContext)c).addAncestorExcpTransition(event.getProcess(), t);
		
		return t;
	}
	
	protected List<Transition> handleEndTerminateEvents(PetriNet net, Container container, ConversionContext c){
		List<Transition> terminateTransitions = new LinkedList<Transition>();
		for(Node node : container.getChildNodes()){
			if(node instanceof EndTerminateEvent){
				EndTerminateEvent event = (EndTerminateEvent)node;
				terminateTransitions.add(handleEndTerminateEvent(net, event, c));
			}
		}
		return terminateTransitions;
	}
	
	/* 
	 * End Exc Handling Mapping 
	 */
}
