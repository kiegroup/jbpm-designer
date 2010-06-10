package de.hpi.PTnet.verification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.Place;
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
public class BoundednessChecker {
	
	protected PTNet net;
	protected PTNetInterpreter interpreter;
	protected Set<String> markings_strings;
	protected List<int[]> markings_stack;
	
	public BoundednessChecker(PTNetInterpreter interpreter, PTNet net) {
		this.net = net;
		this.interpreter = interpreter;
		this.markings_strings = new HashSet<String>();
		this.markings_stack = new ArrayList<int[]>();
	}
	
	public boolean checkBoundedness() {
		return doCheck(net.getInitialMarking());
	}
	
	protected boolean doCheck(Marking marking) {
		String markingStr = marking.toString();
//		System.out.println("Checking marking "+markingStr);
		
		// check if this marking was already processed
		if (markings_strings.contains(markingStr))
			return true;
		markings_strings.add(markingStr);
		
		int[] m_b = getMarking(marking);
		if (hasFoundInferiorMarking(m_b))
			return false;
		markings_stack.add(m_b);

		List<Transition> transitions = interpreter.getEnabledTransitions(net, marking);
		for (Transition t: transitions) {
			Marking newmarking = interpreter.fireTransition(net, marking, t);
			if (!doCheck(newmarking))
				return false;
		}
		markings_stack.remove(markings_stack.size()-1);
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
		for (Iterator<int[]> it=markings_stack.iterator(); it.hasNext(); ) {
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
