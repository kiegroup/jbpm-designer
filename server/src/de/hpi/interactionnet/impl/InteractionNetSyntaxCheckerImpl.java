package de.hpi.interactionnet.impl;

import java.util.Iterator;

import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.petrinet.SyntaxChecker;
import de.hpi.petrinet.TauTransition;
import de.hpi.petrinet.Transition;
import de.hpi.petrinet.impl.PetriNetSyntaxCheckerImpl;

public class InteractionNetSyntaxCheckerImpl extends PetriNetSyntaxCheckerImpl
		implements SyntaxChecker {

	private static final String SENDER_NOT_SET = "Sender not set";
	private static final String RECEIVER_NOT_SET = "Receiver not set";
	private static final String MESSAGETYPE_NOT_SET = "Message type not set";
	private static final String ROLE_NOT_SET = "Role not set";

	public InteractionNetSyntaxCheckerImpl(InteractionNet net) {
		super(net);
	}

	@Override
	public boolean checkSyntax() {
		if (!super.checkSyntax())
			return false;
		if (!checkRolesAndMessageTypes())
			return false;
		errorCode = null;
		return true;
	}

	private boolean checkRolesAndMessageTypes() {
		for (Iterator<Transition> it=net.getTransitions().iterator(); it.hasNext(); ) {
			Transition t = it.next();
			
			if (t instanceof InteractionTransition) {
				InteractionTransition ti = (InteractionTransition)t;
				if (ti.getSender() == null) {
					errorCode = SENDER_NOT_SET+" (transition "+t.getId()+")";
					return false;
				}
				if (ti.getReceiver() == null) {
					errorCode = RECEIVER_NOT_SET+" (transition "+t.getId()+")";
					return false;
				}
				if (ti.getMessageType() == null) {
					errorCode = MESSAGETYPE_NOT_SET+" (transition "+t.getId()+")";
					return false;
				}
				
			} else if (t instanceof ActionTransition) {
				ActionTransition ta = (ActionTransition)t;
				if (ta.getRole() == null) {
					errorCode = ROLE_NOT_SET+" (transition "+t.getId()+")";
				}
				
			} else if (t instanceof TauTransition) {
				
			} else {
				
			}
		}
		return true;
	}

}
