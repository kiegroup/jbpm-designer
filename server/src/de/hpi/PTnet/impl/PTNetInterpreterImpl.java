package de.hpi.PTnet.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetInterpreter;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class PTNetInterpreterImpl implements PTNetInterpreter {

	public Marking fireTransition(PetriNet net, Marking marking, Transition t) {
		Marking newmarking = (Marking)marking.getCopy();
		for (Iterator<FlowRelationship> it2=t.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
			Place p = (Place)it2.next().getSource();
			newmarking.removeToken(p);
		}
		for (Iterator<FlowRelationship> it2=t.getOutgoingFlowRelationships().iterator(); it2.hasNext(); ) {
			Place p = (Place)it2.next().getTarget();
			newmarking.addToken(p);
		}
		return newmarking;
	}

	public List<Transition> getEnabledTransitions(PetriNet net,
			Marking marking) {
		List<Transition> transitions = new ArrayList();
		for (Iterator it=net.getTransitions().iterator(); it.hasNext(); ) {
			Transition t = (Transition)it.next();
			boolean isEnabled = true;
			for (Iterator<FlowRelationship> it2=t.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
				Place p = (Place)it2.next().getSource();
				if (marking.getNumTokens(p) == 0) {
					isEnabled = false;
					break;
				}
			}
			if (isEnabled)
				transitions.add(t);
		}
		return transitions;
	}

	public boolean[] getEnablement(PetriNet net, Marking marking) {
		boolean[] enabled = new boolean[net.getTransitions().size()];
		int i=0;
		for (Iterator it=net.getTransitions().iterator(); it.hasNext(); i++) {
			Transition t = (Transition)it.next();
			enabled[i] = true;
			for (Iterator<FlowRelationship> it2=t.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
				Place p = (Place)it2.next().getSource();
				if (marking.getNumTokens(p) == 0) {
					enabled[i] = false;
					break;
				}
			}
		}
		return enabled;
	}

	public Set<Transition> getReachableTransitions(PetriNet net, de.hpi.petrinet.Marking marking) {
		return new StateSpaceCalculator(this, (PTNet)net, (Marking)marking).getReachableTransitions();
	}

	public de.hpi.petrinet.Marking fireTransition(PetriNet net,
			de.hpi.petrinet.Marking marking, Transition t) {
		return fireTransition(net, (de.hpi.PTnet.Marking)marking, t);
	}

	public List<de.hpi.petrinet.Transition> getEnabledTransitions(PetriNet net,
			de.hpi.petrinet.Marking marking) {
		return getEnabledTransitions(net, (de.hpi.PTnet.Marking)marking);
	}

	public boolean[] getEnablement(PetriNet net, de.hpi.petrinet.Marking marking) {
		return getEnablement(net, (de.hpi.PTnet.Marking)marking);
	}

}
