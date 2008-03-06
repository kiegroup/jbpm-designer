package de.hpi.interactionnet;

import de.hpi.PTnet.PTNetFactory;
import de.hpi.interactionnet.impl.InteractionNetFactoryImpl;


public interface InteractionNetFactory extends PTNetFactory {
	
	InteractionNetFactory eINSTANCE = InteractionNetFactoryImpl.init();

	InteractionNet createInteractionNet();
	
	InteractionTransition createInteractionTransition();
	
	ActionTransition createActionTransition();
	
	Role createRole();

}
