package de.hpi.bpmn.rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.Activity;
import de.hpi.bpmn.Assignment;
import de.hpi.bpmn.Association;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.CollapsedSubprocess;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndCancelEvent;
import de.hpi.bpmn.EndCompensationEvent;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.EndLinkEvent;
import de.hpi.bpmn.EndMessageEvent;
import de.hpi.bpmn.EndMultipleEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.EndSignalEvent;
import de.hpi.bpmn.EndTerminateEvent;
import de.hpi.bpmn.Event;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.IntermediateCancelEvent;
import de.hpi.bpmn.IntermediateCompensationEvent;
import de.hpi.bpmn.IntermediateConditionalEvent;
import de.hpi.bpmn.IntermediateErrorEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.IntermediateLinkEvent;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.IntermediateMultipleEvent;
import de.hpi.bpmn.IntermediatePlainEvent;
import de.hpi.bpmn.IntermediateSignalEvent;
import de.hpi.bpmn.IntermediateTimerEvent;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.MessageFlow;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn.Property;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartConditionalEvent;
import de.hpi.bpmn.StartLinkEvent;
import de.hpi.bpmn.StartMessageEvent;
import de.hpi.bpmn.StartMultipleEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.StartSignalEvent;
import de.hpi.bpmn.StartTimerEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Task;
import de.hpi.bpmn.TextAnnotation;
import de.hpi.bpmn.UndirectedAssociation;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.XOREventBasedGateway;
import de.hpi.bpmn.Activity.LoopType;
import de.hpi.bpmn.Activity.MIFlowCondition;
import de.hpi.bpmn.Activity.MIOrdering;
import de.hpi.bpmn.Activity.TestTime;
import de.hpi.bpmn.exec.ExecDataObject;

/**
 * Copyright (c) 2008 Gero Decker
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/**
 * main method: loadBPMN()
 * 
 * remark: bidirectional associations are interpreted as two separate
 * associations
 * 
 * @author gero.decker
 * 
 */
public class BPMN11RDFImporter {

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

