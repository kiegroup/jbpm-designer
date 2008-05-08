package de.hpi.bpmn.rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.Activity;
import de.hpi.bpmn.Association;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.ConditionalFlow;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.DataObject;
import de.hpi.bpmn.DefaultFlow;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndCancelEvent;
import de.hpi.bpmn.EndCompensationEvent;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.EndLinkEvent;
import de.hpi.bpmn.EndMessageEvent;
import de.hpi.bpmn.EndMultipleEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.EndTerminateEvent;
import de.hpi.bpmn.Event;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.IntermediateCancelEvent;
import de.hpi.bpmn.IntermediateCompensationEvent;
import de.hpi.bpmn.IntermediateErrorEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.IntermediateLinkEvent;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.IntermediateMultipleEvent;
import de.hpi.bpmn.IntermediatePlainEvent;
import de.hpi.bpmn.IntermediateRuleEvent;
import de.hpi.bpmn.IntermediateTimerEvent;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.MessageFlow;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartLinkEvent;
import de.hpi.bpmn.StartMessageEvent;
import de.hpi.bpmn.StartMultipleEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.StartRuleEvent;
import de.hpi.bpmn.StartTimerEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Task;
import de.hpi.bpmn.TextAnnotation;
import de.hpi.bpmn.UndirectedAssociation;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.XOREventBasedGateway;

/**
 * main method: loadBPMN()
 * 
 * remark: bidirectional associations are interpreted as two separate
 * associations
 * 
 * @author gero.decker
 * 
 */
public class BPMNRDFImporter {

	protected Document doc;
	protected BPMNFactory factory;

	protected class ImportContext {
		BPMNDiagram diagram;
		Map<String, de.hpi.bpmn.DiagramObject> objects; // key = resource id,
		// value = diagram
		// object
		Map<String, de.hpi.bpmn.DiagramObject> connections; // key = to resource
		// id, value = from
		// node
		Map<de.hpi.bpmn.Node, String> parentRelationships; // key = child node,
		// value = parent
		// resource id
	}

	public BPMNRDFImporter(Document doc) {
		this.doc = doc;
	}

