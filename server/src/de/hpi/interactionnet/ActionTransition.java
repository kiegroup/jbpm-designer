package de.hpi.interactionnet;

import de.hpi.petrinet.LabeledTransition;

public interface ActionTransition extends LabeledTransition {

	Role getRole();
	
	void setRole(Role role);

}
