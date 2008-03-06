package de.hpi.PTnet.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetInterpreter;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class SafenessChecker {
	
	protected PTNet net;
	protected PTNetInterpreter interpreter;
	protected Set<String> markings;
	
	public SafenessChecker(PTNetInterpreter interpreter, PTNet net) {
		this.net = net;
		this.interpreter = interpreter;
		this.markings = new HashSet<String>();
	}
	
	public boolean checkSafeness() {
		return doCheck(net.getInitialMarking());
	}
	
	protected boolean doCheck(Marking marking) {
		String markingStr = marking.toString();
//		System.out.println("Checking marking "+markingStr);
		
		// check if this marking was already processed
		if (markings.contains(markingStr))
			return true;
		markings.add(markingStr);
		
		for (Place p: net.getPlaces()) {
			if (marking.getNumTokens(p) > 1)
				return false;
		}
		
		List<Transition> transitions = interpreter.getEnabledTransitions(net, marking);
		for (Iterator<Transition> it=transitions.iterator(); it.hasNext(); ) {
			Transition t = it.next();
			Marking newmarking = interpreter.fireTransition(net, marking, t);
			if (!doCheck(newmarking))
				return false;
		}
		return true;
	}

}
