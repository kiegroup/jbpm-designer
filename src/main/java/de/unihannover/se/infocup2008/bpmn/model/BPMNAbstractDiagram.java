package de.unihannover.se.infocup2008.bpmn.model;

import java.util.LinkedList;
import java.util.List;

import de.hpi.layouting.model.LayoutingAbstractDiagram;
import de.hpi.layouting.model.LayoutingElement;

public abstract class BPMNAbstractDiagram extends LayoutingAbstractDiagram<LayoutingElement> implements BPMNDiagram {

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getStartEvents()
	 */
	public List<LayoutingElement> getStartEvents() {
		List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

		for (LayoutingElement element : getElements().values()) {
			if (BPMNType.isAStartEvent(element.getType())) {
				resultList.add((LayoutingElement)element);
			}
		}

		return resultList;
	}

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getConnectingElements()
	 */
	public List<LayoutingElement> getConnectingElements() {
		List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

		for (LayoutingElement element : getElements().values()) {
			if (BPMNType.isAConnectingElement(element.getType())) {
				resultList.add((LayoutingElement)element);
			}
		}

		return resultList;
	}

	/* (non-Javadoc)
	 * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getGateways()
	 */
	public List<LayoutingElement> getGateways() {
		List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

		for (LayoutingElement element : getElements().values()) {
			if (BPMNType.isAGateWay(element.getType())) {
				resultList.add((LayoutingElement)element);
			}
		}

		return resultList;
	}

}
