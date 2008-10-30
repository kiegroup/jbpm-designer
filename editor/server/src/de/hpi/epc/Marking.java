package de.hpi.epc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.hpi.diagram.Diagram;
import de.hpi.diagram.DiagramEdge;
import de.hpi.diagram.DiagramNode;

public class Marking implements Cloneable {
	public enum State {
		POS_TOKEN, NEG_TOKEN, NO_TOKEN
	}

	public enum Context {
		WAIT, DEAD
	}

	String FUNCTION = "Function";
	String XOR_CONNECTOR = "XorConnector";
	String OR_CONNECTOR = "OrConnector";
	String AND_CONNECTOR = "AndConnector";
	String EVENT = "Event";
	String PROCESS_INTERFACE = "ProcessInterface";

	public class NodeNewMarkingPair {
		public DiagramNode node;
		public Marking newMarking;

		public NodeNewMarkingPair(DiagramNode node, Marking newMarking) {
			this.node = node;
			this.newMarking = newMarking;
		}
	}

	HashMap<DiagramEdge, State> state;
	HashMap<DiagramEdge, Context> context;

	public Marking() {
		this(new HashMap<DiagramEdge, State>(),
				new HashMap<DiagramEdge, Context>());
	}

	public Marking(HashMap<DiagramEdge, State> state,
			HashMap<DiagramEdge, Context> context) {
		this.state = state;
		this.context = context;
	}

	public Marking clone() {
		return new Marking((HashMap<DiagramEdge, State>) state.clone(),
				(HashMap<DiagramEdge, Context>) context.clone());
	}

	public LinkedList<NodeNewMarkingPair> propagate(Diagram diag) {
		propagateDeadContext(diag);
		propagateWaitContext(diag);
		propagateNegativeTokens(diag);
		return propagatePositiveTokens(diag);
	}

