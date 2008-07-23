/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet;

import java.util.ArrayList;
import java.util.List;

import de.hpi.petrinet.NodeImpl;


public class Node extends NodeImpl {

	private String id;
	private List<FlowRelationship> incomingFlowRelationships;
	private List<FlowRelationship> outgoingFlowRelationships;
	private ProcessModel processModel;
	private String label;

	public String getId() {
		return id;
	}

	public void setId(String label) {
		this.id = label;
	}

	public List<FlowRelationship> getIncomingFlowRelationships() {
		if (incomingFlowRelationships == null)
			incomingFlowRelationships = new ArrayList();
		return incomingFlowRelationships;
	}

	public List<FlowRelationship> getOutgoingFlowRelationships() {
		if (outgoingFlowRelationships == null)
			outgoingFlowRelationships = new ArrayList();
		return outgoingFlowRelationships;
	}

	public ProcessModel getProcessModel() {
		return processModel;
	}

	public void setProcessModel(ProcessModel value) {
		processModel = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String value) {
		label = value;
	}
	
	public String toString() {
		return getLabel();
	}

} // Node