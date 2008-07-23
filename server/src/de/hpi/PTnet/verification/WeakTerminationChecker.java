package de.hpi.PTnet.verification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.Transition;

/**
 * Copyright (c) 2008 Gero Decker
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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


