package de.hpi.ibpmn.converter;

import java.util.HashMap;
import java.util.Map;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn2pn.converter.StandardConverter;
import de.hpi.bpmn2pn.model.ConversionContext;
import de.hpi.ibpmn.Interaction;
import de.hpi.interactionnet.InteractionNetFactory;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.interactionnet.Role;
import de.hpi.interactionnet.impl.InteractionNetFactoryImpl;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Transition;

public class IBPMNConverter extends StandardConverter {

	public IBPMNConverter(BPMNDiagram diagram) {
		super(diagram, new InteractionNetFactoryImpl());
	}
	
	@Override
	protected ConversionContext setupConversionContext() {
		return new IBPMNConversionContext();
	}

	public class IBPMNConversionContext extends ConversionContext {
		Map<Pool, Role> roleMap = new HashMap();
	}

	@Override
	protected void handleMessageFlows(PetriNet net, ConversionContext c) {
		// not needed...
	}

	// assumption: t1 == t2, (t1 instanceof InteractionTransition || t1 instanceof ActionTransition) 
	@Override
	protected void handleMessageFlow(PetriNet net, Node node, Transition t1, Transition t2, ConversionContext c) {
		IBPMNConversionContext ic = (IBPMNConversionContext)c;
		if (t1 instanceof InteractionTransition && node instanceof Interaction) {
			InteractionTransition it = (InteractionTransition)t1;
			Interaction i = (Interaction)node;
			it.setSender(findOrCreateRole(i.getSenderRole(), ic));
			it.setReceiver(findOrCreateRole(i.getReceiverRole(), ic));
			it.setMessageType(node.getLabel());
		}
	}
	
	protected Role findOrCreateRole(Pool pool, IBPMNConversionContext ic) {
		Role role = ic.roleMap.get(pool);
		if (role == null) {
			role = ((InteractionNetFactory)pnfactory).createRole();
			role.setName(pool.getLabel());
			ic.roleMap.put(pool, role);
		}
		return role;
	}
	
	// TODO: handle ActionTransition
	
	// 
	
}
