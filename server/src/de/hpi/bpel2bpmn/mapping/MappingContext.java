package de.hpi.bpel2bpmn.mapping;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.DiagramObject;

public class MappingContext { 
	
	private BPMNDiagram diagram;
	
	private BPMNFactory factory;

	private Map<Node, DiagramObject> mappingConnectionIn;
	
	private Map<Node, DiagramObject> mappingConnectionOut;

	private Map<Node, String> mappingConnectionOutExpression;

	private Map<String, DiagramObject> controlLinkSource;
	
	private Map<String, DiagramObject> controlLinkTarget;
	
	public MappingContext(BPMNFactory factory) {
		this.factory = factory;
		this.diagram = this.factory.createBPMNDiagram();
		this.mappingConnectionIn = new HashMap<Node, DiagramObject>();
		this.mappingConnectionOut = new HashMap<Node, DiagramObject>();
		this.mappingConnectionOutExpression = new HashMap<Node, String>();
		this.controlLinkSource = new HashMap<String, DiagramObject>();
		this.controlLinkTarget = new HashMap<String, DiagramObject>();
	}
	
	public BPMNDiagram getDiagram() {
		return diagram;
	}

	public void setDiagram(BPMNDiagram diagram) {
		this.diagram = diagram;
	}

	public Map<Node, DiagramObject> getMappingConnectionIn() {
		return mappingConnectionIn;
	}

	public Map<Node, DiagramObject> getMappingConnectionOut() {
		return mappingConnectionOut;
	}

	public Map<Node, String> getMappingConnectionOutExpression() {
		return mappingConnectionOutExpression;
	}

	public BPMNFactory getFactory() {
		return factory;
	}

	public void setFactory(BPMNFactory factory) {
		this.factory = factory;
	}

	public Map<String, DiagramObject> getControlLinkSource() {
		return controlLinkSource;
	}

	public Map<String, DiagramObject> getControlLinkTarget() {
		return controlLinkTarget;
	}
	
	
}