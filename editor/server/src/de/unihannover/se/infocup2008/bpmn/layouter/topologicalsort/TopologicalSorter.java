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
package de.unihannover.se.infocup2008.bpmn.layouter.topologicalsort;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElement;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElementERDF;
import de.unihannover.se.infocup2008.bpmn.model.BPMNType;

/**
 * This class does a slight modified topological sort
 * 
 * @author Team Royal Fawn
 * 
 */
public class TopologicalSorter {

	private LinkedList<BPMNElement> sortetElements;
	private Map<String, SortableBPMNElement> elementsToSort;
	private BPMNDiagram diagram;
	private List<BackwardsEdge> backwardsEdges;

	public TopologicalSorter(BPMNDiagram diagram, BPMNElement parent) {
		this.diagram = diagram;
		// First step to find loops and backpatch backwards edges
		prepareDataAndSort(parent, true);
		// Second step to get the real sorting
		prepareDataAndSort(parent, false);
		//System.out.print("Backwards Edges: ");
		//System.out.println(backwardsEdges);
	}

	private void prepareDataAndSort(BPMNElement parent, boolean shouldBackpatch) {
		sortetElements = new LinkedList<BPMNElement>();
		elementsToSort = new HashMap<String, SortableBPMNElement>();
		backwardsEdges = new LinkedList<BackwardsEdge>();

		// create global start
		BPMNElement globalStartDummyElement = new BPMNElementERDF();
		globalStartDummyElement.setId("#####Global-Start#####");
		globalStartDummyElement.setType(BPMNType.StartEvent);
		for (BPMNElement startElement : this.diagram.getStartEvents()) {
			globalStartDummyElement.addOutgoingLink(startElement);
			startElement.addIncomingLink(globalStartDummyElement);
		}
		elementsToSort.put(globalStartDummyElement.getId(),
				new SortableBPMNElement(globalStartDummyElement));

		addAllChilds(parent);

		topologicalSort();

		if (shouldBackpatch) {
			backpatchBackwardsEdges();
		}
		// write backwards edges in diagram
		reverseBackwardsEdges();
		// remove global start
		for (BPMNElement startElement : this.diagram.getStartEvents()) {
			globalStartDummyElement.removeOutgoingLink(startElement);
			startElement.removeIncomingLink(globalStartDummyElement);
		}
		this.sortetElements.remove(globalStartDummyElement);
	}

	/**
	 * @param parent
	 */
	private void addAllChilds(BPMNElement parent) {
		for (BPMNElement element : diagram.getChildElementsOf(parent)) {
			// BPMNElement element = diagram.getElement(id);
			if (!BPMNType.isAConnectingElement(element.getType())
					&& !element.isADockedIntermediateEvent()
					&& !BPMNType.isASwimlane(element.getType())) {
				elementsToSort.put(element.getId(), new SortableBPMNElement(
						element));
			} else if (BPMNType.isASwimlane(element.getType())) {
				addAllChilds(element);
			}
		}
	}

	public Queue<BPMNElement> getSortedElements() {
		return this.sortetElements;
	}

	private void topologicalSort() {
		while (!elementsToSort.isEmpty()) {
			List<SortableBPMNElement> freeElements = getFreeElements();
			if (freeElements.size() > 0) {
				for (SortableBPMNElement freeElement : freeElements) {
					sortetElements.add(freeElement.getBPMNElement());
					freeElementsFrom(freeElement);
					elementsToSort.remove(freeElement.getId());
				}
			} else { // loops
				SortableBPMNElement entry = getLoopEntryPoint();
				for (String backId : entry.getIncomingLinks().toArray(
						new String[0])) {
					entry.reverseIncomingLinkFrom(backId);
					SortableBPMNElement e = elementsToSort.get(backId);
					e.reverseOutgoingLinkTo(entry.getId());
					backwardsEdges
							.add(new BackwardsEdge(backId, entry.getId()));
				}
			}
		}
	}

	private SortableBPMNElement getLoopEntryPoint()
			throws IllegalStateException {
		for (SortableBPMNElement candidate : elementsToSort.values()) {
			if (candidate.isJoin()
					&& candidate.getOldInCount() > candidate.getIncomingLinks()
							.size()) {
				return candidate;
			}
		}
		/*for (BPMNElement e : this.sortetElements) {
			System.out.println(e.getId());
		}*/
		throw new IllegalStateException(
				"Could not find a valid loop entry point");
	}

	private void freeElementsFrom(SortableBPMNElement freeElement) {
		for (String id : freeElement.getOutgoingLinks()) {
			SortableBPMNElement element = elementsToSort.get(id);
			if (element != null) {
				element.removeIncomingLinkFrom(freeElement.getId());
			}
		}

	}

	private List<SortableBPMNElement> getFreeElements() {
		List<SortableBPMNElement> freeElements = new LinkedList<SortableBPMNElement>();

		for (String id : elementsToSort.keySet()) {
			SortableBPMNElement sortableBPMNElement = elementsToSort.get(id);
			if (sortableBPMNElement.isFree()) {
				freeElements.add(sortableBPMNElement);
			}
		}

		return freeElements;
	}

	private void reverseBackwardsEdges() {
		List<BPMNElement> edges = this.diagram.getConnectingElements();
		for (BackwardsEdge backwardsEdge : this.backwardsEdges) {
			String sourceId = backwardsEdge.getSource();
			String targetId = backwardsEdge.getTarget();
			BPMNElement sourceElement = this.diagram.getElement(sourceId);
			BPMNElement targetElement = this.diagram.getElement(targetId);

			BPMNElement edge = getEdge(edges, sourceElement, targetElement);

			boolean elementSkipped = (edge == null);
			if (elementSkipped) {
				// catching intermediate events skipped
				for (BPMNElement outgoingLink : sourceElement
						.getOutgoingLinks()) {
					if (outgoingLink.isADockedIntermediateEvent()) {
						edge = getEdge(edges, outgoingLink, targetElement);
						if (edge != null) {
							System.err.println("found");
							break;
						}
					}
				}

			}

			backwardsEdge.setEdge(edge);

			// remove edge
			sourceElement.removeOutgoingLink(edge);
			targetElement.removeIncomingLink(edge);

			// add direct back link
			targetElement.addOutgoingLink(sourceElement);
			sourceElement.addIncomingLink(targetElement);
		}

	}

	private void backpatchBackwardsEdges() {
		List<BackwardsEdge> newBackwardsEdges = new LinkedList<BackwardsEdge>();
		newBackwardsEdges.addAll(this.backwardsEdges);

		for (BackwardsEdge edge : this.backwardsEdges) {
			String sourceId = edge.getSource();
			String targetId = edge.getTarget();

			BPMNElement sourceElement = this.diagram.getElement(sourceId);
			while (!(sourceElement.isJoin() || sourceElement.isSplit())) {
				// should be not null and should be only one, because its
				// a path back
				BPMNElement newSourceElement = sourceElement
						.getPrecedingElements().get(0);
				targetId = newSourceElement.getId();
				newBackwardsEdges.add(new BackwardsEdge(targetId, sourceId));

				sourceElement = newSourceElement;
				sourceId = targetId;
			}
		}

		this.backwardsEdges = newBackwardsEdges;

	}

	private static BPMNElement getEdge(List<BPMNElement> edges,
			BPMNElement sourceElement, BPMNElement targetElement) {
		for (BPMNElement edge : edges) {
			if (edge.getIncomingLinks().contains(sourceElement)
					&& edge.getOutgoingLinks().contains(targetElement)) {
				return edge;
			}
		}
		return null;
	}

}
