package de.hpi.PTnet.impl;

import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.impl.PetriNetReducer;

public class PTNetReducer extends PetriNetReducer {
	
	protected Place createNewPlace(PetriNet net, Place p1, Place p2) {
		PTNet ptnet = (PTNet)net;
		Place pnew = ptnet.getFactory().createPlace();
		ptnet.getInitialMarking().setNumTokens(pnew, ptnet.getInitialMarking().getNumTokens(p1) + ptnet.getInitialMarking().getNumTokens(p2));
		return pnew;
	}
	
	

}
