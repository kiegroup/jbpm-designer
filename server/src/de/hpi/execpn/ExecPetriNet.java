package de.hpi.execpn;

import de.hpi.petrinet.PetriNet;

public class ExecPetriNet extends PetriNet {

	protected String name;
	protected AutomaticTransition tr_initPetrinet;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
