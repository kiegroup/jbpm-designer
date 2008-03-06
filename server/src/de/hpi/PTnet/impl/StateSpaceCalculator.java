package de.hpi.PTnet.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetInterpreter;
import de.hpi.petrinet.Transition;


public class StateSpaceCalculator {
	
	protected PTNet net;
	protected PTNetInterpreter interpreter;
	protected Marking marking;
	protected Set<String> markings;
	protected Set<Transition> reachableTransitions;
	
	public static final int MAX_NUM_STATES = 1000;
	
	public StateSpaceCalculator(PTNetInterpreter interpreter, PTNet net, Marking marking) {
		this.net = net;
		this.interpreter = interpreter;
		this.marking = marking;
	}
	
	public int getNumStates() {
		if (markings == null)
			calculateStateSpace();
		return markings.size();
	}
	
	public Set<Transition> getReachableTransitions() {
		if (reachableTransitions == null) {
			calculateStateSpace();
			markings = null;
		}
		return reachableTransitions;
	}

	protected void calculateStateSpace() {
		markings = new HashSet();
		reachableTransitions = new HashSet();
		
		doCalculation(marking);
	}

	protected void doCalculation(Marking marking) {
		if (markings.size() > MAX_NUM_STATES)
			return;
		
		String markingStr = marking.toString();
//		System.out.println("Checking marking "+markingStr);
		
		// check if this marking was already processed
		if (markings.contains(markingStr))
			return;
		markings.add(markingStr);

		List<Transition> transitions = interpreter.getEnabledTransitions(net, marking);
		for (Iterator<Transition> it=transitions.iterator(); it.hasNext(); ) {
			Transition t = it.next();
			reachableTransitions.add(t);
			Marking newmarking = interpreter.fireTransition(net, marking, t);
			doCalculation(newmarking);
		}
	}

}
