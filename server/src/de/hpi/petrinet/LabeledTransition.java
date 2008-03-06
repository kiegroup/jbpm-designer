package de.hpi.petrinet;

public interface LabeledTransition extends Transition {
	
	String getLabel();
	
	void setLabel(String label);

}