	private void propagateDeadContext(Diagram diag) {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (DiagramNode node : diag.getNodes()) {
				for (DiagramEdge edge : node.getIncomingEdges()) {
					if (context.get(edge) == Context.DEAD) {
						for (DiagramEdge outEdge : node.getOutgoingEdges()) {
							// Only put new dead context if there is no token
							if (state.get(outEdge) == State.NO_TOKEN) {
								context.put(outEdge, Context.DEAD);
								changed = true;
							}
						}
						// if an incoming edge have dead context, search
						// can be stopped
						break;
					}
				}
			}
		}
	}

	private void propagateWaitContext(Diagram diag) {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (DiagramNode node : diag.getNodes()) {
				// If event, function or split-connector
				if (node.getIncomingEdges().size() == 1) {
					if (context.get(node.getIncomingEdges().get(0)) == Context.WAIT) {
						for (DiagramEdge outEdge : node.getOutgoingEdges()) {
							// Only put new dead context if there is no token
							if (state.get(outEdge) == State.NO_TOKEN) {
								context.put(outEdge, Context.WAIT);
								changed = true;
							}
						}
					}
					// AND Join
				} else if (AND_CONNECTOR.equals(node.getType())) {
					if (filterByContext(node.getIncomingEdges(), Context.WAIT)
							.size() == node.getIncomingEdges().size()) {
						if (state.get(node.getOutgoingEdges().get(0)) == State.NO_TOKEN) {
							context.put(node.getOutgoingEdges().get(0),
									Context.WAIT);
							changed = true;
						}
					}
					// Xor/ Or Join
				} else if (XOR_CONNECTOR.equals(node.getType())
						|| OR_CONNECTOR.equals(node.getType())) {
					if (filterByContext(node.getIncomingEdges(), Context.WAIT)
							.size() > 0) {
						if (state.get(node.getOutgoingEdges().get(0)) == State.NO_TOKEN) {
							context.put(node.getOutgoingEdges().get(0),
									Context.WAIT);
							changed = true;
						}
					}
				}
			}
		}
	}

	private void propagateNegativeTokens(Diagram diag) {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (DiagramNode node : diag.getNodes()) {
				// if all input arcs hold negative tokens and if there is no
				// positive token on the output arc
				if (filterByState(node.getIncomingEdges(), State.NEG_TOKEN)
						.size() == node.getIncomingEdges().size()
						&& filterByState(node.getOutgoingEdges(),
								State.POS_TOKEN).size() == 0) {
					applyState(node.getIncomingEdges(), State.NO_TOKEN);
					applyState(node.getOutgoingEdges(), State.NEG_TOKEN);
					applyContext(node.getOutgoingEdges(), Context.DEAD);
					changed = true;
				}
			}
		}
	}

	/*
	 * This method applies firing rules without changing the marking: A list of
	 * possible nodes with new marking is returned.
	 */
	private LinkedList<NodeNewMarkingPair> propagatePositiveTokens(Diagram diag) {
		LinkedList<NodeNewMarkingPair> nodeNewMarkings = new LinkedList<NodeNewMarkingPair>();

		// collect nodes which can fire
		for (DiagramNode node : diag.getNodes()) {
			// Event, functions and split connectors
			if (node.getIncomingEdges().size() == 1
					&& state.get(node.getIncomingEdges().get(0)) == State.POS_TOKEN) {
				// (a), (b), (c)
				if (FUNCTION.equals(node.getType())
						|| EVENT.equals(node.getType())
						|| AND_CONNECTOR.equals(node.getType())) {
					NodeNewMarkingPair nodeNewMarking = new NodeNewMarkingPair(
							node, this.clone());

					nodeNewMarking.newMarking.applyContext(node
							.getIncomingEdges(), Context.DEAD);
					nodeNewMarking.newMarking.applyState(node
							.getIncomingEdges(), State.NO_TOKEN);

					nodeNewMarking.newMarking.applyContext(node
							.getOutgoingEdges(), Context.WAIT);
					nodeNewMarking.newMarking.applyState(node
							.getOutgoingEdges(), State.POS_TOKEN);

					nodeNewMarkings.add(nodeNewMarking);
				// (e)
				} else if (XOR_CONNECTOR.equals(node.getType())) {
					// Each of the outgoing edges can receive a token
					for (DiagramEdge edge : node.getOutgoingEdges()) {
						NodeNewMarkingPair nodeNewMarking = new NodeNewMarkingPair(
								node, this.clone());

						nodeNewMarking.newMarking.applyContext(node
								.getIncomingEdges(), Context.DEAD);
						nodeNewMarking.newMarking.applyState(node
								.getIncomingEdges(), State.NO_TOKEN);

						nodeNewMarking.newMarking.applyContext(node
								.getOutgoingEdges(), Context.DEAD);
						nodeNewMarking.newMarking.applyState(node
								.getOutgoingEdges(), State.NO_TOKEN);

						nodeNewMarking.newMarking.applyContext(edge,
								Context.WAIT);
						nodeNewMarking.newMarking.applyState(edge,
								State.POS_TOKEN);

						nodeNewMarkings.add(nodeNewMarking);
					}
				//(g)
				} else if (OR_CONNECTOR.equals(node.getType())){
					for(List<DiagramEdge> edges : (List<List<DiagramEdge>>)de.hpi.bpmn.analysis.Combination.findCombinations(node.getOutgoingEdges())){
						if(edges.size() == 0)
							continue;
						
						NodeNewMarkingPair nodeNewMarking = new NodeNewMarkingPair(
								node, this.clone());

						nodeNewMarking.newMarking.applyContext(node
								.getIncomingEdges(), Context.DEAD);
						nodeNewMarking.newMarking.applyState(node
								.getIncomingEdges(), State.NO_TOKEN);

						nodeNewMarking.newMarking.applyContext(node
								.getOutgoingEdges(), Context.DEAD);
						nodeNewMarking.newMarking.applyState(node
								.getOutgoingEdges(), State.NO_TOKEN);

						nodeNewMarking.newMarking.applyContext(edges,
								Context.WAIT);
						nodeNewMarking.newMarking.applyState(edges,
								State.POS_TOKEN);

						nodeNewMarkings.add(nodeNewMarking);
					}
				}
			// join connectors
			} else if (node.getOutgoingEdges().size() == 1){
				//(d)
				if(AND_CONNECTOR.equals(node.getType()) && filterByState(node.getIncomingEdges(), State.POS_TOKEN).size() == node.getIncomingEdges().size()){
					NodeNewMarkingPair nodeNewMarking = new NodeNewMarkingPair(
							node, this.clone());

					nodeNewMarking.newMarking.applyContext(node
							.getIncomingEdges(), Context.DEAD);
					nodeNewMarking.newMarking.applyState(node
							.getIncomingEdges(), State.NO_TOKEN);

					nodeNewMarking.newMarking.applyContext(node
							.getOutgoingEdges(), Context.WAIT);
					nodeNewMarking.newMarking.applyState(node
							.getOutgoingEdges(), State.POS_TOKEN);

					nodeNewMarkings.add(nodeNewMarking);
				} else if (XOR_CONNECTOR.equals(node.getType()) && filterByState(node.getIncomingEdges(), State.POS_TOKEN).size() == 1){
					NodeNewMarkingPair nodeNewMarking = new NodeNewMarkingPair(
							node, this.clone());

					nodeNewMarking.newMarking.applyContext(filterByState(node.getIncomingEdges(), State.NEG_TOKEN), Context.DEAD);
					nodeNewMarking.newMarking.applyState(node
							.getIncomingEdges(), State.NO_TOKEN);

					nodeNewMarking.newMarking.applyContext(node
							.getOutgoingEdges(), Context.WAIT);
					nodeNewMarking.newMarking.applyState(node
							.getOutgoingEdges(), State.POS_TOKEN);

					nodeNewMarkings.add(nodeNewMarking);
				//(h)
				} else if (OR_CONNECTOR.equals(node.getType()) && filterByState(node.getIncomingEdges(), State.POS_TOKEN).size() == 1){
					// TODO
				}
			}
		}

		return nodeNewMarkings;
	}

	private List<DiagramEdge> filterByContext(List<DiagramEdge> edges,
			Context type) {
		List<DiagramEdge> filtered = new LinkedList<DiagramEdge>();
		for (DiagramEdge edge : edges) {
			if (context.get(edge) == type) {
				filtered.add(edge);
			}
		}
		return filtered;
	}

	private List<DiagramEdge> filterByState(List<DiagramEdge> edges, State type) {
		List<DiagramEdge> filtered = new LinkedList<DiagramEdge>();
		for (DiagramEdge edge : edges) {
			if (state.get(edge) == type) {
				filtered.add(edge);
			}
		}
		return filtered;
	}

	private void applyState(List<DiagramEdge> edges, State type) {
		for (DiagramEdge edge : edges) {
			applyState(edge, type);
		}
	}

	private void applyState(DiagramEdge edge, State type) {
		state.put(edge, type);
	}

	private void applyContext(List<DiagramEdge> edges, Context type) {
		for (DiagramEdge edge : edges) {
			applyContext(edge, type);
		}
	}

	private void applyContext(DiagramEdge edge, Context type) {
		context.put(edge, type);
	}
}
