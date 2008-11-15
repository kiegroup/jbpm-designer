package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

public abstract class Activity extends Node {
	
	public enum LoopType {
		None,Standard,Multiinstance
	}

	
	protected List<IntermediateEvent> attachedEvents;
	
	protected LoopType loopType = LoopType.None;
	
	protected String loopCondition = "";

	public List<IntermediateEvent> getAttachedEvents() {
		if (attachedEvents == null)
			attachedEvents = new ArrayList<IntermediateEvent>();
		return attachedEvents;
	}

	public LoopType getLoopType() {
		return loopType;
	}

	public void setLoopType(LoopType loopType) {
		this.loopType = loopType;
	}

	public String getLoopCondition() {
		return loopCondition;
	}

	public void setLoopCondition(String loopCondition) {
		this.loopCondition = loopCondition;
	}

	@Override
	public Node getCopy() {
		Activity newnode = (Activity)super.getCopy();
		newnode.setLoopCondition(this.getLoopCondition());
		newnode.setLoopType(this.getLoopType());
		return newnode;
	}

}
