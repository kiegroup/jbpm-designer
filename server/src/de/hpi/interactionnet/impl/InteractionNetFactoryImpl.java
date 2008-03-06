package de.hpi.interactionnet.impl;

import de.hpi.PTnet.impl.PTNetFactoryImpl;
import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionNetFactory;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.interactionnet.Role;

public class InteractionNetFactoryImpl extends PTNetFactoryImpl implements InteractionNetFactory {

	public InteractionNet createInteractionNet() {
		return new InteractionNetImpl();
	}

	public Role createRole() {
		return new RoleImpl();
	}

	public InteractionTransition createLabeledTransition() {
		return new InteractionTransitionImpl();
	}

	public static InteractionNetFactory init() {
		return new InteractionNetFactoryImpl();
	}

	public ActionTransition createActionTransition() {
		return new ActionTransitionImpl();
	}

	public InteractionTransition createInteractionTransition() {
		return new InteractionTransitionImpl();
	}

}
