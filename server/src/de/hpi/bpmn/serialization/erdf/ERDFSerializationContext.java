package de.hpi.bpmn.serialization.erdf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Node;

public class ERDFSerializationContext {
	
	private String diagramName;
	
	private int resourceID;
	
	private Map<DiagramObject, Integer> resourceIDs;
	
	public ERDFSerializationContext(BPMNDiagram bpmnDiagram) {
		this.diagramName = bpmnDiagram.getTitle();
		this.resourceID = 0;
		this.resourceIDs = new HashMap<DiagramObject, Integer>();
		
		for(Node node : bpmnDiagram.getChildNodes()) {
			registerResource(node);
		}
		for(Edge edge : bpmnDiagram.getEdges()) {
			registerResource(edge);
		}
		
	}

	private void registerResource(DiagramObject d) {
		resourceIDs.put(d, this.resourceID);
		resourceID++;
	}

	public int getResourceIDForDiagramObject(DiagramObject diagramObject) {
		return this.resourceIDs.get(diagramObject);
	}
	
	public Collection<Integer> getResourceIDs() {
		return this.resourceIDs.values();
	}

	public String getDiagramName() {
		return this.diagramName;
	}
	
}
