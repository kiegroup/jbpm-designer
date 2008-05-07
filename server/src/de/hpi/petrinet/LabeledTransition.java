package de.hpi.petrinet;

public interface LabeledTransition extends Transition {
	
	String getLabel();
	String getAction();
	
	void setLabel(String label);
	void setAction(String action);
}
