package de.hpi.ibpmn.validation;

import de.hpi.bpmn.Node;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.Interaction;

/**
 * @author Gero.Decker
 */
public class IBPMNSyntaxChecker extends BPMNSyntaxChecker {

	private static final String NO_ROLE_SET = "Interactions must have a sender and a receiver role set";

	public IBPMNSyntaxChecker(IBPMNDiagram diagram) {
		super(diagram);
	}

	@Override
	protected boolean checkNode(Node node) {
		if (node instanceof Interaction) {
			Interaction i = (Interaction) node;
			if (i.getSenderRole() == null) {
				addError(node, NO_ROLE_SET);
				return false;
			}
			if (i.getReceiverRole() == null) {
				addError(node, NO_ROLE_SET);
				return false;
			}
		}
		return true;
	}

}


