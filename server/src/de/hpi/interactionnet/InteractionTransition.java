package de.hpi.interactionnet;

import de.hpi.petrinet.LabeledTransitionImpl;

public class InteractionTransition extends LabeledTransitionImpl {
	
	protected Role sender;
	protected Role receiver;
	protected String messageType;

	public String getMessageType() {
		return messageType;
	}

	public Role getReceiver() {
		return receiver;
	}

	public Role getSender() {
		return sender;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public void setReceiver(Role role) {
		this.receiver = role;
	}

	public void setSender(Role role) {
		this.sender = role;
	}
	
	public String toString() {
		return sender+"=>"+receiver+" ("+messageType+")";
	}

}
