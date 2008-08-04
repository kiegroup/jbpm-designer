package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

public abstract class Activity extends Node {
	
	protected List<IntermediateEvent> attachedEvents;
	
	protected LoopType loopType = LoopType.None;

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

}
