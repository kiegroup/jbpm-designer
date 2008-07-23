package de.hpi.petrinet.stepthrough;

import de.hpi.bpmn.DiagramObject;
import de.hpi.petrinet.SilentTransitionImpl;

public class STSilentTransition extends SilentTransitionImpl implements STTransition {

	private DiagramObject BPMNObj;
//	private boolean isOnlyTransitionForObj = true;
	private AutoSwitchLevel level;
	private int timesExecuted = 0;
	
	public DiagramObject getBPMNObj() {
		return BPMNObj;
	}

	public void setBPMNObj(DiagramObject obj) {
		BPMNObj = obj;
	}
	
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
