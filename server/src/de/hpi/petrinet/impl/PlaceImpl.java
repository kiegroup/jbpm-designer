package de.hpi.petrinet.impl;

import de.hpi.petrinet.Node;
import de.hpi.petrinet.Place;

public class PlaceImpl extends NodeImpl implements Place {

	public boolean isSimilarTo(Node node) {
		return (node instanceof Place);
	}
	
}
