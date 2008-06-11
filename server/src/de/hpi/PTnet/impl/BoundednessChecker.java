package de.hpi.PTnet.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetInterpreter;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class BoundednessChecker {
	
	protected PTNet net;
	protected PTNetInterpreter interpreter;
	protected Set<String> markings;
	protected List<int[]> markings_b;
	
	public BoundednessChecker(PTNetInterpreter interpreter, PTNet net) {
		this.net = net;
		this.interpreter = interpreter;
		this.markings = new HashSet<String>();
		this.markings_b = new ArrayList<int[]>();
	}
	
	public boolean checkBoundedness() {
		return doCheck(net.getInitialMarking());
	}
	
	protected boolean doCheck(Marking marking) {
		String markingStr = marking.toString();
//		System.out.println("Checking marking "+markingStr);
		
		// check if this marking was already processed
		if (markings.contains(markingStr))
			return true;
		markings.add(markingStr);
		
		int[] m_b = getMarking(marking);
		if (hasFoundInferiorMarking(m_b))
			return false;
		markings_b.add(m_b);

		List<Transition> transitions = interpreter.getEnabledTransitions(net, marking);
		for (Transition t: transitions) {
			Marking newmarking = interpreter.fireTransition(net, marking, t);
			if (!doCheck(newmarking))
				return false;
		}
		return true;
	}

	protected int[] getMarking(Marking marking) {
		int[] mb = new int[net.getPlaces().size()];
		int i=0;
		for (Iterator<Place> it=net.getPlaces().iterator(); it.hasNext(); i++) {
			Place p = it.next();
			mb[i] = marking.getNumTokens(p);
		}
		return mb;
	}

	protected boolean hasFoundInferiorMarking(int[] mb) {
		for (Iterator<int[]> it=markings_b.iterator(); it.hasNext(); ) {
			int[] mb2 = it.next();
			boolean found = true;
			for (int i=0; i<mb.length; i++) {
				if (mb2[i] > mb[i]) {
					found = false;
					break;
				}
			}
			if (found)
				return true;
		}
		return false;
	}
	

}
