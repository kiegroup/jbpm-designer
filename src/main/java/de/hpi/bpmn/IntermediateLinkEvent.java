package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class IntermediateLinkEvent extends IntermediateEvent {
	
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
		IntermediateLinkEvent newnode = (IntermediateLinkEvent)super.getCopy();
		newnode.setThrowing(this.isThrowing());
		return newnode;
	}

}
