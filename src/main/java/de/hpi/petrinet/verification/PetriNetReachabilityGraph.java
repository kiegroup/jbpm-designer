package de.hpi.petrinet.verification;

import de.hpi.diagram.reachability.ReachabilityGraph;
import de.hpi.petrinet.Marking;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Transition;

public class PetriNetReachabilityGraph extends ReachabilityGraph<PetriNet, Transition, Marking> {

	public PetriNetReachabilityGraph(PetriNet diag) {
		super(diag);
	}
}
