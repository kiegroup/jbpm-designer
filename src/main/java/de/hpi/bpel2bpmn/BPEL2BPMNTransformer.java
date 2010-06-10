package de.hpi.bpel2bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.mapping.ProcessMapping;
import de.hpi.bpel2bpmn.mapping.basic.AssignMapping;
import de.hpi.bpel2bpmn.mapping.basic.CompensateMapping;
import de.hpi.bpel2bpmn.mapping.basic.CompensateScopeMapping;
import de.hpi.bpel2bpmn.mapping.basic.EmptyMapping;
import de.hpi.bpel2bpmn.mapping.basic.ExitMapping;
import de.hpi.bpel2bpmn.mapping.basic.InvokeMapping;
import de.hpi.bpel2bpmn.mapping.basic.OpaqueActivityMapping;
import de.hpi.bpel2bpmn.mapping.basic.ReceiveMapping;
import de.hpi.bpel2bpmn.mapping.basic.ReplyMapping;
import de.hpi.bpel2bpmn.mapping.basic.RethrowMapping;
import de.hpi.bpel2bpmn.mapping.basic.ThrowMapping;
import de.hpi.bpel2bpmn.mapping.basic.WaitMapping;
import de.hpi.bpel2bpmn.mapping.structured.FlowMapping;
import de.hpi.bpel2bpmn.mapping.structured.ForeachMapping;
import de.hpi.bpel2bpmn.mapping.structured.IfMapping;
import de.hpi.bpel2bpmn.mapping.structured.OnAlarmMapping;
import de.hpi.bpel2bpmn.mapping.structured.OnMessageMapping;
import de.hpi.bpel2bpmn.mapping.structured.PickMapping;
import de.hpi.bpel2bpmn.mapping.structured.ScopeMapping;
import de.hpi.bpel2bpmn.mapping.structured.SequenceMapping;
import de.hpi.bpel2bpmn.mapping.structured.WhileRepeatUntilMapping;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.IntermediateCancelEvent;
import de.hpi.bpmn.IntermediateCompensationEvent;
import de.hpi.bpmn.IntermediateErrorEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.IntermediateMultipleEvent;
import de.hpi.bpmn.IntermediatePlainEvent;
import de.hpi.bpmn.IntermediateSignalEvent;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.SubProcess;

public class BPEL2BPMNTransformer {
	
	protected Document doc;
	protected BPMNFactory factory;
	
	protected Map<String,ElementMapping> mappingMap = new HashMap<String, ElementMapping>();
	
	public BPEL2BPMNTransformer(Document doc) {
		this.doc = doc;
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
		
		// set the containment for all bpmn elements recursively
		setContainmentRelations(root,mappingContext.getDiagram(),mappingContext);
		
		mappingContext.getDiagram().identifyProcesses();
		
		return mappingContext.getDiagram();
	}
	
