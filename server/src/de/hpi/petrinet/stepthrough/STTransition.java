package de.hpi.petrinet.stepthrough;

import de.hpi.bpmn.DiagramObject;
import de.hpi.petrinet.Transition;

public interface STTransition extends Transition {
	DiagramObject getBPMNObj();
	void setBPMNObj(DiagramObject obj);
	
//	boolean isOnlyTransitionForObj();
//	void isOnlyTransitionForObj(boolean isOnlyTransition);
	
	AutoSwitchLevel getAutoSwitchLevel();
	void setAutoSwitchLevel(AutoSwitchLevel level);
	
	int getTimesExecuted();
	void incTimesExecuted();
}
