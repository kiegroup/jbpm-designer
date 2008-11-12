package de.hpi.interactionnet;

import de.hpi.PTnet.PTNetFactory;


public class InteractionNetFactory extends PTNetFactory {
	
	public static InteractionNetFactory eINSTANCE = new InteractionNetFactory();

	public InteractionNet createInteractionNet() {
		return new InteractionNet();
	}

	public Role createRole() {
		return new Role();
	}

	public InteractionTransition createLabeledTransition() {
		return new InteractionTransition();
	}

	public static InteractionNetFactory init() {
		return new InteractionNetFactory();
	}

	public ActionTransition createActionTransition() {
		return new ActionTransition();
	}

	public InteractionTransition createInteractionTransition() {
		return new InteractionTransition();
	}

}
