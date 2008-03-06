package de.hpi.petrinet;


public interface Marking {
	
	Marking getCopy();
	
	int getNumTokens(Place p);
	
	int getNumTokens();

}
