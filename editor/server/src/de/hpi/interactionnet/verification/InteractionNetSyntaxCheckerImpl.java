package de.hpi.interactionnet.verification;

import java.util.Iterator;

import de.hpi.diagram.verification.SyntaxChecker;
import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.petrinet.SilentTransition;
import de.hpi.petrinet.Transition;
import de.hpi.petrinet.verification.PetriNetSyntaxChecker;

public class InteractionNetSyntaxCheckerImpl extends PetriNetSyntaxChecker
		implements SyntaxChecker {

	private static final String SENDER_NOT_SET = "InteractionNet_SENDER_NOT_SET";
	private static final String RECEIVER_NOT_SET = "InteractionNet_RECEIVER_NOT_SET";
	private static final String MESSAGETYPE_NOT_SET = "InteractionNet_MESSAGETYPE_NOT_SET";
	private static final String ROLE_NOT_SET = "InteractionNet_ROLE_NOT_SET";

	public InteractionNetSyntaxCheckerImpl(InteractionNet net) {
		super(net);
	}

	@Override
	public boolean checkSyntax() {
		if (!super.checkSyntax())
			return false;
//		if (!checkRolesAndMessageTypes())
//			return false;
		checkRolesAndMessageTypes();
		return (errors.size() == 0);
	}

	private boolean checkRolesAndMessageTypes() {
		for (Iterator<Transition> it=net.getTransitions().iterator(); it.hasNext(); ) {
			Transition t = it.next();
			
			if (t instanceof InteractionTransition) {
				InteractionTransition ti = (InteractionTransition)t;
				if (ti.getSender() == null) {
					addNodeError(ti, SENDER_NOT_SET);
//					return false;
					continue;
				}
				if (ti.getReceiver() == null) {
					addNodeError(ti, RECEIVER_NOT_SET);
//					return false;
					continue;
				}
				if (ti.getMessageType() == null) {
					addNodeError(ti, MESSAGETYPE_NOT_SET);
//					return false;
				}
				
			} else if (t instanceof ActionTransition) {
				ActionTransition ta = (ActionTransition)t;
				if (ta.getRoles().size() == 0) {
					addNodeError(t, ROLE_NOT_SET);
				}
				
			} else if (t instanceof SilentTransition) {
				
			} else {
				
			}
		}
		return true;
	}

}
