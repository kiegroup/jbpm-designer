package de.hpi.petrinet;

import de.hpi.petrinet.impl.PetriNetFactoryImpl;




public interface PetriNetFactory {

	PetriNetFactory eINSTANCE = PetriNetFactoryImpl.init();

	PetriNet createPetriNet();
	
	Place createPlace();
	
	LabeledTransition createLabeledTransition();
	
	TauTransition createTauTransition();
	
	FlowRelationship createFlowRelationship();
	
	Marking createMarking(PetriNet net);
	
	PetriNetInterpreter createInterpreter();

}
