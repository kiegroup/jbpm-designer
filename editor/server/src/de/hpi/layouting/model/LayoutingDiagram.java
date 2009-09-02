package de.hpi.layouting.model;

import java.util.List;
import java.util.Map;

public interface LayoutingDiagram {

	public abstract Map<String, LayoutingElement> getElements();

	public abstract List<LayoutingElement> getChildElementsOf(LayoutingElement parent);

	public abstract List<LayoutingElement> getChildElementsOf(
			List<LayoutingElement> parents);

	public abstract List<LayoutingElement> getElementsOfType(String type);

	public abstract List<LayoutingElement> getElementsWithoutType(String type);
	
	/**
	 * Liefert das bereits bekannte Element oder legt ein neues mit der id an
	 * 
	 * @param id
	 *            die ID des Elements
	 * @return ein LayoutingElement mit der id
	 */
	public abstract LayoutingElement getElement(String id);

	public abstract List<LayoutingElement> getStartEvents();

	public abstract List<LayoutingElement> getConnectingElements();

	public abstract List<LayoutingElement> getGateways();

}
