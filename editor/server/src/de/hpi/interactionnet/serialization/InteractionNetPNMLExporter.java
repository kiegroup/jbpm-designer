package de.hpi.interactionnet.serialization;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.hpi.PTnet.serialization.PTNetPNMLExporter;
import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.interactionnet.Role;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Transition;

/**
 * 
 * @author gero.decker
 *
 */
public class InteractionNetPNMLExporter extends PTNetPNMLExporter {

	@Override
	protected void handlePetriNetAttributes(Document doc, Element node, PetriNet net) {
		super.handlePetriNetAttributes(doc, node, net);
		node.setAttribute("type", "IPN");
	}

	@Override
	protected Element appendTransition(Document doc, Node netnode, Transition transition) {
		Element tnode = super.appendTransition(doc, netnode, transition);
		
		if (transition instanceof InteractionTransition) {
			InteractionTransition t = (InteractionTransition)transition;
			Element inode = (Element)tnode.appendChild(doc.createElement("interaction"));
			if (t.getSender() != null)
				addContentElement(doc, inode, "sender", t.getSender().getName());
			if (t.getReceiver() != null)
				addContentElement(doc, inode, "receiver", t.getReceiver().getName());
			addContentElement(doc, inode, "messageType", t.getMessageType());
	
		} else if (transition instanceof ActionTransition) {
			ActionTransition t = (ActionTransition)transition;
			Element inode = (Element)tnode.appendChild(doc.createElement("action"));
			for (Role r: t.getRoles())
				addContentElement(doc, inode, "role", r.getName());
	
		}

		return tnode;
	}

}
