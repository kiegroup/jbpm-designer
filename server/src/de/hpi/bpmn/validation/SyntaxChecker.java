package de.hpi.bpmn.validation;

import de.hpi.bpmn.Activity;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.ControlFlow;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.Event;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.StartEvent;

public class SyntaxChecker {
	
	protected BPMNDiagram diagram;
	
	public SyntaxChecker(BPMNDiagram diagram) {
		this.diagram = diagram;
	}

	public boolean checkSyntax() {
		if (diagram == null)
			return false;
		
		if (!checkEdges()) return false;
		if (!checkNodesRecursively(diagram)) return false;
		
		return true;
	}

	protected boolean checkEdges() {
		for (Edge edge: diagram.getEdges()) {
			if (edge.getSource() == null) return false;
			if (edge.getTarget() == null) return false;
		}
		return true;
	}

	protected boolean checkNodesRecursively(Container container) {
		for (Node node: container.getChildNodes()) {
			
			if (node.getParent() == null) return false;
			if ((node instanceof Activity || node instanceof Event || node instanceof Gateway)
					&& node.getProcess() == null) return false;

			// cardinality of control flow
			if ((node instanceof EndEvent || node instanceof Gateway)
					&& !hasIncomingControlFlow(node)) return false;
			if ((node instanceof StartEvent || node instanceof Gateway)
					&& !hasOutgoingControlFlow(node)) return false;
			if (node instanceof IntermediateEvent && ((IntermediateEvent)node).getActivity() == null 
					&& !hasIncomingControlFlow(node)) return false;
//			if ((node instanceof Activity || node instanceof EndEvent || node instanceof Gateway)
//					&& !hasIncomingControlFlow(node)) return false;
//			if ((node instanceof Activity || node instanceof StartEvent || node instanceof IntermediateEvent || node instanceof Gateway)
//					&& !hasOutgoingControlFlow(node)) return false;
//			if (node instanceof IntermediateEvent && ((IntermediateEvent)node).getActivity() == null 
//					&& !hasIncomingControlFlow(node)) return false;
			
			if (node instanceof StartEvent 
					&& hasIncomingControlFlow(node)) return false;
			if (node instanceof EndEvent 
					&& hasOutgoingControlFlow(node)) return false;
			if (node instanceof IntermediateEvent && ((IntermediateEvent)node).getActivity() != null 
					&& hasIncomingControlFlow(node)) return false;
			
			if (node instanceof Container)
				if (!checkNodesRecursively((Container)node))
					return false;
		}
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

}
