package de.hpi.bp;

import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Transition;

public final class BehaviouralProfileUtil {
		
	
	public void makeShortCircuitNet(PetriNet pn) {
		// we assume a workflow net
		assert(pn.isWorkflowNet());
		
		Transition te = pn.getFactory().createSilentTransition();
		te.setId("short-circuit");
		pn.getTransitions().add(te);
		
		FlowRelationship f1 = pn.getFactory().createFlowRelationship();
		f1.setSource(pn.getFinalPlace());
		f1.setTarget(te);
		pn.getFlowRelationships().add(f1);
		
		FlowRelationship f2 = pn.getFactory().createFlowRelationship();
		f2.setSource(te);
		f2.setTarget(pn.getInitialPlace());
		pn.getFlowRelationships().add(f2);
	}

}
