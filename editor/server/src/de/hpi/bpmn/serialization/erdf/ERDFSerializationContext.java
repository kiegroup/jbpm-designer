package de.hpi.bpmn.serialization.erdf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SubProcess;

public class ERDFSerializationContext {
	
	private String diagramName;
	private String diagramId;
	
	private Map<DiagramObject, String> resourceIDs;
	
	public ERDFSerializationContext(BPMNDiagram bpmnDiagram) {
		this.diagramName = bpmnDiagram.getTitle();
		this.diagramId = bpmnDiagram.getId();
		this.resourceIDs = new HashMap<DiagramObject, String>();
		
		registerAllChildResources(bpmnDiagram.getChildNodes());
		
		for(Edge edge : bpmnDiagram.getEdges()) {
			registerResource(edge);
		}
		
	}

	private void registerAllChildResources(Collection<Node> nodes) {
		for(Node node : nodes) {
			registerResource(node);
			if (node instanceof SubProcess) {
				registerAllChildResources(((SubProcess)node).getChildNodes());
			}
		}
	}

	
	private void registerResource(DiagramObject d) {
		resourceIDs.put(d, d.getId());
	}

	public String getResourceIDForDiagramObject(DiagramObject diagramObject) {
		return this.resourceIDs.get(diagramObject);
	}
	
	public Collection<String> getResourceIDs() {
		return this.resourceIDs.values();
	}

	public String getDiagramName() {
		return this.diagramName;
	}
	
	public String getDiagramId() {
		return this.diagramId;
	}
	
}
