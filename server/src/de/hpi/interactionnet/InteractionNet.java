package de.hpi.interactionnet;

import java.util.List;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;

public interface InteractionNet extends PTNet {
	
	InteractionNet getCopy();
	
	List<Role> getRoles();
	
	Marking getInitialMarking();
	
	List<Marking> getFinalMarkings();
	
	InteractionNetFactory getFactory();

}
