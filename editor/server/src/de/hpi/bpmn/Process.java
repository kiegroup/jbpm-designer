package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hpi.bpmn2bpel.model.Container4BPEL;
import de.hpi.diagram.OryxUUID;

public class Process implements Container, Container4BPEL {
	
	protected List<Node> childNodes;

	public List<Node> getChildNodes() {
		if (childNodes == null)
			childNodes = new ArrayList();
		return childNodes;
	}

	public List<EndEvent> getEndEvents() {
		ArrayList<EndEvent> endEvents = new ArrayList<EndEvent>();
		for(Iterator<Node> it = getChildNodes().iterator(); it.hasNext();) {
			Node node = it.next();
			if(node instanceof EndEvent) {
				endEvents.add((EndEvent) node);
			}
		}
		
		return endEvents;
	}

	public List<StartEvent> getStartEvents() {
		ArrayList<StartEvent> StartEvents = new ArrayList<StartEvent>();
		for(Iterator<Node> it = getChildNodes().iterator(); it.hasNext();) {
			Node node = it.next();
			if(node instanceof StartEvent) {
				StartEvents.add((StartEvent) node);
			}
		}
		
		return StartEvents;
	}
	
	public List<Task> getTasks() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		for(Iterator<Node> it = this.getChildNodes().iterator(); it.hasNext();) {
			Node node = it.next();
			if (node instanceof Task) {
				tasks.add((Task) node);
			}
		}
		
		return tasks;
	}
	
	/**
	 * Retrieve all {@link Activity} contain by the process.
	 * 
	 * @return
	 * 		List of {@link Activity}
	 */
	public List<Activity> getActivities() {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		for(Node node : this.getChildNodes()) {
			if (node instanceof Activity) {
				activities.add((Activity) node);
			}
		}
		return activities;
	}
	
	/* (non-Javadoc)
	 * @see de.hpi.bpmn2bpel.model.Container4BPEL#addNode(de.hpi.bpmn.Node)
	 */
	public void addNode(Node node) {
		// Generate an id if it hasn't been set before
		if(node.getId() == null) node.setId(OryxUUID.generate());
		
		// Add to parent
		node.setParent(this);
		node.setProcess(this);
	}

	public List<SequenceFlow> getTransitions() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2bpel.model.Container4BPEL#removeNode(de.hpi.bpmn.Node)
	 */
	public void removeNode(Node node) {
		childNodes.remove(node);
//		
//		/* Disconnect node */
//		for(Edge edge : node.getIncomingEdges()) {
//			edge.getSource().getOutgoingEdges().remove(edge);
//			edge.setSource(null);
//			edge.setTarget(null);
//		}
//		
//		for(Edge edge : node.getOutgoingEdges()) {
//			edge.getTarget().getIncomingEdges().remove(edge);
//			edge.setSource(null);
//			edge.setTarget(null);
//		}
		
//		/* Remove connected edges */
//		for(Edge edge : node.getIncomingEdges()) {
//			edge.get
//		}
//		
//		for(Edge edge : node.getOutgoingEdges()) {
//			node.parent.removeEdge(edge);
//		}
		
		node.setParent(null);
		node.setProcess(null);
	}
	
	/**
	 * Connects two nodes with each other and returns the connecting sequence flow.
	 * 
	 * @param source
	 * 			The source node
	 * @param target
	 * 			The target node
	 * @return
	 * 			The connecting sequence flow
	 */
	public SequenceFlow connectNodes(Node source, Node target) {
		SequenceFlow seqFlow = new SequenceFlow();
		
		// Generate an id if it hasn't been set before
		seqFlow.setId(OryxUUID.generate());
		
		seqFlow.setSource(source);
		seqFlow.setTarget(target);
//		diagram.getEdges().add(seqFlow);
		return seqFlow;
	}

}
