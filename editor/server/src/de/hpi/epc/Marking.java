package de.hpi.epc;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.hpi.bpt.hypergraph.abs.IGObject;
import de.hpi.bpt.process.epc.Connector;
import de.hpi.bpt.process.epc.Event;
import de.hpi.bpt.process.epc.Function;
import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.IFlowObject;

public class Marking implements Cloneable {
	public enum State {
		POS_TOKEN, NEG_TOKEN, NO_TOKEN
	}

	public enum Context {
		WAIT, DEAD
	}

	public class NodeNewMarkingPair {
		public IFlowObject node;
		public Marking newMarking;

		public NodeNewMarkingPair(IFlowObject node, Marking newMarking) {
			this.node = node;
			this.newMarking = newMarking;
		}
	}

	HashMap<IControlFlow, State> state;
	HashMap<IControlFlow, Context> context;

	public Marking() {
		this(new HashMap<IControlFlow, State>(),
				new HashMap<IControlFlow, Context>());
	}

	public Marking(HashMap<IControlFlow, State> state,
			HashMap<IControlFlow, Context> context) {
		this.state = state;
		this.context = context;
	}

	public Marking clone() {
		return new Marking((HashMap<IControlFlow, State>) state.clone(),
				(HashMap<IControlFlow, Context>) context.clone());
	}

	public LinkedList<NodeNewMarkingPair> propagate(IEPC diag) {
		propagateDeadContext(diag);
		propagateWaitContext(diag);
		propagateNegativeTokens(diag);
		return propagatePositiveTokens(diag);
	}