	public BPMN11RDFImporter(Document doc) {
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
				} else if (type.equals("CollapsedPool")) {
					addPool(node, c);
				} else if (type.equals("Lane")) {
					addLane(node, c);
				} else if (type.equals("Task")) {
					addTask(node, c);
				} else if (type.equals("Subprocess")) {
					addSubProcess(node, c);
				} else if (type.equals("CollapsedSubprocess")) {
					addCollapsedSubprocess(node, c);

				} else if (type.equals("StartEvent")) {
					addStartPlainEvent(node, c);
				} else if (type.equals("StartMessageEvent")) {
					addStartMessageEvent(node, c);
				} else if (type.equals("StartTimerEvent")) {
					addStartTimerEvent(node, c);
				} else if (type.equals("StartConditionalEvent")) {
					addStartConditionalEvent(node, c);
				} else if (type.equals("StartSignalEvent")) {
					addStartSignalEvent(node, c);
				} else if (type.equals("StartMultipleEvent")) {
					addStartMultipleEvent(node, c);

				} else if (type.equals("IntermediateEvent")) {
					addIntermediatePlainEvent(node, c);
				} else if (type.equals("IntermediateMessageEventCatching")) {
					addIntermediateMessageEvent(node, c, false);
				} else if (type.equals("IntermediateErrorEvent")) {
					addIntermediateErrorEvent(node, c);
				} else if (type.equals("IntermediateTimerEvent")) {
					addIntermediateTimerEvent(node, c);
				} else if (type.equals("IntermediateCancelEvent")) {
					addIntermediateCancelEvent(node, c);
				} else if (type.equals("IntermediateCompensationEventCatching")) {
					addIntermediateCompensationEvent(node, c, false);
				} else if (type.equals("IntermediateConditionalEvent")) {
					addIntermediateConditionalEvent(node, c);
				} else if (type.equals("IntermediateSignalEventCatching")) {
					addIntermediateSignalEvent(node, c, false);
				} else if (type.equals("IntermediateMultipleEventCatching")) {
					addIntermediateMultipleEvent(node, c, false);
				} else if (type.equals("IntermediateLinkEventCatching")) {
					addIntermediateLinkEvent(node, c, false);

				} else if (type.equals("IntermediateMessageEventThrowing")) {
					addIntermediateMessageEvent(node, c, true);
				} else if (type.equals("IntermediateCompensationEventThrowing")) {
					addIntermediateCompensationEvent(node, c, true);
				} else if (type.equals("IntermediateSignalEventThrowing")) {
					addIntermediateSignalEvent(node, c, true);
				} else if (type.equals("IntermediateMultipleEventThrowing")) {
					addIntermediateMultipleEvent(node, c, true);
				} else if (type.equals("IntermediateLinkEventThrowing")) {
					addIntermediateLinkEvent(node, c, true);

				} else if (type.equals("EndEvent")) {
					addEndPlainEvent(node, c);
				} else if (type.equals("EndMessageEvent")) {
					addEndMessageEvent(node, c);
				} else if (type.equals("EndErrorEvent")) {
					addEndErrorEvent(node, c);
				} else if (type.equals("EndCancelEvent")) {
					addEndCancelEvent(node, c);
				} else if (type.equals("EndCompensationEvent")) {
					addEndCompensationEvent(node, c);
				} else if (type.equals("EndSignalEvent")) {
					addEndSignalEvent(node, c);
				} else if (type.equals("EndMultipleEvent")) {
					addEndMultipleEvent(node, c);
				} else if (type.equals("EndTerminateEvent")) {
					addEndTerminateEvent(node, c);

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
//				/* Map additional DataObjects [BPMN2BPEL]*/
//				else if (type.startsWith("dataobject-")) {
//					addBPELDataObjecte(node, c);
//				} 
				/* Map special task used by BPMN2BPEL transformation */
				else if (type.startsWith("task-")) {
					addTask(node,c);
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
				} else if (attribute.equals("name")) {
					c.diagram.setTitle(getContent(n));
				} else if (attribute.equals("id")) {
					c.diagram.setId(getContent(n));
				//BPMN Extension for YAWL: the datatypedefinition attribute
				} else if (attribute.equals("datatypedefinition")) {
					c.diagram.setDataTypeDefinition(getContent(n));
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
				if (attribute.equals("poolid")) {
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
			de.hpi.bpmn.Node cNode = ((de.hpi.bpmn.Node) node);
			if (attribute.equals("parent")) {
				c.parentRelationships.put(cNode, getResourceId(getAttributeValue(n, "rdf:resource")));
			} else if (attribute.equals(label) || attribute.equals("name")) {
				if(cNode.getLabel() == null || cNode.getLabel().equals("")){
					cNode.setLabel(getContent(n));
				}
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
				// if (attribute.equals("poolid")) {
				// pool.setId(getContent(n));
				// } else {
				
				//BPMN Extension for YAWL: the resourcingType attribute
				if (attribute.equals("type")) {
					lane.setResourcingType(getContent(n));
				} else {
					handleStandardAttributes(attribute, n, lane, c, "Name");
				}
				// }
			}
		}

		if (lane.getId() == null)
			lane.setId(lane.getResourceId());
	}

	
	/**
	 * Please use this method for attributes that are part of class Activity 
	 * and not of its subtypes Task and Subprocess.
	 * 
	 * @param node must represent a subtype of activity
	 * @param c 
	 * @param activity must be a Task or a Subprocess
	 */
	protected void handleStandardActivityAttributes(Node node, ImportContext c, Activity activity) {
		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':') + 1);

