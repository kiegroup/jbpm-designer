package de.hpi.petrinet.verification;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class PetriNetGraphAlgorithms {
	public static boolean checkFlowRelationShipsConnected(PetriNet net){
		for(FlowRelationship rel : net.getFlowRelationships() ) {
			if(rel.getSource() == null || rel.getTarget() == null)
				return false;
		}
		return true;
	}
	
	public static boolean checkAlternatingTransitionsAndPlaces(PetriNet net){
		for(FlowRelationship rel : net.getFlowRelationships() ) {
			if(!((rel.getSource() instanceof Transition && rel.getTarget() instanceof Place) ||
					(rel.getSource() instanceof Place && rel.getTarget() instanceof Transition)))
				return false;
		}
		return true;
	}
	
	public static boolean checkUniqueIds(PetriNet net){
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		for(Place node : net.getPlaces()){
			if(node.getId() != null && map.get(node.getId()) != null){
				System.out.println(node.getId());
				return false;
			} else {
				map.put(node.getId(), node);
			}
		}
		for(FlowRelationship node : net.getFlowRelationships()){
			if(node.getId() != null && map.get(node.getId()) != null){
				System.out.println(node.getId());
				return false;
			} else {
				map.put(node.getId(), node);
			}
		}
		for(Transition node : net.getTransitions()){
			if(node.getId() != null && map.get(node.getId()) != null){
				System.out.println(node.getId());
				return false;
			} else {
				map.put(node.getId(), node);
			}
		}
		
		return true;
	}
	
	public static List<Place> getInputPlaces(PetriNet net){
		List<Place> inputPlaces = new LinkedList<Place>();
		
		for(Place place : net.getPlaces()){
			if(place.getIncomingFlowRelationships().size() == 0){
				inputPlaces.add(place);
			}
		}
		
		return inputPlaces;
	}
	
	public static Place getPlaceById(PetriNet net, String id){
		for(Place place : net.getPlaces()){
			if(place.getId().equals(id)) return place;
		}
		return null;
	}
	
	public static Transition getTransitionById(PetriNet net, String id){
		for(Transition trans : net.getTransitions()){
			if(trans.getId().equals(id)) return trans;
		}
		return null;
	}
}
