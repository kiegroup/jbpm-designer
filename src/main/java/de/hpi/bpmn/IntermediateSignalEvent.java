package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class IntermediateSignalEvent extends IntermediateEvent {

	protected boolean isThrowing = false;

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
	
	@Override
	public Node getCopy() {
		IntermediateSignalEvent newnode = (IntermediateSignalEvent)super.getCopy();
		newnode.setThrowing(this.isThrowing());
		return newnode;
	}

}
