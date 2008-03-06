package de.hpi.petrinet.impl;

import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Marking;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;
import de.hpi.petrinet.PetriNetInterpreter;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.TauTransition;

public class PetriNetFactoryImpl implements PetriNetFactory {

	public FlowRelationship createFlowRelationship() {
		return new FlowRelationshipImpl();
	}

	public Place createPlace() {
		return new PlaceImpl();
	}

	public LabeledTransition createLabeledTransition() {
		return new LabeledTransitionImpl();
	}

	public TauTransition createTauTransition() {
		return new TauTransitionImpl();
	}

	public PetriNetInterpreter createInterpreter() {
		return null;
	}

	public Marking createMarking(PetriNet net) {
		return null;
	}

	public PetriNet createPetriNet() {
		return new PetriNetImpl();
	}

	public static PetriNetFactory init() {
		return new PetriNetFactoryImpl();
	}

}
