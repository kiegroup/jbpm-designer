package de.hpi.bpmn.analysis;

import java.util.Vector;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.Activity;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.EndCancelEvent;
import de.hpi.bpmn.EndCompensationEvent;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.EndLinkEvent;
import de.hpi.bpmn.EndMessageEvent;
import de.hpi.bpmn.EndMultipleEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.EndSignalEvent;
import de.hpi.bpmn.EndTerminateEvent;
import de.hpi.bpmn.IntermediateCancelEvent;
import de.hpi.bpmn.IntermediateCompensationEvent;
import de.hpi.bpmn.IntermediateErrorEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.IntermediateLinkEvent;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.IntermediateMultipleEvent;
import de.hpi.bpmn.IntermediatePlainEvent;
import de.hpi.bpmn.IntermediateSignalEvent;
import de.hpi.bpmn.IntermediateTimerEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.StartLinkEvent;
import de.hpi.bpmn.StartMessageEvent;
import de.hpi.bpmn.StartMultipleEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.StartSignalEvent;
import de.hpi.bpmn.StartTimerEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.XOREventBasedGateway;
import de.hpi.diagram.OryxUUID;

public class BPMNNormalizer {
	BPMNDiagram diagram;
	
	public boolean normalizeMultipleEndEvents = true; 

	public BPMNNormalizer(BPMNDiagram diagram) {
		this.diagram = diagram;
	}

	/*
	 * BPMNDiagram#identifyProcesses must be called before!!
	 */
	public void normalize() {
		for (Container process : diagram.getProcesses()) {
			normalizeRecursively(process);
		}
		// diagram.identifyProcesses();
	}

	private void normalizeRecursively(Container process) {
		Vector<StartEvent> startEvents = new Vector<StartEvent>();
		Vector<EndEvent> endEvents = new Vector<EndEvent>();
		Vector<EndTerminateEvent> endTerminateEvents = new Vector<EndTerminateEvent>();
		Vector<Node> nodesWithoutIncomingSequenceFlow = new Vector<Node>();
		Vector<Node> nodesWithoutOutgoingSequenceFlow = new Vector<Node>();
		Vector<Node> activities = new Vector<Node>();

		for (Node node : process.getChildNodes()) {
			if (node instanceof StartEvent)
				startEvents.add((StartEvent) node);
			else if (node instanceof EndTerminateEvent)
				endTerminateEvents.add((EndTerminateEvent) node);
			else if (node instanceof EndEvent)
				endEvents.add((EndEvent) node);
			else if (node instanceof SubProcess)
				normalizeRecursively((SubProcess) node);
			// TODO CompensationActivities shouldn't be counted here
			else if (!(node instanceof IntermediateEvent)){
				if(node.getIncomingSequenceFlows().size() == 0)
					nodesWithoutIncomingSequenceFlow.add(node);
				if(node.getOutgoingSequenceFlows().size() == 0)
					nodesWithoutOutgoingSequenceFlow.add(node);
			}
			
			if(node instanceof Activity){
				activities.add(node);
			}
		}
		
		for(Node activity : activities){
			normalizeMultipleFlowsForActivity(process, activity);
		}

		if (startEvents.size() > 1) {
			normalizeMultipleStartEvents(process, startEvents);
		//Merge all nodes which have no incoming seq flow
		} else if (nodesWithoutIncomingSequenceFlow.size() > 0) { 
			normalizeNodesWithoutIncomingSequenceFlow(process,
					nodesWithoutIncomingSequenceFlow);
		}
		if (endEvents.size() > 1) {
			normalizeMultipleEndEvents(process, endEvents);
		} else if (nodesWithoutOutgoingSequenceFlow.size() > 0) {
			normalizeNodesWithoutOutgoingSequenceFlow(process,
					nodesWithoutOutgoingSequenceFlow);
		}
	}

	// Gives all nodes one start event
	protected void normalizeNodesWithoutIncomingSequenceFlow(Container process,
			Vector<Node> nodes) {
		if (nodes.size() < 1)
			return;

		StartPlainEvent start = new StartPlainEvent();
		addNode(start, nodes.get(0));

		if (nodes.size() == 1) {
			connectNodes(start, nodes.get(0));
		} else { // node splitting gateway is needed
			ANDGateway gateway = new ANDGateway();
			addNode(gateway, nodes.get(0));

			connectNodes(start, gateway);

			for (Node node : nodes) {
				connectNodes(gateway, node);
			}
		}
	}

	// Gives all nodes one start event
	protected void normalizeNodesWithoutOutgoingSequenceFlow(Container process,
			Vector<Node> nodes) {
		if (nodes.size() < 1)
			return;

		EndPlainEvent end = new EndPlainEvent();
		addNode(end, nodes.get(0));

		if (nodes.size() == 1) { // node splitting gateway is needed
			connectNodes(nodes.get(0), end);
		} else {
			ANDGateway gateway = new ANDGateway();
			addNode(gateway, nodes.get(0));

			connectNodes(gateway, end);

			for (Node node : nodes) {
				connectNodes(node, gateway);
			}
		}
	}

	protected void normalizeMultipleStartEvents(Container process,
			Vector<StartEvent> startEvents) {
		if (startEvents.size() < 2)
			return;

		StartPlainEvent start = new StartPlainEvent();
		addNode(start, startEvents.get(0));

		XOREventBasedGateway gateway = new XOREventBasedGateway();
		addNode(gateway, startEvents.get(0));

		connectNodes(start, gateway);

		for (StartEvent s : startEvents) {
			Container sParent = s.getParent();
			
			removeNode(s);

			IntermediateEvent iEvent = convertToIntermediateEvent(s);

			addNode(iEvent, process, sParent);

			// Connect new intermediate event with outgoing from replacing start
			// event
			s.getOutgoingEdges().get(0).setSource(iEvent);

			connectNodes(gateway, iEvent);
		}
	}

