package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class DataObject extends Node {
	
	protected String state;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}

	@Override
	public Node getCopy() {
		DataObject newnode = (DataObject)super.getCopy();
		newnode.setState(this.getState());
		return newnode;
	}

}
