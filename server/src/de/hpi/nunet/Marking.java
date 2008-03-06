package de.hpi.nunet;

import java.util.List;
import java.util.Set;

public interface Marking {
	
	Marking getCopy();
	
	boolean containsName(String name);
	
	Set<String> getNames();
	
	String toStringUncolored(List<Place> places);

	String toStringUncolored();
	
	List<Token> getTokens(Place p);
	
	int getNumTokens();

}
