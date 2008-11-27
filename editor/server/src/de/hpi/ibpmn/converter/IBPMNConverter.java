package de.hpi.ibpmn.converter;

import java.util.HashMap;
import java.util.Map;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn2pn.converter.StandardConverter;
import de.hpi.bpmn2pn.model.ConversionContext;
import de.hpi.ibpmn.Interaction;
import de.hpi.ibpmn.OwnedNode;
import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionNetFactory;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.interactionnet.Role;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Transition;

public class IBPMNConverter extends StandardConverter {

	public IBPMNConverter(BPMNDiagram diagram) {
		super(diagram, new InteractionNetFactory());
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

	@Override
	protected void handleMessageFlow(PetriNet net, Node node, Transition t1, Transition t2, ConversionContext c) {
		// not needed...
	}
	
	@Override
	protected Transition addLabeledTransition(PetriNet net, String id, DiagramObject obj, int autoLevel, String label, ConversionContext c) {
		if (obj instanceof Interaction) {
			Interaction i = (Interaction)obj;
			InteractionTransition t = ((InteractionNetFactory)pnfactory).createInteractionTransition();
			net.getTransitions().add(t);
			t.setId(id);
			t.setLabel(label);
			t.setSender(findOrCreateRole((InteractionNet)net, i.getSenderRole(), (IBPMNConversionContext)c));
			t.setReceiver(findOrCreateRole((InteractionNet)net, i.getReceiverRole(), (IBPMNConversionContext)c));
			t.setMessageType(((Node)obj).getLabel());
			return t;
		
		} else if (obj instanceof OwnedNode) {
			OwnedNode on = (OwnedNode)obj;
			ActionTransition t = ((InteractionNetFactory)pnfactory).createActionTransition();
			net.getTransitions().add(t);
			t.setId(id);
			t.setLabel(label);
			for (Pool p: on.getOwners())
				t.getRoles().add(findOrCreateRole((InteractionNet)net, p, (IBPMNConversionContext)c));
			return t;

		} else if (obj instanceof StartPlainEvent || obj instanceof EndPlainEvent) {
			return addSimpleSilentTransition(net, id);
		
		} else {
			return super.addLabeledTransition(net, id, obj, autoLevel, label, c);
		}
	}

	@Override
	protected Transition addXOROptionTransition(PetriNet net, Edge e, ConversionContext c) {
		ActionTransition t = ((InteractionNetFactory)pnfactory).createActionTransition();
		net.getTransitions().add(t);
		t.setId(e.getId());
		t.setLabel(((SequenceFlow)e).getConditionExpression());
		for (Pool p: ((OwnedNode)e.getSource()).getOwners())
			t.getRoles().add(findOrCreateRole((InteractionNet)net, p, (IBPMNConversionContext)c));
		return t;
	}

	protected Role findOrCreateRole(InteractionNet net, Pool pool, IBPMNConversionContext ic) {
		Role role = ic.roleMap.get(pool);
		if (role == null) {
			role = ((InteractionNetFactory)pnfactory).createRole();
			net.getRoles().add(role);
			role.setName(pool.getLabel());
			ic.roleMap.put(pool, role);
		}
		return role;
	}
	
}
