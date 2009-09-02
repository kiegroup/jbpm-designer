/**
 * Copyright (c) 2009
 * Ingo Kitzmann, Christoph Koenig
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package de.hpi.layouting.topologicalsort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.hpi.layouting.model.LayoutingDiagram;
import de.hpi.layouting.model.LayoutingElement;
import de.hpi.layouting.model.LayoutingElementImpl;

public abstract class TopologicalSorter {
	
	protected LinkedList<LayoutingElement> sortetElements;
	protected Map<String, SortableLayoutingElement> elementsToSort;
	protected LayoutingDiagram diagram;
	protected List<BackwardsEdge> backwardsEdges;

	public TopologicalSorter(LayoutingDiagram diagram, LayoutingElement parent) {
		this.diagram = diagram;
		// First step to find loops and backpatch backwards edges
		prepareDataAndSort(parent, true);
		// Second step to get the real sorting
		prepareDataAndSort(parent, false);
	}

	protected void prepareDataAndSort(LayoutingElement parent, boolean shouldBackpatch) {
		sortetElements = new LinkedList<LayoutingElement>();
		elementsToSort = new HashMap<String, SortableLayoutingElement>();
		backwardsEdges = new LinkedList<BackwardsEdge>();

		// create global start
		LayoutingElement globalStartDummyElement = new LayoutingElementImpl();
		globalStartDummyElement.setId("#####Global-Start#####");
		
		// cache start events
		List<LayoutingElement> startEvents = new ArrayList<LayoutingElement>();
		for (LayoutingElement startElement : this.diagram.getStartEvents()) {
			globalStartDummyElement.addOutgoingLink(startElement);
			startElement.addIncomingLink(globalStartDummyElement);
			startEvents.add(startElement);
		}
		elementsToSort.put(globalStartDummyElement.getId(),
				new SortableLayoutingElement(globalStartDummyElement));

		addAllChilds(parent);

		topologicalSort();

		if (shouldBackpatch) {
			backpatchBackwardsEdges();
		}
		// write backwards edges in diagram
		reverseBackwardsEdges();
		// remove global start
		for (LayoutingElement startElement : startEvents) {
			globalStartDummyElement.removeOutgoingLink(startElement);
			startElement.removeIncomingLink(globalStartDummyElement);
		}
		this.sortetElements.remove(globalStartDummyElement);
	}

	protected void addAllChilds(LayoutingElement parent) {
		for (LayoutingElement element : diagram.getChildElementsOf(parent)) {
			elementsToSort.put(element.getId(), new SortableLayoutingElement(
						element));
		}
	}

	public Queue<LayoutingElement> getSortedElements() {
		return this.sortetElements;
	}

	protected void topologicalSort() {
		while (!elementsToSort.isEmpty()) {
			List<SortableLayoutingElement> freeElements = getFreeElements();
			if (freeElements.size() > 0) {
				for (SortableLayoutingElement freeElement : freeElements) {
					sortetElements.add((LayoutingElement)freeElement.getLayoutingElement());
					freeElementsFrom(freeElement);
					elementsToSort.remove(freeElement.getId());
				}
			} else { // loops
				SortableLayoutingElement entry = getLoopEntryPoint();
				for (String backId : entry.getIncomingLinks().toArray(
						new String[0])) {
					entry.reverseIncomingLinkFrom(backId);
					SortableLayoutingElement e = elementsToSort.get(backId);
					e.reverseOutgoingLinkTo(entry.getId());
					backwardsEdges
							.add(new BackwardsEdge(backId, entry.getId()));
				}
			}
		}
	}

	protected SortableLayoutingElement getLoopEntryPoint()
			throws IllegalStateException {
		for (SortableLayoutingElement candidate : elementsToSort.values()) {
			if (candidate.isJoin()
					&& candidate.getOldInCount() > candidate.getIncomingLinks()
							.size()) {
				return candidate;
			}
		}
		/*for (LayoutingElement e : this.sortetElements) {
			System.out.println(e.getId());
		}*/
		throw new IllegalStateException(
				"Could not find a valid loop entry point");
	}

	protected void freeElementsFrom(SortableLayoutingElement freeElement) {
		for (String id : freeElement.getOutgoingLinks()) {
			SortableLayoutingElement element = elementsToSort.get(id);
			if (element != null) {
				element.removeIncomingLinkFrom(freeElement.getId());
			}
		}

	}

	protected List<SortableLayoutingElement> getFreeElements() {
		List<SortableLayoutingElement> freeElements = new LinkedList<SortableLayoutingElement>();

		for (String id : elementsToSort.keySet()) {
			SortableLayoutingElement sortableLayoutingElement = elementsToSort.get(id);
			if (sortableLayoutingElement.isFree()) {
				freeElements.add(sortableLayoutingElement);
			}
		}

		return freeElements;
	}

	protected void reverseBackwardsEdges() {
		List<LayoutingElement> edges = this.diagram.getConnectingElements();
		for (BackwardsEdge backwardsEdge : this.backwardsEdges) {
			String sourceId = backwardsEdge.getSource();
			String targetId = backwardsEdge.getTarget();
			LayoutingElement sourceElement = (LayoutingElement) this.diagram.getElement(sourceId);
			LayoutingElement targetElement = (LayoutingElement) this.diagram.getElement(targetId);

			LayoutingElement edge = getEdge(edges, sourceElement, targetElement);

			backwardsEdge.setEdge(edge);

			// remove edge
			sourceElement.removeOutgoingLink(edge);
			targetElement.removeIncomingLink(edge);

			// add direct back link
			targetElement.addOutgoingLink(sourceElement);
			sourceElement.addIncomingLink(targetElement);
		}

	}

	protected void backpatchBackwardsEdges() {
		List<BackwardsEdge> newBackwardsEdges = new LinkedList<BackwardsEdge>();
		newBackwardsEdges.addAll(this.backwardsEdges);

		for (BackwardsEdge edge : this.backwardsEdges) {
			String sourceId = edge.getSource();
			String targetId = edge.getTarget();

			LayoutingElement sourceElement = this.diagram.getElement(sourceId);
			while (!(sourceElement.isJoin() || sourceElement.isSplit())) {
				// should be not null and should be only one, because its
				// a path back
				LayoutingElement newSourceElement = (LayoutingElement) sourceElement
						.getPrecedingElements().get(0);
				targetId = newSourceElement.getId();
				newBackwardsEdges.add(new BackwardsEdge(targetId, sourceId));

				sourceElement = newSourceElement;
				sourceId = targetId;
			}
		}

		this.backwardsEdges = newBackwardsEdges;

	}

	protected static LayoutingElement getEdge(List<LayoutingElement> edges,
			LayoutingElement sourceElement, LayoutingElement targetElement) {
		for (LayoutingElement edge : edges) {
			if (edge.getIncomingLinks().contains(sourceElement)
					&& edge.getOutgoingLinks().contains(targetElement)) {
				return edge;
			}
		}
		return null;
	}


}