	public BPMNDiagram loadBPMN() {
		Node root = getRootNode(doc);
		if (root == null)
			return null;

		// TODO: find out the type of BPMN
		factory = new BPMNFactory(); // for the moment: assume plain BPMN

		ImportContext c = new ImportContext();
		c.diagram = factory.createBPMNDiagram();
		// Map map = new HashMap();
		c.objects = new HashMap(); // key = resource id, value = node
		c.connections = new HashMap(); // key = to resource id, value = from
		// node
		c.parentRelationships = new HashMap();

		List<Node> edges = new ArrayList();

		// handle nodes
		if (root.hasChildNodes()) {
			Node node = root.getFirstChild();
			while ((node = node.getNextSibling()) != null) {
				if (node instanceof Text)
					continue;

				String type = getType(node);
				if (type == null)
					continue;

				if (type.equals("BPMNDiagram")) {
					handleDiagram(node, c);
				} else if (type.equals("Pool")) {
					addPool(node, c);
				} else if (type.equals("Lane")) {
					addLane(node, c);
				} else if (type.equals("Task")) {
					addTask(node, c);
				} else if (type.equals("Subprocess")) {
					addSubProcess(node, c);

				} else if (type.equals("StartEvent")) {
					addStartPlainEvent(node, c);
				} else if (type.equals("StartMessageEvent")) {
					addStartMessageEvent(node, c);
				} else if (type.equals("StartTimerEvent")) {
					addStartTimerEvent(node, c);
				} else if (type.equals("StartRuleEvent")) {
					addStartRuleEvent(node, c);
				} else if (type.equals("StartLinkEvent")) {
					addStartLinkEvent(node, c);
				} else if (type.equals("StartMultipleEvent")) {
					addStartMultipleEvent(node, c);

				} else if (type.equals("IntermediateEvent")) {
					addIntermediatePlainEvent(node, c);
				} else if (type.equals("IntermediateMessageEvent")) {
					addIntermediateMessageEvent(node, c);
				} else if (type.equals("IntermediateErrorEvent")) {
					addIntermediateErrorEvent(node, c);
				} else if (type.equals("IntermediateTimerEvent")) {
					addIntermediateTimerEvent(node, c);
				} else if (type.equals("IntermediateCancelEvent")) {
					addIntermediateCancelEvent(node, c);
				} else if (type.equals("IntermediateCompensationEvent")) {
					addIntermediateCompensationEvent(node, c);
				} else if (type.equals("IntermediateRuleEvent")) {
					addIntermediateRuleEvent(node, c);
				} else if (type.equals("IntermediateLinkEvent")) {
					addIntermediateLinkEvent(node, c);
				} else if (type.equals("IntermediateMultipleEvent")) {
					addIntermediateMultipleEvent(node, c);

					// TODO: talk to Martin regarding other end events...
				} else if (type.equals("EndEvent")) {
					String result = getContent(getChild(node, "result"));
					if (result.equals("None")) {
						addEndPlainEvent(node, c);
					} else if (result.equals("Cancel")) {
						addEndCancelEvent(node, c);
					} else if (result.equals("Compensation")) {
						addEndCompensationEvent(node, c);
					} else if (result.equals("Message")) {
						addEndMessageEvent(node, c);
					}

				} else if (type.equals("Exclusive_Databased_Gateway")) {
					addXORDataBasedGateway(node, c);
				} else if (type.equals("Exclusive_Eventbased_Gateway")) {
					addXOREventBasedGateway(node, c);
				} else if (type.equals("AND_Gateway")) {
					addANDGateway(node, c);
				} else if (type.equals("Complex_Gateway")) {
					addComplexGateway(node, c);
				} else if (type.equals("OR_Gateway")) {
					addORGateway(node, c);

				} else if (type.equals("DataObject")) {
					addDataObject(node, c);
				} else if (type.equals("TextAnnotation")) {
					addTextAnnotation(node, c);

				} else if (type.equals("SequenceFlow")) {
					edges.add(node);
				} else if (type.equals("MessageFlow")) {
					edges.add(node);
				} else if (type.equals("DefaultFlow")) {
					edges.add(node);
				} else if (type.equals("ConditionalFlow")) {
					edges.add(node);
				} else if (type.equals("Association_Unidirectional")) {
					edges.add(node);
				} else if (type.equals("Association_Bidirectional")) {
					edges.add(node);
				} else if (type.equals("Association_Undirected")) {
					edges.add(node);
					// } else if (type.equals("Association_Unidirectional")) {
					// addAssociation_Unidirectional(diagram, node, map);
					// } else if (type.equals("Association_Undirected")) {
					// addAssociation_Undirected(diagram, node, map);
				}
			}
		}

		// handle edges (except undirected associations)
		for (Node edgeNode : edges) {
			String type = getType(edgeNode);
			if (type.equals("SequenceFlow")) {
				addSequenceFlow(edgeNode, c);
			} else if (type.equals("MessageFlow")) {
				addMessageFlow(edgeNode, c);
			} else if (type.equals("DefaultFlow")) {
				addDefaultFlow(edgeNode, c);
			} else if (type.equals("ConditionalFlow")) {
				addConditionalFlow(edgeNode, c);
			} else if (type.equals("Association_Unidirectional")) {
				addAssociation(edgeNode, c);
			} else if (type.equals("Association_Bidirectional")) {
				addBidirectionalAssociation(edgeNode, c);
			}
		}

		// handle undirected associations
		for (Node edgeNode : edges) {
			String type = getType(edgeNode);
			if (type.equals("Association_Undirected")) {
				addUndirectedAssociation(edgeNode, c);
			}
		}

		// handle intermediate events
		attachIntermediateEvents(c);

		// handle parent relationships
		setupParentRelationships(c);

		c.diagram.identifyProcesses();
		return c.diagram;
	}

	protected void attachIntermediateEvents(ImportContext c) {
		for (de.hpi.bpmn.Node node : c.diagram.getChildNodes()) {
			if (node instanceof IntermediateEvent) {
				de.hpi.bpmn.DiagramObject source = c.connections.get(node
						.getResourceId());
				if (source instanceof Activity)
					((IntermediateEvent) node).setActivity((Activity) source);
			}
		}
	}

