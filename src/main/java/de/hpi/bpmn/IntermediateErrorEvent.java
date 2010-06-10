package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class IntermediateErrorEvent extends IntermediateEvent {
	
	protected String errorCode;

	public String getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}


	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}

	@Override
	public Node getCopy() {
		IntermediateErrorEvent newnode = (IntermediateErrorEvent)super.getCopy();
		newnode.setErrorCode(this.getErrorCode());
		return newnode;
	}

}
