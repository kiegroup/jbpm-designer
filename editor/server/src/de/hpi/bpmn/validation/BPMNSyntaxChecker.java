package de.hpi.bpmn.validation;

import java.util.HashMap;
import java.util.Map;

import de.hpi.bpmn.Activity;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.Event;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.MessageFlow;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.XOREventBasedGateway;
import de.hpi.petrinet.verification.SyntaxChecker;

/**
 * Copyright (c) 2008 Gero Decker
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class BPMNSyntaxChecker implements SyntaxChecker {
	
	private static final String NO_SOURCE = "An edge must have a source.";
	private static final String NO_TARGET = "An edge must have a target.";
	private static final String DIFFERENT_PROCESS = "Source and target node must be contained in the same process.";
	private static final String SAME_PROCESS = "Source and target node must be contained in different pools.";
	private static final String FLOWOBJECT_NOT_CONTAINED_IN_PROCESS = "a flow object must be contained in a process.";
	private static final String ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "An end event must have incoming sequence flow.";
	private static final String STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "A start event must have outgoing sequence flow.";
//	private static final String INTERMEDIATEEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "An intermediate event must have incoming sequence flow.";
	private static final String STARTEVENT_WITH_INCOMING_CONTROL_FLOW = "Start events must not have incoming sequence flow.";
	private static final String ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW = "Attached intermediate events must not have incoming sequence flow.";
	private static final String ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "Attached intermediate events must have outgoing sequence flow.";
	private static final String ENDEVENT_WITH_OUTGOING_CONTROL_FLOW = "End events must not have outgoing sequence flow.";
	private static final String EVENTBASEDGATEWAY_BADCONTINUATION = "Event-based gateways must not be followed by gateways or subprocesses.";

	protected BPMNDiagram diagram;
	protected Map<String,String> errors;
	
	public BPMNSyntaxChecker(BPMNDiagram diagram) {
		this.diagram = diagram;
		this.errors = new HashMap<String,String>();
	}

	public boolean checkSyntax() {
		return checkSyntax(false);
	}
	
	public boolean checkSyntax(boolean checkControlFlowOnly) {
		errors.clear();
		if (diagram == null)
			return false;
		
//		if (!checkEdges()) return false;
//		if (!checkNodesRecursively(diagram)) return false;
		checkEdges(checkControlFlowOnly);
		checkNodesRecursively(diagram);
		
		return errors.size() == 0;
	}

	public Map<String,String> getErrors() {
		return errors;
	}

	protected boolean checkEdges(boolean checkControlFlowOnly) {
		for (Edge edge: diagram.getEdges()) {
			if (checkControlFlowOnly && !(edge instanceof SequenceFlow || edge instanceof MessageFlow))
				continue;
			
			if (edge.getSource() == null)
				addError(edge, NO_SOURCE);
				//return false;
			else if (edge.getTarget() == null) 
				addError(edge, NO_TARGET);
				//return false;
			else if (edge instanceof SequenceFlow) {
				if (((Node)edge.getSource()).getProcess() != ((Node)edge.getTarget()).getProcess())
					addError(edge, DIFFERENT_PROCESS);
			}
			else if (edge instanceof MessageFlow) {
				if (getPool(((Node)edge.getSource())) == getPool(((Node)edge.getTarget())))
					addError(edge, SAME_PROCESS);
			}
		}
		return true;
	}
	
	protected Pool getPool(DiagramObject obj){
		// if object itself is a Pool (for message flows coming from or going to pools)
		if(obj instanceof Pool){
			return (Pool)obj; 
		// if object itself is a Node (for message flows coming from or going to activities)
		} else if (obj instanceof Node) {
			return getPool(((Node)obj).getParent());
		} else {
			return null;
		}
	}

	protected Pool getPool(Container container) {
		while (container != null && !(container instanceof Pool) && !(container instanceof BPMNDiagram)) {
			if (container instanceof Node)
				container = ((Node)container).getParent();
			else
				return null;
		}
		if (container instanceof Pool)
			return (Pool)container;
		else
			return null;
	}

	protected boolean checkNodesRecursively(Container container) {
		for (Node node: container.getChildNodes()) {
			
			checkNode(node);
			
			if ((node instanceof Activity || node instanceof Event || node instanceof Gateway)
					&& node.getProcess() == null) {
				addError(node, FLOWOBJECT_NOT_CONTAINED_IN_PROCESS);
			}

			// cardinality of control flow
			if ((node instanceof EndEvent) // || node instanceof Gateway)
					&& !hasIncomingControlFlow(node)) {
				addError(node, ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW);
			}
			if ((node instanceof StartEvent) // || node instanceof Gateway)
					&& !hasOutgoingControlFlow(node)) {
				addError(node, STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW);
			}

			if (node instanceof StartEvent 
					&& hasIncomingControlFlow(node)) {
				addError(node, STARTEVENT_WITH_INCOMING_CONTROL_FLOW);
			}
			
			if (node instanceof EndEvent 
					&& hasOutgoingControlFlow(node)) {
				addError(node, ENDEVENT_WITH_OUTGOING_CONTROL_FLOW);
			}
			
			//attached intermediate events
			if (node instanceof IntermediateEvent && ((IntermediateEvent)node).getActivity() != null ) {
				if(hasIncomingControlFlow(node))
					addError(node, ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW);
				if(!hasOutgoingControlFlow(node))
					addError(node, ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW);
			}
			
			if (node instanceof XOREventBasedGateway) {
				checkEventBasedGateway((XOREventBasedGateway)node);
			}
			
			if (node instanceof Container)
				checkNodesRecursively((Container)node);
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
			if (edge instanceof SequenceFlow)
				return true;
		return false;
	}

	protected boolean hasOutgoingControlFlow(Node node) {
		for (Edge edge: node.getOutgoingEdges())
			if (edge instanceof SequenceFlow)
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
