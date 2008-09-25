package de.hpi.bpmn2pn.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hpi.BPMNHelpers;
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
import de.hpi.bpmn.SequenceFlow;
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
import de.hpi.highpetrinet.HighTransition;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.SilentTransition;
import de.hpi.petrinet.Transition;

public class HighConverter extends StandardConverter {
	protected BPMNFactory factory;
	
	protected Map<Transition, List<Edge>> orJoinTransitions;
	
	public static void main(String [ ] args){
		BPMNHelpers.printBPMN(BPMNHelpers.loadRDFDiagram("bpmn.rdf"));
	}
	
	public HighConverter(BPMNDiagram diagram) {
		super(diagram, new HighPetriNetFactory());
		factory = new BPMNFactory();
		
		orJoinTransitions = new HashMap<Transition, List<Edge>>();
	}

	public HighConverter(BPMNDiagram diagram, PetriNetFactory pnfactory) {
		super(diagram, pnfactory);
		factory = new BPMNFactory();
		
		orJoinTransitions = new HashMap<Transition, List<Edge>>();
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
		List<List<Edge>> incomingPathesComb = (List<List<Edge>>)de.hpi.bpmn.analysis.Combination.findCombinations(gateway.getIncomingEdges());
		int index = 0;
		for(List<Edge> posEdges : incomingPathesComb){
			if(posEdges.size() == 0)
				continue;
			index++;
			Transition t = addSilentTransition(net, "orJoin_"+gateway.getId()+String.valueOf(index), gateway, 1);
			//for each positive edge
			for(Edge edge : posEdges){
				addFlowRelationship(net, c.map.get(edge), t);
			}
			// negative edges are handled in post processing
			//TODO performance: howto impl negEdges = incomingEdges -  posEdges 
			List<Edge> negEdges = new LinkedList<Edge>();
			for(Edge edge : gateway.getIncomingEdges()){
				if(!posEdges.contains(edge)){
					negEdges.add(edge);
				}
			}
			orJoinTransitions.put(t, negEdges);
			
			addFlowRelationship(net, t, c.map.get(gateway.getOutgoingEdges().get(0)));
		}
	}
	
	//TODO post dominators
	private void handleUpstream(PetriNet net, HighTransition gatewayTransition, Place edge, HighConversionContext c, List<de.hpi.petrinet.Node> visitedNodes){
		if(visitedNodes.contains(edge))
			return;
		visitedNodes.add(edge);
		//if(edge.getId().contains("ok"))
		//	return;
		for(HighFlowRelationship relEdge : (List<HighFlowRelationship>)edge.getIncomingFlowRelationships()){
			if(relEdge.getType() != HighFlowRelationship.ArcType.Plain)
				continue;
			HighTransition incomingTransition = (HighTransition)relEdge.getSource();
			DiagramObject incomingBPMN = incomingTransition.getBPMNObj();
			//HACK!!!! XorSplits maps to Places!!!!!
			//better solution: give places getBPMNObj and include in XOR-mapping
			//that places gets BPM objects
			if(incomingBPMN instanceof SequenceFlow){
				incomingBPMN = (DiagramObject)((SequenceFlow)incomingTransition.getBPMNObj()).getSource();
			}

			addInhibitorFlowRelationship(net, edge, gatewayTransition);
			
			if(!checkDominator((Node)incomingBPMN, (Node)gatewayTransition.getBPMNObj(), c)){
				for(HighFlowRelationship rel : (List<HighFlowRelationship>)incomingTransition.getIncomingFlowRelationships()){
					handleUpstream(net, gatewayTransition, (Place)rel.getSource(), c, visitedNodes);
				}
			}
		}
	}
	
	/* 
	 * Checks whether a BPMN node node1 or any of its parents is a 
	 * dominator of another BPMN node node2
	 */
	private boolean checkDominator(Node node1, Node node2, HighConversionContext c ){
		boolean isDominator = c.getDominatorFinder(node2.getParent()).getDominators(node2).contains(node1);
		if(!isDominator && node1.getParent() instanceof Node){
			isDominator = checkDominator((Node)node1.getParent(), node2, c);
		}
		return isDominator;
	}
	
	@Override
	protected void postProcessDiagram(PetriNet net, ConversionContext c) {
		super.postProcessDiagram(net, c);
		
		//post process or-joins
		for(Transition gatewayTransition : orJoinTransitions.keySet()){
			//handle the negative edges which hasn't been handled in handleORJoin()
			List<de.hpi.petrinet.Node> visitedNodes = new ArrayList<de.hpi.petrinet.Node>();
			List<Edge> negEdges = orJoinTransitions.get(gatewayTransition);
			for(Edge edge : negEdges){
				handleUpstream(net, (HighTransition)gatewayTransition, c.map.get(edge), (HighConversionContext)c, visitedNodes);
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
