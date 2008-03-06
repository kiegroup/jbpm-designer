package de.hpi.petrinet.impl;

import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;

public class LabeledTransitionImpl extends NodeImpl implements
		LabeledTransition {
	
	protected String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isSimilarTo(Node node) {
		if (node instanceof LabeledTransition && getLabel() != null) {
			return (getLabel().equals(((LabeledTransition)node).getLabel()));
		}
		return false;
	}
	
	public String toString() {
		return getId()+"("+getLabel()+")";
	}

}
