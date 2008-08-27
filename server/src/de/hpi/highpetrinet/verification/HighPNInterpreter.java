package de.hpi.highpetrinet.verification;

import java.util.ArrayList;
import java.util.List;

import de.hpi.PTnet.Marking;
import de.hpi.highpetrinet.HighFlowRelationship;
import de.hpi.highpetrinet.HighPetriNet;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Transition;

public class HighPNInterpreter extends de.hpi.PTnet.verification.PTNetInterpreter {
	@Override
	public Marking fireTransition(PetriNet _net, Marking marking, Transition t){
		HighPetriNet net = (HighPetriNet)_net;
		Marking newmarking = (Marking) marking.getCopy();
		for (HighFlowRelationship rel : (List<HighFlowRelationship>)t.getIncomingFlowRelationships()) {
			de.hpi.petrinet.Place p = (de.hpi.petrinet.Place) rel.getSource();

			/*
			 * TODO: later on, different arc types should get its own classes,
			 * implementing custom fire behavior
			 */
			switch (rel.getType()) {
			case Plain:
				newmarking.removeToken(p);
				break;
			case Reset:
				newmarking.setNumTokens(p, 0);
				break;
			case Inhibitor:
				// do nothing
				break;
			case Read:
				// do nothing
				break;
			}
		}
		for (HighFlowRelationship rel : (List<HighFlowRelationship>)t.getOutgoingFlowRelationships()) {
			de.hpi.petrinet.Place p = (de.hpi.petrinet.Place) rel.getTarget();
			newmarking.addToken(p);
		}
		return newmarking;
	}

	@Override
	public List<Transition> getEnabledTransitions(PetriNet _net, Marking marking) {
		HighPetriNet net = (HighPetriNet)_net;
		List<Transition> transitions = new ArrayList<Transition>();
		for (Transition t : (List<Transition>)net.getTransitions()) {
			boolean isEnabled = true;
			for (HighFlowRelationship rel : (List<HighFlowRelationship>)t.getIncomingFlowRelationships()) {
				de.hpi.petrinet.Place p = (de.hpi.petrinet.Place) rel.getSource();
				switch (rel.getType()) {
				case Plain:
					isEnabled = (marking.getNumTokens(p) != 0);
					break;
				case Reset:
					// do nothing
					break;
				case Inhibitor:
					isEnabled = (marking.getNumTokens(p) == 0);
					break;
				case Read:
					isEnabled = (marking.getNumTokens(p) != 0);
					break;
				}
				if(!isEnabled)
					break;
			}
			if (isEnabled)
				transitions.add(t);
		}
		return transitions;
	}
}
