package de.hpi.diagram.reachability;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.hpi.bpt.graph.abs.AbstractDirectedGraph;
import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;

public class ReachabilityGraph<Diagram, FlowObject, Marking>
		extends
		AbstractDirectedGraph<ReachabilityTransition<FlowObject, Marking>, ReachabilityNode<Marking>> {
	protected Diagram diag;
	protected DirectedGraphAlgorithms<ReachabilityTransition<FlowObject, Marking>, ReachabilityNode<Marking>> directedGraphAlgorithms;

	public ReachabilityGraph(Diagram diag) {
		this.diag = diag;
		directedGraphAlgorithms = new DirectedGraphAlgorithms<ReachabilityTransition<FlowObject, Marking>, ReachabilityNode<Marking>>();
	}

	// FIXME Very dirty!
	// Code just copied because of cast exception
	@Override
	public ReachabilityTransition<FlowObject, Marking> addEdge(
			ReachabilityNode<Marking> s, ReachabilityNode<Marking> t) {
		if (s == null || t == null)
			return null;
		Collection<ReachabilityTransition<FlowObject, Marking>> es = this
				.getEdgesWithSourceAndTarget(s, t);
		if (es.size() > 0) {
			Iterator<ReachabilityTransition<FlowObject, Marking>> i = es
					.iterator();
			while (i.hasNext()) {
				ReachabilityTransition<FlowObject, Marking> e = i.next();
				if (e.getVertices().size() == 2)
					return null;
			}
		}

		ReachabilityTransition<FlowObject, Marking> e = new ReachabilityTransition<FlowObject, Marking>(
				this, s, t);
		return e;
	}

	public void clear() {
		this.getVertices().clear();
	}
	
	/**
	 * Override to handle markings which should not be added by add method.
	 * Called just after add is called.
	 * @param fromMarking
	 * @param toMarking
	 * @param node
	 * @return true if given markings should be added
	 */
	public boolean checkIfShouldBeAdded(Marking fromMarking, Marking toMarking, FlowObject node){
		return true;
	}

	public void add(Marking fromMarking, Marking toMarking, FlowObject node) {
		if (!checkIfShouldBeAdded(fromMarking, toMarking, node))
			return;			

		ReachabilityNode<Marking> fromMarkingNode = findByMarking(fromMarking);
		if (fromMarkingNode == null) {
			fromMarkingNode = new ReachabilityNode<Marking>(fromMarking);
			addVertex(fromMarkingNode);
		}
		ReachabilityNode<Marking> toMarkingNode = findByMarking(toMarking);
		if (toMarkingNode == null) {
			toMarkingNode = new ReachabilityNode<Marking>(toMarking);
			addVertex(toMarkingNode);
		}

		// Only add new transition if there isn't already one
		if (this.getEdgesWithSourceAndTarget(fromMarkingNode, toMarkingNode)
				.size() == 0) {
			ReachabilityTransition<FlowObject, Marking> transition = this
					.addEdge(fromMarkingNode, toMarkingNode);
			transition.setFlowObject(node);
		}
	}

	/*
	 * public void doubledMarkings(){ for( Marking m1 : getMarkings() ){ for(
	 * Marking m2 : getMarkings() ){ if(m1 == m2) break; if(m1.equals(m2)){
	 * System.out.print("+"+m2.toString()); return; } } } }
	 */

	public boolean contains(Marking m) {
		return findByMarking(m) != null;
	}

	public List<Marking> getMarkings() {
		List<Marking> markings = new LinkedList<Marking>();
		for (ReachabilityNode<Marking> mNode : this.getVertices()) {
			markings.add(mNode.getMarking());
		}
		return markings;
	}

	public ReachabilityNode<Marking> findByMarking(Marking m) {
		for (ReachabilityNode<Marking> fo : this.getVertices()) {
			if (fo.getMarking().equals(m)) {
				return fo;
			}
		}
		return null;
	}

	public boolean isRoot(Marking m) {
		return this.getIncomingEdges(findByMarking(m)).size() == 0;
	}

	public boolean isLeaf(Marking m) {
		return this.getOutgoingEdges(findByMarking(m)).size() == 0;
	}

	/* TODO should this return all predecessors or only direct ones??? */
	// TODO expensive search!! Avoid Marking => MarkingNode => Marking
	public List<Marking> getPredecessors(Marking m) {
		List<Marking> list = new LinkedList<Marking>();
		for (ReachabilityNode<Marking> markingNode : getPredecessors(findByMarking(m))) {
			list.add(markingNode.getMarking());
		}
		return list;
	}

	/* TODO should this return all successors or only direct ones??? */
	// TODO expensive search!! Avoid Marking => MarkingNode => Marking
	public List<Marking> getSuccessors(Marking m) {
		List<Marking> list = new LinkedList<Marking>();
		for (ReachabilityNode<Marking> markingNode : getSuccessors(findByMarking(m))) {
			list.add(markingNode.getMarking());
		}
		return list;
	}

	public List<Marking> getRoots() {
		List<Marking> list = new LinkedList<Marking>();
		for (ReachabilityNode<Marking> node : directedGraphAlgorithms
				.getInputVertices(this)) {
			list.add(node.getMarking());
		}
		return list;
	}

	public List<Marking> getLeaves() {
		List<Marking> list = new LinkedList<Marking>();
		for (ReachabilityNode<Marking> node : directedGraphAlgorithms
				.getOutputVertices(this)) {
			list.add(node.getMarking());
		}
		return list;
	}
}
