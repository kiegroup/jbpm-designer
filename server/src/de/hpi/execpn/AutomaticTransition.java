package de.hpi.execpn;

import de.hpi.petrinet.TauTransition;

public interface AutomaticTransition extends TauTransition {
	
	String getAction();
	String getLabel();
	String getTask();
	String getXsltURL();
	
	void setAction(String action);
	void setLabel(String label);
	void setTask(String taskId);
	void setXsltURL(String xsltUrl);

}
