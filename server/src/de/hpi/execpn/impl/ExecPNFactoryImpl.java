package de.hpi.execpn.impl;

import de.hpi.bpmn2pn.model.ExecPlaceImpl;
import de.hpi.execpn.AutomaticTransition;
import de.hpi.execpn.ExecFlowRelationship;
import de.hpi.execpn.FormTransition;
import de.hpi.execpn.TransformationTransition;
import de.hpi.petrinet.ExecPlace;
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
		AutomaticTransition tau = new AutomaticTransitionImpl();
		return tau;
	}
	
	public TransformationTransition createTransformationTransition(){
		return new TransformationTransitionImpl();
	}

	public AutomaticTransition createAutomaticTransition(){
		return new AutomaticTransitionImpl();
	}

	public ExecFlowRelationship createExecFlowRelationship(){
		return new ExecFlowRelationshipImpl();
	}
	
	public FormTransition createFormTransition() {
		return new FormTransitionImpl();
	}
	
	@Override
	public PetriNet createPetriNet() {
		return new ExecPetriNetImpl();
	}
	
	public ExecPlace createPlace() {
		return new ExecPlaceImpl();
	}

}
