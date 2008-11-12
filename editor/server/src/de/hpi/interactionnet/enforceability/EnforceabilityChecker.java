package de.hpi.interactionnet.enforceability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.verification.PTNetInterpreter;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionNetFactory;
import de.hpi.interactionnet.InteractionTransition;

public class EnforceabilityChecker {

	private InteractionNet net;
	private PTNetInterpreter interpreter;
	
	private Set<String> visited;
	private Set<String> visitedcancel;
	private boolean[] wasreached;
	
	private int numtransitions;
	private boolean[][] shareRole;

	public EnforceabilityChecker(InteractionNet net) {
		this.net = net;
		interpreter = InteractionNetFactory.eINSTANCE.createInterpreter();
		
		visited = new HashSet();
		visitedcancel = new HashSet();
		wasreached = new boolean[net.getTransitions().size()];

		numtransitions = net.getTransitions().size();
		setupShareRole();
	}
	
	public boolean checkEnforceability() {
		visited.clear();
		visitedcancel.clear();
		for (int i=0; i<wasreached.length; i++)
			wasreached[i] = false;
		
		
		if (!recursivelyCheck(net.getInitialMarking(), new boolean[numtransitions]))
			return false;
		
		for (int i=0; i<wasreached.length; i++)
			if (!wasreached[i])
				return false;
		return true;
	}

	private boolean recursivelyCheck(Marking marking, boolean[] blocked) {
		
		String markingstr = marking.toString();
		if (!visited.add(markingstr+" + "+blocked))
			return true;

		boolean completed = false;
		boolean hasEnabledTransitions = false;
		
		boolean[] enabled = getEnablement(marking); // enabled(M)
		for (int ui=0; ui<numtransitions; ui++) 
			if (enabled[ui]) {
				hasEnabledTransitions = true;
				if (!blocked[ui]) {
					
					Marking marking2 = interpreter.fireTransition(net, marking, net.getTransitions().get(ui));
					boolean[] enabled2 = getEnablement(marking2); // enabled(M')
					
					for (int vi=0; vi<numtransitions; vi++) {
						if (enabled[vi] && !enabled2[vi] && !shareRole[ui][vi])
							return false;
					}
					
					boolean[] blocked2 = new boolean[numtransitions];
					for (int vi=0; vi<numtransitions; vi++) {
						if (enabled2[vi] && !enabled[vi] && !shareRole[ui][vi])
							blocked2[vi] = true;
						else if (blocked[vi] && !shareRole[ui][vi])
							blocked2[vi] = true;
					}
					
					if (!recursivelyCheck(marking2, blocked2))
						return false;
					
					if (!visitedcancel.contains(marking2+" + "+blocked2)) {
						wasreached[ui] = true;
						completed = true;
					}
				}
		}
		
		if (!completed && hasEnabledTransitions) {
			visitedcancel.add(marking+" + "+blocked);
		}
		
		return true;
	}
	
	protected void setupShareRole() {
		shareRole = new boolean[numtransitions][numtransitions];
		int x1 = 0;
		for (Iterator iter=net.getTransitions().iterator(); iter.hasNext(); ) {
			InteractionTransition i1 = (InteractionTransition)iter.next();
			int x2 = 0;
			for (Iterator iter2=net.getTransitions().iterator(); iter2.hasNext(); ) {
				InteractionTransition i2 = (InteractionTransition)iter2.next();
				
				shareRole[x1][x2] = (i1.getSender().equals(i2.getSender()) || 
						i1.getSender().equals(i2.getReceiver()) ||
						i1.getReceiver().equals(i2.getSender()) ||
						i1.getReceiver().equals(i2.getReceiver()));
				x2++;
			}
			x1++;
		}
	}
	
	private Map<String,boolean[]> enablementMap = new HashMap();
	
	private boolean[] getEnablement(Marking marking) {
		boolean[] enablement = enablementMap.get(marking.toString());
		if (enablement != null)
			return enablement;
		
		enablement = interpreter.getEnablement(net, marking);
		enablementMap.put(marking.toString(), enablement);
		
		return enablement;
	}

}