	protected void setupParentRelationships(ImportContext c) {
		for (Entry<de.hpi.bpmn.Node, String> entry : c.parentRelationships
				.entrySet()) {
			de.hpi.bpmn.Node child = entry.getKey();
			de.hpi.bpmn.DiagramObject parent = c.objects.get(entry.getValue());
			if (parent instanceof Container)
				child.setParent((Container) parent);
		}
	}

	protected void handleDiagram(Node node, ImportContext c) {
		if (node.hasChildNodes()) {

			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				// TODO: add further attributes...
				if (attribute.equals("title")) {
					c.diagram.setTitle(getContent(n));
					// } else {
					// handleStandardAttributes(attribute, n, pool, c, "Name");
				}
			}
		}
	}

	protected void addPool(Node node, ImportContext c) {
		Pool pool = factory.createPool();
		pool.setResourceId(getResourceId(node));
		pool.setParent(c.diagram);
		c.objects.put(pool.getResourceId(), pool);

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				// TODO: add further attributes...
				if (attribute.equals("poolId")) {
					pool.setId(getContent(n));
				} else {
					handleStandardAttributes(attribute, n, pool, c, "Name");
				}
			}
		}
		if (pool.getId() == null)
			pool.setId(pool.getResourceId());
	}

	protected boolean handleStandardAttributes(String attribute, Node n,
			de.hpi.bpmn.DiagramObject node, ImportContext c, String label) {
		if (attribute.equals("id")) {
			node.setId(getContent(n));
		} else if (attribute.equals("outgoing")) {
			c.connections.put(
					getResourceId(getAttributeValue(n, "rdf:resource")), node);
		} else if (node instanceof de.hpi.bpmn.Node) {
			if (attribute.equals("parent")) {
				c.parentRelationships.put((de.hpi.bpmn.Node) node,
						getResourceId(getAttributeValue(n, "rdf:resource")));
			} else if (attribute.equals(label)) {
				((de.hpi.bpmn.Node) node).setLabel(getContent(n));
			}
		} else {
			return false;
		}
		return true;
	}

	protected void addLane(Node node, ImportContext c) {
		Lane lane = factory.createLane();
		lane.setResourceId(getResourceId(node));
		c.objects.put(lane.getResourceId(), lane);

		if (node.hasChildNodes()) {

			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				// TODO: add further attributes...
				// if (attribute.equals("poolId")) {
				// pool.setId(getContent(n));
				// } else {
				handleStandardAttributes(attribute, n, lane, c, "Name");
				// }
			}
		}

		if (lane.getId() == null)
			lane.setId(lane.getResourceId());
	}

	protected void addTask(Node node, ImportContext c) {
		Task task = factory.createTask();
		task.setResourceId(getResourceId(node));
		task.setParent(c.diagram);
		c.objects.put(task.getResourceId(), task);

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':') + 1);

				if (attribute.equals("isskippable")) {
					String adHocValue = getContent(n);
					if (adHocValue != null && adHocValue.equals("true")) {
						task.setSkippable(true);
					} else {
						task.setSkippable(false);
					}
				}
				
 				if (attribute.equals("rolename")) {
					String roleValue = getContent(n);
					if (roleValue != null) {
						task.setRolename(roleValue);
					}
					else {
						task.setRolename("Default");
					}
				}
				
				if (attribute.equals("rightinitprocess")) {
					String rightValue = getContent(n);
					if (rightValue != null && rightValue.equals("true")) {
						task.setRightInitProcess("true");
					}
					else {
						task.setRightInitProcess("false");
					}
				}

				if (attribute.equals("rightexecutetask")) {
					String rightValue = getContent(n);
					if (rightValue != null && rightValue.equals("true")) {
						task.setRightExecuteTask("true");
					}
					else {
						task.setRightExecuteTask("false");
					}
				}
				
				if (attribute.equals("rightskiptask")) {
					String rightValue = getContent(n);
					if (rightValue != null && rightValue.equals("true")) {
						task.setRightSkipTask("true");
					}
					else {
						task.setRightSkipTask("false");
					}
				}
				
				if (attribute.equals("rightdelegateTask")) {
					String rightValue = getContent(n);
					if (rightValue != null && rightValue.equals("true")) {
						task.setRightDelegateTask("true");
					}
					else {
						task.setRightDelegateTask("false");
					}
				}
				
				if (attribute.equals("form")) {
					String form = getContent(n);
					if (form != null && form.equals("true")) {
						task.setForm(form);
					} else {
						task.setForm(null);
					}
				}
				// TODO: add further attributes...
				// if (attribute.equals("poolId")) {
				// pool.setId(getContent(n));
				// } else {
				handleStandardAttributes(attribute, n, task, c, "name");
				// }
				

			}
		}
		if (task.getId() == null)
			task.setId(task.getResourceId());
	}

	protected void addSubProcess(Node node, ImportContext c) {
		SubProcess sp = factory.createSubProcess();
		sp.setResourceId(getResourceId(node));
		sp.setParent(c.diagram);
		c.objects.put(sp.getResourceId(), sp);

		if (node.hasChildNodes()) {

			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				// TODO: add further attributes...
				if (attribute.equals("isadhoc")) {
					String adHocValue = getContent(n);
					if (adHocValue != null && adHocValue.equals("true")) {
						sp.setAdhoc(true);
					} else {
						sp.setAdhoc(false);
					}
				}
				else if (attribute.equals("AdHocOrdering")) {
					// standard case is sequential ordering
					String ordering = getContent(n);
					if (ordering.equals("Parallel")) {
						sp.setParallelOrdering(true);
					} else {
						sp.setParallelOrdering(false);
					}
				} else if (attribute.equals("AdHocCompletionCondition")) {
					sp.setCompletionCondition(getContent(n));					
				} else {
					handleStandardAttributes(attribute, n, sp, c, "name");
				}
			}
		}
		if (sp.getId() == null)
			sp.setId(sp.getResourceId());
	}

	protected void addStartPlainEvent(Node node, ImportContext c) {
		StartPlainEvent event = factory.createStartPlainEvent();
		handleEvent(node, event, c, "documentation");
	}

	protected void addStartMessageEvent(Node node, ImportContext c) {
		StartMessageEvent event = factory.createStartMessageEvent();
		handleEvent(node, event, c, "message");
	}

	protected void addStartTimerEvent(Node node, ImportContext c) {
		StartTimerEvent event = factory.createStartTimerEvent();
		handleEvent(node, event, c, "timeDate");
	}

	protected void addStartRuleEvent(Node node, ImportContext c) {
		StartRuleEvent event = factory.createStartRuleEvent();
		handleEvent(node, event, c, "ruleName");
	}

	protected void addStartLinkEvent(Node node, ImportContext c) {
		StartLinkEvent event = factory.createStartLinkEvent();
		handleEvent(node, event, c, "linkId");
	}

	protected void addStartMultipleEvent(Node node, ImportContext c) {
		StartMultipleEvent event = factory.createStartMultipleEvent();
		handleEvent(node, event, c, "triggers");
	}

	protected void addIntermediatePlainEvent(Node node, ImportContext c) {
		IntermediatePlainEvent event = factory.createIntermediatePlainEvent();
		handleEvent(node, event, c, "documentation");
	}

	protected void addIntermediateTimerEvent(Node node, ImportContext c) {
		IntermediateTimerEvent event = factory.createIntermediateTimerEvent();
		handleEvent(node, event, c, "timeCycle");
	}

	protected void addIntermediateCancelEvent(Node node, ImportContext c) {
		IntermediateCancelEvent event = factory.createIntermediateCancelEvent();
		handleEvent(node, event, c, "target");
	}

	protected void addIntermediateCompensationEvent(Node node, ImportContext c) {
		IntermediateCompensationEvent event = factory
				.createIntermediateCompensationEvent();
		handleEvent(node, event, c, "target");
	}

	protected void addIntermediateRuleEvent(Node node, ImportContext c) {
		IntermediateRuleEvent event = factory.createIntermediateRuleEvent();
		handleEvent(node, event, c, "ruleName");
	}

	protected void addIntermediateLinkEvent(Node node, ImportContext c) {
		IntermediateLinkEvent event = factory.createIntermediateLinkEvent();
		handleEvent(node, event, c, "linkId");
	}

	protected void addIntermediateMultipleEvent(Node node, ImportContext c) {
		IntermediateMultipleEvent event = factory
				.createIntermediateMultipleEvent();
		handleEvent(node, event, c, "triggers");
	}

	protected void addIntermediateMessageEvent(Node node, ImportContext c) {
		IntermediateMessageEvent event = factory
				.createIntermediateMessageEvent();
		handleEvent(node, event, c, "message");
	}

	protected void addIntermediateErrorEvent(Node node, ImportContext c) {
		IntermediateErrorEvent event = factory.createIntermediateErrorEvent();
		handleEvent(node, event, c, "errorCode");
	}

	protected void addEndPlainEvent(Node node, ImportContext c) {
		EndPlainEvent event = factory.createEndPlainEvent();
		handleEvent(node, event, c, "documentation");
	}

	protected void addEndCancelEvent(Node node, ImportContext c) {
		EndCancelEvent event = factory.createEndCancelEvent();
		handleEvent(node, event, c, "documentation");
	}

	protected void addEndErrorEvent(Node node, ImportContext c) {
		EndErrorEvent event = factory.createEndErrorEvent();
		handleEvent(node, event, c, "errorCode");
	}

	protected void addEndCompensationEvent(Node node, ImportContext c) {
		EndCompensationEvent event = factory.createEndCompensationEvent();
		handleEvent(node, event, c, "activity");
	}

	protected void addEndMessageEvent(Node node, ImportContext c) {
		EndMessageEvent event = factory.createEndMessageEvent();
		handleEvent(node, event, c, "message");
	}

	protected void addEndTerminateEvent(Node node, ImportContext c) {
		EndTerminateEvent event = factory.createEndTerminateEvent();
		handleEvent(node, event, c, "documentation");
	}

	protected void addEndLinkEvent(Node node, ImportContext c) {
		EndLinkEvent event = factory.createEndLinkEvent();
		handleEvent(node, event, c, "linkId");
	}

	protected void addEndMultipleEvent(Node node, ImportContext c) {
		EndMultipleEvent event = factory.createEndMultipleEvent();
		handleEvent(node, event, c, "results");
	}

	protected void handleEvent(Node node, Event event, ImportContext c,
			String label) {
		event.setResourceId(getResourceId(node));
		event.setParent(c.diagram);
		c.objects.put(event.getResourceId(), event);

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				handleStandardAttributes(attribute, n, event, c, label);
			}
		}
		if (event.getId() == null)
			event.setId(event.getResourceId());
	}

	protected void addXORDataBasedGateway(Node node, ImportContext c) {
		XORDataBasedGateway gateway = factory.createXORDataBasedGateway();
		handleGateway(node, gateway, c);
	}

	protected void addXOREventBasedGateway(Node node, ImportContext c) {
		XOREventBasedGateway gateway = factory.createXOREventBasedGateway();
		handleGateway(node, gateway, c);
	}

	protected void addANDGateway(Node node, ImportContext c) {
		ANDGateway gateway = factory.createANDGateway();
		handleGateway(node, gateway, c);
	}

	protected void addComplexGateway(Node node, ImportContext c) {
		ComplexGateway gateway = factory.createComplexGateway();
		handleGateway(node, gateway, c);
	}

	protected void addORGateway(Node node, ImportContext c) {
		ORGateway gateway = factory.createORGateway();
		handleGateway(node, gateway, c);
	}

	protected void handleGateway(Node node, Gateway gateway, ImportContext c) {
		gateway.setResourceId(getResourceId(node));
		gateway.setParent(c.diagram);
		c.objects.put(gateway.getResourceId(), gateway);

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);
				handleStandardAttributes(attribute, n, gateway, c,
						"Documentation");
			}
		}
		if (gateway.getId() == null)
			gateway.setId(gateway.getResourceId());
	}

	protected void addDataObject(Node node, ImportContext c) {
		DataObject obj = factory.createDataObject();
		obj.setResourceId(getResourceId(node));
		c.diagram.getDataObjects().add(obj);
		c.objects.put(obj.getResourceId(), obj);

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				// TODO: add further attributes...
				if (attribute.equals("state")) {
					obj.setState(getContent(n));
				} else {
					handleStandardAttributes(attribute, n, obj, c, "name");
				}
			}
		}
		if (obj.getId() == null)
			obj.setId(obj.getResourceId());
	}

	protected void addTextAnnotation(Node node, ImportContext c) {
		TextAnnotation ta = factory.createTextAnnotation();
		ta.setResourceId(getResourceId(node));
		ta.setParent(c.diagram);
		c.objects.put(ta.getResourceId(), ta);

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				// TODO: add further attributes...
				// if (attribute.equals("state")) {
				// obj.setState(getContent(n));
				// } else {
				handleStandardAttributes(attribute, n, ta, c, "text");
				// }
			}
		}
		if (ta.getId() == null)
			ta.setId(ta.getResourceId());
	}

	protected void addSequenceFlow(Node node, ImportContext c) {
		SequenceFlow flow = factory.createSequenceFlow();
		c.diagram.getEdges().add(flow);
		setConnections(flow, node, c);
	}

	protected void addMessageFlow(Node node, ImportContext c) {
		MessageFlow flow = factory.createMessageFlow();
		c.diagram.getEdges().add(flow);
		setConnections(flow, node, c);
	}

	protected void addDefaultFlow(Node node, ImportContext c) {
		DefaultFlow flow = factory.createDefaultFlow();
		c.diagram.getEdges().add(flow);
		setConnections(flow, node, c);
	}

	protected void addConditionalFlow(Node node, ImportContext c) {
		ConditionalFlow flow = factory.createConditionalFlow();
		c.diagram.getEdges().add(flow);
		setConnections(flow, node, c);
	}

	protected void addAssociation(Node node, ImportContext c) {
		Association ass = factory.createAssociation();
		c.diagram.getEdges().add(ass);
		setConnections(ass, node, c);
	}

	// introduce 2 associations
	protected void addBidirectionalAssociation(Node node, ImportContext c) {
		Association ass = factory.createAssociation();
		c.diagram.getEdges().add(ass);
		setConnections(ass, node, c);

		Association ass2 = factory.createAssociation();
		c.diagram.getEdges().add(ass);
		ass2.setId(ass.getId());
		ass2.setResourceId(ass.getResourceId());
		ass2.setSource(ass.getTarget());
		ass2.setTarget(ass.getSource());
	}

	protected void addUndirectedAssociation(Node node, ImportContext c) {
		UndirectedAssociation ass = factory.createUndirectedAssociation();
		c.diagram.getEdges().add(ass);
		setConnections(ass, node, c);
	}

	protected void setConnections(Edge edge, Node node, ImportContext c) {
		edge.setResourceId(getResourceId(node));
		c.objects.put(edge.getResourceId(), edge);
		edge.setSource(c.connections.get(edge.getResourceId()));

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				if (attribute.equals("id")) {
					edge.setId(getContent(n));
				} else if (attribute.equals("outgoing")) {
					if (edge.getTarget() == null)
						edge.setTarget(c.objects
								.get(getResourceId(getAttributeValue(n,
										"rdf:resource"))));
					else
						c.connections.put(getResourceId(getAttributeValue(n,
								"rdf:resource")), edge);
				}
			}
		}
		if (edge.getId() == null)
			edge.setId(edge.getResourceId());
	}

	protected String getContent(Node node) {
		if (node != null && node.hasChildNodes())
			return node.getFirstChild().getNodeValue();
		return null;
	}

	private String getAttributeValue(Node node, String attribute) {
		Node item = node.getAttributes().getNamedItem(attribute);
		if (item != null)
			return item.getNodeValue();
		else
			return null;
	}

	protected String getType(Node node) {
		String type = getContent(getChild(node, "type"));
		if (type != null)
			return type.substring(type.indexOf('#') + 1);
		else
			return null;
	}

	protected String getResourceId(Node node) {
		Node item = node.getAttributes().getNamedItem("rdf:about");
		if (item != null)
			return getResourceId(item.getNodeValue());
		else
			return null;
	}

	protected String getResourceId(String id) {
		return id.substring(id.indexOf('#'));
	}

	protected Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().indexOf(name) >= 0) 
				return node;
		return null;
	}

	protected Node getRootNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("rdf:RDF"))
			return null;
		return node;
	}

}
