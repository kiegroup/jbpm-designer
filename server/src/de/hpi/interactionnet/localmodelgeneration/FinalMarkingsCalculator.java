package de.hpi.interactionnet.localmodelgeneration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.verification.PTNetInterpreter;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

/**
 * @author Gero.Decker
 */
public class FinalMarkingsCalculator {

	protected PTNet net;
	protected PTNetInterpreter interpreter;
	protected Set<String> markings;
	protected Set<String> finalMarkings;
	protected List<int[]> markings_b;
	
	public FinalMarkingsCalculator(PTNet net) {
		this.net = net;
		this.interpreter = (PTNetInterpreter)net.getInterpreter();
		this.markings = new HashSet<String>();
		this.finalMarkings = new HashSet<String>();
		this.markings_b = new ArrayList<int[]>();
	}
	
	/**
	 * 
	 * @return null if the net is unbounded, the list of final markings otherwise
	 */
	public List<Marking> getFinalMarkings() {
		List<Marking> mlist = new ArrayList<Marking>();
		if (doCheck(net.getInitialMarking(), mlist))
			return mlist;
		else
			return null;
	}
	
	protected boolean doCheck(Marking marking,List<Marking> mlist) {
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
		if (transitions.size() == 0) {
			if (!finalMarkings.contains(markingStr)) {
				finalMarkings.add(markingStr);
				mlist.add(marking);
			}
		} else
			for (Transition t: transitions) {
				Marking newmarking = interpreter.fireTransition(net, marking, t);
				if (!doCheck(newmarking, mlist))
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