				if (attribute.equals("looptype")) {
					String looptypeValue = getContent(n);
					if (looptypeValue != null && looptypeValue.equalsIgnoreCase("Standard")) {
						activity.setLoopType(LoopType.Standard);
					} else if (looptypeValue != null && looptypeValue.equalsIgnoreCase("MultiInstance")) {
						activity.setLoopType(LoopType.Multiinstance);
					}
				} else if (attribute.equals("loopcondition")) {
					String loopconditionValue = getContent(n);
					if (loopconditionValue != null) {
						activity.setLoopCondition(loopconditionValue);
					}
				} else if (attribute.equals("mitcondition")) {
					String miconditionValue = getContent(n);
					if (miconditionValue != null) {
						activity.setMiCondition(miconditionValue);
					}
				} else if (attribute.equals("testtime")) {
					String testtimeValue = getContent(n);
					if (testtimeValue != null && testtimeValue.equalsIgnoreCase("before")) {
						activity.setTestTime(TestTime.Before);
					} else if (testtimeValue != null && testtimeValue.equalsIgnoreCase("after")) {
						activity.setTestTime(TestTime.After);
					}
				} else if (attribute.equals("miordering")) {
					String miorderingValue = getContent(n);
					if (miorderingValue != null && miorderingValue.equalsIgnoreCase("sequential")) {
						activity.setMiOrdering(MIOrdering.Sequential);
					} else if (miorderingValue != null && miorderingValue.equalsIgnoreCase("parallel")) {
						activity.setMiOrdering(MIOrdering.Parallel);
					}
				} else if (attribute.equals("miflowcondition")) {
					String miflowconditionValue = getContent(n);
					if (miflowconditionValue != null && miflowconditionValue.equalsIgnoreCase("one")) {
						activity.setMiFlowCondition(MIFlowCondition.One);
					} else if (miflowconditionValue != null && miflowconditionValue.equalsIgnoreCase("all")) {
						activity.setMiFlowCondition(MIFlowCondition.All);
					} else if (miflowconditionValue != null && miflowconditionValue.equalsIgnoreCase("complex")) {
						activity.setMiFlowCondition(MIFlowCondition.Complex);
					} else if (miflowconditionValue != null && miflowconditionValue.equalsIgnoreCase("none")) {
						activity.setMiFlowCondition(MIFlowCondition.None);
					}
				//BPMN Extension for YAWL: the assignments attributes
				} else if (attribute.equals("assignments")) {
					String assignmentValue = getContent(n);
					ArrayList<Assignment> assignmentList = handleAssignments(assignmentValue);
					activity.getAssignments().addAll(assignmentList);
					
				//BPMN Extension for YAWL: the properties attributes
				} else if (attribute.equals("properties")) {
					String propertiesValue = getContent(n);
				
					//if the activity has properties
					if (propertiesValue != null) {
						String propertiesItems = propertiesValue.substring(propertiesValue.indexOf("[") + 1, propertiesValue.indexOf("]"));
					
						//split the properties string for each entry
						String[] propertiesAsText = propertiesItems.split("}");
					
						for(String seperateProperty : propertiesAsText){
							String name = "";
							String type = "";
							String value = "";
							Boolean correlation = false;
						
							if(seperateProperty.startsWith(",")){
								seperateProperty = seperateProperty.substring(1);
							}
							seperateProperty = seperateProperty.trim();
							seperateProperty = seperateProperty.substring(1);
							String[] propertyComponents = seperateProperty.split(",");
						
							for(String propertyComponent : propertyComponents){
								String propertyLine = propertyComponent.replace("\"", "");
								if(propertyLine.startsWith("name")){
									String rawValue = propertyLine.split(":")[1];
									name = rawValue.trim();
								}
								else if(propertyLine.startsWith("type")){
									String rawValue = propertyLine.split(":")[1];
									type = rawValue.trim();
								}
								else if(propertyLine.startsWith("value")){
									String rawValue = propertyLine.split(":")[1];
									value = rawValue.trim();
								}
								else if(propertyLine.startsWith("correlation")){
									String rawValue = propertyLine.split(":")[1];
									String correlationValue = rawValue.trim();
								
									if(correlationValue.equalsIgnoreCase("true")){
										correlation = true;
									}else{
										correlation = false;
									}
								}	
							}
							Property property = new Property(name, type, value, correlation);
							activity.getProperties().add(property);
						}
					}
				}
			}
		}
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

				// TODO: add further attributes...
				if (attribute.equals("bgcolor")){

					task.setColor(getContent(n));

					/* Set input message type attribute */
				} else if (attribute.equals("inmessagetype")) {
					task.setInMessageType(getContent(n));
					
					/* Set input message type attribute */
				} else if (attribute.equals("outmessagetype")) {
					task.setOutMessageType(getContent(n));	

					/* Set web method parameters values for properties property */
				} else if (attribute.equals("inputsets")) {
					JSONObject params = null;
					try {
						String content = getContent(n);
						/* Assertion: Content is a JSONObject of a ComplexType property */
						if(content != null) {
							JSONObject jsonAttribute = new JSONObject(getContent(n));
							params = jsonAttribute.getJSONArray("items").getJSONObject(0);
						}
					} catch (JSONException e) {
						/* Set empty inputsets property */
						task.setInputSets(null);
					}
					task.setInputSets(params);
				}

				/* Set namespace attribute */
				else if (attribute.equals("namespace")) {
					task.setNamespace(getContent(n));
				} 
				
				/* Set wsdlUrl attribute */
				else if (attribute.equals("wsdlurl")) {
					task.setWsdlUrl(getContent(n));
				} 
				
				/* Set form attribute */
				else if (attribute.equals("script")) {
					task.setForm(getContent(n));
				} 

				/* Set servicename attribute */
				else if (attribute.equals("servicename")) {
					task.setServiceName(getContent(n));

				}

				/* Set operation attribute */
				else if (attribute.equals("operation")) {
					task.setOperation(getContent(n));

				}

				/* Set port type attribute */
				else if (attribute.equals("porttype")) {
					task.setPortType(getContent(n));

				}
				
				
				/* BPMN Extension for YAWL
				 * Set yawl_offeredBy attribute */
				else if (attribute.equals("yawl_offeredby"))
					task.setYawl_offeredBy(getContent(n));
				
				/* BPMN Extension for YAWL
				 * Set yawl_allocatedBy attribute */
				else if (attribute.equals("yawl_allocatedby"))
					task.setYawl_allocatedBy(getContent(n));
				
				/* BPMN Extension for YAWL
				 * Set yawl_startedBy attribute */
				else if (attribute.equals("yawl_startedby"))
					task.setYawl_startedBy(getContent(n));
				
				else {
					handleStandardAttributes(attribute, n, task, c, "name");
				}


			}
		}
		
		handleStandardActivityAttributes(node, c, task);
		
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
				else if (attribute.equals("adhocordering")) {
					// standard case is sequential ordering
					String ordering = getContent(n);
					if (ordering.equals("Parallel")) {
						sp.setParallelOrdering(true);
					} else {
						sp.setParallelOrdering(false);
					}
				} else if (attribute.equals("adhoccompletioncondition")) {
					sp.setCompletionCondition(getContent(n));					
				} else {
					handleStandardAttributes(attribute, n, sp, c, "name");
				}
			}
		}
		
		handleStandardActivityAttributes(node, c, sp);
		
		if (sp.getId() == null)
			sp.setId(sp.getResourceId());
	}
	
	protected void addCollapsedSubprocess(Node node, ImportContext c) {
		CollapsedSubprocess sp = factory.createCollapsedSubprocess();
		sp.setResourceId(getResourceId(node));
		sp.setParent(c.diagram);
		c.objects.put(sp.getResourceId(), sp);

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':') + 1);

				// TODO: add further attributes...
				if (attribute.equals("bgcolor")){
					sp.setColor(getContent(n));
					
					/* Set subProcess reference attribute */
				} else if (attribute.equals("entry")) {
					sp.setSubprocessRef(getContent(n));
					
					/* Set input message type attribute */
				} else if (attribute.equals("inmessagetype")) {
					sp.setInMessageType(getContent(n));
					
					/* Set input message type attribute */
				} else if (attribute.equals("outmessagetype")) {
					sp.setOutMessageType(getContent(n));	

					/* Set web method parameters values for properties property */
				} else if (attribute.equals("inputsets")) {
					JSONObject params = null;
					try {
						String content = getContent(n);
						if(content != null) {
							JSONObject jsonAttribute = new JSONObject(getContent(n));
							params = jsonAttribute.getJSONArray("items").getJSONObject(0);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sp.setInputSets(params);
				}

				/* Set namespace attribute */
				else if (attribute.equals("namespace")) {
					sp.setNamespace(getContent(n));
				} 

				/* Set servicename attribute */
				else if (attribute.equals("servicename")) {
					sp.setServiceName(getContent(n));
				}

				/* Set operation attribute */
				else if (attribute.equals("operation")) {
					sp.setOperation(getContent(n));
				}

				/* Set port type attribute */
				else if (attribute.equals("porttype")) {
					sp.setPortType(getContent(n));
				}
				else {
					handleStandardAttributes(attribute, n, sp, c, "name");
				}


			}
		}
		
		handleStandardActivityAttributes(node, c, sp);
		
		if (sp.getId() == null)
			sp.setId(sp.getResourceId());	}

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
		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':') + 1);
				//BUG FIXED
				if (attribute.equals("timedate")) {
					String timeDateValue = getContent(n);
					if (timeDateValue != null)
						event.setTimeDate(timeDateValue);
				}
				if (attribute.equals("timecycle")) {
					String timeCycleValue = getContent(n);
					if (timeCycleValue != null)
						event.setTimeCycle(timeCycleValue);
				}
			}
		}
		handleEvent(node, event, c, "timeDate");
	}

	protected void addStartConditionalEvent(Node node, ImportContext c) {
		StartConditionalEvent event = factory.createStartConditionalEvent();
		handleEvent(node, event, c, "ruleName");
	}

	protected void addStartLinkEvent(Node node, ImportContext c) {
		StartLinkEvent event = factory.createStartLinkEvent();
		handleEvent(node, event, c, "linkId");
	}

	protected void addStartSignalEvent(Node node, ImportContext c) {
		StartSignalEvent event = factory.createStartSignalEvent();
		handleEvent(node, event, c, "triggers");
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
		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':') + 1);

				//BUG FIXED
				if (attribute.equals("timedate")) {
					String timeDateValue = getContent(n);
					if (timeDateValue != null)
						event.setTimeDate(timeDateValue);
				}
				if (attribute.equals("timecycle")) {
					String timeCycleValue = getContent(n);
					if (timeCycleValue != null)
						event.setTimeCycle(timeCycleValue);
				}
			}
		}
		handleEvent(node, event, c, "timeCycle");
	}

	protected void addIntermediateCancelEvent(Node node, ImportContext c) {
		IntermediateCancelEvent event = factory.createIntermediateCancelEvent();
		handleEvent(node, event, c, "target");
	}

	protected void addIntermediateCompensationEvent(Node node, ImportContext c, boolean isThrowing) {
		IntermediateCompensationEvent event = factory
				.createIntermediateCompensationEvent();
		event.setThrowing(isThrowing);
		handleEvent(node, event, c, "target");
	}

	protected void addIntermediateConditionalEvent(Node node, ImportContext c) {
		IntermediateConditionalEvent event = factory.createIntermediateConditionalEvent();
		handleEvent(node, event, c, "ruleName");
	}

	protected void addIntermediateLinkEvent(Node node, ImportContext c, boolean isThrowing) {
		IntermediateLinkEvent event = factory.createIntermediateLinkEvent();
		event.setThrowing(isThrowing);
		handleEvent(node, event, c, "linkId");
	}

	protected void addIntermediateSignalEvent(Node node, ImportContext c, boolean isThrowing) {
		IntermediateSignalEvent event = factory
				.createIntermediateSignalEvent();
		event.setThrowing(isThrowing);
		handleEvent(node, event, c, "triggers");
	}

	protected void addIntermediateMultipleEvent(Node node, ImportContext c, boolean isThrowing) {
		IntermediateMultipleEvent event = factory
				.createIntermediateMultipleEvent();
		event.setThrowing(isThrowing);
		handleEvent(node, event, c, "triggers");
	}

	protected void addIntermediateMessageEvent(Node node, ImportContext c, boolean isThrowing) {
		IntermediateMessageEvent event = factory
				.createIntermediateMessageEvent();
		event.setThrowing(isThrowing);
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

	protected void addEndSignalEvent(Node node, ImportContext c) {
		EndSignalEvent event = factory.createEndSignalEvent();
		handleEvent(node, event, c, "");
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
				
				//BPMN Extension for YAWL
				if (attribute.equals("assignments")) {
					String assignmentValue = getContent(n);
					ArrayList<Assignment> assignmentList = handleAssignments(assignmentValue);
					event.getAssignments().addAll(assignmentList);
				}

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
		
		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':') + 1);

				if (attribute.equals("instantiate")) {
					String instantiateValue = getContent(n);
					if (instantiateValue != null && instantiateValue.equalsIgnoreCase("true")) {
						gateway.setInstantiate(true);
					}
				}
			}
		}
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
				
				//BPMN Extension for YAWL
				if (attribute.equals("assignments")) {
					String assignmentValue = getContent(n);
					ArrayList<Assignment> assignmentList = handleAssignments(assignmentValue);
					gateway.getAssignments().addAll(assignmentList);
				}
				
				handleStandardAttributes(attribute, n, gateway, c,
						"Documentation");
			}
		}
		if (gateway.getId() == null)
			gateway.setId(gateway.getResourceId());
	}

	protected void addDataObject(Node node, ImportContext c) {
		ExecDataObject obj = factory.createExecDataObject();
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
					obj.setState(getContent(n)); }
				//BPMN Extension for YAWL: the datatype attribute
				else if (attribute.equals("datatype"))
					obj.setDataType(getContent(n));	
				//BPMN Extension for YAWL: the value attribute
				else if (attribute.equals("value"))
					obj.setValue(getContent(n));
				
				/* Set the target parameter of a copy task. Used by BPMN2BPEL */
				else if (attribute.equals("targetofcopy")) {
					obj.setTargetOfCopy(getContent(n));
				}
				
				else {
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
		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				if (attribute.equals("conditiontype")) {
					String ctype = getContent(n);
					if (ctype.equals("Expression"))
						flow.setConditionType(SequenceFlow.ConditionType.EXPRESSION);
					else if (ctype.equals("Default"))
						flow.setConditionType(SequenceFlow.ConditionType.DEFAULT);
				} else if (attribute.equals("conditionexpression")) {
					String expression = getContent(n);
					if (expression != null)
						flow.setConditionExpression(expression);
				} else if (attribute.equals("name")) {
					String name = getContent(n);
					if (name != null)
						flow.setName(name);
				}
			}
		}
		setConnections(flow, node, c);
	}

	protected void addMessageFlow(Node node, ImportContext c) {
		MessageFlow flow = factory.createMessageFlow();
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
		return id.substring(id.indexOf('#')+1);
	}

	
	// bugfix
	protected Node getChild(Node n, String name) {
		if (n == null)
			return null;
		Node result = null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().indexOf(name) == 0){
				return node;
			} else if (node.getNodeName().indexOf(name) > 0){ 
				// backup
				result = node;
			}
		return result;
	}

	protected Node getRootNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("rdf:RDF"))
			return null;
		return node;
	}
	
	/**
	 * parses assignment attribute string
	 * @param rawAssignment assignment attribute string
	 * @return list of Assignment objects
	 */
	protected ArrayList<Assignment> handleAssignments(String rawAssignment){
		ArrayList<Assignment> assignments = new ArrayList<Assignment>();
		
		if (rawAssignment != null) {
			String assignmentItems = rawAssignment.substring(rawAssignment.indexOf("[") + 1, rawAssignment.indexOf("]"));
			
			//split the properties string for each entry
			String[] assignmentsAsText = assignmentItems.split("}");
			
			for(String seperateAssignment : assignmentsAsText){
				String to = "";
				String from = "";
				Assignment.AssignTime assignTime = Assignment.AssignTime.Start;
				
				if(seperateAssignment.startsWith(",")){
					seperateAssignment = seperateAssignment.substring(1);
				}
				seperateAssignment = seperateAssignment.trim();
				seperateAssignment = seperateAssignment.substring(1);
				
				String[] assignmentComponents = seperateAssignment.split(", ");
				
				for(String assignmentComponent : assignmentComponents){
					String assignmentLine = assignmentComponent.replace("\"", "");
					if(assignmentLine.startsWith("to")){
						String rawValue = assignmentLine.split(":")[1];
						to = rawValue.trim();
					}
					else if(assignmentLine.startsWith("from")){
						String rawValue = assignmentLine.split(":")[1];
						from = rawValue.trim();
					}
					else if(assignmentLine.startsWith("assigntime")){
						String rawValue = assignmentLine.split(":")[1];
						String assignTimeValue = rawValue.trim();
						
						if(assignTimeValue.equalsIgnoreCase("Start"))
							assignTime = Assignment.AssignTime.Start;
						
						else if (assignTimeValue.equalsIgnoreCase("End"))
							assignTime = Assignment.AssignTime.End;
					}	
				}
				Assignment assignment = new Assignment(to, from, assignTime);
				assignments.add(assignment);
			}
		}
		return assignments;
	}

}
