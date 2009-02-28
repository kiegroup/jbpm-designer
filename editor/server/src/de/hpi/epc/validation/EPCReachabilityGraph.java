package de.hpi.epc.validation;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import de.hpi.bpt.process.epc.FlowObject;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.IFlowObject;
import de.hpi.diagram.reachability.ReachabilityGraph;
import de.hpi.epc.Marking;
import de.hpi.epc.Marking.NodeNewMarkingPair;

/*
 * TODO title thesis?
 * Implementation as proposed by Jan Mendling, p. 90 
 */
public class EPCReachabilityGraph
		extends
		ReachabilityGraph<IEPC, IFlowObject, Marking> {

	public EPCReachabilityGraph(IEPC diag) {
		super(diag);
	}

	public boolean checkIfShouldBeAdded(Marking fromMarking, Marking toMarking,
			FlowObject node) {
		return toMarking.hasToken(diag);
	}

	// Calculates RG for all possible initial markings
	public void calculate() {
		// TODO this calculation should be made public in a graph algo class
		List<IFlowObject> startNodes = new LinkedList<IFlowObject>();
		for (FlowObject fo : (Collection<FlowObject>) diag.getFlowObjects()) {
			if (diag.getIncomingControlFlow(fo).size() == 0) {
				startNodes.add(fo);
			}
		}

		List<Marking> initialMarkings = new LinkedList<Marking>();
		for (List<IFlowObject> initialNodes : (List<List<IFlowObject>>) de.hpi.bpmn.analysis.Combination
				.findCombinations(startNodes)) {
			if (initialNodes.size() > 0)
				initialMarkings.add(Marking.getInitialMarking(diag,
						initialNodes));
		}
		calculate(initialMarkings);
	}

	public void calculate(List<Marking> initialMarkings) {
		clear();

		Stack<Marking> toBePropagated = new Stack<Marking>();
		System.out.println("InitialMarking: "
				+ initialMarkings.get(0).toString());
		toBePropagated.addAll(initialMarkings);

		Set<Marking> propagated = new HashSet<Marking>();

		while (toBePropagated.size() > 0) {
			Marking currentMarking = toBePropagated.pop();
			Marking oldMarking = currentMarking.clone();
			List<NodeNewMarkingPair> nodeNewMarkings = currentMarking.clone()
					.propagate(diag);
			propagated.add(oldMarking);
			for (NodeNewMarkingPair nodeNewMarking : nodeNewMarkings) {
				add(oldMarking, nodeNewMarking.newMarking, nodeNewMarking.node);

				// TODO why cannot be used propagate.contains?
				boolean contains = false;
				for (Marking m : propagated) {
					if (m.equals(nodeNewMarking.newMarking)) {
						contains = true;
					}
				}
				// if(!propagated.contains(nodeNewMarking.newMarking)){
				if (!contains) {
					toBePropagated.push(nodeNewMarking.newMarking);
				}
			}
		}
	}
}
