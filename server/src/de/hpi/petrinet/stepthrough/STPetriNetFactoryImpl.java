package de.hpi.petrinet.stepthrough;

import de.hpi.PTnet.PTNetFactory;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.SilentTransition;

public class STPetriNetFactoryImpl extends PTNetFactory { // oder PetriNetFactoryImpl?
	
	public LabeledTransition createLabeledTransition() {
		return new STLabeledTransitionImpl();
	}

	public SilentTransition createSilentTransition() {
		return new STSilentTransition();
	}

}
