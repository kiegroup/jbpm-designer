package de.hpi.petrinet;

import java.util.List;
import java.util.Set;

public interface PetriNetInterpreter {
	
	List<Transition> getEnabledTransitions(PetriNet net, Marking marking);
	
	boolean[] getEnablement(PetriNet net, Marking marking);
	
	Marking fireTransition(PetriNet net, Marking marking, Transition t);
	
	Set<Transition> getReachableTransitions(PetriNet net, Marking marking);

}
