package de.hpi.petrinet;



public abstract class AbstractPetriNetTest {

	protected FlowRelationship createFlowRelationship(PetriNet net, Node source, Node target){
		FlowRelationship rel = new FlowRelationship();
		rel.setSource(source);
		rel.setTarget(target);
		net.getFlowRelationships().add(rel);
		return rel;
	}

	protected Transition createTransition(PetriNet net){
		return createTransition(net, null);
	}
	
	
	protected Transition createTransition(PetriNet net, String id){
		Transition trans = new LabeledTransitionImpl();
		trans.setId(id);
		net.getTransitions().add(trans);
		return trans;
	}
	
	protected Place createPlace(PetriNet net, String id){
		Place place = new PlaceImpl();
		place.setId(id);
		net.getPlaces().add(place);
		return place;
	}
}
