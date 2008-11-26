package de.hpi.ibpmn.validation;

import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.Interaction;

/**
 * @author Gero.Decker
 */
public class IBPMNSyntaxChecker extends BPMNSyntaxChecker {

	protected static final String NO_ROLE_SET = "Interactions must have a sender and a receiver role set";
	protected static final String NO_INCOMING_SEQFLOW = "This node must have incoming sequence flow.";
	protected static final String NO_OUTGOING_SEQFLOW = "This node must have outgoing sequence flow.";

	public IBPMNSyntaxChecker(IBPMNDiagram diagram) {
		super(diagram);
	}

	@Override
	protected boolean checkNode(Node node) {
		if (node instanceof Interaction) {
			Interaction i = (Interaction) node;
			if (i.getSenderRole() == null) {
				addError(node, NO_ROLE_SET);
//				return false;
			}
			if (i.getReceiverRole() == null) {
				addError(node, NO_ROLE_SET);
//				return false;
			}
		} 
		if (node instanceof IntermediateEvent || node instanceof Gateway) {
			if (node.getIncomingEdges().size() == 0) {
				addError(node, NO_INCOMING_SEQFLOW);
			}
		}
		if (node instanceof IntermediateEvent || node instanceof Gateway) {
			if (node.getOutgoingEdges().size() == 0) {
				addError(node, NO_OUTGOING_SEQFLOW);
			}
		}
		return true;
	}

}


