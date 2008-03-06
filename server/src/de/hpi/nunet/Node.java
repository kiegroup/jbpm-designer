/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet;

import java.util.List;

public interface Node {

	List<FlowRelationship> getIncomingFlowRelationships();

	List<FlowRelationship> getOutgoingFlowRelationships();

	ProcessModel getProcessModel();

	void setProcessModel(ProcessModel value);

	String getLabel();

	void setLabel(String value);

} // Node