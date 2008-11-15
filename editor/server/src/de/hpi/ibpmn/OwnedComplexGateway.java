package de.hpi.ibpmn;

import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.Pool;

/**
 * @author Gero.Decker
 */
public class OwnedComplexGateway extends ComplexGateway implements OwnedGateway {

	protected Pool owner;

	public Pool getDecisionOwner() {
		return owner;
	}

	public void setDecisionOwner(Pool owner) {
		this.owner = owner;
	}

}


