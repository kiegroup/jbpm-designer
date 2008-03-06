package de.hpi.PTnet;

import java.util.List;

import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetInterpreter;
import de.hpi.petrinet.Transition;

public interface PTNetInterpreter extends PetriNetInterpreter {
	
	List<Transition> getEnabledTransitions(PetriNet net, Marking marking);
	
	boolean[] getEnablement(PetriNet net, Marking marking);
	
	Marking fireTransition(PetriNet net, Marking marking, Transition t);

}
