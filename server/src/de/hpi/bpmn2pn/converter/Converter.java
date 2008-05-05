package de.hpi.bpmn2pn.converter;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.ControlFlow;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.MessageFlow;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Task;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.XOREventBasedGateway;
import de.hpi.bpmn2pn.model.ConversionContext;
import de.hpi.bpmn2pn.model.SubProcessPlaces;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.TauTransition;
import de.hpi.petrinet.Transition;

// TODO: handle termination events, throwing exceptions
public abstract class Converter {

	protected BPMNDiagram diagram;
	protected PetriNetFactory pnfactory;

	public Converter(BPMNDiagram diagram, PetriNetFactory pnfactory) {
		this.diagram = diagram;
		this.pnfactory = pnfactory;
	}

	// ********************************************************************
	// main method
	// ********************************************************************

	public PetriNet convert() {
		if (diagram == null)
			return null;
		diagram.identifyProcesses();

		ConversionContext c = setupConversionContext();

		PetriNet net = pnfactory.createPetriNet();
		handleDiagram(net, c);

		// create places
		createStartPlaces(net, c);
		handleSequenceFlows(net, c);
		handleMessageFlows(net, c);
		try {
			handleDataObjects(net, c);
		} catch (DataObjectNoInitStateException donis) {

		}
		// create transitions
		handleNodesRecursively(net, diagram, c);

		return net;
	}

	protected void handleDiagram(PetriNet net, ConversionContext c) {
		// do nothing
	}

	// precondition: net instanceof PTNet, override for other Petri net types!
	protected void createStartPlaces(PetriNet net, ConversionContext c) {
		int i = 1;
		for (Container process : diagram.getProcesses()) {
			Place p = addPlace(net, "start" + i);
			SubProcessPlaces pl = c.getSubprocessPlaces(process);
			pl.startP = p;
			i++;
		}
	}

	protected ConversionContext setupConversionContext() {
		return new ConversionContext();
	}

	// ********************************************************************
	// sequence flow
	// ********************************************************************

	protected void handleSequenceFlows(PetriNet net, ConversionContext c) {
		for (Edge edge : diagram.getEdges()) {
			if (edge instanceof SequenceFlow) {
				handleSequenceFlow(net, (SequenceFlow) edge, c);
			}
		}
	}

	protected void handleSequenceFlow(PetriNet net, SequenceFlow flow,
			ConversionContext c) {
		if (flow.getSource() instanceof XOREventBasedGateway) {
			Place p = c.map.get(flow.getSource());
			if (p == null) {
				p = addPlace(net, flow.getSource().getId());
				c.map.put(flow.getSource(), p);
			}
			c.map.put(flow, p);
		} else {
			Place p = addPlace(net, flow.getId());
			c.map.put(flow, p);
		}
	}

	// ********************************************************************
	// message flow
	// ********************************************************************

	// basic idea: all flows sharing their source or their origin are realized
	// as one place
	protected void handleMessageFlows(PetriNet net, ConversionContext c) {
		// get all flows
		List<MessageFlow> allFlows = new ArrayList();
		for (Edge edge : diagram.getEdges()) {
			if (edge instanceof MessageFlow)
				allFlows.add((MessageFlow) edge);
		}

		while (allFlows.size() > 0) {
			MessageFlow flow = allFlows.get(0);
			Place p = addPlace(net, flow.getId());
			addMessageFlowRecursively(net, flow, c, allFlows, p);
		}
	}

	protected void addMessageFlowRecursively(PetriNet net, MessageFlow flow,
			ConversionContext c, List<MessageFlow> allFlows, Place p) {
		if (!allFlows.contains(flow))
			return;
		allFlows.remove(flow);
		c.map.put(flow, p);
		for (Edge edge : flow.getSource().getOutgoingEdges())
			if (edge instanceof MessageFlow)
				addMessageFlowRecursively(net, (MessageFlow) edge, c, allFlows, p);
		for (Edge edge : flow.getTarget().getIncomingEdges())
			if (edge instanceof MessageFlow)
				addMessageFlowRecursively(net, (MessageFlow) edge, c, allFlows,
						p);
	}

