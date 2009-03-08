package de.hpi.petrinet.verification;

import java.util.Iterator;
import java.util.List;

import de.hpi.petrinet.Marking;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class PetriNetRGCalculator {
	PetriNet net;
	PetriNetInterpreter interpreter;
	PetriNetReachabilityGraph rg;
	
	public PetriNetRGCalculator(PetriNet net, PetriNetInterpreter interpreter){
		this.net = net;
		this.interpreter = interpreter;
	}
	
	public PetriNetReachabilityGraph calculate(){
		rg = new PetriNetReachabilityGraph(net);
		
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

	//TODO prevent livelocks!!!!
	protected void doCalculation(Marking marking) {
		List<Transition> transitions = interpreter.getEnabledTransitions(net, marking);
		for (Iterator<Transition> it=transitions.iterator(); it.hasNext(); ) {
			Transition t = it.next();
			Marking newmarking = interpreter.fireTransition(net, marking, t);

			if(!rg.contains(newmarking)){ // check if this marking was already processed
				rg.addMarking(newmarking);
				doCalculation(newmarking);
			}
			
			rg.addTransition(marking, newmarking, t);				
		}
	}
}
