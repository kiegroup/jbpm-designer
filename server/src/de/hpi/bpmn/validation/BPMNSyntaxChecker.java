package de.hpi.bpmn.validation;

import java.util.HashMap;
import java.util.Map;

import de.hpi.bpmn.Activity;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.ControlFlow;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.Event;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.XOREventBasedGateway;
import de.hpi.petrinet.SyntaxChecker;

public class BPMNSyntaxChecker implements SyntaxChecker {
	
	private static final String NO_SOURCE = "Each edge must have a source";
	private static final String NO_TARGET = "Each edge must have a target";
	private static final String FLOWOBJECT_NOT_CONTAINED_IN_PROCESS = "Each flow object must be contained in a process";
	private static final String ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "End events must have incoming sequence flow";
	private static final String STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "Start events must have outgoing sequence flow";
	private static final String INTERMEDIATEEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "Intermediate events must have incoming sequence flow";
	private static final String STARTEVENT_WITH_INCOMING_CONTROL_FLOW = "Start events must not have incoming sequence flow";
	private static final String ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW = "Attached intermediate events must not have incoming sequence flow";
	private static final String ENDEVENT_WITH_OUTGOING_CONTROL_FLOW = "End events must not have outgoing sequence flow";
	private static final String EVENTBASEDGATEWAY_BADCONTINUATION = "Event-based gateways must not be followed by gateways or subprocesses.";

	protected BPMNDiagram diagram;
	protected Map<String,String> errors;
	
	public BPMNSyntaxChecker(BPMNDiagram diagram) {
		this.diagram = diagram;
		this.errors = new HashMap<String,String>();
	}

	public boolean checkSyntax() {
		errors.clear();
		if (diagram == null)
			return false;
		
//		if (!checkEdges()) return false;
//		if (!checkNodesRecursively(diagram)) return false;
		checkEdges();
		checkNodesRecursively(diagram);
		
		return errors.size() == 0;
	}
	
	public Map<String,String> getErrors() {
		return errors;
	}

	protected boolean checkEdges() {
		for (Edge edge: diagram.getEdges()) {
			if (edge.getSource() == null)
				addError(edge, NO_SOURCE);
				//return false;
			if (edge.getTarget() == null) 
				addError(edge, NO_TARGET);
				//return false;
		}
		return true;
	}

	protected boolean checkNodesRecursively(Container container) {
		for (Node node: container.getChildNodes()) {
			
			checkNode(node);
//			if (!checkNode(node))
//				return false;
			
			if ((node instanceof Activity || node instanceof Event || node instanceof Gateway)
					&& node.getProcess() == null) {
				addError(node, FLOWOBJECT_NOT_CONTAINED_IN_PROCESS);
//				return false;
			}

			// cardinality of control flow
			if ((node instanceof EndEvent) // || node instanceof Gateway)
					&& !hasIncomingControlFlow(node)) {
				addError(node, ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW);
//				return false;
			}
			if ((node instanceof StartEvent) // || node instanceof Gateway)
					&& !hasOutgoingControlFlow(node)) {
				addError(node, STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW);
//				return false;
			}
			if (node instanceof IntermediateEvent && ((IntermediateEvent)node).getActivity() == null 
					&& !hasIncomingControlFlow(node)) {
				addError(node, INTERMEDIATEEVENT_WITHOUT_INCOMING_CONTROL_FLOW);
//				return false;
			}
//			if ((node instanceof Activity || node instanceof EndEvent || node instanceof Gateway)
//					&& !hasIncomingControlFlow(node)) return false;
//			if ((node instanceof Activity || node instanceof StartEvent || node instanceof IntermediateEvent || node instanceof Gateway)
//					&& !hasOutgoingControlFlow(node)) return false;
//			if (node instanceof IntermediateEvent && ((IntermediateEvent)node).getActivity() == null 
//					&& !hasIncomingControlFlow(node)) return false;
			
			if (node instanceof StartEvent 
					&& hasIncomingControlFlow(node)) {
				addError(node, STARTEVENT_WITH_INCOMING_CONTROL_FLOW);
//				return false;
			}
			if (node instanceof EndEvent 
					&& hasOutgoingControlFlow(node)) {
				addError(node, ENDEVENT_WITH_OUTGOING_CONTROL_FLOW);
//				return false;
			}
			if (node instanceof IntermediateEvent && ((IntermediateEvent)node).getActivity() != null 
					&& hasIncomingControlFlow(node)) {
				addError(node, ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW);
//				return false;
			}
			
			if (node instanceof XOREventBasedGateway) {
				checkEventBasedGateway((XOREventBasedGateway)node);
			}
			
			if (node instanceof Container)
				checkNodesRecursively((Container)node);
//				if (!checkNodesRecursively((Container)node))
//					return false;
		}
		return (errors.size() == 0);
	}

	protected boolean checkNode(Node node) {
		if (node.getParent() == null) 
			return false;
		
		return true;
	}

	protected boolean hasIncomingControlFlow(Node node) {
		for (Edge edge: node.getIncomingEdges())
			if (edge instanceof ControlFlow)
				return true;
		return false;
	}

	protected boolean hasOutgoingControlFlow(Node node) {
		for (Edge edge: node.getOutgoingEdges())
			if (edge instanceof ControlFlow)
				return true;
		return false;
	}
	
	protected void checkEventBasedGateway(XOREventBasedGateway gateway) {
		for (Edge e: gateway.getOutgoingEdges()) {
			DiagramObject obj = e.getTarget();
			if (obj instanceof Gateway || obj instanceof SubProcess) 
				addError(gateway, EVENTBASEDGATEWAY_BADCONTINUATION);
		}
	}

	protected void addError(DiagramObject obj, String errorCode) {
		errors.put(obj.getResourceId(), errorCode);
	}

}
