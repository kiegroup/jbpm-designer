package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hpi.bpmn2bpel.model.Container4BPEL;

public class Process implements Container, Container4BPEL {
	
	protected List<Node> childNodes;

	public List<Node> getChildNodes() {
		if (childNodes == null)
			childNodes = new ArrayList();
		return childNodes;
	}

	@Override
	public List<EndEvent> getEndEvents() {
		ArrayList<EndEvent> endEvents = new ArrayList<EndEvent>();
		for(Iterator<Node> it = getChildNodes().iterator(); it.hasNext();) {
			Node node = it.next();
			if(node instanceof EndEvent) {
				endEvents.add((EndEvent) node);
			}
		}
		
		return endEvents;
	}

	@Override
	public List<StartEvent> getStartEvents() {
		ArrayList<StartEvent> StartEvents = new ArrayList<StartEvent>();
		for(Iterator<Node> it = getChildNodes().iterator(); it.hasNext();) {
			Node node = it.next();
			if(node instanceof StartEvent) {
				StartEvents.add((StartEvent) node);
			}
		}
		
		return StartEvents;
	}

}
