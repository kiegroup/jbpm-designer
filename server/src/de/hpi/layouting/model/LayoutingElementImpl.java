package de.hpi.layouting.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class LayoutingElementImpl implements LayoutingElement {

	private String type = "";
	private String id = "";
	private List<LayoutingElement> outgoingLinks = new LinkedList<LayoutingElement>();
	private List<LayoutingElement> incomingLinks = new LinkedList<LayoutingElement>();
	protected LayoutingBounds geometry = new LayoutingBoundsImpl();
	private LayoutingElement parent = null;
	protected LayoutingDockers dockers = new LayoutingDockers();


	public LayoutingElementImpl() {
		super();
	}

	/**
	 * @return the geometry
	 */
	public LayoutingBounds getGeometry() {
		return geometry;
	}

	/**
	 * @param geometry
	 *            the geometry to set
	 */
	public void setGeometry(LayoutingBounds geometry) {
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

	public List<LayoutingElement> getOutgoingLinks() {
		return outgoingLinks;
	}

	public void setOutgoingLinks(List<LayoutingElement> outgoingLinks) {
		this.outgoingLinks = outgoingLinks;
	}

	public void addOutgoingLink(LayoutingElement element) {
		this.outgoingLinks.add(element);
	}

	public List<LayoutingElement> getIncomingLinks() {
		return this.incomingLinks;
	}

	public void setIncomingLinks(List<LayoutingElement> incomingLinks) {
		this.incomingLinks = incomingLinks;
	}

	public void addIncomingLink(LayoutingElement element) {
		this.incomingLinks.add(element);
	}
	
	public void removeIncomingLink(LayoutingElement element) {
		this.incomingLinks.remove(element);
	}

	public void removeOutgoingLink(LayoutingElement element) {
		this.outgoingLinks.remove(element);
	}

	public boolean isJoin() {
		return this.getPrecedingElements().size() > 1;
	}

	public boolean isSplit() {
		return this.getFollowingElements().size() > 1;
	}
	public int backwardDistanceTo(LayoutingElement other) {
		return _backwardDistanceTo(other, Collections.EMPTY_SET);
	}

	/**
	 * @param other
	 * @return
	 */
	private int _backwardDistanceTo(LayoutingElement other, Set<LayoutingElement> history) {
		
		if (other == this) {
			return 0;
		}
		if (history.contains(this)){
			//Workaround to backwardsSeek Bug
			return Integer.MAX_VALUE;
		}
		int d = Integer.MAX_VALUE;
		Set<LayoutingElement> newHistory = new HashSet<LayoutingElement>(history);
		newHistory.add(this);
		for (LayoutingElement el : this.getPrecedingElements()) {
			d = Math.min(d, ((LayoutingElementImpl) el)._backwardDistanceTo(other, newHistory));
		}
		return d == Integer.MAX_VALUE ? d : d + 1;
	}

	public int forwardDistanceTo(LayoutingElement other) {
		if (other == this) {
			return 0;
		}
		int d = Integer.MAX_VALUE;
		for (LayoutingElement el : this.getFollowingElements()) {
			d = Math.min(d, el.forwardDistanceTo(other));
		}
		return d == Integer.MAX_VALUE ? d : d + 1;
	}

	public LayoutingElement prevSplit() {
		int distance = Integer.MAX_VALUE;
		int candidateDistance = 0;
		LayoutingElement split = null;
		LayoutingElement candidate;
		for (LayoutingElement elem : this.getPrecedingElements()) {
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

	public LayoutingElement getParent() {
		return this.parent;
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	public void setParent(LayoutingElement element) {
		this.parent = element;
	}

	public String toString() {
		String out = "LayoutingElement: ";
		out += " ID=" + getId();
		out += " Type=" + getType();
		out += geometry.toString();
		out += " links=" + getOutgoingLinks().size();
		return out;
	}

	public LayoutingDockers getDockers() {
		return dockers;
	}

	public void setDockers(LayoutingDockers dockers) {
		this.dockers = dockers;
	}

	public List<LayoutingElement> getFollowingElements() {
		List<LayoutingElement> followingElements = new LinkedList<LayoutingElement>();
		for (LayoutingElement element : getOutgoingLinks()) {
				followingElements.add(element);
		}
		return followingElements;
	}

	public List<LayoutingElement> getPrecedingElements() {
		List<LayoutingElement> precedingElements = new LinkedList<LayoutingElement>();
		for (LayoutingElement element : getIncomingLinks()) {
			precedingElements.add(element);
		}
		return precedingElements;
	}

}
