package de.hpi.highpetrinet;

import de.hpi.PTnet.PTNetFactory;

public class HighPetriNetFactory extends PTNetFactory {
	@Override
	public HighPetriNet createPetriNet() {
		return new HighPetriNet();
	}
	
	@Override
	public HighFlowRelationship createFlowRelationship() {
		return new HighFlowRelationship();
	}
}
