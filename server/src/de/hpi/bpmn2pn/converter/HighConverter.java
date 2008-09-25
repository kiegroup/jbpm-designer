package de.hpi.bpmn2pn.converter;

import java.util.LinkedList;
import java.util.List;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.EndTerminateEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn2pn.model.ConversionContext;
import de.hpi.bpmn2pn.model.HighConversionContext;
import de.hpi.bpmn2pn.model.SubProcessPlaces;
import de.hpi.highpetrinet.HighFlowRelationship;
import de.hpi.highpetrinet.HighLabeledTransition;
import de.hpi.highpetrinet.HighPetriNet;
import de.hpi.highpetrinet.HighPetriNetFactory;
import de.hpi.highpetrinet.HighSilentTransition;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.SilentTransition;
import de.hpi.petrinet.Transition;

public class HighConverter extends StandardConverter {
	protected BPMNFactory factory;
	
	public HighConverter(BPMNDiagram diagram) {
		super(diagram, new HighPetriNetFactory());
		factory = new BPMNFactory();
	}

	public HighConverter(BPMNDiagram diagram, PetriNetFactory pnfactory) {
		super(diagram, pnfactory);
		factory = new BPMNFactory();
	}
	
	@Override
	public HighPetriNet convert() {
		return (HighPetriNet)super.convert();
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
	
	protected HighFlowRelationship addInhibitorFlowRelationship(PetriNet net,
			de.hpi.petrinet.Place source, de.hpi.petrinet.Transition target) {	
		HighFlowRelationship rel = (HighFlowRelationship)addFlowRelationship(net, source, target);
		if (rel == null){
			return null;
		}
		rel.setType(HighFlowRelationship.ArcType.Inhibitor);
		return rel;
	}
	
	@Override
	protected SilentTransition addSilentTransition(PetriNet net, String id, DiagramObject BPMNObj, int autoLevel) {
		HighSilentTransition t = (HighSilentTransition) addSimpleSilentTransition(net, id);
		t.setBPMNObj(BPMNObj);
		return t;
	}
	
	@Override
	protected LabeledTransition addLabeledTransition(PetriNet net, String id, DiagramObject BPMNObj, int autoLevel, String label) {
		HighLabeledTransition t = (HighLabeledTransition) addSimpleLabeledTransition(net, id, label);
		t.setBPMNObj(BPMNObj);
		return t;
	}
	
	// high Petri net has its own conversion context
	@Override
	protected ConversionContext setupConversionContext() {
		return new HighConversionContext();
	}
	
	@Override
	protected void handleORGateway(PetriNet net, ORGateway gateway,
			ConversionContext c) {
		if(gateway.getIncomingEdges().size()==1){
			handleORSplit(net, gateway, (HighConversionContext)c);
		} else {
			handleORJoin(net, gateway, (HighConversionContext)c);
		}
	}
	
	protected void handleORSplit(PetriNet net, ORGateway gateway,
			HighConversionContext c) {
		//handle all possible combinations
		for(List<Edge> edges : (List<List<Edge>>)de.hpi.bpmn.analysis.Combination.findCombinations(gateway.getOutgoingEdges())){
			if(edges.size() == 0)
				continue;
			Transition t = addSilentTransition(net, "orSplit_"+gateway.getId(), gateway, 1);
			for(Edge edge : edges){
				addFlowRelationship(net, t, c.map.get(edge));
			}
			addFlowRelationship(net, c.map.get(gateway.getIncomingEdges().get(0)), t);
		}
	}

	protected void handleORJoin(PetriNet net, ORGateway gateway,
			HighConversionContext c) {
		//handle all combinations how many pathes can terminate
		for(List<Edge> posEdges : (List<List<Edge>>)de.hpi.bpmn.analysis.Combination.findCombinations(gateway.getIncomingEdges())){
			if(posEdges.size() == 0)
				continue;
			Transition t = addSilentTransition(net, "orJoin_"+gateway.getId(), gateway, 1);
			//for each positive edge
			for(Edge edge : posEdges){
				addFlowRelationship(net, c.map.get(edge), t);
			}
			//for each negative edge (gateway.getIncomingEdges() - posEdges)
			//TODO performance
			for(Edge edge : gateway.getIncomingEdges()){
				if(!posEdges.contains(edge)){
					handleUpstream(net, gateway, t, edge, c);
				}
			}
			addFlowRelationship(net, t, c.map.get(gateway.getOutgoingEdges().get(0)));
		}
	}
	
	//TODO post dominators
	//TODO: Some nodes (e.g. MI) need some special handling, because it could be
	//that they don't have a place yet!
	private void handleUpstream(PetriNet net, ORGateway gateway, Transition t, Edge edge, HighConversionContext c){
		addInhibitorFlowRelationship(net, c.map.get(edge), t);
		//if previous node is no dominator, then handle its incoming edges, too
		//gateway.getParent() => can be a subprocess or top process (=! gateway.getProcess(), which would return top process)
		if(!c.getDominatorFinder(gateway.getParent()).getDominators(gateway).contains(edge.getSource())){
			for(Edge e : edge.getSource().getIncomingEdges()){
				handleUpstream(net, gateway, t, e, c);
			}
		}
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
