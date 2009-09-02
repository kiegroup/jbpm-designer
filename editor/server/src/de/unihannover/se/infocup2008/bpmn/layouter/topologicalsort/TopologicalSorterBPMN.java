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

import java.util.List;

import de.hpi.layouting.model.LayoutingDiagram;
import de.hpi.layouting.model.LayoutingElement;
import de.hpi.layouting.topologicalsort.BackwardsEdge;
import de.hpi.layouting.topologicalsort.SortableLayoutingElement;
import de.hpi.layouting.topologicalsort.TopologicalSorter;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElement;
import de.unihannover.se.infocup2008.bpmn.model.BPMNType;

/**
 * This class does a slight modified topological sort
 * 
 * @author Team Royal Fawn
 * 
 */
public class TopologicalSorterBPMN extends TopologicalSorter {


	public TopologicalSorterBPMN(LayoutingDiagram diagram,
			LayoutingElement parent) {
		super(diagram, parent);
	}

//	@Override
//	protected void prepareDataAndSort(LayoutingElement parent, boolean shouldBackpatch) {
//		sortetElements = new LinkedList<LayoutingElement>();
//		elementsToSort = new HashMap<String, SortableLayoutingElement>();
//		backwardsEdges = new LinkedList<BackwardsEdge>();
//
//		// create global start
//		LayoutingElement globalStartDummyElement = new LayoutingElementImpl();
//		globalStartDummyElement.setId("#####Global-Start#####");
//		globalStartDummyElement.setType(BPMNType.StartEvent);
//		for (LayoutingElement startElement : this.diagram.getStartEvents()) {
//			globalStartDummyElement.addOutgoingLink(startElement);
//			startElement.addIncomingLink(globalStartDummyElement);
//		}
//		elementsToSort.put(globalStartDummyElement.getId(),
//				new SortableLayoutingElement(globalStartDummyElement));
//
//		addAllChilds(parent);
//
//		topologicalSort();
//
//		if (shouldBackpatch) {
//			backpatchBackwardsEdges();
//		}
//		// write backwards edges in diagram
//		reverseBackwardsEdges();
//		// remove global start
//		for (LayoutingElement startElement : this.diagram.getStartEvents()) {
//			globalStartDummyElement.removeOutgoingLink(startElement);
//			startElement.removeIncomingLink(globalStartDummyElement);
//		}
//		this.sortetElements.remove(globalStartDummyElement);
//	}

	@Override
	/**
	 * @param parent
	 */
	protected void addAllChilds(LayoutingElement parent) {
		for (LayoutingElement el : diagram.getChildElementsOf(parent)) {
			BPMNElement element = (BPMNElement)el;
			// LayoutingElement element = diagram.getElement(id);
			if (!BPMNType.isAConnectingElement(element.getType())
					&& !element.isADockedIntermediateEvent()
					&& !BPMNType.isASwimlane(element.getType())) {
				elementsToSort.put(element.getId(), new SortableLayoutingElement(
						element));
			} else if (BPMNType.isASwimlane(element.getType())) {
				addAllChilds(element);
			}
		}
	}

	@Override
	protected void reverseBackwardsEdges() {
		List<LayoutingElement> edges = this.diagram.getConnectingElements();
		for (BackwardsEdge backwardsEdge : this.backwardsEdges) {
			String sourceId = backwardsEdge.getSource();
			String targetId = backwardsEdge.getTarget();
			LayoutingElement sourceElement = (LayoutingElement) this.diagram.getElement(sourceId);
			LayoutingElement targetElement = (LayoutingElement) this.diagram.getElement(targetId);

			LayoutingElement edge = getEdge(edges, (LayoutingElement)sourceElement, (LayoutingElement)targetElement);

			boolean elementSkipped = (edge == null);
			if (elementSkipped) {
				// catching intermediate events skipped
				for (LayoutingElement ol : sourceElement
						.getOutgoingLinks()) {
					BPMNElement outgoingLink = (BPMNElement) ol;
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
}
