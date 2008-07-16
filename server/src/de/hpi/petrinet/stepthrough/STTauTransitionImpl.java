package de.hpi.petrinet.stepthrough;

import de.hpi.bpmn.DiagramObject;
import de.hpi.petrinet.TauTransition;
import de.hpi.petrinet.impl.TauTransitionImpl;

public class STTauTransitionImpl extends TauTransitionImpl implements STTransition, TauTransition {

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
