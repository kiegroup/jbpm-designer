package de.hpi.PTnet;

import de.hpi.petrinet.Place;


public interface Marking extends de.hpi.petrinet.Marking {
	
	void setNumTokens(Place p, int numTokens);
	
	void addToken(Place p);
	
	boolean removeToken(Place p);
	
}
