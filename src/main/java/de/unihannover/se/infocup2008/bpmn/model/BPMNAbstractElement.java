package de.unihannover.se.infocup2008.bpmn.model;

import java.util.LinkedList;
import java.util.List;

import de.hpi.layouting.model.LayoutingElement;
import de.hpi.layouting.model.LayoutingElementImpl;


public abstract class BPMNAbstractElement extends LayoutingElementImpl implements BPMNElement {


	@Override
	public List<LayoutingElement> getFollowingElements() {
		List<LayoutingElement> followingElements = new LinkedList<LayoutingElement>();
	
		for (LayoutingElement element : getOutgoingLinks()) {
			if (BPMNType.isAConnectingElement(element.getType())) {
				followingElements.addAll(element.getFollowingElements());
			} else if (BPMNType.isAActivity(getType())
					&& BPMNType.isACatchingIntermediateEvent(element.getType())) {
				followingElements.addAll(element.getFollowingElements());
			} else if (!BPMNType.isASwimlane(element.getType())) {
				followingElements.add(element);
			}
		}
	
		return followingElements;
	}

	@Override
	public List<LayoutingElement> getPrecedingElements() {
		List<LayoutingElement> precedingElements = new LinkedList<LayoutingElement>();
	
		for (LayoutingElement element : getIncomingLinks()) {
			if (BPMNType.isAConnectingElement(element.getType())) {
				precedingElements.addAll(element.getPrecedingElements());
			} else if (BPMNType.isACatchingIntermediateEvent(getType())
					&& BPMNType.isAActivity(element.getType())) {
				precedingElements.addAll(element.getPrecedingElements());
			} else if (element instanceof BPMNElement) {
				if (((BPMNElement)element).isADockedIntermediateEvent())
					precedingElements.addAll(element.getIncomingLinks());
				else if (!BPMNType.isASwimlane(element.getType()))
					precedingElements.add(element);
			} else if (!BPMNType.isASwimlane(element.getType())) {
				precedingElements.add(element);
			}
		}
	
		return precedingElements;
	}



	public boolean isADockedIntermediateEvent() {
		if (!BPMNType.isACatchingIntermediateEvent(getType())) {
			return false;
		}
	
		for (LayoutingElement element : getIncomingLinks()) {
			if (BPMNType.isAActivity(element.getType())) {
				return true;
			}
		}
	
		return false;
	}
}