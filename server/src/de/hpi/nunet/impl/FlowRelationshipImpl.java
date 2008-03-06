/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet.impl;

import java.util.ArrayList;
import java.util.List;

import de.hpi.nunet.FlowRelationship;
import de.hpi.nunet.Node;


public class FlowRelationshipImpl implements FlowRelationship {
	
	Node source;
	Node target;
	List variables;
	
	public Node getSource() {
		return this.source;
	}

	public void setSource(Node value) {
		if (source != null)
			source.getOutgoingFlowRelationships().remove(this);
		source = value;
		if (source != null)
			source.getOutgoingFlowRelationships().add(this);
	}

	public Node getTarget() {
		return this.target;
	}

	public void setTarget(Node value) {
		if (target != null)
			target.getIncomingFlowRelationships().remove(this);
		target = value;
		if (target != null)
			target.getIncomingFlowRelationships().add(this);
	}

	public List getVariables() {
		if (variables == null)
			variables = new ArrayList();
		return variables;
	}
	
	public String toString() {
		return "(("+source+", "+target+"), "+getVariables()+")";
	}

} // FlowRelationship