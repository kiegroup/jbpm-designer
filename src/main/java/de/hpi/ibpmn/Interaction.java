package de.hpi.ibpmn;

import de.hpi.bpmn.Pool;

public interface Interaction {
	
	public Pool getSenderRole();

	public void setSenderRole(Pool pool);

	public Pool getReceiverRole();

	public void setReceiverRole(Pool pool);

}
