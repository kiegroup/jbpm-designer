package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class IntermediateMultipleEvent extends IntermediateEvent {

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
		IntermediateMultipleEvent newnode = (IntermediateMultipleEvent)super.getCopy();
		newnode.setThrowing(this.isThrowing());
		return newnode;
	}

}
