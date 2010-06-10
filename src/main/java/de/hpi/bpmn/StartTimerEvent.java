package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class StartTimerEvent extends StartEvent {

	protected String timeDate;
	
	protected String timeCycle;
	
	public String getTimeCycle() {
		return timeCycle;
	}


	public void setTimeCycle(String timeCycle) {
		this.timeCycle = timeCycle;
	}


	public String getTimeDate() {
		return timeDate;
	}


	public void setTimeDate(String timeDate) {
		this.timeDate = timeDate;
	}

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
	
	@Override
	public Node getCopy() {
		StartTimerEvent newnode = (StartTimerEvent)super.getCopy();
		newnode.setTimeCycle(this.getTimeCycle());
		newnode.setTimeDate(this.getTimeDate());
		return newnode;
	}

}