	// this method is used by most nodes
	// assumption: at most one incoming and at most one outgoing message flow
	protected void handleMessageFlow(PetriNet net, Node node, Transition t1,
			Transition t2, ConversionContext c) {
		for (Edge edge : node.getIncomingEdges())
			if (edge instanceof MessageFlow)
				addFlowRelationship(net, c.map.get(edge), t1);
		for (Edge edge : node.getOutgoingEdges())
			if (edge instanceof MessageFlow)
				addFlowRelationship(net, t2, c.map.get(edge));
	}

	// ********************************************************************
	// data objects
	// ********************************************************************

	protected void handleDataObjects(PetriNet net, ConversionContext c)
			throws DataObjectNoInitStateException {
		// do nothing
	}

	// ********************************************************************
	// recursive handling of nodes
	// ********************************************************************

	// TODO: include anc.. into context
	protected void handleNodesRecursively(PetriNet net, Container container,
			ConversionContext c) {
		for (Node node : container.getChildNodes()) {
			if (node instanceof Task) {
				handleTask(net, (Task) node, c);
			} else if (node instanceof SubProcess) {
				handleSubProcess(net, (SubProcess) node, c);
			} else if (node instanceof StartEvent) {
				handleStartEvent(net, (StartEvent) node, c);
			} else if (node instanceof IntermediateEvent) {
				handleIntermediateEvent(net, (IntermediateEvent) node, c);
			} else if (node instanceof EndEvent) {
				handleEndEvent(net, (EndEvent) node, c);
			} else if (node instanceof ANDGateway) {
				handleANDGateway(net, (ANDGateway) node, c);
			} else if (node instanceof XORDataBasedGateway) {
				handleXORDataBasedGateway(net, (XORDataBasedGateway) node, c);
			} else if (node instanceof XOREventBasedGateway) {
				handleXOREventBasedGateway(net, (XOREventBasedGateway) node, c);
			} else if (node instanceof ORGateway) {
				handleORGateway(net, (ORGateway) node, c);
			} else if (node instanceof ComplexGateway) {
				handleComplexGateway(net, (ComplexGateway) node, c);
			} else if (node instanceof Pool) {
				handlePool(net, (Pool) node, c);
			} else if (node instanceof Lane) {
				handleLane(net, (Lane) node, c);
			}
		}
	}

	// ********************************************************************
	// pools + lanes
	// ********************************************************************

	protected void handlePool(PetriNet net, Pool pool, ConversionContext c) {
		handleNodesRecursively(net, pool, c);
	}

	protected void handleLane(PetriNet net, Lane lane, ConversionContext c) {
		handleNodesRecursively(net, lane, c);
	}

	// ********************************************************************
	// task
	// ********************************************************************

