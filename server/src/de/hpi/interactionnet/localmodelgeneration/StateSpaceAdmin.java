package de.hpi.interactionnet.localmodelgeneration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNetInterpreter;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.petrinet.Transition;

/**
 * @author Gero.Decker
 */
public class StateSpaceAdmin {
	
	private InteractionNet net;
	private PTNetInterpreter interpreter;
//	private Set<String> markings;
//	private Map<String,List<String>> 
	
	public StateSpaceAdmin(InteractionNet net) {
		this.net = net;
		this.interpreter = (PTNetInterpreter)net.getInterpreter();
	}
	
	public Marking findPostMarking(Transition t) {
		// brute force...
		Set<String> markings = new HashSet<String>();
		return doFindMarking(net.getInitialMarking(), t, markings);
	}

	private Marking doFindMarking(Marking marking, Transition tr, Set<String> markings) {
		String markingStr = marking.toString();
		
		// check if this marking was already processed
		if (markings.contains(markingStr))
			return null;
		markings.add(markingStr);
		
		List<Transition> transitions = interpreter.getEnabledTransitions(net, marking);
		for (Transition t: transitions) {
			Marking newmarking = interpreter.fireTransition(net, marking, t);
			if (t == tr)
				return newmarking;
			Marking m = doFindMarking(newmarking, tr, markings);
			if (m != null)
				return m;
		}
		return null;
	}

//	public boolean isReachableMarking(Marking m) {
//		// brute force...
//		Set<String> markings = new HashSet<String>();
//		return isReachableMarking(net.getInitialMarking(), m.toString(), markings);
//	}
//
//	private boolean isReachableMarking(Marking marking, String m, Set<String> markings) {
//		String markingStr = marking.toString();
//		if (markingStr.equals(m))
//			return true;
//		
//		// check if this marking was already processed
//		if (markings.contains(markingStr))
//			return false;
//		markings.add(markingStr);
//		
//		List<Transition> transitions = interpreter.getEnabledTransitions(net, marking);
//		for (Transition t: transitions) {
//			Marking newmarking = interpreter.fireTransition(net, marking, t);
//			if (isReachableMarking(newmarking, m, markings))
//				return true;
//		}
//		return false;
//	}
//	
//	public void removePlace(Place p) {
//		
//	}
//	
//	public void refreshTransition(Transition t) {
//		
//	}

}


