package de.hpi.PTnet.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetInterpreter;
import de.hpi.petrinet.Transition;

/**
 * @author Gero.Decker
 */
public class WeakTerminationChecker {
	
	protected PTNet net;
	protected PTNetInterpreter interpreter;
	protected Set<String> goodMarkings;
	protected Set<String> badMarkings;
	protected Set<String> visitedMarkings;
	protected Set<String> finalMarkings;
	
	public WeakTerminationChecker(PTNet net, List<Marking> finalMarkings) {
		this.net = net;
		this.interpreter = (PTNetInterpreter)net.getInterpreter();
		this.goodMarkings = new HashSet<String>();
		this.badMarkings = new HashSet<String>();
		this.visitedMarkings = new HashSet<String>();
		this.finalMarkings = new HashSet<String>();
		for (Marking m: finalMarkings)
			this.finalMarkings.add(m.toString());
	}
	
	/**
	 * precondition: the net is bounded
	 * @param conflictingTransitions
	 * @return
	 */
	public boolean check(List<Transition> conflictingTransitions) {
		if (conflictingTransitions == null)
			conflictingTransitions = new ArrayList<Transition>();
		else
			conflictingTransitions.clear();
		
		return doCheck(net.getInitialMarking(), conflictingTransitions, false);
	}

	protected boolean doCheck(Marking marking, List<Transition> conflictingTransitions, boolean returnFalseIfVisited) {
		String markingStr = marking.toString();
//		System.out.println("Checking marking "+markingStr);
		
		// check if this marking was already processed		
		if (goodMarkings.contains(markingStr))
			return true;
		if (badMarkings.contains(markingStr))
			return false;
		
		boolean alreadyVisited = visitedMarkings.contains(markingStr);
		if (alreadyVisited && returnFalseIfVisited)
			return false;
		if (!alreadyVisited)
			visitedMarkings.add(markingStr);
		
		boolean leadsToGoodMarking = finalMarkings.contains(markingStr);
		List<Transition> transitions = interpreter.getEnabledTransitions(net, marking);
		List<Transition> badTransitions = new ArrayList<Transition>();
		for (Transition t: transitions) {
			Marking newmarking = interpreter.fireTransition(net, marking, t);
			
			boolean cresult = doCheck(newmarking, conflictingTransitions, alreadyVisited);
			leadsToGoodMarking |= cresult;
			if (!cresult)
				badTransitions.add(t);
		}
		
		if (leadsToGoodMarking) {
			visitedMarkings.remove(markingStr);
			goodMarkings.add(markingStr);
			for (Transition t: badTransitions)
				if (!conflictingTransitions.contains(t))
					conflictingTransitions.add(t);
		}
		
		return leadsToGoodMarking;
	}

}


