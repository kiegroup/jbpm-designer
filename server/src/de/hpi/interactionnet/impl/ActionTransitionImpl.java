package de.hpi.interactionnet.impl;

import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.Role;
import de.hpi.petrinet.impl.LabeledTransitionImpl;

public class ActionTransitionImpl extends LabeledTransitionImpl implements
		ActionTransition {
	
	protected Role role;

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
		label = ""+role;
	}

}
