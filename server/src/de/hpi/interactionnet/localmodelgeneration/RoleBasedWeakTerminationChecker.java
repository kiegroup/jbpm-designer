package de.hpi.interactionnet.localmodelgeneration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.verification.PTNetInterpreter;
import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.petrinet.Transition;

/**
 * @author Gero.Decker
 */
public class RoleBasedWeakTerminationChecker {
	
	/**
	 * Pre-condition: the net weakly terminates. 
	 * @param net
	 * @return true if there are no final markings in which a send interaction can fire
	 */
	public boolean check(InteractionNet net, String roleName) {
		Set<String> markings = new HashSet<String>();
		for (Marking m: net.getFinalMarkings()) {
			if (!doCheck(net, (PTNetInterpreter)net.getInterpreter(), m, roleName, markings))
				return false;
		}
		return net.getFinalMarkings().size() > 0;
	}

	private boolean doCheck(InteractionNet net, PTNetInterpreter interpreter, Marking marking, String roleName, Set<String> markings) {
		String markingStr = marking.toString();
//		System.out.println("Checking marking "+markingStr);
		
		// check if this marking was already processed		
		if (markings.contains(markingStr))
			return true;
		markings.add(markingStr);
		
		List<Transition> transitions = interpreter.getEnabledTransitions(net, marking);
		for (Transition t: transitions) {
			
			// check
			if (t instanceof InteractionTransition && roleName.equals(((InteractionTransition)t).getSender().getName()))
				return false;
			if (t instanceof ActionTransition)
				return false;
			
			Marking newmarking = interpreter.fireTransition(net, marking, t);
			if (!doCheck(net, interpreter, newmarking, roleName, markings))
				return false;
		}
		
		return true;
	}

}


