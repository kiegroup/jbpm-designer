package de.hpi.execpn;

import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.SilentTransition;

public class ExecPNFactory extends PetriNetFactory {

	protected String modelURL;

	public ExecPNFactory(String modelURL) {
		super();
		this.modelURL = modelURL;
	}

	// TODO: is this OK ? 
	@Override
	public LabeledTransition createLabeledTransition() {
		FormTransition t = new FormTransition();
		t.setModelURL(modelURL);
		return t;
	}

	@Override
	public SilentTransition createSilentTransition() {
		AutomaticTransition tau = new AutomaticTransition();
		return tau;
	}
	
	public TransformationTransition createTransformationTransition(){
		return new TransformationTransition();
	}

	public AutomaticTransition createAutomaticTransition(){
		return new AutomaticTransition();
	}

	public ExecFlowRelationship createFlowRelationship(){
		return new ExecFlowRelationship();
	}
	
	public FormTransition createFormTransition() {
		return new FormTransition();
	}
	
	@Override
	public PetriNet createPetriNet() {
		return new ExecPetriNet();
	}
	
	public Place createPlace() {
		return new ExecPlace();
	}

}
