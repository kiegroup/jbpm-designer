package de.hpi.petrinet.stepthrough;

import de.hpi.highpetrinet.HighLabeledTransition;

public class STLabeledTransitionImpl extends HighLabeledTransition implements STTransition {

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