	// Do not pass any terminate events!
	protected void normalizeMultipleEndEvents(Container process,
			Vector<EndEvent> endEvents) {
		if(!normalizeMultipleEndEvents) return;
		
		EndPlainEvent end = new EndPlainEvent();
		addNode(end, endEvents.get(0));

		ORGateway gateway = new ORGateway();
		addNode(gateway, endEvents.get(0));

		connectNodes(gateway, end);

		int index = 0;
		for (EndEvent e : endEvents) {
			Container eParent = e.getParent();
			
			removeNode(e);

			IntermediateEvent iEvent = convertToIntermediateEvent(e);

			addNode(iEvent, process, eParent);

			e.getIncomingEdges().get(0).setTarget(iEvent);

			// Id is needed because incoming edges of or-join needs ids to find
			// all combinations
			connectNodes(iEvent, gateway).setId(
					"seq" + String.valueOf(index) + e.getId());
			index++;
		}
	}
	
	// Handles multiple incoming or outgoing flows of activities (mapping to gateways)
	protected void normalizeMultipleFlowsForActivity(Container process, Node activity){
		if(activity.getIncomingSequenceFlows().size() > 1){
			XORDataBasedGateway gateway = new XORDataBasedGateway();
			addNode(gateway, activity);
			for(SequenceFlow seqFlow : activity.getIncomingSequenceFlows()){
				seqFlow.setTarget(gateway);
			}
			connectNodes(gateway, activity);
		}
		if(activity.getOutgoingSequenceFlows().size() > 1){
			ANDGateway gateway = new ANDGateway();
			addNode(gateway, activity);
			for(SequenceFlow seqFlow : activity.getOutgoingSequenceFlows()){
				seqFlow.setSource(gateway);
			}
			connectNodes(activity, gateway);
		}
	}

	protected SequenceFlow connectNodes(Node source, Node target) {
		SequenceFlow seqFlow = new SequenceFlow();
		
		// Generate an id if it hasn't been set before
		seqFlow.setId(OryxUUID.generate());
		
		seqFlow.setSource(source);
		seqFlow.setTarget(target);
		diagram.getEdges().add(seqFlow);
		return seqFlow;
	}

	protected void addNode(Node node, Container process, Container parent) {
		// Generate an id if it hasn't been set before
		if(node.getId() == null) node.setId(OryxUUID.generate());
		// Add to parent
		node.setParent(parent);
		node.setProcess(process);
	}
	
	/**
	 * 
	 * @param node Node to add.
	 * @param fromNode Node which is used to set the parent and process of given node to add.
	 */
	protected void addNode(Node node, Node fromNode) {
		addNode(node, fromNode.getProcess(), fromNode.getParent());
	}

	protected void removeNode(Node node) {
		diagram.getChildNodes().remove(node);
		node.setParent(null);
		node.setProcess(null);
	}

	static public IntermediateEvent convertToIntermediateEvent(StartEvent sEvent) {
		IntermediateEvent iEvent = null;

		// Find corresponding class
		if (sEvent instanceof StartPlainEvent) {
			iEvent = new IntermediatePlainEvent();
		} else if (sEvent instanceof StartLinkEvent) {
			iEvent = new IntermediateLinkEvent();
		} else if (sEvent instanceof StartMessageEvent) {
			iEvent = new IntermediateMessageEvent();
		} else if (sEvent instanceof StartMultipleEvent) {
			iEvent = new IntermediateMultipleEvent();
		} else if (sEvent instanceof StartSignalEvent) {
			iEvent = new IntermediateSignalEvent();
		} else if (sEvent instanceof StartTimerEvent) {
			iEvent = new IntermediateTimerEvent();
		}

		copyNodeValues(sEvent, iEvent);

		return iEvent;
	}

	static public IntermediateEvent convertToIntermediateEvent(EndEvent eEvent) {
		IntermediateEvent iEvent = null;

		// Find corresponding class
		if (eEvent instanceof EndPlainEvent) {
			iEvent = new IntermediatePlainEvent();
		} else if (eEvent instanceof EndCancelEvent) {
			iEvent = new IntermediateCancelEvent();
		} else if (eEvent instanceof EndCompensationEvent) {
			iEvent = new IntermediateCompensationEvent();
		} else if (eEvent instanceof EndErrorEvent) {
			iEvent = new IntermediateErrorEvent();
		} else if (eEvent instanceof EndLinkEvent) {
			iEvent = new IntermediateLinkEvent();
		} else if (eEvent instanceof EndMessageEvent) {
			iEvent = new IntermediateMessageEvent();
		} else if (eEvent instanceof EndMultipleEvent) {
			iEvent = new IntermediateMultipleEvent();
		} else if (eEvent instanceof EndSignalEvent) {
			iEvent = new IntermediateSignalEvent();
		}

		copyNodeValues(eEvent, iEvent);

		return iEvent;
	}

	// Does not copy parent and process attribute!
	static public void copyNodeValues(Node source, Node target) {
		target.setId("i" + source.getId());
		target.setLabel(source.getLabel());
		target.setResourceId(source.getResourceId());
	}
}
