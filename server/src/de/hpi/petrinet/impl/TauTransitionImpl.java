package de.hpi.petrinet.impl;

import de.hpi.petrinet.Node;
import de.hpi.petrinet.TauTransition;

public class TauTransitionImpl extends NodeImpl implements TauTransition {

	public boolean isSimilarTo(Node node) {
		return (node instanceof TauTransition);
	}

}
