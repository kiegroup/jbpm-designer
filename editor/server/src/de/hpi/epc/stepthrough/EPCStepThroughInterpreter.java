package de.hpi.epc.stepthrough;

import java.util.LinkedList;
import java.util.List;

import de.hpi.diagram.Diagram;
import de.hpi.diagram.DiagramEdge;
import de.hpi.diagram.DiagramNode;
import de.hpi.diagram.DiagramObject;
import de.hpi.diagram.stepthrough.IStepThroughInterpreter;
import de.hpi.epc.Marking;
import de.hpi.epc.Marking.NodeNewMarkingPair;
import de.hpi.petrinet.stepthrough.AutoSwitchLevel;

public class EPCStepThroughInterpreter implements IStepThroughInterpreter {

	LinkedList<NodeNewMarkingPair> nodeNewMarkings;
	// A list of nodes that have changed because an object has been fired
	List<DiagramObject> changedObjects;

	Diagram epcDiag;

	public EPCStepThroughInterpreter(Diagram epcDiag) {
		this.epcDiag = epcDiag;

		Marking marking = new Marking();
		for (DiagramEdge edge : epcDiag.getEdges()) {
			marking.applyContext(edge, Marking.Context.WAIT);
			marking.applyState(edge, Marking.State.NEG_TOKEN);
		}
		
		for (DiagramNode node : epcDiag.getNodes()) {
			if (node.getIncomingEdges().size() == 0) {
				marking.applyState(node.getOutgoingEdges().get(0),
						Marking.State.POS_TOKEN);
			}
		}


		nodeNewMarkings = marking.propagate(epcDiag);

		changedObjects = new LinkedList<DiagramObject>();
		changedObjects.addAll(getFireableNodes());
	}

	public void clearChangedObjs() {
		changedObjects.clear();
	}

	public boolean fireObject(String resourceId) {
		// TODO why # needed?
		if(!resourceId.startsWith("#"))
			resourceId = "#" + resourceId;
		
		for (NodeNewMarkingPair nodeNewMarking : nodeNewMarkings) {
			DiagramNode node = nodeNewMarking.node;
			boolean fire = false; // only fire current marking, fire = true
			DiagramObject changedObject = node;
			
			if (node.getResourceId().equals(resourceId)) {
				fire = true;
			// If Xor Split is enabled, look for if current marking is for given edge
			} else if (Marking.XOR_CONNECTOR.equals(node.getType())) {
				for(DiagramEdge edge : node.getOutgoingEdges()){
					if(edge.getResourceId().equals(resourceId) &&
							nodeNewMarking.newMarking.hasToken(edge)){
						// edge and not xor split changed
						changedObject = edge;
						fire = true;
					}
				}
			}
			
			if(fire){
				fireMarking(nodeNewMarking.newMarking, changedObject);
				return true;
			}
		}
		
		return false;
	}
	
	/* Fire given marking. Normally, marking.node would be added to changedObjects,
	 * but by giving a change object directly, this behavior can be avoided. 
	 * This method performs automatic execution of Events, XOR-Joins and AND-Connectors,
	 * depending on auto swtich level
	 */
	//TODO implement auto switch level
	protected void fireMarking(Marking marking, DiagramObject changedObject){
		changedObjects.add(changedObject);
		nodeNewMarkings = marking.propagate(epcDiag);
		
		boolean changed = true;
		
		// Perform some automatic execution of AND connectors, XOR-joins and events
		while(changed && this.getFireableNodes().size() > 0){
			NodeNewMarkingPair markingPairToFire = null;
			for(NodeNewMarkingPair nodeNewMarking : nodeNewMarkings){
				DiagramNode node = nodeNewMarking.node;
				if(node.getType().equals(Marking.AND_CONNECTOR) ||
						node.getType().equals(Marking.EVENT) ||
						(node.getType().equals(Marking.XOR_CONNECTOR)) && node.getOutgoingEdges().size() == 1){
					markingPairToFire = nodeNewMarking;
					break; //leave for loop because markings have changed
				}
			}
			changed = (markingPairToFire != null);
			
			if(markingPairToFire != null){
				changedObjects.add(markingPairToFire.node);
				nodeNewMarkings = markingPairToFire.newMarking.propagate(epcDiag);
			}
		}
		
		changedObjects.addAll(getFireableNodes());
	}

	protected List<DiagramNode> getFireableNodes() {
		List<DiagramNode> list = new LinkedList<DiagramNode>();
		for (NodeNewMarkingPair nodeNewMarking : nodeNewMarkings) {
			list.add(nodeNewMarking.node);
		}
		return list;
	}

	public String getChangedObjsAsString() {
		StringBuilder sb = new StringBuilder(15 * changedObjects.size());
		List<DiagramObject> fireableObjects = new LinkedList<DiagramObject>();
		fireableObjects.addAll(getFireableNodes());

		// Start with the transitions
		for (DiagramObject object : changedObjects) {
			// TODO times executed: node.getTimesExecuted()
			// if xor split
			if(Marking.XOR_CONNECTOR.equals(object.getType()) &&
					((DiagramNode)object).getOutgoingEdges().size() > 1){
				for(DiagramEdge edge : ((DiagramNode)object).getOutgoingEdges()){
					sb.append(buildChangedObjsString(edge.getResourceId(), 1, true));
				}
			} else {
				sb.append(buildChangedObjsString(object.getResourceId(), 1, fireableObjects.contains(object)));
			}
			
			// For deactivating control flows coming from xor connectors
			//TODO this should be changed for OR Splits!!!!
			if(Marking.CONTROL_FLOW.equals(object.getType())) {
				DiagramEdge cf = (DiagramEdge)object;
				for(DiagramEdge edge : cf.getSource().getOutgoingEdges()){
					if(edge != cf){
						sb.append(buildChangedObjsString(edge.getResourceId(), 0, false));
					}
				}
			}
		}

		return sb.toString();
	}

	public void setAutoSwitchLevel(AutoSwitchLevel autoSwitchLevel) {
		// do nothing
	}
	
	//TODO this should be provided by superclass
	private String buildChangedObjsString(String resourceId, int timesExecuted, boolean fireable){
		return resourceId + "," + String.valueOf(timesExecuted) + "," + (fireable ? "t" : "f") + ";";
	}

}
