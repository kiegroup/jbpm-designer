/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet;

import java.util.List;

public interface NuNet {
	
	public static String NEW = "new";
	
	List<Place> getPlaces();

	List<Transition> getTransitions();

	List<FlowRelationship> getFlowRelationships();

	Marking getInitialMarking();

} // NuNet