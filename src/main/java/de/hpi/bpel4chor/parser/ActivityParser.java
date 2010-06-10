package de.hpi.bpel4chor.parser;

import java.util.ArrayList;
import java.util.List;
import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.SubProcess;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.activities.AssignTask;
import de.hpi.bpel4chor.model.activities.BlockActivity;
import de.hpi.bpel4chor.model.activities.EmptyTask;
import de.hpi.bpel4chor.model.activities.EndEvent;
import de.hpi.bpel4chor.model.activities.Event;
import de.hpi.bpel4chor.model.activities.Gateway;
import de.hpi.bpel4chor.model.activities.Handler;
import de.hpi.bpel4chor.model.activities.IntermediateEvent;
import de.hpi.bpel4chor.model.activities.NoneTask;
import de.hpi.bpel4chor.model.activities.ReceiveTask;
import de.hpi.bpel4chor.model.activities.Scope;
import de.hpi.bpel4chor.model.activities.SendTask;
import de.hpi.bpel4chor.model.activities.ServiceTask;
import de.hpi.bpel4chor.model.activities.StartEvent;
import de.hpi.bpel4chor.model.activities.Task;
import de.hpi.bpel4chor.model.activities.ValidateTask;
import de.hpi.bpel4chor.model.supporting.CorrelationSet;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import de.hpi.bpel4chor.util.BPELUtil;
import de.hpi.bpel4chor.util.Output;
import de.hpi.bpel4chor.util.XMLUtil;

/**
 * This class parses the diagram activities from the xpdl4chor input.
 * Activities are tasks, events, gateways and sub-processes (scopes, handlers).
 * The parsed activities will be added to a specified diagram.
 * 
 * An activity parser instance can only be used for one diagram. 
 */
public class ActivityParser {
	
	private static final String NAME = "Name";
	private static final String SUPPRESS_JOIN_FAILURE = "SuppressJoinFailure";
	
	private static final String IMPLEMENTATION = "Implementation";
	private static final String TASK = "Task";
	private static final String ROUTE = "Route";
	private static final String BLOCK = "BlockActivity";
	private static final String EVENT = "Event";
	
	// task
	private static final String TASK_SERVICE = "TaskService";
	private static final String TASK_RECEIVE = "TaskReceive";
	private static final String TASK_SEND = "TaskSend";
	private static final String TASK_ASSIGN = "TaskAssign";
	private static final String TASK_EMPTY = "TaskEmpty";
	private static final String TASK_VALIDATE = "TaskValidate";
	private static final String TASK_NONE = "TaskNone";
	
	// activity attribtues
	private static final String OPAQUE_INPUT = "OpaqueInput";
	private static final String OPAQUE_OUTPUT = "OpaqueOutput";
	
	// receive
	private static final String FAULT_NAME = "FaultName";
	private static final String MESSAGE_EXCHANGE = "MessageExchange";
	private static final String INSTANTIATE = "Instantiate";
	
	// assign
	private static final String VALIDATE = "Validate";
	private static final String COPY = "Copy";
	
	// gateway
	private static final String TYPE_XOR = "XOR";
	
	private static final String SPLIT_XOREVENT = "XOREVENT";
	private static final String SPLIT_XORDATA = "XOR";
	
	private static final String GATEWAY_TYPE = "GatewayType";
	private static final String GATEWAY_INSTANTIATE = "Instantiate";
	private static final String TRANSITION_RESTRICTIONS = "TransitionRestrictions";
	private static final String TRANSITION_RESTRICTION = "TransitionRestriction";
	private static final String SPLIT = "Split";
	private static final String SPLIT_TYPE = "Type";
	
	private static final String TRANSITION_REFS = "TransitionRefs";
	
	// block activity
	private static final String SUB_PROCESS_ID = "ActivitySetId";
	private static final String TYPE_SCOPE = "Scope";
	private static final String TYPE_HANDLER = "Handler";
	private static final String ISOLATED = "Isolated";
	private static final String EXIT_ON_STANDARD_FAULT = "ExitOnStandardFault";
	
	// scope
	private static final String MESSAGE_EXCHANGES = "MessageExchanges";
	
	// handler
	private static final String TYPE = "HandlerType";
	
	// event
	private static final String START_EVENT = "StartEvent";
	private static final String INTERMEDIATE_EVENT = "IntermediateEvent";
	private static final String END_EVENT = "EndEvent";
	private static final String TRIGGER = "Trigger";
	private static final String TARGET = "Target";
	private static final String IS_TERMINATION = "IsTermination";
	
	private static final String TRIGGER_RESULT_MESSAGE = "TriggerResultMessage";
	private static final String TRIGGER_TIMER = "TriggerTimer";
	private static final String RESULT_ERROR = "ResultError";
	private static final String RESULT_COMPENSATION = "ResultCompensation";
	
	// FromParts and ToParts
	private static final String FROM_PARTS = "FromParts";
	private static final String TO_PARTS = "ToParts";
	
	// Correlations
	private static final String CORRELATIONS = "Correlations";
	
	private Diagram diagram = null;
	private Output output = null;
	
	// Loop
	private static final String LOOP = "Loop";
	
