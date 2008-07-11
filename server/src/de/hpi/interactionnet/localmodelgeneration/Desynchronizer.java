package de.hpi.interactionnet.localmodelgeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetFactory;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionNetFactory;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.interactionnet.Role;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.TauTransition;
import de.hpi.petrinet.Transition;

/**
 * @author Gero.Decker
 */
public class Desynchronizer {
	
	public PTNet getDesynchronizedNet(InteractionNet net, List<Marking> newFinalMarkings) {
		InteractionNetFactory factory = net.getFactory();
		PTNet newnet = factory.createPetriNet();
		
		// create places for all interaction models
		Map<String,Place> channelMap = createChannelPlaces(newnet, net, factory);
		
		// add role projections for each role
		addRoleProjections(newnet, net, newFinalMarkings, channelMap, factory);
		
		return newnet;
	}

	private Map<String, Place> createChannelPlaces(PTNet newnet, InteractionNet net, PTNetFactory factory) {
		Map<String, Place> channelMap = new HashMap<String, Place>();
		
		for (Transition t: net.getTransitions()) {
			if (t instanceof InteractionTransition) {
				InteractionTransition i = (InteractionTransition)t;
				if (!channelMap.containsKey(i.toString())) {
					Place p = factory.createPlace();
					p.setId(i.toString());
					newnet.getPlaces().add(p);
					channelMap.put(i.toString(), p);
				}
			}
		}
		
		return channelMap;
	}

	private void addRoleProjections(PTNet newnet, InteractionNet net, List<Marking> finalMarkings, Map<String, Place> channelMap, InteractionNetFactory factory) {
		LocalModelGenerator generator = new LocalModelGenerator();
		for (Role r: net.getRoles()) {
			// generate local model
			InteractionNet rnet = generator.generateLocalModel(net, r.getName());
			
			Map<Node,Node> map = new HashMap<Node,Node>();
			
			// copy places
			for (Place p: rnet.getPlaces()) {
				Place newp = factory.createPlace();
				newnet.getPlaces().add(newp);
				newp.setId(r.getName()+"_"+p.getId());
				map.put(p, newp);
				newnet.getInitialMarking().setNumTokens(newp, rnet.getInitialMarking().getNumTokens(p));
			}
			// handle final markings
			if (finalMarkings.size() == 0)
				for (Marking m: rnet.getFinalMarkings()) {
					Marking newm = factory.createMarking(newnet);
					addMarking(newm, m, rnet, map);
					finalMarkings.add(newm);
				}
			else {
				List<Marking> oldFinalMarkings = new ArrayList<Marking>(finalMarkings);
				finalMarkings.clear();
				for (Marking m: rnet.getFinalMarkings()) {
					for (Marking m2: oldFinalMarkings) {
						Marking newm = (Marking)m2.getCopy();
						addMarking(newm, m, rnet, map);
						finalMarkings.add(newm);
					}
				}
			}

			// copy transitions
			for (Transition t: rnet.getTransitions()) {
				Transition newt;
				if (t instanceof TauTransition)
					newt = factory.createTauTransition();
				else {
					newt = factory.createLabeledTransition();
					((LabeledTransition)newt).setLabel(((LabeledTransition)t).getLabel());
				}
				newnet.getTransitions().add(newt);
				newt.setId(t.getId());
				map.put(t, newt);
				
				// add flow relationships to channel places
				Place ch = channelMap.get(t.toString());
				if (ch != null) {
					FlowRelationship newrel = factory.createFlowRelationship();
					if (((InteractionTransition)t).getSender().getName().equals(r.getName())) {
						newrel.setSource(newt);
						newrel.setTarget(ch);
					} else {
						newrel.setSource(ch);
						newrel.setTarget(newt);
					}
				}
			}
			
			// copy flow relationships
			for (FlowRelationship rel: rnet.getFlowRelationships()) {
				FlowRelationship newrel = factory.createFlowRelationship();
				newrel.setSource(map.get(rel.getSource()));
				newrel.setTarget(map.get(rel.getTarget()));
			}
		}
	}

	private void addMarking(Marking newm, Marking m, InteractionNet net, Map<Node,Node> map) {
		for (Place p: net.getPlaces())
			newm.setNumTokens((Place)map.get(p), m.getNumTokens(p));
	}

}