	// assumption: exactly one input and one output edge
	protected void handleTask(PetriNet net, Task task, ConversionContext c) {
		Transition t = addLabeledTransition(net, task.getId(), task.getLabel());
		handleMessageFlow(net, task, t, t, c);
		addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(task)), t);
		addFlowRelationship(net, t, c.map.get(getOutgoingSequenceFlow(task)));
		if (c.ancestorHasExcpH)
			handleExceptions(net, task, t, c);

		for (IntermediateEvent event : task.getAttachedEvents())
			handleAttachedIntermediateEventForTask(net, event, c);
	}

	// ********************************************************************
	// subprocesses
	// ********************************************************************

	// assumption: exactly one input and one output edge
	protected void handleSubProcess(PetriNet net, SubProcess process,
			ConversionContext c) {
		//standard subprocess
		// TODO multiple start events bound as XOR ??
		SubProcessPlaces pl = c.getSubprocessPlaces(process);

		Transition startT = addTauTransition(net, "start" + process.getId());
		pl.startP = addPlace(net, "start" + process.getId());
		addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(process)),
				startT);
		addFlowRelationship(net, startT, pl.startP);

		Transition endT = addTauTransition(net, "end" + process.getId());
		pl.endP = addPlace(net, "end" + process.getId());
		addFlowRelationship(net, pl.endP, endT);
		addFlowRelationship(net, endT, c.map
				.get(getOutgoingSequenceFlow(process)));

		handleMessageFlow(net, process, startT, endT, c);

		// exception handling
		if (c.ancestorHasExcpH || process.getAttachedEvents().size() > 0)
			prepareExceptionHandling(net, process, startT, endT, c);
		for (IntermediateEvent event : process.getAttachedEvents())
			handleAttachedIntermediateEventForSubProcess(net, event, c);

		boolean ancestorHasExcpH = c.ancestorHasExcpH;
		c.ancestorHasExcpH |= process.getAttachedEvents().size() > 0;
		handleNodesRecursively(net, process, c);
		c.ancestorHasExcpH = ancestorHasExcpH;
	}

	// ********************************************************************
	// events (without attached intermediate events)
	// ********************************************************************

	// assumption: exactly one output edge
	protected void handleStartEvent(PetriNet net, StartEvent event,
			ConversionContext c) {
		Container process = event.getProcess();
		// TODO fix this little hack
		if (process == null) {
			process = event.getParent();
		}
		Place p = c.getSubprocessPlaces(process).startP;
		Transition t = addLabeledTransition(net, event.getId(), event.getLabel());
		handleMessageFlow(net, event, t, t, c);
		addFlowRelationship(net, p, t);
		addFlowRelationship(net, t, c.map.get(getOutgoingSequenceFlow(event)));
		if (c.ancestorHasExcpH)
			handleExceptions(net, event, t, c);
	}

	// assumption: exactly one input and one output edge
	protected void handleIntermediateEvent(PetriNet net,
			IntermediateEvent event, ConversionContext c) {
		// do not handle attached intermediate events here...
		if (event.getActivity() != null)
			return;
		Transition t = addLabeledTransition(net, event.getId(), event
				.getLabel());
		handleMessageFlow(net, event, t, t, c);
		addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(event)), t);
		addFlowRelationship(net, t, c.map.get(getOutgoingSequenceFlow(event)));
		if (c.ancestorHasExcpH)
			handleExceptions(net, event, t, c);
	}

	// assumption: exactly one input edge
	protected void handleEndEvent(PetriNet net, EndEvent event,	ConversionContext c) {
		Container process = event.getProcess();
		if (process == null) {
			process = event.getParent();
		}

		Transition t = addTauTransition(net, event.getId());
		handleMessageFlow(net, event, t, t, c);
		addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(event)), t);
		Place p = c.getSubprocessPlaces(process).endP;
		if (p == null)
			p = addPlace(net, "end" + event.getId());
		addFlowRelationship(net, t, p);
		if (c.ancestorHasExcpH)
			handleExceptions(net, event, t, c);
	}

	// ********************************************************************
	// gateways
	// ********************************************************************

	// assumption: at least one input and at least one output edge
	protected void handleANDGateway(PetriNet net, ANDGateway gateway,
			ConversionContext c) {
		Transition t = addTauTransition(net, gateway.getId());
		for (Edge e : gateway.getIncomingEdges())
			addFlowRelationship(net, c.map.get(e), t);
		for (Edge e : gateway.getOutgoingEdges())
			addFlowRelationship(net, t, c.map.get(e));
	}

	// assumption: at least one input and at least one output edge
	protected void handleXORDataBasedGateway(PetriNet net,
			XORDataBasedGateway gateway, ConversionContext c) {
		Place p = null;
		if (gateway.getIncomingEdges().size() > 1
				&& gateway.getOutgoingEdges().size() > 1) {
			p = addPlace(net, "xor" + gateway.getId());
		} else if (gateway.getIncomingEdges().size() == 1) {
			p = c.map.get(getIncomingSequenceFlow(gateway));
		} else if (gateway.getOutgoingEdges().size() == 1) {
			p = c.map.get(getOutgoingSequenceFlow(gateway));
		}

		if (gateway.getIncomingEdges().size() > 1) {
			for (Edge e : gateway.getIncomingEdges()) {
				Transition t2 = addTauTransition(net, "merge" + e.getId());
				addFlowRelationship(net, c.map.get(e), t2);
				addFlowRelationship(net, t2, p);
			}
		}
		if (gateway.getOutgoingEdges().size() > 1
				|| gateway.getIncomingEdges().size() == 1) {
			for (Edge e : gateway.getOutgoingEdges()) {
				Transition t2 = addTauTransition(net, "option" + e.getId());
				addFlowRelationship(net, p, t2);
				addFlowRelationship(net, t2, c.map.get(e));
			}
		}
	}

	// assumption: at least one input and at least one output edge
	protected void handleXOREventBasedGateway(PetriNet net,
			XOREventBasedGateway gateway, ConversionContext c) {
		Place p = c.map.get(gateway);
		for (Edge e : gateway.getIncomingEdges()) {
			Transition t = addTauTransition(net, "merge" + e.getId());
			addFlowRelationship(net, c.map.get(e), t);
			addFlowRelationship(net, t, p);
		}
		// question: why do we create a transition even in those cases where
		// there is only one incoming branch?
		// answer: because there was a place created for the incoming sequence
		// flow and for the gateway.
		// in order to merge these places, the sequence flow mapping would need
		// to be altered
	}

	// assumption: at least one input and at least one output edge
	protected void handleORGateway(PetriNet net, ORGateway gateway,
			ConversionContext c) {
		// not implemented
	}

	// assumption: at least one input and at least one output edge
	protected void handleComplexGateway(PetriNet net, ComplexGateway gateway,
			ConversionContext c) {
		// not implemented
	}

	// ********************************************************************
	// exception handling
	// ********************************************************************

	// precondition: there actually is an enclosing exception
	protected void prepareExceptionHandling(PetriNet net, SubProcess process,
			Transition startT, Transition endT, ConversionContext c) {
		SubProcessPlaces pl = c.getSubprocessPlaces(process);
		pl.ok = addPlace(net, "ok" + process.getId());
		pl.nok = addPlace(net, "nok" + process.getId());
		// pl.enabled = addPlace(net, "enabled"+process.getId());

		addFlowRelationship(net, startT, pl.ok);
		addFlowRelationship(net, pl.ok, endT);
		// addFlowRelationship(net, endT, pl.enabled);
		// addFlowRelationship(net, pl.enabled, startT);

		if (c.ancestorHasExcpH) {
			pl.cancel = addPlace(net, "cancel" + process.getId());
			Transition tcancel = addTauTransition(net, "cancel"
					+ process.getId());
			addFlowRelationship(net, pl.ok, tcancel);
			addFlowRelationship(net, tcancel, pl.nok);
			addFlowRelationship(net, tcancel, pl.cancel);
			SubProcessPlaces parentpl = c.getSubprocessPlaces(process
					.getProcess());
			addFlowRelationship(net, tcancel, parentpl.nok);
			addFlowRelationship(net, parentpl.nok, tcancel);

			Transition tnok = addTauTransition(net, "nok" + process.getId());
			addFlowRelationship(net, pl.cancel, tnok);
			addFlowRelationship(net, pl.nok, tnok);
			addFlowRelationship(net, pl.endP, tnok);
			// addFlowRelationship(net, tnok, pl.enabled);
			addFlowRelationship(net, tnok, c.map
					.get(getOutgoingSequenceFlow(process)));
		}
	}

	// assumption: exactly one output edge
	protected void handleAttachedIntermediateEventForSubProcess(PetriNet net,
			IntermediateEvent event, ConversionContext c) {
		Transition t = addTauTransition(net, event.getId());
		handleMessageFlow(net, event, t, t, c);

		SubProcessPlaces pl = c.getSubprocessPlaces((SubProcess) event
				.getActivity());

		Place excp = addPlace(net, "excp" + event.getId());
		addFlowRelationship(net, pl.ok, t);
		addFlowRelationship(net, t, pl.nok);
		addFlowRelationship(net, t, excp);

		Transition texcp = addTauTransition(net, "excp" + event.getId());
		addFlowRelationship(net, excp, texcp);
		addFlowRelationship(net, pl.nok, texcp);
		addFlowRelationship(net, pl.endP, texcp);
		// addFlowRelationship(net, t, pl.enabled);
		addFlowRelationship(net, t, c.map.get(getOutgoingSequenceFlow(event)));
	}

	// assumption: exactly one output edge
	protected void handleAttachedIntermediateEventForTask(PetriNet net,
			IntermediateEvent event, ConversionContext c) {
		Transition t = addTauTransition(net, event.getId());
		handleMessageFlow(net, event, t, t, c);
		Place p = c.map.get(getIncomingSequenceFlow(event.getActivity()));
		addFlowRelationship(net, p, t);
		addFlowRelationship(net, t, c.map.get(getOutgoingSequenceFlow(event)));
	}

	// precondition: there actually is an enclosing exception
	protected void handleExceptions(PetriNet net, Node node, Transition t,
			ConversionContext c) {
		// skip transition
		Transition tskip = addTauTransition(net, "skip" + node.getId());
		for (FlowRelationship rel : t.getIncomingFlowRelationships())
			addFlowRelationship(net, rel.getSource(), tskip);
		for (FlowRelationship rel : t.getOutgoingFlowRelationships())
			addFlowRelationship(net, tskip, rel.getTarget());
		Place pnok = c.getSubprocessPlaces(node.getProcess()).nok;
		addFlowRelationship(net, tskip, pnok);
		addFlowRelationship(net, pnok, tskip);

		// biflows to ok places
		Container proc = node.getProcess();
		do {
			Place pok = c.getSubprocessPlaces(proc).ok;
			if (pok == null)
				break;
			addFlowRelationship(net, t, pok);
			addFlowRelationship(net, pok, t);
			proc = ((SubProcess) proc).getProcess();
		} while (proc instanceof SubProcess);
	}

	// ********************************************************************
	// utility methods
	// ********************************************************************

	public ControlFlow getIncomingSequenceFlow(Node node) {
		for (Edge edge : node.getIncomingEdges())
			if (edge instanceof ControlFlow)
				return (ControlFlow) edge;
		return null;
	}

	public ControlFlow getOutgoingSequenceFlow(Node node) {
		for (Edge edge : node.getOutgoingEdges())
			if (edge instanceof ControlFlow)
				return (ControlFlow) edge;
		return null;
	}

	public Place addPlace(PetriNet net, String id) {
		Place p = pnfactory.createPlace();
		p.setId(id);
		net.getPlaces().add(p);
		return p;
	}

	public TauTransition addTauTransition(PetriNet net, String id) {
		TauTransition t = pnfactory.createTauTransition();
		t.setId(id);
		net.getTransitions().add(t);
		return t;
	}

	public LabeledTransition addLabeledTransition(PetriNet net, String id, String label) {
		LabeledTransition t = pnfactory.createLabeledTransition();
		t.setId(id);
		t.setLabel(label);
		net.getTransitions().add(t);
		return t;
	}

	public FlowRelationship addFlowRelationship(PetriNet net,
			de.hpi.petrinet.Node source, de.hpi.petrinet.Node target) {
		if (source == null || target == null)
			return null;
		FlowRelationship rel = pnfactory.createFlowRelationship();
		rel.setSource(source);
		rel.setTarget(target);
		net.getFlowRelationships().add(rel);
		return rel;
	}

}
