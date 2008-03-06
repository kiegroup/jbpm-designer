package de.hpi.execpn.impl;

import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.TauTransition;
import de.hpi.petrinet.impl.PetriNetFactoryImpl;

public class ExecPNFactoryImpl extends PetriNetFactoryImpl {

	protected String modelURL;

	public ExecPNFactoryImpl(String modelURL) {
		super();
		this.modelURL = modelURL;
	}

	// TODO: is this OK ? 
	@Override
	public LabeledTransition createLabeledTransition() {
		FormTransitionImpl t = new FormTransitionImpl();
		t.setModelURL(modelURL);
		return t;
	}

	@Override
	public TauTransition createTauTransition() {
		return new AutomaticTransitionImpl();
	}

	@Override
	public PetriNet createPetriNet() {
		return new ExecPetriNetImpl();
	}

}
