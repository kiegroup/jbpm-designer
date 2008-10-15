package de.hpi.nunet.simulation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.nunet.Marking;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.EnabledTransition;

/**
 * 
 * @author gero.decker
 *
 * TODO handle creation of new names
 */
public class StateSpaceCalculator {
	
	private Interpreter interpreter;
	private NuNet net;
	private Set markings;
	
	public static final int MAX_NUM_STATES = 1000;
	
	public StateSpaceCalculator(NuNet net) {
		this.interpreter = new Interpreter();
		this.net = net;
		this.markings = new HashSet();
	}

	/**
	 * This method is not thread-safe!
	 * 
	 * @return the number of reachable markings (including the current), 
	 * will return -1 if there are more than MAX_NUM_STATES markings reachable
	 */
	public int getStateSpace() {
		markings.clear();
		
		Marking marking = net.getInitialMarking();
		calculcateStateSpace(marking);
		
		if (markings.size() <= MAX_NUM_STATES)
			return markings.size();
		else
			return -1;
	}
	
	private void calculcateStateSpace(Marking marking) {
		
		if (markings.size() > MAX_NUM_STATES)
			return;
		
		String markingStr = marking.toString();
//		System.out.println("Checking marking "+markingStr);
		
		// check if this marking was already processed
		if (markings.contains(markingStr))
			return;
		markings.add(markingStr);

		List<EnabledTransition> tmodes = interpreter.getEnabledTransitions(net, marking);
		for (Iterator<EnabledTransition> it=tmodes.iterator(); it.hasNext(); ) {
			EnabledTransition tmode = it.next();
			Marking newmarking = marking.getCopy();
			interpreter.fireTransition(net, newmarking, tmode);
			calculcateStateSpace(newmarking);
		}
	}

}
