package de.hpi.nunet.simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.nunet.Marking;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.EnabledTransition;

/**
 * In order to avoid stack overflow in the case of an infinite number of tuples to check (which
 * occurs as soon as one of the nets has an infinite number of states) the algorithm will abort
 * after MAX_NUM_TUPLES
 *  
 * @author gero.decker
 *
 */
public class SigmaBisimulationChecker {
	
	private Interpreter interpreter;
	private List<EnabledTransition> transitions;
	private Set markings;
	
	private Marking lastM1;
	private Marking lastM2;
	private EnabledTransition lasttmode;
	
	public static final int MAX_NUM_TUPLES = 1000;

	public SigmaBisimulationChecker() {
		this.interpreter = new Interpreter();
		this.transitions = new ArrayList();
		this.markings = new HashSet();
	}
	
	/**
	 * This method is not thread-safe!
	 * 
	 * @param net1
	 * @param net2
	 * @return
	 */
	public boolean checkSigmaBisimilarity(NuNet net1, NuNet net2) {
		transitions.clear();
		markings.clear();
		
		Marking m1 = net1.getInitialMarking();
		Marking m2 = net2.getInitialMarking();
		
		return doCheck(net1, m1, net2, m2);
	}
	
	public int getBisimulationRelationSize() {
		return markings.size();
	}
	
	public Marking[] getLastMarkingsChecked() {
		return new Marking[]{lastM1, lastM2};
	}
	
	public EnabledTransition getLastTransitionModeChecked() {
		return lasttmode;
	}
	

	private boolean doCheck(NuNet net1, Marking m1, NuNet net2, Marking m2) {
		
		if (markings.size() > MAX_NUM_TUPLES)
			return false;
		
		String m1str = m1.toString();
		String m2str = m2.toString();
		
		// have we checked these two markings already?
		if (markings.contains(m1str+"+"+m2str))
			return true;
		markings.add(m1str+"+"+m2str);

		List<EnabledTransition> tmodes1 = interpreter.getEnabledTransitions(net1, m1);
		List<EnabledTransition> tmodes2 = interpreter.getEnabledTransitions(net2, m2);
		
		// ---- > direction 1 => 2
		if (!doCheck(net1, m1, tmodes1, net2, m2, tmodes2))
			return false;
		
		// ---- > direction 2 => 1
		if (!doCheck(net2, m2, tmodes2, net1, m1, tmodes1))
			return false;
		
		return true;
	}
	
	private boolean doCheck(NuNet net1, Marking m1, List<EnabledTransition> tmodes1, NuNet net2, Marking m2, List<EnabledTransition> tmodes2) {
		lastM1 = m1;
		lastM2 = m2;
		
		for (Iterator<EnabledTransition> it1=tmodes1.iterator(); it1.hasNext(); ) {
			EnabledTransition tmode1 = it1.next();
			
			lasttmode = tmode1;
			
			// FIRE! (1)
			String newName = null;
			if (tmode1.createsFreshName()) {
				newName = createFreshName(m1, m2);
				tmode1.mode.put(NuNet.NEW, newName);
			}
			Marking newm1 = m1.getCopy();
			interpreter.fireTransition(net1, newm1, tmode1);
			tmode1.mode.remove(NuNet.NEW);
			
			// now try to find a matching transition for net2 / m2
			boolean found = false;
			for (Iterator<EnabledTransition> it2=tmodes2.iterator(); it2.hasNext(); ) {
				EnabledTransition tmode2 = it2.next();
				
				// see if it the transitions / modes match
				if (!tmode1.matches(tmode2))
					continue;
				
				// FIRE! (2)
				if (tmode2.createsFreshName()) {
					if (newName == null)
						newName = createFreshName(m1, m2);
					tmode2.mode.put(NuNet.NEW, newName);
				}
				Marking newm2 = m2.getCopy();
				interpreter.fireTransition(net2, newm2, tmode2);
				tmode2.mode.remove(NuNet.NEW);

				// check if the two resulting marked nets are bisimilar
				if (doCheck(net1, newm1, net2, newm2)) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

	private String createFreshName(Marking m1, Marking m2) {
        int freshNameCounter = 1;
		while (m1.containsName("new#"+freshNameCounter) || m2.containsName("new#"+freshNameCounter)) {
			freshNameCounter++;
		}
		return "new#"+freshNameCounter;
	}

}
