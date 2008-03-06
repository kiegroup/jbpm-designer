package de.hpi.ibpmn;

import de.hpi.bpmn.Pool;
import de.hpi.bpmn.StartMessageEvent;

public class StartInteraction extends StartMessageEvent implements Interaction {
	
	protected Pool senderRole;
	protected Pool receiverRole;

	public Pool getReceiverRole() {
		return receiverRole;
	}

	public void setReceiverRole(Pool receiverRole) {
		this.receiverRole = receiverRole;
	}

	public Pool getSenderRole() {
		return senderRole;
	}

	public void setSenderRole(Pool senderRole) {
		this.senderRole = senderRole;
	}

}
