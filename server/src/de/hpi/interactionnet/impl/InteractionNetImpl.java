package de.hpi.interactionnet.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hpi.PTnet.impl.PTNetImpl;
import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionNetFactory;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.interactionnet.Role;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.PTnet.Marking;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.SyntaxChecker;
import de.hpi.petrinet.Transition;

public class InteractionNetImpl extends PTNetImpl implements InteractionNet {
	
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
		
		for (Iterator<Role> it=getRoles().iterator(); it.hasNext(); ) {
			Role r1 = it.next();
			
			Role r2 = factory.createRole();
			newnet.getRoles().add(r2);
			map.put(r1, r2);
			
			r2.setName(r1.getName());
		}
		
		for (Iterator iter=getPlaces().iterator(); iter.hasNext(); ) {
			Place p1 = (Place)iter.next();
			
			Place p2 = factory.createPlace();
			newnet.getPlaces().add(p2);
			map.put(p1, p2);
			
			p2.setId(p1.getId());
			newnet.getInitialMarking().setNumTokens(p2, getInitialMarking().getNumTokens(p1));
		}
		
		for (Iterator<Transition> iter=getTransitions().iterator(); iter.hasNext(); ) {
			Transition t1 = iter.next();
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
				t2 = factory.createTauTransition();
				
			}

			t2.setId(t1.getId());
			newnet.getTransitions().add(t2);
			map.put(t1, t2);
		}
		
		for (Iterator iter=getFlowRelationships().iterator(); iter.hasNext(); ) {
			FlowRelationship f1 = (FlowRelationship)iter.next();
			
			FlowRelationship f2 = factory.createFlowRelationship();
			newnet.getFlowRelationships().add(f2);
			
			f2.setSource((Node)map.get(f1.getSource()));
			f2.setTarget((Node)map.get(f1.getTarget()));
		}
		
		return newnet;		
	}
	
}
