package de.hpi.petrinet;

public interface LabeledTransition extends Transition {
	
	String getLabel();
	
	//TODO: Doesn't belong here => execpn
	String getAction();
	String getTask();
	
	void setLabel(String label);
	
	//TODO: Doesn't belong here => execpn
	void setAction(String action);
	void setTask(String taskId);
}
