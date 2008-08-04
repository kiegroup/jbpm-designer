package de.hpi.PTnet.verification;

import java.util.HashSet;
import java.util.Iterator;
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
