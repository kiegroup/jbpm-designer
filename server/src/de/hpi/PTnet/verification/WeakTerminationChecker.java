package de.hpi.PTnet.verification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.FlowRelationship;
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
 * 
 * TODO reuse more sophisticated technique to decide on unboundedness
 */
public class WeakTerminationChecker {
	
	protected PTNet net;
	protected PTNetInterpreter interpreter;
	protected Set<String> goodMarkings;
	protected Set<String> badMarkings;
	protected Set<String> visitedMarkings;
	protected Set<String> finalMarkings;
	protected List<Transition> conflictTransitions;
	protected List<Transition> deadlockingTransitions;
	protected Transition unsafeTransition;
	protected int stateCount;
	protected int unsafeTransitionSearchDepth;
	protected List<int[]> markings_stack;
	
	
	public WeakTerminationChecker(PTNet net, List<Marking> finalMarkings) {
		this.net = net;
		this.interpreter = (PTNetInterpreter)net.getInterpreter();
		this.goodMarkings = new HashSet<String>();
		this.badMarkings = new HashSet<String>();
		this.visitedMarkings = new HashSet<String>();
		this.finalMarkings = new HashSet<String>();
		for (Marking m: finalMarkings)
			this.finalMarkings.add(m.toString());
		
		this.conflictTransitions = new ArrayList<Transition>();
		this.deadlockingTransitions = new ArrayList<Transition>();
//		this.unsafeTransitions = new ArrayList<Transition>();
		this.markings_stack = new ArrayList<int[]>();
	}
	
	/**
	 * precondition: the net is bounded
	 * @param conflictingTransitions
	 * @return
	 * @throws UnboundedNetException 
	 */
	public boolean check() throws MaxStatesExceededException, UnboundedNetException {
		conflictTransitions.clear();
		deadlockingTransitions.clear();
//		unsafeTransitions.clear();
		unsafeTransition = null;
		unsafeTransitionSearchDepth = -1;
		
		stateCount = 0;
		
		return doCheck(net.getInitialMarking(), false, 0);
	}
	
	protected boolean doCheck(Marking marking, boolean returnFalseIfVisited, int searchDepth) throws MaxStatesExceededException, UnboundedNetException {
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
		
		stateCount++;
		if (stateCount > MaxStatesExceededException.MAX_NUM_STATES)
			throw new MaxStatesExceededException();
		
		boolean leadsToGoodMarking = leadsToGoodMarking(marking);
		List<Transition> transitions = interpreter.getEnabledTransitions(net, marking);
		List<Transition> badTransitions = new ArrayList<Transition>();
		
		int[] m_b = getMarking(marking);
		if (hasSeveralTokenOnOnePlace(m_b) && hasFoundInferiorMarking(m_b)){
			throw new UnboundedNetException();
		}
		
		
		if (transitions.size() > 0) {
			
			markings_stack.add(m_b);
			
			for (Transition t: transitions) {
				Marking newmarking = interpreter.fireTransition(net, marking, t);
				
				// unsafe net?
				Place unsafePlace = newmarking.findUnsafePlace();
				if (unsafePlace != null) {
					if (unsafeTransitionSearchDepth == -1 || searchDepth < unsafeTransitionSearchDepth) {
						unsafeTransition = t;
						unsafeTransitionSearchDepth = searchDepth;
					}
				}
				
				boolean cresult;
				try {
					cresult= doCheck(newmarking, alreadyVisited, searchDepth+1);
				} catch (UnboundedNetException e) {
					e.setCause(t);
					throw e;
				}
				leadsToGoodMarking |= cresult;
				if (!cresult) {
					badTransitions.add(t);
				}
			}
	
			markings_stack.remove(markings_stack.size() - 1);
			
		} else {
			// is deadlock?
			if (!leadsToGoodMarking) {
				addDeadlockingTransitions(marking);
			}
		}
		
		if (leadsToGoodMarking) {
			visitedMarkings.remove(markingStr);
			goodMarkings.add(markingStr);
			for (Transition t: badTransitions)
				if (!conflictTransitions.contains(t))
					conflictTransitions.add(t);
		}
		
		return leadsToGoodMarking;
	}

	private boolean hasSeveralTokenOnOnePlace(int[] mB) {
		for (int i : mB) {
			if (i > 1) {
				return true;
			}
		}
		return false;
	}

	protected boolean leadsToGoodMarking(Marking marking) {
		return finalMarkings.contains(marking.toString());
	}

	protected void addDeadlockingTransitions(Marking marking) {
		for (Place p: marking.getMarkedPlaces()) {
			for (FlowRelationship rel: p.getOutgoingFlowRelationships()) {
				Transition t = (Transition)rel.getTarget();
				if (!deadlockingTransitions.contains(t))
					deadlockingTransitions.add(t);
			}
		}
	}

	/**
	 * @return if there exists a path to a final marking then the list of conflict transitions are those transitions
	 * that lead to not reaching a final marking any longer
	 */
	public List<Transition> getConflictTransitions() {
		return conflictTransitions;
	}

	/**
	 * @return if there is a deadlock then this list contains (some of) those transitions are returned that are partly enabled 
	 * (at least one of the input places is marked)
	 */
	public List<Transition> getDeadlockingTransitions() {
		return deadlockingTransitions;
	}

	/**
	 * @return if there is an unsafe marking (at least two tokens on a place) then a transition is returned that produced such a marking 
	 * and is preceeded by the shortest firing sequence   
	 */
	public Transition getUnsafeTransition() {
		return unsafeTransition;
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

	public static class UnboundedNetException extends Exception {

		private static final long serialVersionUID = -8014065307835455L;
		
		String causeId;

		public String getCauseId() {
			return causeId!=null ? causeId : "";
		}

		public void setCause(Transition t) {
			this.causeId = t.getId();
		}	
		
	}
}


