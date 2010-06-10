package de.hpi.epc.layouting.model;

import java.util.LinkedList;
import java.util.List;

import de.hpi.layouting.model.LayoutingAbstractDiagram;
import de.hpi.layouting.model.LayoutingElement;

public class EPCDiagramImpl extends LayoutingAbstractDiagram<LayoutingElement> implements EPCDiagram {

	@Override
	protected EPCElement newElement() {
		return new EPCElementJSON();
	}
	
	public List<LayoutingElement> getStartEvents() {
		List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

		for (LayoutingElement element : getElements().values()) {
			if (!EPCType.isAnEvent(element.getType()))
				continue;
			if (element.getIncomingLinks().size() == 0)
				resultList.add((LayoutingElement)element);
		}

		return resultList;
	}

	public List<LayoutingElement> getConnectingElements() {
		List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

		for (LayoutingElement element : getElements().values()) {
			if (EPCType.isAConnectingElement(element.getType())) {
				resultList.add((LayoutingElement)element);
			}
		}

		return resultList;
	}

	public List<LayoutingElement> getGateways() {
		List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

		for (LayoutingElement element : getElements().values()) {
			if (EPCType.isAConnector(element.getType())) {
				resultList.add((LayoutingElement)element);
			}
		}

		return resultList;
	}


}
