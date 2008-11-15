package de.hpi.ibpmn;

import de.hpi.bpmn.Pool;
import de.hpi.bpmn.XORDataBasedGateway;

/**
 * @author Gero.Decker
 */
public class OwnedXORDataBasedGateway extends XORDataBasedGateway implements OwnedGateway {
	
	protected Pool owner;

	public Pool getDecisionOwner() {
		return owner;
	}

	public void setDecisionOwner(Pool owner) {
		this.owner = owner;
	}

}


