package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class DataObject extends Node {
	
	protected String state;
	protected String targetOfCopy;
	
	/**
	 * Returns a {@link Task} that is associated as an input of the data object.
	 * @return
	 * 		The first input {@link Task}
	 */
	public Task getFirstInputTask() {

		for (Edge e : this.getIncomingEdges()) {
			if ((e instanceof Association) && (e.source instanceof Task) ) {
				return (Task) e.source;
			}
		}
		
		return null;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTargetOfCopy() {
		return targetOfCopy;
	}

	public void setTargetOfCopy(String targetOfCopy) {
		this.targetOfCopy = targetOfCopy;
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
