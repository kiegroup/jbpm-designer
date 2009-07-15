package de.unihannover.se.infocup2008.bpmn.model;

import java.util.List;
import java.util.Map;

public interface BPMNDiagram {

	public abstract Map<String, BPMNElement> getElements();

	public abstract List<BPMNElement> getChildElementsOf(BPMNElement parent);

	public abstract List<BPMNElement> getChildElementsOf(
			List<BPMNElement> parents);

	public abstract List<BPMNElement> getElementsOfType(String type);

	public abstract List<BPMNElement> getElementsWithoutType(String type);

	/**
	 * Liefert das bereits bekannte Element oder legt ein neues mit der id an
	 * 
	 * @param id
	 *            die ID des Elements
	 * @return ein BPMNElement mit der id
	 */
	public abstract BPMNElement getElement(String id);

	public abstract List<BPMNElement> getStartEvents();

	public abstract List<BPMNElement> getConnectingElements();

	public abstract List<BPMNElement> getGateways();

}