	/**
	 * Constructor. Initializes the activity parser. 
	 * 
	 * @param diagram The diagram, to store the information in.
	 * @param output  The Output to print the errors to. 
	 */
	public ActivityParser(Diagram diagram, Output output) {
		this.diagram = diagram;
		this.output = output;
	}
	
	/**
	 * Parses an activity object from the given activity node. 
	 * 
	 * If the node contains an implementation node or a task node, the result
	 * will be a Task object. If the node contains a route node, the result
	 * will be a Gateway object. If the node contains a block activity node,
	 * the result will be a BlockActivity object. If the node contains an event
	 * node, the result will be an Event object.
	 * 
	 * @param activityNode The activity node to be parsed.
	 * 
	 * @return The parsed Task, Gateway, BlockActivity or Event object.
	 */
	public Activity parseActivity(Node activityNode) {		
		Activity result = null;
		
		NodeList childs = activityNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(IMPLEMENTATION)) {
				result = parseImplementation(activityNode, childs.item(i));
				break;
			} else if (child.getLocalName().equals(TASK)) {
				result = parseTask(activityNode, childs.item(i));
				break;
			} else if (child.getLocalName().equals(ROUTE)) {
				result = parseGateway(activityNode, childs.item(i));
				break;
			} else if (child.getLocalName().equals(BLOCK)) {
				result = parseBlockActivity(activityNode, childs.item(i));
				break;
			}  else if (child.getLocalName().equals(EVENT)) {
				// event as last to set the target which is task or block activity
				result = parseEvent(activityNode, childs.item(i));
				break;
			}
		}
		return result;
	}
	
	/**
	 * Parses a StartEvent object from the given start event node. 
	 * It sets the trigger type and the trigger object of the start event.
	 * 
	 * @param start         The start event, to store the information to.
	 * @param eventTypeNode The start event node to be parsed. 
	 */
	private void parseStartEvent(StartEvent start, Node eventTypeNode) {
		Node triggerAttributeNode = eventTypeNode.getAttributes().getNamedItem(TRIGGER);
		if (triggerAttributeNode != null) {
			String triggerValue = triggerAttributeNode.getNodeValue();
			start.setTriggerType(triggerValue, this.output);
		}
		
		Node triggerNode = null;
		if (start.getTriggerType().equals(StartEvent.TRIGGER_MESSAGE)) {
			if ((start.getName() == null) || (start.getName().equals(""))) {
				this.output.addError(
						"The receiving message event " +
						"must define a name.", start.getId());
			}
			triggerNode = 
				XMLUtil.getChildWithName(eventTypeNode, TRIGGER_RESULT_MESSAGE);
		} else if (start.getTriggerType().equals(StartEvent.TRIGGER_TIMER)) {
			triggerNode = 
				XMLUtil.getChildWithName(eventTypeNode, TRIGGER_TIMER);
		} 
		start.setTrigger(TriggerParser.parseTrigger(triggerNode, this.output));
	}
	
	/**
	 * Parses a StartEvent object from the given activity node and a start
	 * event node. It creates a new start event and sets the common activity
	 * attributs of the event. Moreover, it sets the trigger type and the
	 * trigger object of the start event.
	 * 
	 * @param activityNode  The activity node to parse the common activity
	 *                      attributes from.
	 * @param eventTypeNode The start event node to be parsed.
	 * 
	 * @return The parsed StartEvent.
	 */
	private StartEvent parseStartEvent(Node activityNode, Node eventTypeNode) {
		StartEvent event = new StartEvent(this.output);
		parseActivity(event, activityNode);
		parseStartEvent(event, eventTypeNode);
		return event;
	}
	
	/**
	 * Parses the attributes from a given intermediate event node to an
	 * IntermediateEvent object.
	 * It sets the trigger type of the intermediate event. Furthermore, the
	 * target activity is determined, the event is attached to.
	 * 
	 * @param event         The intermediate event, to store the information to.
	 * @param eventTypeNode The intermediate event node to be parsed. 
	 */
	private void parseIntermediateEventAttributes(
			IntermediateEvent event, Node eventTypeNode) {
		NamedNodeMap attributes = eventTypeNode.getAttributes();
		String triggerType = null;
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(TRIGGER)) {
				triggerType = attribute.getNodeValue();
			} else if (attribute.getLocalName().equals(IS_TERMINATION)) {
				boolean terminationValue = 
					new Boolean(attribute.getNodeValue()).booleanValue();
				if (terminationValue) {
					triggerType = IntermediateEvent.TRIGGER_TERMINATION;
				}
			} 
		}
		if (triggerType != null) {
			event.setTriggerType(triggerType, this.output);
		}
		
		// parse target after trigger type has been parsed
		Node attribute = attributes.getNamedItem(TARGET);
		if (attribute != null) {
			String objectId = attribute.getNodeValue();
			Object object = this.diagram.getObject(objectId);
			if ((object == null) || !(object instanceof Activity)) {
				this.output.addError("The target object with the Id " + 
						objectId + " defined for this event " + 
						" does not exists in the diagram.", event.getId() );
			} else {
				String trigger = event.getTriggerType();
				if (trigger.equals(IntermediateEvent.TRIGGER_COMPENSATION) || 
					(trigger.equals(IntermediateEvent.TRIGGER_ERROR) ||
					(trigger.equals(IntermediateEvent.TRIGGER_TERMINATION)))) {
					
					event.setTarget((Activity)object);
				}
			}
		}
	}
	
	/**
	 * Parses the child elements of a given intermediate event node to an
	 * IntermediateEvent object.
	 * It sets the trigger object of the intermediate event, depending on
	 * the trigger type defined in the event.
	 * 
	 * @param event         The intermediate event, to store the information to.
	 * @param eventTypeNode The intermediate event node to be parsed. 
	 */
	private void parseIntermediateEventElements(IntermediateEvent event, 
			Node eventTypeNode) {
		Node triggerNode = null;
		String trigger = event.getTriggerType();
		if (trigger.equals(IntermediateEvent.TRIGGER_MESSAGE)) {
			if ((event.getName() == null) || (event.getName().equals(""))) {
				this.output.addError(
						"This receiving message event " + 
						" must define a name.", event.getId());
			}
			triggerNode = 
				XMLUtil.getChildWithName(eventTypeNode, TRIGGER_RESULT_MESSAGE);
		} else if (trigger.equals(IntermediateEvent.TRIGGER_TIMER)) {
			triggerNode = 
				XMLUtil.getChildWithName(eventTypeNode, TRIGGER_TIMER);
		} else if (trigger.equals(IntermediateEvent.TRIGGER_ERROR)) {
			triggerNode = 
				XMLUtil.getChildWithName(eventTypeNode, RESULT_ERROR);
		} else if (trigger.equals(IntermediateEvent.TRIGGER_COMPENSATION)) {
			triggerNode = 
				XMLUtil.getChildWithName(eventTypeNode, RESULT_COMPENSATION);
		} 
		event.setTrigger(TriggerParser.parseTrigger(triggerNode, this.output));
	}
	
	/**
	 * Parses the attributes and child elements of an activity node and an
	 * intermediate event node to an IntermediateEvent object.
	 * It creates a new intermediate event and sets the common activity
	 * attributs of the event. Moreover, it sets the trigger type and the
	 * trigger object of the intermediate event and the target activity,
	 * the event is attached to.
	 * 
	 * @param activityNode  The activity node to parse the common activity
	 *                      attributes from.
	 * @param eventTypeNode The intermediate event node to be parsed. 
	 * 
	 * @return The parsed IntermediateEvent.
	 */
	private IntermediateEvent parseIntermediateEvent(Node activityNode, 
			Node eventTypeNode) {
		IntermediateEvent event = new IntermediateEvent(this.output);
		parseActivity(event, activityNode);
		parseIntermediateEventAttributes(event, eventTypeNode);
		parseIntermediateEventElements(event, eventTypeNode);
		return event;
	}
	
	/**
	 * Parses the attributes and child elements of an activity node and an end
	 * event node to an EndEvent object.
	 * It creates a new end event and sets the common activity
	 * attributs of the event.
	 * 
	 * @param activityNode  The activity node to parse the common activity
	 *                      attributes from.
	 * 
	 * @return The parsed EndEvent.
	 */
	private EndEvent parseEndEvent(Node activityNode) {
		EndEvent event = new EndEvent(this.output);
		parseActivity(event, activityNode);		
		return event;
	}
	
	/**
	 * Parses an Event object form a given activity node and a event node.
	 * It determines the type of the event and parses the event node depending
	 * on this type (see {@link #parseStartEvent(Node, Node)}, 
	 * {@link #parseIntermediateEvent(Node, Node)}, 
	 * {@link #parseEndEvent(Node)}).  
	 * 
	 * @param activityNode  The activity node to parse the common activity
	 *                      attributes from.
	 * @param eventNode     The event node to be parsed. 
	 * 
	 * @return The parsed EndEvent.
	 */
	private Event parseEvent(Node activityNode, Node eventNode) {
		Event result = null;
		NodeList childs = eventNode.getChildNodes();
		Node eventTypeNode = XMLUtil.getFirstElement(childs);
		if (eventTypeNode != null) {
			if (eventTypeNode.getLocalName().equals(START_EVENT)) {
				result = parseStartEvent(activityNode, eventTypeNode);
			} else if (eventTypeNode.getLocalName().equals(INTERMEDIATE_EVENT)) {
				result = parseIntermediateEvent(activityNode, eventTypeNode);
			} else if (eventTypeNode.getLocalName().equals(END_EVENT)) {
				result = parseEndEvent(activityNode);
			}
		} else {
			this.output.addParseError("An event has no event type defined.", eventNode);
		}
		return result;
	}
	
	/**
	 * Parses a message exchanges node and adds the information to a Scope object.
	 *  
	 * @param scope                The Scope object to add the information to.
	 * @param messageExchangesNode The message exchanges node to parse.
	 */
	private void parseMessageExchanges(Scope scope, Node messageExchangesNode) {
		NodeList messageExchangeNodes = messageExchangesNode.getChildNodes();
		for (int i = 0; i < messageExchangeNodes.getLength(); i++) {
			Node messageExchangeNode = messageExchangeNodes.item(i);
			if ((messageExchangeNode.getLocalName() != null) && 
					messageExchangeNode.getLocalName().equals(MESSAGE_EXCHANGE)) {
				String messageExchange = 
					XMLUtil.getNodeValue(messageExchangeNodes.item(i), this.output);
				scope.addMessageExchange(messageExchange);
			}
		}
	}
	
	/**
	 * Parses the child elements of a scope node and addes the information
	 * to a Scope object.
	 * 
	 * @param scope     The Scope object to add the information to.  
	 * @param scopeNode The scope node to be parsed.
	 */
	private void parseScopeElements(Scope scope, Node scopeNode) {
		NodeList childs = scopeNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if ((child.getLocalName() != null) && 
					child.getLocalName().equals(MESSAGE_EXCHANGES)) {
				parseMessageExchanges(scope, child);
				break;
			}
		}
	}
	
	/**
	 * Parses the data fields node of the given activity node to determine
	 * the correlation sets of a Scope. The parsed correlation sets will be
	 * added to the given Scope object.
	 * 
	 * @param scope        The Scope object to add the correlation sets to.
	 * @param activityNode The activity node to be parsed.
	 */
	private void parseDataFields(Scope scope, Node activityNode) {
		
		Node dataFieldsNode = XMLUtil.getChildWithName(
				activityNode, "DataFields");
		
		if (dataFieldsNode != null) { 
			NodeList dataFieldNodes = dataFieldsNode.getChildNodes();
			for (int i = 0; i < dataFieldNodes.getLength(); i++) {
				Node dataFieldNode = dataFieldNodes.item(i);
				// parse data field node
				if ((dataFieldNode.getLocalName() != null) && 
						dataFieldNode.getLocalName().equals("DataField")) {
					
					Node correlation = dataFieldNode.getAttributes().
						getNamedItem("Correlation");
					if ((correlation != null) && new Boolean(
							correlation.getNodeValue()).booleanValue()) {
						CorrelationSet correlationSet = 
							SupportingParser.parseCorrelationSet(
									dataFieldNode, this.output);
						scope.addCorrelationSet(correlationSet);
					}
				}
			}
		}
	}
	
	/**
	 * Parses the attributes and child elements of an activity node, a
	 * block activity node and a scope node to a Scope object.
	 * It creates a new Scope and sets the common activity
	 * attributes and the common block activity attributes of the scope. 
	 * Moreover, it sets the message exchanges and correlation sets for
	 * the scope.
	 * 
	 * @param activityNode      The activity node to parse the common activity
	 *                          attributes and correlation sets from.
	 * @param blockActivityNode The block activity node to parse the common
	 * 						    block activity attributes from.
	 * @param scopeNode         The scope node to parse the message exchanges from. 
	 * 
	 * @return The parsed Scope.
	 */
	private Scope parseScope(Node activityNode, Node blockActivityNode, Node scopeNode) {
		Scope scope = new Scope(this.output);
		parseBlockActivity(scope, activityNode, blockActivityNode);
		parseScopeElements(scope, scopeNode);
		parseDataFields(scope, activityNode);
		return scope;
	}
	
	/**
	 * Parses a handler node to determine the type of the handler it
	 * represents. It sets the determined handler type to the given Handler
	 * object. 
	 * 
	 * @param handler     The handler object to set the handler type for.
	 * @param handlerNode The handler node to parse the handler type from.
	 */
	private void parseHandler(Handler handler, Node handlerNode) {
		NamedNodeMap attributes = handlerNode.getAttributes();
		
		Node attribute = attributes.getNamedItem(TYPE);
		if (attribute != null) {		
			String typeValue = attribute.getNodeValue();
			if (typeValue.equals(Handler.TYPE_FAULT) ||
					typeValue.equals(Handler.TYPE_MESSAGE) ||
					typeValue.equals(Handler.TYPE_TIMER) ||
					typeValue.equals(Handler.TYPE_TERMINATION) ||
					typeValue.equals(Handler.TYPE_COMPENSATION)) {
				handler.setHandlerType(typeValue); 
			}
		}
	}
	
	/**
	 * Parses the attributes and child elements of an activity node, a
	 * block activity node and a handler node to a Handler object.
	 * It creates a new Handler and sets the common activity
	 * attributes and the common block activity attributes and the handler 
	 * type of the handler.
	 * 
	 * @param activityNode      The activity node to parse the common activity
	 *                          attributes from.
	 * @param blockActivityNode The block activity node to parse the common
	 * 						    block activity attributes from.
	 * @param handlerNode       The handler node to parse the handler type from. 
	 * 
	 * @return The parsed Handler.
	 */
	private Handler parseHandler(Node activityNode, Node blockActivityNode, Node handlerNode) {
		Handler handler = new Handler(this.output);
		parseBlockActivity(handler, activityNode, blockActivityNode);
		parseHandler(handler, handlerNode);
		return handler;
	}
	
	/**
	 * Parses the attributes of a block activity node and adds the information
	 * to the given BlockActivity object.
	 * 
	 * One of the attributes identifies the activity set the block activity
	 * references. If this activity set does not exist or if the block activity
	 * does not define an activity set, an error is added to the output.
	 * 
	 * @param blockAct          The BlockActivity object to add the information
	 *                          to.
	 * @param blockActivityNode The block activity node to be parsed.
	 */
	private void parseBlockActivityAttributes(
			BlockActivity blockAct, Node blockActivityNode) {
		
		NamedNodeMap attributes = blockActivityNode.getAttributes();
		
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(ISOLATED)) {
				blockAct.setIsolated(BPELUtil.booleanToYesNo(
						new Boolean(attribute.getNodeValue()).booleanValue()));
			} else if (attribute.getLocalName().equals(EXIT_ON_STANDARD_FAULT)) {
				blockAct.setExitOnStandardFault(BPELUtil.booleanToYesNo( 
					new Boolean(attribute.getNodeValue()).booleanValue()));
			} else if (attribute.getLocalName().equals(SUB_PROCESS_ID)) {
				String subProcessId = attribute.getNodeValue();
				Object object = this.diagram.getObject(subProcessId);
				if ((object == null) || !(object instanceof SubProcess)) {
					this.output.addError("The activity set with the id " + 
							subProcessId + " referenced by this block activity does not exist.", blockAct.getId());
				} else {
					SubProcess subProcess = (SubProcess)object;
					blockAct.setSubProcess(subProcess);
					subProcess.setBlockActivity(blockAct);
				}
			}
		}

		if (blockAct.getSubProcess() == null) {
			this.output.addError("There is a block activity without a defined " +
					"activity set.", blockAct.getId());
		}
	}
	
	/**
	 * Parses the attributes and child elements of an activity node and a
	 * block activity node to a BlockActivity object.
	 * 
	 * @param blockAct          The block activity to store the parsed
	 *                          information to.
	 * @param activityNode      The activity node to parse the common activity
	 *                          attributes from.
	 * @param blockActivityNode The block activity node to parse the common
	 * 						    block activity attributes from.
	 */
	private void parseBlockActivity(BlockActivity blockAct, Node activityNode, Node blockActivityNode) {
		parseActivity(blockAct, activityNode);
		parseBlockActivityAttributes(blockAct, blockActivityNode);
	}
	
	/**
	 * Parses the attributes and child elements of an activity node and a
	 * block activity node to a BlockActivity object. It determines the type
	 * of the block activity (scope or handler) and creates a Handler or Scope
	 * object depending on the determined type (see 
	 * {@link #parseScope(Node, Node, Node)}, 
	 * {@link #parseHandler(Node, Node, Node)}).
	 * 
	 * @param activityNode      The activity node to parse the common activity
	 *                          attributes from.
	 * @param blockActivityNode The block activity node to parse the common
	 * 						    block activity attributes from.
	 * 
	 * @return The parsed BlockActivity.
	 */
	private BlockActivity parseBlockActivity(Node activityNode, Node blockActivityNode) {	
		BlockActivity result = null;
		NodeList childs = blockActivityNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node blockActivityTypeNode = childs.item(i);
			if (blockActivityTypeNode.getLocalName() == null) {
				continue;
			}
			if (blockActivityTypeNode.getLocalName().equals(TYPE_SCOPE)) {
				result = parseScope(activityNode, blockActivityNode, blockActivityTypeNode);
				break;
			} else if (blockActivityTypeNode.getLocalName().equals(TYPE_HANDLER)) {
				result = parseHandler(activityNode, blockActivityNode, blockActivityTypeNode);
				break;
			}
		}
		if (result == null) {
			this.output.addParseError("The block activity must either " + 
					"define a scope or a handler element.", blockActivityNode);
		}
		return result;
	}
	
	/**
	 * Parses the transition references node of a gateway to determine
	 * the evaluation order of the outgoing transitions.
	 * 
	 * @param transitionRefsNode The transition references node to be parsed.
	 * @return A list with the ids of the defined transition references. The order
	 * of the ids defines the evaluation order.
	 */
	private List<String> parseTransitionRefs(Node transitionRefsNode) {
		if (transitionRefsNode == null) {
			return null;
		}
		List<String> result = new ArrayList<String>();
		NodeList transitionRefs = transitionRefsNode.getChildNodes();
		for (int i = 0; i < transitionRefs.getLength(); i++) {
			Node transitionRef = transitionRefs.item(i);
			if (transitionRef.getLocalName() == null) {
				continue;
			} else if (transitionRef.getLocalName().equals("TransitionRef")) {
				Node id = transitionRef.getAttributes().getNamedItem("Id");
				if (id != null) {
					result.add(id.getNodeValue());
				}
			}
		}
		return result;
	}
	
	/**
	 * Parses a Gateway object form an activity node and a route node. 
	 * 
	 * @param gateway      The gateway object to add the parsed information to.
	 * @param activityNode The activity node to be parsed.
	 * @param routeNode    The route node to be parsed.
	 */
	private void parseGateway(Gateway gateway, Node activityNode, Node routeNode) {
		Node typeNode = routeNode.getAttributes().getNamedItem(GATEWAY_TYPE);
		if (typeNode != null) {
			gateway.setGatewayType(typeNode.getNodeValue());
		}
		
		Node instantiateNode = 
			routeNode.getAttributes().getNamedItem(GATEWAY_INSTANTIATE);
		if (instantiateNode != null) {
			gateway.setCreateInstance(
					new Boolean(instantiateNode.getNodeValue()).booleanValue());
		}
		
		if (gateway.getGatewayType().equals(TYPE_XOR)) {
			Node transitionRestrictionsNode = 
				XMLUtil.getChildWithName(activityNode, TRANSITION_RESTRICTIONS);
			Node transitionRestrictionNode = XMLUtil.getChildWithName(
					transitionRestrictionsNode, TRANSITION_RESTRICTION);
			Node splitNode = 
				XMLUtil.getChildWithName(transitionRestrictionNode, SPLIT);
			if (splitNode != null) {
				Node transitionRefs = XMLUtil.getChildWithName(
						splitNode, TRANSITION_REFS);
				
				gateway.setEvaluationOrder(parseTransitionRefs(transitionRefs));
				
				Node splitTypeNode = 
					splitNode.getAttributes().getNamedItem(SPLIT_TYPE);
				if (splitTypeNode != null) {
					String splitTypeValue = splitTypeNode.getNodeValue();
					if (splitTypeValue.equals(SPLIT_XOREVENT) || 
							splitTypeValue.equals(SPLIT_XORDATA)) {
						gateway.setSplitType(splitTypeValue); 
					}
				}
			}
		}
	}
	
	/**
	 * Parses a Gateway object form an activity node and a route node. 
	 * It creates a new gateway object to store the parsed information to.
	 * 
	 * @param activityNode The activity node to be parsed.
	 * @param gatewayNode  The route node to be parsed.
	 * 
	 * @return The parsed Gateway object.
	 */
	private Gateway parseGateway(Node activityNode, Node gatewayNode) {
		Gateway gateway = new Gateway(this.output);
		parseActivity(gateway, activityNode);
		parseGateway(gateway, activityNode, gatewayNode);
		return gateway;
	}
	
	/**
	 * Parses the common activity attributes from an activity node. 
	 * 
	 * @param activity     The Activity object to add the parsed information to 
	 * @param activityNode The activity node to be parsed.
	 */
	private void parseActivityAttributes(Activity activity, Node activityNode) {
		NamedNodeMap attributes = activityNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			
			if (attribute.getLocalName().equals(NAME)) {
				activity.setName(BPELUtil.stringToNCName(attribute.getNodeValue()));
			} else if (attribute.getLocalName().equals(SUPPRESS_JOIN_FAILURE)) {
				activity.setSuppressJoinFailure(BPELUtil.booleanToYesNo(
					new Boolean(attribute.getNodeValue()).booleanValue()));
			}
		}
	}
	
	/**
	 * Parses an Activity object from an activity node.
	 * It determines the common attributes of graphical objects, the loop
	 * object and the common activity attributes.
	 * 
	 * @param activity     The Activity to add the information to. 
	 * @param activityNode The activity node to be parsed.
	 */
	private void parseActivity(Activity activity, Node activityNode) {
		GraphicalObjectParser.parse(activity, activityNode, this.output);
		Node loopNode = XMLUtil.getChildWithName(activityNode, LOOP);
		if (loopNode != null) {
			activity.setLoop(SupportingParser.parseLoop(loopNode, this.output));
		}
		parseActivityAttributes(activity, activityNode);
	}
	
	/**
	 * Parses the attributes of a service task node to a ServiceTask object.
	 * If the given service task has not a name defined, an error is added to
	 * the output.
	 * 
	 * @param service         The ServiceTask object to add the information to.
	 * @param serviceTaskNode The service task node to be parsed.
	 */
	private void parseServiceAttributes(ServiceTask service, Node serviceTaskNode) {
		if (service.getName() == null) {
			this.output.addError(
					"The invoke task " +
					"must define a name.", service.getId());
		}
		NamedNodeMap attributes = serviceTaskNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(OPAQUE_INPUT)) {
				service.setOpaqueInput(new Boolean(attribute.getNodeValue()).booleanValue());
			} else if (attribute.getLocalName().equals(OPAQUE_OUTPUT)) {
				service.setOpaqueOutput(new Boolean(attribute.getNodeValue()).booleanValue());
			}
		}
	}
	
	/**
	 * Parses the elements of a service task node to a ServiceTask object.
	 * 
	 * @param service         The ServiceTask object to add the information to.
	 * @param serviceTaskNode The service task node to be parsed.
	 */
	private void parseServiceElements(ServiceTask service, Node serviceTaskNode) {
		NodeList childs = serviceTaskNode.getChildNodes();
		
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(CORRELATIONS)) {
				service.setCorrelations(SupportingParser.parseCorrelations(child, this.output));
			} else if (child.getLocalName().equals(FROM_PARTS)) {
				service.setFromParts(SupportingParser.parseFromParts(child));
			} else if (child.getLocalName().equals(TO_PARTS)) {
				service.setToParts(SupportingParser.parseToParts(child));
			}
		}
	}
	
	/**
	 * Parses a ServiceTask from an activity node and a task node.
	 * It creates a new ServiceTask object and adds the common activity
	 * attributes, the service task attributes and the service task elements.
	 * 
	 * @param activityNode    The activity node to get the common activity
	 *                        attributes from.
	 * @param taskNode        The service task node to be parsed.
	 * 
	 * @return The parsed ServiceTask.
	 */
	private ServiceTask parseService(Node activityNode, Node taskNode) {
		ServiceTask result = new ServiceTask(this.output);
		parseActivity(result, activityNode);
		parseServiceAttributes(result, taskNode);
		parseServiceElements(result, taskNode);
		return result;
	}
	
	/**
	 * Parses the attributes of a receive task node to a ReceiveTask object.
	 * If the given receive task has not a name defined, an error is added to
	 * the output.
	 * 
	 * @param receive         The ReceiveTask object to add the information to.
	 * @param receiveTaskNode The receive task node to be parsed.
	 */
	private void parseReceiveAttributes(ReceiveTask receive, Node receiveTaskNode) {
		if ((receive.getName() == null) || (receive.getName().equals(""))) {
			this.output.addError(
					"The receive task " + 
					" must define a name.", receive.getId());
		}
		
		NamedNodeMap attributes = receiveTaskNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(OPAQUE_OUTPUT)) {
				receive.setOpaqueOutput(new Boolean(attribute.getNodeValue()).booleanValue());
			} else if (attribute.getLocalName().equals(MESSAGE_EXCHANGE)) {
				receive.setMessageExchange(attribute.getNodeValue());
			} else if (attribute.getLocalName().equals(INSTANTIATE)) {
				receive.setInstantiate(new Boolean(attribute.getNodeValue()).booleanValue());
			}
		}
	}
	
	/**
	 * Parses the elements of a receive task node to a ReceiveTask object.
	 * 
	 * @param receive         The ReceiveTask object to add the information to.
	 * @param receiveTaskNode The receive task node to be parsed.
	 */
	private void parseReceiveElements(ReceiveTask receive, Node receiveTaskNode) {
		NodeList childs = receiveTaskNode.getChildNodes();
		
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(CORRELATIONS)) {
				receive.setCorrelations(SupportingParser.parseCorrelations(child, this.output));
			} else if (child.getLocalName().equals(FROM_PARTS)) {
				receive.setFromParts(SupportingParser.parseFromParts(child));
			}
		}
	}
	
	/**
	 * Parses a ReceiveTask from an activity node and a task node.
	 * It creates a new ReceiveTask object and adds the common activity
	 * attributes, the receive task attributes and the receive task elements.
	 * 
	 * @param activityNode    The activity node to get the common activity
	 *                        attributes from.
	 * @param taskNode        The receive task node to be parsed.
	 * 
	 * @return The parsed ReceiveTask.
	 */
	private ReceiveTask parseReceive(Node activityNode, Node taskNode) {
		ReceiveTask result = new ReceiveTask(this.output);
		parseActivity(result, activityNode);
		parseReceiveAttributes(result, taskNode);
		parseReceiveElements(result, taskNode);
		return result;
	}
	
	/**
	 * Parses the attributes of a send task node to a SendTask object.
	 * If the given send task has no name defined, an error is added to
	 * the output.
	 * 
	 * @param send         The SendTask object to add the information to.
	 * @param sendTaskNode The send task node to be parsed.
	 */
	private void parseSendAttributes(SendTask send, Node sendTaskNode) {
		if ((send.getName() == null) || (send.getName().equals(""))) {
			this.output.addError(
					"The send task " +
					"must define a name.", send.getId());
		}
		NamedNodeMap attributes = sendTaskNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(OPAQUE_INPUT)) {
				send.setOpaqueInput(new Boolean(attribute.getNodeValue()).booleanValue());
			} else if (attribute.getLocalName().equals(FAULT_NAME)) {
				send.setFaultName(attribute.getNodeValue());
			} else if (attribute.getLocalName().equals(MESSAGE_EXCHANGE)) {
				send.setMessageExchange(attribute.getNodeValue());
			}
		}
	}
	
	/**
	 * Parses the elements of a sebd task node to a SendTask object.
	 * 
	 * @param send         The SendTask object to add the information to.
	 * @param sendTaskNode The send task node to be parsed.
	 */
	private void parseSendElements(SendTask send, Node sendTaskNode) {
		NodeList childs = sendTaskNode.getChildNodes();
		
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(CORRELATIONS)) {
				send.setCorrelations(SupportingParser.parseCorrelations(child, this.output));
			} else if (child.getLocalName().equals(TO_PARTS)) {
				send.setToParts(SupportingParser.parseToParts(child));
			}
		}
	}
	
	/**
	 * Parses a SendTask from an activity node and a task node.
	 * It creates a new SendTask object and adds the common activity
	 * attributes, the send task attributes and the send task elements.
	 * 
	 * @param activityNode    The activity node to get the common activity
	 *                        attributes from.
	 * @param taskNode        The send task node to be parsed.
	 * 
	 * @return The parsed SendTask.
	 */
	private SendTask parseSend(Node activityNode, Node taskNode) {
		SendTask result = new SendTask(this.output);
		parseActivity(result, activityNode);
		parseSendAttributes(result, taskNode);
		parseSendElements(result, taskNode);
		return result;
	}
	
	/**
	 * Parses the attributes of an assign task node to an AssignTask object.
	 * 
	 * @param assign         The AssignTask object to add the information to.
	 * @param assignTaskNode The assign task node to be parsed.
	 */
	private void parseAssignAttributes(AssignTask assign, Node assignTaskNode) {
		NamedNodeMap attributes = assignTaskNode.getAttributes();
		
		if (attributes.getNamedItem(VALIDATE) != null) {		
			assign.setValidate(BPELUtil.booleanToYesNo(
					new Boolean(attributes.getNamedItem(VALIDATE)
					.getNodeValue()).booleanValue()));
		}
	}
	
	/**
	 * Parses the child elements of an assign task node to an AssignTask object.
	 * 
	 * @param assign         The AssignTask object to add the information to.
	 * @param assignTaskNode The assign task node to be parsed.
	 */
	private void parseAssignElements(AssignTask assign, Node assignTaskNode) {
		NodeList childs = assignTaskNode.getChildNodes();
		
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if ((child.getLocalName() != null) && 
					child.getLocalName().equals(COPY)) {
				assign.addCopyElement(SupportingParser.parseCopy(child, this.output));
			}
		}
	}
	
	/**
	 * Parses an AssignTask from an activity node and an assign task node.
	 * It creates a new AssignTask object and adds the common activity
	 * attributes, the assign task attributes and the assign task elements.
	 * 
	 * @param activityNode    The activity node to get the common activity
	 *                        attributes from.
	 * @param taskNode        The assign task node to be parsed.
	 * 
	 * @return The parsed AssignTask.
	 */
	private AssignTask parseAssign(Node activityNode, Node taskNode) {
		AssignTask result = new AssignTask(this.output);
		parseActivity(result, activityNode);
		parseAssignAttributes(result, taskNode);
		parseAssignElements(result, taskNode);
		return result;
	}
	
	/**
	 * Parses an EmptyTask from an activity node and an empty task node.
	 * It creates a new EmptyTask object and adds the common activity
	 * attributes.
	 * 
	 * @param activityNode    The activity node to get the common activity
	 *                        attributes from.
	 * 
	 * @return The parsed EmptyTask.
	 */
	private EmptyTask parseEmpty(Node activityNode) {
		EmptyTask result = new EmptyTask(this.output);
		parseActivity(result, activityNode);
		return result;
	}
	
	/**
	 * Parses a ValidateTask from an activity node and a validate task node.
	 * It creates a new ValidateTask object and adds the common activity
	 * attributes.
	 * 
	 * @param activityNode    The activity node to get the common activity
	 *                        attributes from.
	 * 
	 * @return The parsed ValidateTask.
	 */
	private ValidateTask parseValidate(Node activityNode) {
		ValidateTask result = new ValidateTask(this.output);
		parseActivity(result, activityNode);
		return result;
	}
	
	/**
	 * Parses a non-typed Task from an activity node.
	 * It creates a new NoneTask object and adds the common activity
	 * attributes.
	 * 
	 * @param activityNode    The activity node to get the common activity
	 *                        attributes from.
	 * 
	 * @return The parsed NoneTask.
	 */
	private NoneTask parseNone(Node activityNode) {
		NoneTask result = new NoneTask(this.output);
		parseActivity(result, activityNode);
		return result;
	}
	
	/**
	 * Parses the implementation node of an activity node. This node
	 * contains the task node a Task object will be parsed from.
	 *  
	 * @param activityNode       The activity node that contains the
	 *                           implementation node.
	 * @param implementationNode The implementation node of the activity node.
	 * @return The Task object parsed from the task noded contained in the
	 * implementation node.
	 */
	private Task parseImplementation(Node activityNode, Node implementationNode) {
		NodeList childs = implementationNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(TASK)) {
				return parseTask(activityNode, child);
			}
		}
		return null;
	}
	
	/**
	 * Parses a task from an activity node and a task node.
	 * It determines the task type from the child element of the task node
	 * and creates a new Task object depending on the determined type. 
	 * 
	 * @param activityNode The activity node to be parsed.
	 * @param taskNode     The task node to be parsed.
	 * @return The parsed Task object.
	 */
	private Task parseTask(Node activityNode, Node taskNode) {
		Task result = null;
		NodeList childs = taskNode.getChildNodes();
		
		for (int i = 0; i < childs.getLength(); i++) {
			Node taskTypeNode = childs.item(i);
			if (taskTypeNode.getLocalName() == null) {
				continue;
			}
			if (taskTypeNode.getLocalName().equals(TASK_SERVICE)) {
				result = parseService(activityNode, taskTypeNode);
				break;
			} else if (taskTypeNode.getLocalName().equals(TASK_RECEIVE)) {
				result = parseReceive(activityNode, taskTypeNode);
				break;
			} else if (taskTypeNode.getLocalName().equals(TASK_SEND)) {
				result = parseSend(activityNode, taskTypeNode);
				break;
			} else if (taskTypeNode.getLocalName().equals(TASK_ASSIGN)) {
				result = parseAssign(activityNode, taskTypeNode);
				break;
			} else if (taskTypeNode.getLocalName().equals(TASK_EMPTY)) {
				result = parseEmpty(activityNode);
				break;
			} else if (taskTypeNode.getLocalName().equals(TASK_VALIDATE)) {
				result = parseValidate(activityNode);
				break;
			} else if (taskTypeNode.getLocalName().equals(TASK_NONE)) {
				result = parseNone(activityNode);
				break;
			}
		}
		return result;
	}
}
