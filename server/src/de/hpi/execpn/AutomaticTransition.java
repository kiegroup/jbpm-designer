package de.hpi.execpn;

import de.hpi.petrinet.TauTransition;

public interface AutomaticTransition extends TauTransition {
	
	String getAction();
	String getLabel();
	void setAction(String action);
	void setLabel(String label);

}