	/**
	 * Triggers the mapping for a certain node. This method is first called
	 * with the root node. Subsequently, all nodes are traversed recursively.
	 * Depending on the node type, a mapping class is instantiated and applied.
	 * 
	 * @param node
	 * @param mappingContext
	 */
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
		if (nodeName.equals("process")) {
			ProcessMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("invoke")) {
			InvokeMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("receive")) {
			ReceiveMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("reply")) {
			ReplyMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("wait")) {
			WaitMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("exit")) {
			ExitMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("empty")) {
			EmptyMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("throw")) {
			ThrowMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("rethrow")) {
			RethrowMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("compensate")) {
			CompensateMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("compensatescope")) {
			CompensateScopeMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("sequence")) {
			SequenceMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("pick")) {
			PickMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("onmessage")) {
			OnMessageMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("onalarm")) {
			OnAlarmMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("while") || nodeName.equals("repeatuntil")) {
			WhileRepeatUntilMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("flow")) {
			FlowMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("if")) {
			IfMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("foreach")) {
			ForeachMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("scope")) {
			ScopeMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("assign")) {
			AssignMapping.getInstance().mapElement(node, mappingContext);
		} else if (nodeName.equals("opaqueactivity")) {
			OpaqueActivityMapping.getInstance().mapElement(node, mappingContext);
		} else {
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
		if (mappingContext.getMappingElements().containsKey(domNode)) {
			for (de.hpi.bpmn.Node node : mappingContext.getMappingElements().get(domNode)) {
				if (node.getParent() == null)
					node.setParent(parent);
				if (node instanceof SubProcess) {
					nextContainer = (Container) node;
				}
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
	 * <li>removal of gateways with exactly one incoming and one outgoing flow</li>
	 * <li>transformation of intermediate events without outgoing flow into end events</li>
	 * 
	 * @param mappingContext The mapping context encapsulates the mapping result, i.e. the BPMN diagram.
	 */
	protected void postProcessMappingResult(MappingContext mappingContext) {
		
		// TODO: remove mapping of empty activity
		
		/*
		 * -1-
		 * Remove all gateways with one incoming and one outgoing flow.
		 * They might have been created because of control links. 
		 */
		/*
		 * -2-
		 * Create plain end event for gateways without outgoing flow
		 * They might have been created because of an if/pick/flow structure. 
		 */
		/*
		 * -3-
		 * Transform all intermediate events without outgoing flow into
		 * end events of the same type.
		 */
		Collection<de.hpi.bpmn.Node> gatewaysToRemove = new HashSet<de.hpi.bpmn.Node>();
		Collection<de.hpi.bpmn.Node> eventsToRemove = new HashSet<de.hpi.bpmn.Node>();
		Map<Node, de.hpi.bpmn.Node> addToMappingContext = new HashMap<Node, de.hpi.bpmn.Node>();
		for (Node domNode : mappingContext.getMappingElements().keySet()) {
			for (de.hpi.bpmn.Node node : mappingContext.getMappingElements().get(domNode)) {
				// -1-, -2-
				if (node instanceof Gateway) {
					// -1-
					if (node.getIncomingEdges().size() == 1 && node.getOutgoingEdges().size() == 1) {
						Edge in = node.getIncomingEdges().get(0);
						Edge out = node.getOutgoingEdges().get(0);
						// target of first edge is set to target of second edge
						in.setTarget(out.getTarget());
						// remove the second edge
						mappingContext.getDiagram().getEdges().remove(out);
						gatewaysToRemove.add(node);
					}
					// -2-
					if (node.getOutgoingEdges().size() == 0) {
						EndEvent event = mappingContext.getFactory().createEndPlainEvent();
						event.setParent(node.getParent());
						addToMappingContext.put(domNode, event);
						SequenceFlow sequenceFlow = mappingContext.getFactory().createSequenceFlow();
						sequenceFlow.setSource(node);
						sequenceFlow.setTarget(event);
						mappingContext.getDiagram().getEdges().add(sequenceFlow);
					}
				}
				
				// -3-
				// is the node an intermediate event without outgoing edges?
				if (node instanceof IntermediateEvent && node.getOutgoingSequenceFlows().size() == 0) {
					EndEvent event = null;
					if (node instanceof IntermediateErrorEvent) {
						event = mappingContext.getFactory().createEndErrorEvent();
						((EndErrorEvent)event).setErrorCode(((IntermediateErrorEvent)node).getErrorCode());
					} else if (node instanceof IntermediateCancelEvent) {
						event = mappingContext.getFactory().createEndCancelEvent();
					} else if (node instanceof IntermediateCompensationEvent) {
						if (((IntermediateCompensationEvent)node).isThrowing()) {
							event = mappingContext.getFactory().createEndCompensationEvent();
						}
					} else if (node instanceof IntermediateMessageEvent) {
						if (((IntermediateMessageEvent)node).isThrowing()) {
							event = mappingContext.getFactory().createEndMessageEvent();
						}
					} else if (node instanceof IntermediateMultipleEvent) {
						if (((IntermediateMultipleEvent)node).isThrowing()) {
							event = mappingContext.getFactory().createEndMultipleEvent();
						}
					} else if (node instanceof IntermediatePlainEvent) {
						event = mappingContext.getFactory().createEndPlainEvent();
					} else if (node instanceof IntermediateSignalEvent) {
						if (((IntermediateSignalEvent)node).isThrowing()) {
							event = mappingContext.getFactory().createEndSignalEvent();
						}				
					}
					if (event != null) {
						List<Edge> tmpList = new ArrayList<Edge>();
						for(Edge e : node.getIncomingEdges()) {
							tmpList.add(e);
						}
						for(Edge e : tmpList) {
							e.setTarget(event);
						}
						event.setLabel(node.getLabel());
						event.setParent(node.getParent());
						mappingContext.addMappingElementToSet(domNode,event);
						eventsToRemove.add(node);
					}
				}
			}
			// -1-, -2-
			// remove the gateways from the diagram
			mappingContext.getDiagram().getChildNodes().removeAll(gatewaysToRemove);
			// remove the gateways from the mapping context
			mappingContext.getMappingElements().get(domNode).removeAll(gatewaysToRemove);
			gatewaysToRemove.clear();
			
			// add elements that were created in addition
			for (Node n : addToMappingContext.keySet()){
				mappingContext.addMappingElementToSet(n,addToMappingContext.get(n));
			}
			addToMappingContext.clear();
			
			// -3-
			// remove the events from the diagram
			mappingContext.getDiagram().getChildNodes().removeAll(eventsToRemove);
			// remove the events from the mapping context
			mappingContext.getMappingElements().get(domNode).removeAll(eventsToRemove);
			eventsToRemove.clear();
		}
	}
}
