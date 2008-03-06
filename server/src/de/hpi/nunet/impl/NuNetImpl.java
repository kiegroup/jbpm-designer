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
import de.hpi.nunet.Marking;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.NuNetFactory;
import de.hpi.nunet.Place;
import de.hpi.nunet.Transition;

public class NuNetImpl implements NuNet {
	
	private List<Place> places;
	private List<Transition> transitions;
	private List<FlowRelationship> flowRelationships;
	private Marking initialMarking;
	
	public List getPlaces() {
		if (places == null)
			places = new ArrayList();
		return places;
	}

	public List<Transition> getTransitions() {
		if (transitions == null)
			transitions = new ArrayList();
		return transitions;
	}

	public List<FlowRelationship> getFlowRelationships() {
		if (flowRelationships == null)
			flowRelationships = new ArrayList();
		return flowRelationships;
	}

	public Marking getInitialMarking() {
		if (initialMarking == null)
			initialMarking = NuNetFactory.eINSTANCE.createMarking(this);
		return initialMarking;
	}
	
} // NuNet