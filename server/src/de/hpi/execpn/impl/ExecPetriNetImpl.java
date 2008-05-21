package de.hpi.execpn.impl;

import de.hpi.execpn.AutomaticTransition;
import de.hpi.execpn.ExecPetriNet;
import de.hpi.petrinet.impl.PetriNetImpl;

public class ExecPetriNetImpl extends PetriNetImpl implements ExecPetriNet {
	
	protected String name;
	protected AutomaticTransition tr_initPetrinet;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
