package de.hpi.petrinet.stepthrough;

import de.hpi.PTnet.impl.PTNetFactoryImpl;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.TauTransition;
import de.hpi.petrinet.impl.PetriNetFactoryImpl;

public class STPetriNetFactoryImpl extends PTNetFactoryImpl { // oder PetriNetFactoryImpl?
	
	public LabeledTransition createLabeledTransition() {
		return new STLabeledTransitionImpl();
	}

	public TauTransition createTauTransition() {
		return new STTauTransitionImpl();
	}

}
