package de.hpi.highpetrinet;

import de.hpi.PTnet.PTNet;
import de.hpi.highpetrinet.verification.HighPNInterpreter;

public class HighPetriNet extends PTNet {

	@Override
	public HighPNInterpreter getInterpreter() {
		return new HighPNInterpreter();
	}

}