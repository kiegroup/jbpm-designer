package de.hpi.bpmn2pn.model;

import java.util.HashMap;
import java.util.Map;

import de.hpi.bpmn.Container;
import de.hpi.petrinet.Place;

public class ConversionContext {
	public ConversionConfiguration config = null;
	public Map<Object, Place> map = new HashMap<Object, Place>(); // key = edge or
	// event-based gateway
	// or attached event,
	// value = place
	public Map<Container, SubProcessPlaces> subprocessMap = new HashMap<Container, SubProcessPlaces>();
	public boolean ancestorHasExcpH = false;

	public SubProcessPlaces getSubprocessPlaces(Container container) {
		SubProcessPlaces pl = subprocessMap.get(container);
		if (pl == null) {
			pl = new SubProcessPlaces();
			subprocessMap.put(container, pl);
		}
		return pl;
	}
}