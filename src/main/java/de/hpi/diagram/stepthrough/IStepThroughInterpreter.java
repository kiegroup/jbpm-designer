package de.hpi.diagram.stepthrough;

import de.hpi.petrinet.stepthrough.AutoSwitchLevel;

public interface IStepThroughInterpreter {
	public boolean fireObject(String resourceId);
	public String getChangedObjsAsString();
	public void clearChangedObjs();
	public void setAutoSwitchLevel(AutoSwitchLevel autoSwitchLevel);
}
