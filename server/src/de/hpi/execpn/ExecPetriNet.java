package de.hpi.execpn;

import de.hpi.petrinet.PetriNet;

public interface ExecPetriNet extends PetriNet {

	String getName();
	
	void setName(String name);
}
