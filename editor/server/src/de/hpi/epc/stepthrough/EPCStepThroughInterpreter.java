package de.hpi.epc.stepthrough;

import java.util.LinkedList;
import java.util.List;

import de.hpi.diagram.Diagram;
import de.hpi.diagram.DiagramEdge;
import de.hpi.diagram.DiagramNode;
import de.hpi.diagram.stepthrough.IStepThroughInterpreter;
import de.hpi.epc.Marking;
import de.hpi.epc.Marking.NodeNewMarkingPair;
import de.hpi.petrinet.stepthrough.AutoSwitchLevel;

public class EPCStepThroughInterpreter implements IStepThroughInterpreter {

	LinkedList<NodeNewMarkingPair> nodeNewMarkings;
	// A list of nodes that have changed because an object has been fired
	List<DiagramNode> changedNodes;

	Diagram epcDiag;

	public EPCStepThroughInterpreter(Diagram epcDiag) {
		this.epcDiag = epcDiag;

		DiagramNode startNode = null;
		for (DiagramNode node : epcDiag.getNodes()) {
			if (node.getIncomingEdges().size() == 0) {
				startNode = node;
			}
		}

		Marking marking = new Marking();
		for (DiagramEdge edge : epcDiag.getEdges()) {
			marking.applyContext(edge, Marking.Context.WAIT);
			marking.applyState(edge, Marking.State.NEG_TOKEN);
		}
		// TODO what about several startNodes
		marking.applyState(startNode.getOutgoingEdges().get(0),
				Marking.State.POS_TOKEN);

		nodeNewMarkings = marking.propagate(epcDiag);

		changedNodes = getFireableNodes();
	}

	public void clearChangedObjs() {
		changedNodes.clear();
	}

	public boolean fireObject(String resourceId) {
		for (NodeNewMarkingPair nodeNewMarking : nodeNewMarkings) {
			// TODO why # needed?
			if (nodeNewMarking.node.getResourceId().equals("#" + resourceId)) {
				changedNodes.add(nodeNewMarking.node);
				nodeNewMarkings = nodeNewMarking.newMarking.propagate(epcDiag);
				changedNodes.addAll(getFireableNodes());
				return true;
			}
		}
		return false;
	}

	protected List<DiagramNode> getFireableNodes() {
		List<DiagramNode> list = new LinkedList<DiagramNode>();
		for (NodeNewMarkingPair nodeNewMarking : nodeNewMarkings) {
			list.add(nodeNewMarking.node);
		}
		return list;
	}

	public String getChangedObjsAsString() {
		StringBuilder sb = new StringBuilder(15 * changedNodes.size());
		List<DiagramNode> fireableObjects = getFireableNodes();

		// Start with the transitions
		for (DiagramNode node : changedNodes) {
			// Non-empty subprocesses should only be highlighted if the user has
			// to fire them
			sb.append(node.getResourceId());
			sb.append(",");
			// TODO times executed
			// sb.append(node.getTimesExecuted());
			sb.append("1");
			sb.append(",");
			if (fireableObjects.contains(node))
				sb.append("t;");
			else
				sb.append("f;");
		}
		// XOR Splits
		/*
		 * for(DiagramObject d : changedXORSplits) {
		 * sb.append(d.getResourceId()); // TimesExecuted is not calculated for
		 * these sb.append(",-1,f;"); }
		 */

		return sb.toString();
	}

	public void setAutoSwitchLevel(AutoSwitchLevel autoSwitchLevel) {
		// do nothing
	}

}
