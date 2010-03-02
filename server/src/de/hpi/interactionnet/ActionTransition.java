package de.hpi.interactionnet;

import java.util.ArrayList;
import java.util.List;

import de.hpi.petrinet.LabeledTransitionImpl;

public class ActionTransition extends LabeledTransitionImpl {

	protected List<Role> roles;

	public List<Role> getRoles() {
		if (roles == null)
			roles = new ArrayList<Role>();
		return roles;
	}

}
