package de.hpi.PTnet.impl;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetFactory;
import de.hpi.PTnet.PTNetInterpreter;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.impl.PetriNetFactoryImpl;

public class PTNetFactoryImpl extends PetriNetFactoryImpl implements
		PTNetFactory {

	public Marking createMarking(PetriNet net) {
		return new MarkingImpl(net);
	}

	public PTNet createPetriNet() {
		return new PTNetImpl();
	}

	public static PTNetFactory init() {
		return new PTNetFactoryImpl();
	}

	public PTNetInterpreter createInterpreter() {
		return new PTNetInterpreterImpl();
	}

}
