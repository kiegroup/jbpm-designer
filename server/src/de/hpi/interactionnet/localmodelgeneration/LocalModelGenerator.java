package de.hpi.interactionnet.localmodelgeneration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.interactionnet.impl.InteractionNetReducer;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.TauTransition;
import de.hpi.petrinet.Transition;

public class LocalModelGenerator extends InteractionNetReducer {
	
	public InteractionNet generateLocalModel(InteractionNet net, String roleName) {
		// deep copy the net for later reduction
		InteractionNet newnet = net.getCopy();
		removeTransitions(newnet, getInteractionsOfOthers(newnet, roleName));
		
		List<Transition> tautransitions = new ArrayList();
		do {
			tautransitions.clear();
			getTauTransitions(newnet, tautransitions);
			removeTransitions(newnet, tautransitions);
		} while (tautransitions.size() > 0);
		
		return newnet;
	}
	
	protected List<Transition> getInteractionsOfOthers(InteractionNet net, String roleName) {
		// retrieve all interactions where the role is not involved 
		List<Transition> removeList = new ArrayList();
		for (Iterator<Transition> iter=net.getTransitions().iterator(); iter.hasNext(); ) {
			Transition t = iter.next();
			
			if (t instanceof InteractionTransition) {
				InteractionTransition ti = (InteractionTransition)t;
				if (!ti.getSender().getName().equals(roleName) && !ti.getReceiver().getName().equals(roleName)) {
					removeList.add(ti);
//					return removeList;
				}
				
			} else if (t instanceof ActionTransition) {
				ActionTransition ta = (ActionTransition)t;
				if (!roleName.equals(ta.getRole().getName())) {
					removeList.add(ta);
//					return removeList;
				}

			}
		}
		return removeList;
	}
	
	/**
	 * Returns all tau transitions sharing an input place with another transition and having input places that are start places
	 * @param net
	 * @param tautransitions
	 */
	protected void getTauTransitions(InteractionNet net,List<Transition> tautransitions) {
		for (Iterator<Transition> it=net.getTransitions().iterator(); it.hasNext(); ) {
			Transition t = it.next();
			if (t instanceof TauTransition) {
				for (Iterator<FlowRelationship> it2=t.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
					Place p = (Place)it2.next().getSource();
					if (p.getOutgoingFlowRelationships().size() > 1) {
						boolean isOutputPlace = false;
						for (Iterator<FlowRelationship> it3=t.getOutgoingFlowRelationships().iterator(); it3.hasNext(); ) {
							if (it3.next().getTarget() == p) {
								isOutputPlace = true;
								break;
							}
						}
						if (!isOutputPlace) {
							tautransitions.add(t);
//							return; //TODO remove
							break;
						}
					}
					if (p.getIncomingFlowRelationships().size() == 0) {
						tautransitions.add(t);
						break;
					}
				}
			}
		}
	}

}
