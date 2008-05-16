package de.hpi.petrinet.impl;

import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.Node;

public class FlowRelationshipImpl implements FlowRelationship {
	
	protected Node source;
	protected Node target;
	protected int mode = FlowRelationship.RELATION_MODE_TAKETOKEN;

	public Node getSource() {
		return source;
	}

	public void setSource(Node value) {
		if (source != null)
			source.getOutgoingFlowRelationships().remove(this);
		source = value;
		if (source != null)
			source.getOutgoingFlowRelationships().add(this);
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node value) {
		if (target != null)
			target.getIncomingFlowRelationships().remove(this);
		target = value;
		if (target != null)
			target.getIncomingFlowRelationships().add(this);
	}
	
	
	public String toString() {
		return "("+source+","+target+")";
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

}
