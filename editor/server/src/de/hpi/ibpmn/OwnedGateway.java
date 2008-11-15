package de.hpi.ibpmn;

import de.hpi.bpmn.Pool;

/**
 * @author Gero.Decker
 */
public interface OwnedGateway {
	
	public Pool getDecisionOwner();
	
	public void setDecisionOwner(Pool owner);

}


