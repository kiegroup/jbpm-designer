package de.hpi.petrinet.stepthrough;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;

public class STSyntaxChecker extends BPMNSyntaxChecker {
	
	private static final String COMP_GATEWAY = "The Complex Gateway is not supported.";
	private static final String OR_GATEWAY = "The OR Gateway is not supported.";
//	private static final String INCOMING_EDGES = "Multiple incoming edges are not supported.";
//	private static final String OUTGOING_EDGES = "Multiple outgoing edges are not supported.";

	public STSyntaxChecker(BPMNDiagram diagram) {
		super(diagram);
	}
	
	@Override
	protected boolean checkNode(Node node) {
		boolean ok = super.checkNode(node);
		
		// Complex Gateway and OR Gateway are not supported
		if (node instanceof ComplexGateway) {
			addError(node, COMP_GATEWAY);
			ok = false;
		}
		if (node instanceof ORGateway) {
			//addError(node, OR_GATEWAY);
			//ok = false;
		}

		return ok;
	}

//			// For activities (Task / SubProcess):
//			// only one incoming and one outgoing edge is supported
//			if (node instanceof Activity) {
//				int edges = 0;
//				for (Edge edge : node.getIncomingEdges()) {
//					if (edge instanceof ControlFlow)
//						edges++;
//				}
//				if (edges > 1) {
//					addError(node, INCOMING_EDGES);
//					incompatibilityFound = true;
//				}
//				edges = 0;
//				for (Edge edge : node.getOutgoingEdges()) {
//					if (edge instanceof ControlFlow)
//						edges++;
//				}
//				if (edges > 1) {
//					addError(node, OUTGOING_EDGES);
//					incompatibilityFound = true;
//				}
//			}
	
}
