package de.hpi.highpetrinet;

import de.hpi.PTnet.PTNetFactory;
import de.hpi.PTnet.verification.PTNetInterpreter;
import de.hpi.highpetrinet.verification.HighPNInterpreter;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.SilentTransition;

public class HighPetriNetFactory extends PTNetFactory {
	@Override
	public PTNetInterpreter createInterpreter() {
		return new HighPNInterpreter();
	}

	@Override
	public HighPetriNet createPetriNet() {
		return new HighPetriNet();
	}
	
	@Override
	public HighFlowRelationship createFlowRelationship() {
		return new HighFlowRelationship();
	}

	@Override
	public LabeledTransition createLabeledTransition() {
		return new HighLabeledTransition();
	}

	@Override
	public SilentTransition createSilentTransition() {
		return new HighSilentTransition();
	}
	
	
}
