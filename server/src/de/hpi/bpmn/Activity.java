package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

public abstract class Activity extends Node {
	
	protected List<IntermediateEvent> attachedEvents;

	public List<IntermediateEvent> getAttachedEvents() {
		if (attachedEvents == null)
			attachedEvents = new ArrayList<IntermediateEvent>();
		return attachedEvents;
	}

}
