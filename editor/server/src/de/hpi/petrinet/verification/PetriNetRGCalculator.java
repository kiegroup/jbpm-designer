package de.hpi.petrinet.verification;

import java.util.Iterator;
import java.util.List;

import de.hpi.diagram.reachability.ReachabilityGraph;
import de.hpi.petrinet.Marking;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class PetriNetRGCalculator {
	PetriNet net;
	PetriNetInterpreter interpreter;
	ReachabilityGraph rg;
	
	public PetriNetRGCalculator(PetriNet net, PetriNetInterpreter interpreter){
		this.net = net;
		this.interpreter = interpreter;
	}
	
	public ReachabilityGraph<PetriNet, Transition, Marking> calculate(){
		rg = new ReachabilityGraph<PetriNet, Transition, Marking>(net);
		
		Marking initialMarking = calcInitialMarking();
		rg.addMarking(initialMarking);
		doCalculation(initialMarking);
		
		return rg;
	}
	
	public Marking calcInitialMarking(){
		Marking marking = net.getInitialMarking();
		for(Place place : net.getPlaces()){
			if(place.getIncomingFlowRelationships().size() == 0){
				marking.setNumTokens(place, 1);
			}
		}
		return marking;
	}

	protected void doCalculation(Marking marking) {
		//if (rg.getMarkings().size() > MAX_NUM_STATES)
		//	return;
		
		// check if this marking was already processed
		if (rg.contains(marking))
			return;

		List<Transition> transitions = interpreter.getEnabledTransitions(net, marking);
		for (Iterator<Transition> it=transitions.iterator(); it.hasNext(); ) {
			Transition t = it.next();
			Marking newmarking = interpreter.fireTransition(net, marking, t);
			rg.addMarking(newmarking);
			rg.addTransition(marking, newmarking);
			doCalculation(newmarking);
		}
	}
}
