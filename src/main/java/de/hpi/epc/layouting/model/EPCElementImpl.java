package de.hpi.epc.layouting.model;

import java.util.LinkedList;
import java.util.List;

import de.hpi.layouting.model.LayoutingElement;
import de.hpi.layouting.model.LayoutingElementImpl;

public class EPCElementImpl extends LayoutingElementImpl implements
		EPCElement {

	@Override
	public List<LayoutingElement> getFollowingElements() {
		List<LayoutingElement> followingElements = new LinkedList<LayoutingElement>();
		
		for (LayoutingElement element : getOutgoingLinks()) {
			if (EPCType.isAConnectingElement(element.getType())) {
				followingElements.addAll(element.getFollowingElements());
			}
			else {
				followingElements.add(element);
			}
		}
	
		return followingElements;
	}

	@Override
	public List<LayoutingElement> getPrecedingElements() {
		List<LayoutingElement> precedingElements = new LinkedList<LayoutingElement>();
		
		for (LayoutingElement element : getIncomingLinks()) {
			if (EPCType.isAConnectingElement(element.getType())) {
				precedingElements.addAll(element.getPrecedingElements());
			}
			else {
				precedingElements.add(element);
			}
		}
	
		return precedingElements;
	}

}
