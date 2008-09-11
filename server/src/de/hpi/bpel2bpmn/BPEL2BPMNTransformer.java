package de.hpi.bpel2bpmn;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.mapping.ProcessMapping;
import de.hpi.bpel2bpmn.mapping.basic.EmptyMapping;
import de.hpi.bpel2bpmn.mapping.basic.ExitMapping;
import de.hpi.bpel2bpmn.mapping.basic.InvokeMapping;
import de.hpi.bpel2bpmn.mapping.basic.ReceiveMapping;
import de.hpi.bpel2bpmn.mapping.basic.ReplyMapping;
import de.hpi.bpel2bpmn.mapping.basic.RethrowMapping;
import de.hpi.bpel2bpmn.mapping.basic.ThrowMapping;
import de.hpi.bpel2bpmn.mapping.basic.WaitMapping;
import de.hpi.bpel2bpmn.mapping.structured.FlowMapping;
import de.hpi.bpel2bpmn.mapping.structured.OnAlarmMapping;
import de.hpi.bpel2bpmn.mapping.structured.OnMessageMapping;
import de.hpi.bpel2bpmn.mapping.structured.PickMapping;
import de.hpi.bpel2bpmn.mapping.structured.ScopeMapping;
import de.hpi.bpel2bpmn.mapping.structured.SequenceMapping;
import de.hpi.bpel2bpmn.mapping.structured.WhileMapping;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.SubProcess;

public class BPEL2BPMNTransformer {
	
	protected Document doc;
	protected BPMNFactory factory;
	
	protected Map<String,ElementMapping> mappingMap = new HashMap<String, ElementMapping>();
	
	public BPEL2BPMNTransformer(Document doc) {
		this.doc = doc;
		initMappingMap();
	}
	
	private void initMappingMap() {
		this.mappingMap.put("process", ProcessMapping.getInstance());

		this.mappingMap.put("invoke", InvokeMapping.getInstance());

		this.mappingMap.put("receive", ReceiveMapping.getInstance());

		this.mappingMap.put("reply", ReplyMapping.getInstance());
		
		this.mappingMap.put("wait", WaitMapping.getInstance());

		this.mappingMap.put("exit", ExitMapping.getInstance());

		this.mappingMap.put("empty", EmptyMapping.getInstance());

		this.mappingMap.put("throw", ThrowMapping.getInstance());

		this.mappingMap.put("rethrow", RethrowMapping.getInstance());

		this.mappingMap.put("sequence", SequenceMapping.getInstance());

		this.mappingMap.put("pick", PickMapping.getInstance());

		this.mappingMap.put("onmessage", OnMessageMapping.getInstance());

		this.mappingMap.put("onalarm", OnAlarmMapping.getInstance());

		this.mappingMap.put("while", WhileMapping.getInstance());
		
		this.mappingMap.put("flow", FlowMapping.getInstance());
		
		this.mappingMap.put("scope", ScopeMapping.getInstance());
	}

	public BPMNDiagram mapBPEL2BPMN() {
		// get the root node
		Node root = getRootNode(doc);
		
		// we really need a root node
		if (root == null)
			return null;

		// initialize
		this.factory = new BPMNFactory(); 
		MappingContext mappingContext = new MappingContext(this.factory);
		
		// start the mapping recursively from the root node
		mapNode(root, mappingContext);
		
		// do some postprocessing
		postProcessMappingResult(mappingContext);
				
		mappingContext.getDiagram().identifyProcesses();
		
		return mappingContext.getDiagram();
	}
	
	protected void mapNode(Node node, MappingContext mappingContext) {
		/*
		 * Ignore pure text, e.g. empty lines
		 */
		if (node instanceof Text)
			return;
		
		/*
		 * Recursively call this method for all childs of the current node
		 */
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			//System.out.println("map Node: " + child.getLocalName());
			mapNode(child, mappingContext);
		}
		
		/*
		 * Trigger all element mappings for the current node
		 */
		String nodeName = node.getLocalName().toLowerCase();
		if (this.mappingMap.containsKey(nodeName)) {
			this.mappingMap.get(nodeName).mapElement(node, mappingContext);
		}
		else {
			System.err.println("Did not find any mappings for node " + nodeName);
		}
	}
	
	/**
	 * Gets the root node of the document and checks whether this
	 * elements is really a "process".
	 * 
	 * @param doc The DOM.
	 * @return The root node.
	 */
	protected Node getRootNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equalsIgnoreCase("process"))
			return null;
		return node;
	}

	protected void setContainmentRelations(Node domNode, Container parent, MappingContext mappingContext) {
		/*
		 * Ignore pure text, e.g. empty lines
		 */
		if (domNode instanceof Text)
			return;

		Container nextContainer = parent;
		
		/*
		 * Set the parent for all BPMN elements that have been created for the 
		 * domNode to the parent container that was given as a parameter.
		 * 
		 * If the mapping of the domNode contains a subprocess, we use it as the new parent.
		 */
		for (de.hpi.bpmn.Node node : mappingContext.getMappingElements().get(domNode)) {
			if (node.getParent() == null)
				node.setParent(parent);
			if (node instanceof SubProcess) {
				nextContainer = (Container) node;
			}
		}
		
		/*
		 * Trigger setting of the containment recursively. 
		 */
		for (Node child = domNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeName().equalsIgnoreCase("faulthandlers") ||
				child.getNodeName().equalsIgnoreCase("compensationhandler")) {
				/*
				 * The elements that result from a mapping of FC-handlers are 
				 * not contained in the subprocess that corresponds to the BPEL scope.
				 * Therefore we trigger the setContainmentRelations procedure with the
				 * parent of the respective subprocess and not with the subprocess itself.
				 */
				setContainmentRelations(child,parent,mappingContext);
			}
			else {
				setContainmentRelations(child,nextContainer,mappingContext);
			}
		}
	}
	
	/**
	 * Postprocessing includes the following steps:
	 * removal of gateways with exactly one incoming and one outgoing flow
	 * 
	 * @param mappingContext The mapping context encapsulates the mapping result, i.e. the BPMN diagram.
	 */
	protected void postProcessMappingResult(MappingContext mappingContext) {
		/*
		 * Remove all gateways with one incoming and one outgoing flow.
		 * They might have been created because of control links. 
		 */
		Collection<de.hpi.bpmn.Node> gatewaysToRemove = new HashSet<de.hpi.bpmn.Node>();
		for (Node domNode : mappingContext.getMappingElements().keySet()) {
			for (de.hpi.bpmn.Node node : mappingContext.getMappingElements().get(domNode)) {
				if (node instanceof Gateway) {
					if (node.getIncomingEdges().size() == 1 && node.getOutgoingEdges().size() == 1) {
						Edge in = node.getIncomingEdges().get(0);
						Edge out = node.getOutgoingEdges().get(0);
						// target of first edge is set to target of second edge
						in.setTarget(out.getTarget());
						// remove the second edge
						mappingContext.getDiagram().getEdges().remove(out);
						gatewaysToRemove.add(node);
					}
				}
			}
			// remove the gateways from the diagram
			mappingContext.getDiagram().getChildNodes().removeAll(gatewaysToRemove);
			// remove the gateways from the mapping context
			mappingContext.getMappingElements().get(domNode).removeAll(gatewaysToRemove);
			gatewaysToRemove.clear();
		}
	}
}
