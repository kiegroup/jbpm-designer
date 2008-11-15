package de.hpi.ibpmn2bpmn;

import de.hpi.bpmn.Activity;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.EndTerminateEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.XOREventBasedGateway;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.validation.IBPMNSyntaxChecker;

/**
 * @author Gero.Decker
 * 
 * Constraints to be checked:
 *  - no event-based gateways, no OR-gateways, no Complex Gateways
 *  - no multi-instance activities
 *  - no attached intermediate events
 *  - no end terminate events
 *  - TODO every (sub-)process has exactly one start and one end event
 *  - TODO no pool sets
 *  - TODO every node has at most one incoming and one outgoing edge (except gateways)
 *   
 */
public class ConstraintChecker extends IBPMNSyntaxChecker {

	private static final String GATEWAY_TYPE_NOT_SUPPORTED = "This gateway type is not supported in the mapping";
	private static final String MULTIINSTANCE_NOT_SUPPORTED = "Multi instance activities are not supported in the mapping";
	private static final String ATTACHED_EVENTS_NOT_SUPPORTED = "Attached events are not supported in the mapping";
	private static final String EVENT_TYPE_NOT_SUPPORTED = "This event type is not supported in the mapping";
	
	public ConstraintChecker(IBPMNDiagram diagram) {
		super(diagram);
	}

	@Override
	protected boolean checkNode(Node node) {
		boolean isOk = super.checkNode(node);
		
		if (node instanceof XOREventBasedGateway || node instanceof ORGateway || node instanceof ComplexGateway) {
			addError(node, GATEWAY_TYPE_NOT_SUPPORTED);
		} else if (node instanceof Activity) {
			Activity a = (Activity)node;
			if (a.getLoopType() == Activity.LoopType.Multiinstance) {
				addError(node, MULTIINSTANCE_NOT_SUPPORTED);
			}
			if (a.getAttachedEvents().size() > 0) {
				addError(node, ATTACHED_EVENTS_NOT_SUPPORTED);
			}
		} else if (node instanceof EndTerminateEvent) {
			addError(node, EVENT_TYPE_NOT_SUPPORTED);
		} 
		
		return isOk;
	}

}


