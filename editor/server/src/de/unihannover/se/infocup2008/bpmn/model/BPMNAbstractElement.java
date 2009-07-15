package de.unihannover.se.infocup2008.bpmn.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public abstract class BPMNAbstractElement implements BPMNElement {

	private String type = "";
	private String id = "";
	private List<BPMNElement> outgoingLinks = new LinkedList<BPMNElement>();
	private List<BPMNElement> incomingLinks = new LinkedList<BPMNElement>();
	protected BPMNBounds geometry = new BPMNBoundsImpl();
	private BPMNElement parent = null;
	protected BPMNDockers dockers = new BPMNDockers();

	public BPMNAbstractElement() {
		super();
	}

	/**
	 * @return the geometry
	 */
	public BPMNBounds getGeometry() {
		return geometry;
	}

	/**
	 * @param geometry
	 *            the geometry to set
	 */
	public void setGeometry(BPMNBounds geometry) {
		this.geometry = geometry;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<BPMNElement> getOutgoingLinks() {
		return outgoingLinks;
	}

	public void setOutgoingLinks(List<BPMNElement> outgoingLinks) {
		this.outgoingLinks = outgoingLinks;
	}

	public void addOutgoingLink(BPMNElement element) {
		this.outgoingLinks.add(element);
	}

	public List<BPMNElement> getFollowingElements() {
		List<BPMNElement> followingElements = new LinkedList<BPMNElement>();
	
		for (BPMNElement element : getOutgoingLinks()) {
			if (BPMNType.isAConnectingElement(element.getType())) {
				followingElements.addAll(element.getFollowingElements());
			} else if (BPMNType.isAActivity(this.type)
					&& BPMNType.isACatchingIntermediateEvent(element.getType())) {
				followingElements.addAll(element.getFollowingElements());
			} else if (!BPMNType.isASwimlane(element.getType())) {
				followingElements.add(element);
			}
		}
	
		return followingElements;
	}

	public List<BPMNElement> getIncomingLinks() {
		return this.incomingLinks;
	}

	public void setIncomingLinks(List<BPMNElement> incomingLinks) {
		this.incomingLinks = incomingLinks;
	}

	public void addIncomingLink(BPMNElement element) {
		this.incomingLinks.add(element);
	}

	public List<BPMNElement> getPrecedingElements() {
		List<BPMNElement> precedingElements = new LinkedList<BPMNElement>();
	
		for (BPMNElement element : getIncomingLinks()) {
			if (BPMNType.isAConnectingElement(element.getType())) {
				precedingElements.addAll(element.getPrecedingElements());
			} else if (BPMNType.isACatchingIntermediateEvent(this.type)
					&& BPMNType.isAActivity(element.getType())) {
				precedingElements.addAll(element.getPrecedingElements());
			} else if (element.isADockedIntermediateEvent()) {
				precedingElements.addAll(element.getIncomingLinks());
			} else if (!BPMNType.isASwimlane(element.getType())) {
				precedingElements.add(element);
			}
		}
	
		return precedingElements;
	}

	public void removeIncomingLink(BPMNElement element) {
		this.incomingLinks.remove(element);
	}

	public void removeOutgoingLink(BPMNElement element) {
		this.outgoingLinks.remove(element);
	}

	public boolean isJoin() {
		return this.getPrecedingElements().size() > 1;
	}

	public boolean isSplit() {
		return this.getFollowingElements().size() > 1;
	}

	public int backwardDistanceTo(BPMNElement other) {
		return _backwardDistanceTo(other, Collections.EMPTY_SET);
	}

	/**
	 * @param other
	 * @return
	 */
	private int _backwardDistanceTo(BPMNElement other, Set<BPMNElement> history) {
		
		if (other == this) {
			return 0;
		}
		if (history.contains(this)){
			//Workaround to backwardsSeek Bug
			return Integer.MAX_VALUE;
		}
		int d = Integer.MAX_VALUE;
		Set<BPMNElement> newHistory = new HashSet<BPMNElement>(history);
		newHistory.add(this);
		for (BPMNElement el : this.getPrecedingElements()) {
			d = Math.min(d, ((BPMNAbstractElement) el)._backwardDistanceTo(other, newHistory));
		}
		return d == Integer.MAX_VALUE ? d : d + 1;
	}

	public int forwardDistanceTo(BPMNElement other) {
		if (other == this) {
			return 0;
		}
		int d = Integer.MAX_VALUE;
		for (BPMNElement el : this.getFollowingElements()) {
			d = Math.min(d, el.forwardDistanceTo(other));
		}
		return d == Integer.MAX_VALUE ? d : d + 1;
	}

	public BPMNElement prevSplit() {
		int distance = Integer.MAX_VALUE;
		int candidateDistance = 0;
		BPMNElement split = null;
		BPMNElement candidate;
		for (BPMNElement elem : this.getPrecedingElements()) {
			if (elem.isSplit() && elem.getParent() == this.getParent()) {
				return elem;
			}
			candidate = elem.prevSplit();
			if (this.isJoin()) {
				// Performance Twaek. If this is not a join, we have only one
				// precedessor and do not need to determine the closest one
				candidateDistance = elem.backwardDistanceTo(candidate);
			}
			if (candidateDistance < distance) {
				split = candidate;
				distance = candidateDistance;
			}
		}
		return split;
	}

	public boolean isADockedIntermediateEvent() {
		if (!BPMNType.isACatchingIntermediateEvent(this.type)) {
			return false;
		}
	
		for (BPMNElement element : this.incomingLinks) {
			if (BPMNType.isAActivity(element.getType())) {
				return true;
			}
		}
	
		return false;
	}

	public BPMNElement getParent() {
		return this.parent;
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	public void setParent(BPMNElement element) {
		this.parent = element;
	}

	public String toString() {
		String out = "BPMNElement: ";
		out += " ID=" + getId();
		out += " Type=" + getType();
		out += geometry.toString();
		out += " links=" + getOutgoingLinks().size();
		return out;
	}

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNElement#getDockers()
	 */
	public BPMNDockers getDockers() {
		return dockers;
	}

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNElement#setDockers(de.unihannover.se.infocup2008.bpmn.model.BPMNDockers)
	 */
	public void setDockers(BPMNDockers dockers) {
		this.dockers = dockers;
	}

}