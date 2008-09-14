package de.hpi.petrinet.stepthrough;

import de.hpi.highpetrinet.HighSilentTransition;
import de.hpi.highpetrinet.HighTransition;

public class STSilentTransition extends HighSilentTransition implements HighTransition {

//	private boolean isOnlyTransitionForObj = true;
	private AutoSwitchLevel level;
	private int timesExecuted = 0;
		
	public int getTimesExecuted() {
		return timesExecuted;
	}

	public void incTimesExecuted() {
		timesExecuted ++;
	}

	public AutoSwitchLevel getAutoSwitchLevel() {
		return level;
	}
	
	public void setAutoSwitchLevel(AutoSwitchLevel level) {
		this.level = level;
	}

//	@Override
//	public boolean isOnlyTransitionForObj() {
//		return isOnlyTransitionForObj;
//	}
//
//	@Override
//	public void isOnlyTransitionForObj(boolean isOnlyTransition) {
//		isOnlyTransitionForObj = isOnlyTransition;
//	}
}
