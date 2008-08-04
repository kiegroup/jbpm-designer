package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class IntermediateSignalEvent extends IntermediateEvent {

	protected boolean isThrowing;

	public boolean isThrowing() {
		return isThrowing;
	}

	public void setThrowing(boolean isThrowing) {
		this.isThrowing = isThrowing;
	}

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
