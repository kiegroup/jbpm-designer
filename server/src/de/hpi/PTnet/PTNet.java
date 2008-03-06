package de.hpi.PTnet;

import de.hpi.petrinet.PetriNet;

public interface PTNet extends PetriNet {
	
	PTNet getCopy();
	
	Marking getInitialMarking();
	
	PTNetFactory getFactory();
	
}
