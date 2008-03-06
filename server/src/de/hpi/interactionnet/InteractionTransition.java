package de.hpi.interactionnet;

import de.hpi.petrinet.LabeledTransition;

public interface InteractionTransition extends LabeledTransition {
	
	Role getSender();
	
	void setSender(Role role);

	Role getReceiver();
	
	void setReceiver(Role role);

	String getMessageType();
	
	void setMessageType(String messageType);

}
