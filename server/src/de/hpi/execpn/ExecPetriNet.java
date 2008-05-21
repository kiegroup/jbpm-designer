package de.hpi.execpn;

import de.hpi.petrinet.PetriNet;
import de.hpi.execpn.AutomaticTransition;

public interface ExecPetriNet extends PetriNet {

	String getName();
	
	void setName(String name);
}
