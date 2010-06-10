package de.hpi.petrinet.stepthrough;

import de.hpi.highpetrinet.HighPetriNetFactory;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.SilentTransition;

public class STPetriNetFactoryImpl extends HighPetriNetFactory { // oder PetriNetFactoryImpl?
	public LabeledTransition createLabeledTransition() {
		return new STLabeledTransitionImpl();
	}

	public SilentTransition createSilentTransition() {
		return new STSilentTransition();
	}
}
