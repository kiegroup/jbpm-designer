package de.hpi.PTnet;

import de.hpi.PTnet.impl.PTNetFactoryImpl;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;

public interface PTNetFactory extends PetriNetFactory {
	
	PTNetFactory eINSTANCE = PTNetFactoryImpl.init();
	
	PTNet createPetriNet();

	Marking createMarking(PetriNet net);
	
	PTNetInterpreter createInterpreter();

}
