/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet;

import java.util.List;


public interface FlowRelationship {

	Node getSource();

	void setSource(Node value);

	Node getTarget();

	void setTarget(Node value);

	List<String> getVariables();

} // FlowRelationship