package de.hpi.ibpmn;

import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.Pool;

/**
 * @author Gero.Decker
 */
public class OwnedORGateway extends ORGateway implements OwnedGateway {

	protected Pool owner;

	public Pool getDecisionOwner() {
		return owner;
	}

	public void setDecisionOwner(Pool owner) {
		this.owner = owner;
	}

}


