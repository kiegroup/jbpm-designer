package de.hpi.petrinet;

import java.util.List;
import java.util.Map;

public interface PetriNet {
	
	PetriNet getCopy();

	List<Place> getPlaces();
	
	List<Transition> getTransitions();
	
	List<FlowRelationship> getFlowRelationships();
	
	Marking getInitialMarking();
	
	
	/**
	 * removes unnecessary tau transitions and redundant places and transitions
	 */
	void optimize(Map<String,Boolean> parameters);
	
	static final String REMOVE_REDUNDANTPLACES = "REMOVE_REDUNDANTPLACES";
	static final String REMOVE_REDUNDANTTRANSITIONS = "REMOVE_REDUNDANTTRANSITIONS";
	static final String REMOVE_UNNECESSARYPLACES = "REMOVE_UNNECESSARYPLACES";
	static final String REMOVE_UNNECESSARYTRANSITIONS = "REMOVE_UNNECESSARYTRANSITIONS";
	static final String REMOVE_ALLTAUTRANSITIONS = "REMOVE_ALLTAUTRANSITIONS";
	static final String REMOVE_EASYTAUTRANSITIONS = "REMOVE_EASYTAUTRANSITIONS";
	static final String REMOVE_UNREACHABLETRANSITIONS = "REMOVE_UNREACHABLETRANSITIONS";	
	
	
	SyntaxChecker getSyntaxChecker();
	
	PetriNetInterpreter getInterpreter();

	PetriNetFactory getFactory();
	
}
