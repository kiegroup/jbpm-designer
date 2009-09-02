package de.hpi.epc.layouting;

import de.hpi.epc.layouting.model.EPCElement;
import de.hpi.epc.layouting.model.EPCType;
import de.hpi.layouting.model.LayoutingDiagram;
import de.hpi.layouting.model.LayoutingElement;
import de.hpi.layouting.topologicalsort.SortableLayoutingElement;
import de.hpi.layouting.topologicalsort.TopologicalSorter;

public class TopologicalSorterEPC extends TopologicalSorter {

	public TopologicalSorterEPC(LayoutingDiagram diagram,
			LayoutingElement parent) {
		super(diagram, parent);
	}
	
	@Override
	/**
	 * @param parent
	 */
	protected void addAllChilds(LayoutingElement parent) {
		for (LayoutingElement el : diagram.getElements().values()) {
			EPCElement element = (EPCElement)el;
			// LayoutingElement element = diagram.getElement(id);
			if (!EPCType.isAConnectingElement(element.getType())) {
				elementsToSort.put(element.getId(), new SortableLayoutingElement(
						element));
			}
		}
	}

}
