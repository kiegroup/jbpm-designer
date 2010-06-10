package de.hpi.bpel2bpmn.mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.DiagramObject;

/**
 * This class captures all context information in order to
 * apply one of the specific mapping classes. In particular,
 * for every BPEL node, we have to track the BPMN
 * elements indicating the beginning and the end of the 
 * corresponding process part.
 * 
 * In addition, sources and targets for control links are 
 * stored in this class.
 * 
 * @author matthias.weidlich
 *
 */
public class MappingContext { 
	
	private BPMNDiagram diagram;
	
	private BPMNFactory factory;

	/*
	 * All BPMN elements that correspond to a certain BPEL node.
	 */
	private Map<Node, Set<de.hpi.bpmn.Node>> mappingElements;

	/*
	 * The BPMN element that indicates the beginning of the process part
	 * that corresponds to a certain BPEL node.
	 */
	private Map<Node, DiagramObject> mappingConnectionIn;
	
	/*
	 * The BPMN element that indicates the end of the process part
	 * that corresponds to a certain BPEL node.
	 */
	private Map<Node, DiagramObject> mappingConnectionOut;

	/*
	 * We might need to consider an expression, when connecting the
	 * end of the mapped process part. If so, the expression is stored
	 * in the following map.
	 */
	private Map<Node, String> mappingConnectionOutExpression;

	/*
	 * Store the BPMN element that represents the source of a certain link.
	 */
	private Map<String, DiagramObject> controlLinkSource;
	
	/*
	 * Store the transition condition for an outgoing link.
	 */
	private Map<String, String> controlLinkSourceTransitionConditions;
	
	/*
	 * Store the BPMN element that represents the target of a certain link.
	 */
	private Map<String, DiagramObject> controlLinkTarget;
	
	public MappingContext(BPMNFactory factory) {
		this.factory = factory;
		this.diagram = this.factory.createBPMNDiagram();
		this.mappingConnectionIn = new HashMap<Node, DiagramObject>();
		this.mappingConnectionOut = new HashMap<Node, DiagramObject>();
		this.mappingConnectionOutExpression = new HashMap<Node, String>();
		this.controlLinkSource = new HashMap<String, DiagramObject>();
		this.controlLinkTarget = new HashMap<String, DiagramObject>();
		this.mappingElements = new HashMap<Node, Set<de.hpi.bpmn.Node>>();
		this.controlLinkSourceTransitionConditions = new HashMap<String,String>();

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

	public Map<Node, Set<de.hpi.bpmn.Node>> getMappingElements() {
		return mappingElements;
	}
	
	public void addMappingElementToSet(Node domNode, de.hpi.bpmn.Node node) {
		if (this.mappingElements.containsKey(domNode)) {
			this.mappingElements.get(domNode).add(node);
		}
		else {
			Set<de.hpi.bpmn.Node> objects = new HashSet<de.hpi.bpmn.Node>();
			objects.add(node);
			this.mappingElements.put(domNode,objects);
		}
	}

	public Map<String, String> getControlLinkSourceTransitionConditions() {
		return controlLinkSourceTransitionConditions;
	}
}