	private void propagateDeadContext(IEPC diag) {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (IFlowObject node : diag.getFlowObjects()) {
				if ( // if one incoming edge have dead context ...
				filterByContext(diag.getIncomingControlFlow(node), Context.DEAD).size() > 0
						&&
						// ... and if one of outgoing edges without token have
						// wait context
						filterByContext(
								filterByState(diag.getOutgoingControlFlow(node),
										State.NO_TOKEN), Context.WAIT).size() > 0) {
					applyContext(filterByState(diag.getOutgoingControlFlow(node),
							State.NO_TOKEN), Context.DEAD);
					changed = true;
				}
			}
		}
	}

	private void propagateWaitContext(IEPC diag) {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (IFlowObject node : diag.getFlowObjects()) {
				// If event, function or split-connector
				if (diag.getIncomingControlFlow(node).size() == 1) {
					if (context.get(diag.getIncomingControlFlow(node).iterator().next()) == Context.WAIT
							&& filterByContext(diag.getOutgoingControlFlow(node),
									Context.DEAD).size() > 0) {
						for (IControlFlow outEdge : diag.getOutgoingEdges(node)) {
							// Only put new dead context if there is no token
							if (state.get(outEdge) == State.NO_TOKEN) {
								context.put(outEdge, Context.WAIT);
								changed = true;
							}
						}
					}
					// AND Join
				} else if (node instanceof Connector && this.isAndConnector(node)) {
					if (filterByContext(diag.getIncomingControlFlow(node), Context.WAIT)
							.size() == diag.getIncomingControlFlow(node).size()
							&& context.get(diag.getOutgoingControlFlow(node).iterator().next()) == Context.DEAD
							&& state.get(diag.getOutgoingControlFlow(node).iterator().next()) == State.NO_TOKEN) {
						applyContext(diag.getOutgoingControlFlow(node), Context.WAIT);
						changed = true;
					}
					// Xor/ Or Join
				} else if ( isXorConnector(node) || isOrConnector(node)) {
					if (filterByContext(diag.getIncomingControlFlow(node), Context.WAIT).size() > 0 &&
							state.get(diag.getOutgoingControlFlow(node).iterator().next()) == State.NO_TOKEN &&
							context.get(diag.getOutgoingControlFlow(node).iterator().next()) != Context.WAIT) {
						context.put(diag.getOutgoingControlFlow(node).iterator().next(),
								Context.WAIT);
						changed = true;
					}
				}
			}
		}
	}

	private void propagateNegativeTokens(IEPC diag) {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (IFlowObject node : diag.getFlowObjects()) {
				// if all input arcs hold negative tokens and if there is no
				// positive token on the output arc
				if (diag.getIncomingControlFlow(node).size() > 0
						&& filterByState(diag.getIncomingControlFlow(node),
								State.NEG_TOKEN).size() == diag.getIncomingControlFlow(node).size()
						&& filterByState(diag.getOutgoingControlFlow(node),
								State.POS_TOKEN).size() == 0) {
					applyState(diag.getIncomingControlFlow(node), State.NO_TOKEN);
					applyState(diag.getOutgoingControlFlow(node), State.NEG_TOKEN);
					applyContext(diag.getOutgoingControlFlow(node), Context.DEAD);
					changed = true;
				}
			}
		}
	}

	/*
	 * This method applies firing rules without changing the marking: A list of
	 * possible nodes with new marking is returned.
	 */
	private LinkedList<NodeNewMarkingPair> propagatePositiveTokens(IEPC diag) {
		LinkedList<NodeNewMarkingPair> nodeNewMarkings = new LinkedList<NodeNewMarkingPair>();

		// collect nodes which can fire
		for (IFlowObject node : diag.getFlowObjects()) {
			// Event, functions and split connectors
			if (diag.getIncomingControlFlow(node).size() == 1 && state.get(diag.getIncomingControlFlow(node).iterator().next()) == State.POS_TOKEN) {
				// (a), (b), (c)
				if ( node instanceof Function || node instanceof Event || isAndConnector(node) ) {
					NodeNewMarkingPair nodeNewMarking = new NodeNewMarkingPair(node, this.clone());

					nodeNewMarking.newMarking.applyContext(diag.getIncomingControlFlow(node), Context.DEAD);
					nodeNewMarking.newMarking.applyState(diag.getIncomingControlFlow(node), State.NO_TOKEN);

					nodeNewMarking.newMarking.applyContext(diag.getOutgoingControlFlow(node), Context.WAIT);
					nodeNewMarking.newMarking.applyState(diag.getOutgoingControlFlow(node), State.POS_TOKEN);

					nodeNewMarkings.add(nodeNewMarking);
					// (e)
				} else if ( this.isXorConnector(node)) {
					// Each of the outgoing edges can receive a token
					for (IControlFlow edge : diag.getOutgoingControlFlow(node)) {
						NodeNewMarkingPair nodeNewMarking = new NodeNewMarkingPair(
								node, this.clone());

						nodeNewMarking.newMarking.applyContext(diag.getIncomingControlFlow(node), Context.DEAD);
						nodeNewMarking.newMarking.applyState(diag.getIncomingControlFlow(node), State.NO_TOKEN);

						nodeNewMarking.newMarking.applyContext(diag.getOutgoingControlFlow(node), Context.DEAD);
						nodeNewMarking.newMarking.applyState(diag.getOutgoingControlFlow(node), State.NO_TOKEN);

						nodeNewMarking.newMarking.applyContext(edge,
								Context.WAIT);
						nodeNewMarking.newMarking.applyState(edge,
								State.POS_TOKEN);

						nodeNewMarkings.add(nodeNewMarking);
					}
					// (g)
				} else if (this.isOrConnector(node)) {
					List<IControlFlow> controlFlowList = new LinkedList<IControlFlow>();
					controlFlowList.addAll(diag.getOutgoingControlFlow(node));
					for (List<IControlFlow> edges : (List<List<IControlFlow>>) de.hpi.bpmn.analysis.Combination.findCombinations(controlFlowList)) {
						if (edges.size() == 0)
							continue;

						NodeNewMarkingPair nodeNewMarking = new NodeNewMarkingPair(
								node, this.clone());

						nodeNewMarking.newMarking.applyContext(diag.getIncomingControlFlow(node), Context.DEAD);
						nodeNewMarking.newMarking.applyState(diag.getIncomingControlFlow(node), State.NO_TOKEN);

						nodeNewMarking.newMarking.applyContext(diag.getOutgoingControlFlow(node), Context.DEAD);
						nodeNewMarking.newMarking.applyState(diag.getOutgoingControlFlow(node), State.NO_TOKEN);

						nodeNewMarking.newMarking.applyContext(edges,
								Context.WAIT);
						nodeNewMarking.newMarking.applyState(edges,
								State.POS_TOKEN);

						nodeNewMarkings.add(nodeNewMarking);
					}
				}
				// join connectors
			} else if (diag.getOutgoingControlFlow(node).size() == 1) {
				// (d)
				if (this.isAndConnector(node)
						&& filterByState(diag.getIncomingControlFlow(node),
								State.POS_TOKEN).size() == diag.getIncomingControlFlow(node).size()) {
					NodeNewMarkingPair nodeNewMarking = new NodeNewMarkingPair(
							node, this.clone());

					nodeNewMarking.newMarking.applyContext(diag.getIncomingControlFlow(node), Context.DEAD);
					nodeNewMarking.newMarking.applyState(diag.getIncomingControlFlow(node), State.NO_TOKEN);

					nodeNewMarking.newMarking.applyContext(diag.getOutgoingControlFlow(node), Context.WAIT);
					nodeNewMarking.newMarking.applyState(diag.getOutgoingControlFlow(node), State.POS_TOKEN);

					nodeNewMarkings.add(nodeNewMarking);
				} else if (this.isXorConnector(node)
						&& filterByState(diag.getIncomingControlFlow(node),
								State.POS_TOKEN).size() == 1) {
					NodeNewMarkingPair nodeNewMarking = new NodeNewMarkingPair(
							node, this.clone());

					nodeNewMarking.newMarking
							.applyContext(filterByState(
									diag.getIncomingControlFlow(node), State.NEG_TOKEN),
									Context.DEAD);
					nodeNewMarking.newMarking.applyState(diag.getIncomingControlFlow(node), State.NO_TOKEN);

					nodeNewMarking.newMarking.applyContext(diag.getOutgoingControlFlow(node), Context.WAIT);
					nodeNewMarking.newMarking.applyState(diag.getOutgoingControlFlow(node), State.POS_TOKEN);

					nodeNewMarkings.add(nodeNewMarking);
					// (h)
				} else if (this.isOrConnector(node)){
					Collection<IControlFlow> incomingControlFlow = diag.getIncomingControlFlow((Connector)node);
					// Collect the control flows which would enable the or join to fire
					// (pos + neg tokens, dead context)
					Collection<IControlFlow> cfReadyForFiring = filterByState(incomingControlFlow, State.POS_TOKEN);
					// There should be at least one positive token
					if(cfReadyForFiring.size() > 0){
						cfReadyForFiring.addAll(filterByState(incomingControlFlow, State.NEG_TOKEN));
						cfReadyForFiring.addAll(filterByContext(incomingControlFlow, Context.DEAD));
						// Are all incomingControlFlow ready for firing?
						//TODO perhaps just ask for cf with wait context?
						if(cfReadyForFiring.containsAll(incomingControlFlow)){
							NodeNewMarkingPair nodeNewMarking = new NodeNewMarkingPair(
									node, this.clone());
							
							//TODO negative upper corona, see p. 76
							nodeNewMarking.newMarking.applyContext(incomingControlFlow, Context.DEAD);
							nodeNewMarking.newMarking.applyState(incomingControlFlow, State.NO_TOKEN);
							
							nodeNewMarking.newMarking.applyState(diag.getOutgoingControlFlow((Connector)node), State.POS_TOKEN);
							nodeNewMarking.newMarking.applyContext(diag.getOutgoingControlFlow((Connector)node), Context.WAIT);
							
							nodeNewMarkings.add(nodeNewMarking);
						}
					}
				}
			}
		}

		return nodeNewMarkings;
	}

	private Collection<IControlFlow> filterByContext(Collection<IControlFlow> edges,
			Context type) {
		Collection<IControlFlow> filtered = new LinkedList<IControlFlow>();
		for (IControlFlow edge : edges) {
			if (context.get(edge) == type) {
				filtered.add(edge);
			}
		}
		return filtered;
	}

	private Collection<IControlFlow> filterByState(Collection<IControlFlow> edges, State type) {
		Collection<IControlFlow> filtered = new LinkedList<IControlFlow>();
		for (IControlFlow edge : edges) {
			if (state.get(edge) == type) {
				filtered.add(edge);
			}
		}
		return filtered;
	}

	public void applyState(Collection<IControlFlow> edges, State type) {
		for (IControlFlow edge : edges) {
			applyState(edge, type);
		}
	}

	public void applyState(IControlFlow edge, State type) {
		state.put(edge, type);
	}

	public void applyContext(Collection<IControlFlow> edges, Context type) {
		for (IControlFlow edge : edges) {
			applyContext(edge, type);
		}
	}

	public void applyContext(IControlFlow edge, Context type) {
		context.put(edge, type);
	}
	
	public boolean hasToken(IControlFlow edge){
		return state.get(edge).equals(State.POS_TOKEN);
	}
	
	static public boolean isAndConnector(IGObject flowObject){
		return (flowObject instanceof Connector) && ((Connector)flowObject).getConnectorType().equals(de.hpi.bpt.process.epc.ConnectorType.AND);
	}
	static public boolean isOrConnector(IGObject flowObject){
		return (flowObject instanceof Connector) && ((Connector)flowObject).getConnectorType().equals(de.hpi.bpt.process.epc.ConnectorType.OR);
	}
	static public boolean isXorConnector(IGObject flowObject){
		return (flowObject instanceof Connector) && ((Connector)flowObject).getConnectorType().equals(de.hpi.bpt.process.epc.ConnectorType.XOR);
	}
	static public boolean isSplit(IGObject flowObject, IEPC diag){
		return (flowObject instanceof Connector) &&  diag.getOutgoingControlFlow((Connector)flowObject).size() > 1;
	}
	static public boolean isJoin(IGObject flowObject, IEPC diag){
		return (flowObject instanceof Connector) &&  diag.getIncomingControlFlow((Connector)flowObject).size() > 1;
	}
}
