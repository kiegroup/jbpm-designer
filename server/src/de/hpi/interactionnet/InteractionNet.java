package de.hpi.interactionnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.interactionnet.verification.InteractionNetSyntaxCheckerImpl;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;
import de.hpi.petrinet.verification.SyntaxChecker;

public class InteractionNet extends PTNet {
	
	private List<Role> roles;
	private List<Marking> finalMarkings;

	public List<Role> getRoles() {
		if (roles == null)
			roles = new ArrayList();
		return roles;
	}

	public List<Marking> getFinalMarkings() {
		if (finalMarkings == null)
			finalMarkings = new ArrayList<Marking>();
		return finalMarkings;
	}

	@Override
	public SyntaxChecker getSyntaxChecker() {
		return new InteractionNetSyntaxCheckerImpl(this);
	}

	@Override
	public InteractionNetFactory getFactory() {
		return InteractionNetFactory.eINSTANCE;
	}

	@Override
	public InteractionNet getCopy() {
		InteractionNetFactory factory = getFactory();
		InteractionNet newnet = factory.createInteractionNet();
		Map map = new HashMap();
		
		for (Role r1: getRoles()) {
			Role r2 = factory.createRole();
			newnet.getRoles().add(r2);
			map.put(r1, r2);
			
			r2.setName(r1.getName());
		}
		
		for (Place p1: getPlaces()) {
			Place p2 = factory.createPlace();
			newnet.getPlaces().add(p2);
			map.put(p1, p2);
			
			p2.setId(p1.getId());
			newnet.getInitialMarking().setNumTokens(p2, getInitialMarking().getNumTokens(p1));
		}
		
		for (Marking m: getFinalMarkings()) {
			Marking newm = factory.createMarking(newnet);
			newnet.getFinalMarkings().add(newm);
			for (Place p1: getPlaces())
				newm.setNumTokens((Place)map.get(p1), m.getNumTokens(p1));
		}
		
		for (Transition t1: getTransitions()) {
			Transition t2;
			
			if (t1 instanceof InteractionTransition) {
				InteractionTransition i1 = (InteractionTransition)t1;
				InteractionTransition i2 = factory.createInteractionTransition();
				i2.setLabel(i1.getLabel());
				i2.setSender((Role)map.get(i1.getSender()));
				i2.setReceiver((Role)map.get(i1.getReceiver()));
				i2.setMessageType(i1.getMessageType());
				t2 = i2;
				
			} else if (t1 instanceof ActionTransition) {
				ActionTransition i1 = (ActionTransition)t1;
				ActionTransition i2 = factory.createActionTransition();
				i2.setLabel(i1.getLabel());
				i2.setRole((Role)map.get(i1.getRole()));
				t2 = i2;
				
			} else {
				t2 = factory.createSilentTransition();
				
			}

			t2.setId(t1.getId());
			newnet.getTransitions().add(t2);
			map.put(t1, t2);
		}
		
		for (FlowRelationship f1: getFlowRelationships()) {
			FlowRelationship f2 = factory.createFlowRelationship();
			newnet.getFlowRelationships().add(f2);
			
			f2.setSource((Node)map.get(f1.getSource()));
			f2.setTarget((Node)map.get(f1.getTarget()));
		}
		
		return newnet;		
	}
	
}
