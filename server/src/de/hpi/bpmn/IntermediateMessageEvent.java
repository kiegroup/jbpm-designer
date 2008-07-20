package de.hpi.bpmn;

public class IntermediateMessageEvent extends IntermediateEvent {

	protected boolean isThrowing;

	public boolean isThrowing() {
		return isThrowing;
	}

	public void setThrowing(boolean isThrowing) {
		this.isThrowing = isThrowing;
	}

}
