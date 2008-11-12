package de.hpi.interactionnet;

import de.hpi.petrinet.LabeledTransitionImpl;

public class ActionTransition extends LabeledTransitionImpl {

	protected Role role;

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
		label = ""+role;
	}

}
