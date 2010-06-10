package de.hpi.petrinet.stepthrough;

import de.hpi.highpetrinet.HighTransition;

public interface STTransition extends HighTransition {
	AutoSwitchLevel getAutoSwitchLevel();
	void setAutoSwitchLevel(AutoSwitchLevel level);
	
	int getTimesExecuted();
	void incTimesExecuted();
	
}
