package de.hpi.interactionnet.serialization;

import java.util.Iterator;

import org.w3c.dom.Node;

import de.hpi.PTnet.serialization.PTNetPNMLImporter;
import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionNetFactory;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.interactionnet.Role;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;

public class InteractionNetPNMLImporter extends PTNetPNMLImporter {

	@Override
	protected PetriNet createPetriNet(Node nnode) {
		return InteractionNetFactory.eINSTANCE.createInteractionNet();
	}

	@Override
	protected LabeledTransition createLabeledTransition(PetriNet net, Node tnode) {
		Node cnode = getChild(tnode, "interaction");
		if (cnode != null) {
			InteractionTransition ti = InteractionNetFactory.eINSTANCE.createInteractionTransition();
	
			ti.setSender(getRole((InteractionNet)net, getContent(getChild(cnode, "sender"))));
			ti.setReceiver(getRole((InteractionNet)net, getContent(getChild(cnode, "receiver"))));
			ti.setMessageType(getContent(getChild(cnode, "messageType")));
			
			return ti;

		} else if ((cnode = getChild(tnode, "action")) != null) {
			ActionTransition ta = InteractionNetFactory.eINSTANCE.createActionTransition();

			ta.getRoles().add(getRole((InteractionNet)net, getContent(getChild(cnode, "role"))));
			
			return ta;

		} else {
			return null;
		}
	}

	private Role getRole(InteractionNet net, String name) {
		for (Iterator<Role> it=net.getRoles().iterator(); it.hasNext(); ) {
			Role role = it.next();
			if (role.getName().equals(name)) {
				return role;
			}
		}
		Role role = InteractionNetFactory.eINSTANCE.createRole();
		role.setName(name);
		net.getRoles().add(role);
		return role;
	}

}
