package de.hpi.bpmn.serialization;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SequenceFlow;

public class BPMNDiagramBuilder {
	/**
	 * Creates a new sequence flow with given source and target and adding it to diag
	 * TODO: this should distinguish between sequence flows, message flows and data flow!!!
	 * @param diag BPMN diagram the new edge should belong to
	 * @param source Source of the edge
	 * @param target Target of the edge
	 * @return SequenceFlow connected with source and target
	 */
	static public SequenceFlow connectNodes(BPMNDiagram diag, DiagramObject source, DiagramObject target){
		SequenceFlow edge = new SequenceFlow();
		edge.setResourceId(generateUUID());
		edge.setId(edge.getResourceId());
		edge.setSource(source);
		edge.setTarget(target);
		diag.getEdges().add(edge);
		return edge;
	}
	
	/**
	 * Adds node of type T to diagram
	 * @param container A container (BPMNDiagram, SubProcess) the new node should belong to
	 * @param <T>
	 * @return created node
	 */
	static public <T extends Node> T addNode(Container container, T node){
		if(node.getResourceId() == null)
			node.setResourceId(generateUUID());
		if(node.getId() == null)
			node.setId(node.getResourceId());
		if(!container.getChildNodes().contains(node))
			container.getChildNodes().add(node);
		return node;
	}
	
	static public String generateUUID(){
		return java.util.UUID.randomUUID().toString();
	}
}
