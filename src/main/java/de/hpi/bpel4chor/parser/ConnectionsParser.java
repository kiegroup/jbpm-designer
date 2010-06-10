package de.hpi.bpel4chor.parser;

import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.GraphicalObject;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.connections.Association;
import de.hpi.bpel4chor.model.connections.MessageFlow;
import de.hpi.bpel4chor.model.connections.Transition;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.hpi.bpel4chor.util.Output;
import de.hpi.bpel4chor.util.XMLUtil;

/**
 * This class parses the connecting objects from the xpdl4chor input.
 * Connecting objects can be associations, transitions (sequence flow) 
 * and message flow. The parsed conntectors will be added to a specified
 * diagram.
 * 
 * The connecting objects should be parsed after the activities and artifacts
 * of the diagram have been parsed.
 */
public class ConnectionsParser {
	
	private static final String SOURCE = "Source";
	private static final String TARGET = "Target";
	
	// association
	private static final String DIRECTION = "AssociationDirection";
	
	// message flow
	private static final String NAME = "Name";
	private static final String MESSAGE = "Message";
	
	// transition
	private static final String TRANSITION_SOURCE = "From";
	private static final String TRANSITION_TARGET = "To";
	private static final String CONDITION = "Condition";
	private static final String EXPRESSION = "Expression";
	private static final String CONDITION_TYPE = "Type";
	
	private Diagram diagram = null;
	private Output output = null;
	
	/**
	 * Constructor. Initializes the connections parser. 
	 * 
	 * @param diagram The diagram, to store the information in.
	 * @param output  The Output to print the errors to. 
	 */
	public ConnectionsParser(Diagram diagram, Output output) {
		this.diagram = diagram;
		this.output = output;
	}
	
	/**
	 * Parses the attributes of an association node and adds the information
	 * to a given association.
	 * 
	 * If the association defines a source or a target object
	 * that is not contained in the diagram, an error is added to the output. 
	 * 
	 * @param assoc           The association to store the information in.
	 * @param associationNode The association node to be parsed.
	 */
	private void parseAssociationAttributes(Association assoc, Node associationNode) {
		NamedNodeMap attributes = associationNode.getAttributes();
		
		if (attributes.getNamedItem(DIRECTION) != null) {
			assoc.setDirection(attributes.getNamedItem(DIRECTION).getNodeValue());
		}
		
		if (attributes.getNamedItem(SOURCE) == null) {
			this.output.addParseError("There is an association without " +
				"a defined source element.", associationNode);
		} else {
			String sourceId = attributes.getNamedItem(SOURCE).getNodeValue();
			Object object = this.diagram.getObject(sourceId);
			if ((object == null) || !(object instanceof GraphicalObject)) {
				this.output.addError("A source object with the Id " + 
						sourceId + " does not exist for this association ", assoc.getId());
			} else {
				assoc.setSource((GraphicalObject)object);
			}
		}
		
		if (attributes.getNamedItem(TARGET) == null) {
			this.output.addParseError("There is an association without " +
				"a defined target element.", associationNode);
		} else {
			String targetId = attributes.getNamedItem(TARGET).getNodeValue();
			Object object = this.diagram.getObject(targetId);
			if ((object == null) || !(object instanceof GraphicalObject)){
				this.output.addError("A target object with the Id " + 
						targetId + " does not exist for this association ", assoc.getId());
			} else {
				assoc.setTarget((GraphicalObject)object);
			}
		}
	}
	
	/**
	 * Parses an association from an association node.
	 * 
	 * Creates a new Association object and adds the parsed
	 * information to it.
	 * 
	 * @param associationNode The association node to be parsed.
	 * 
	 * @return The created and filled association object.
	 */
	public Association parseAssociation(Node associationNode) {
		Association assoc = new Association(this.output);
		GraphicalObjectParser.parse(assoc, associationNode, this.output);
		parseAssociationAttributes(assoc, associationNode);
		return assoc;
	}
	
	/**
	 * Parses the attributes of a message flow node and adds the information to 
	 * a given message flow.
	 * 
	 * If the message flow defines a source or a target object
	 * that is not contained in the diagram, an error is added to the output. 
	 * 
	 * @param flow            The message flow to store the information in.
	 * @param messageFlowNode The message flow node to be parsed.
	 */
	private void parseMessageFlowAttributes(MessageFlow flow, Node messageFlowNode) {
		NamedNodeMap attributes = messageFlowNode.getAttributes();
		
		Node attribute = attributes.getNamedItem(NAME);
		if (attribute != null) {
			flow.setName(attribute.getNodeValue());
		}
		
		if (attributes.getNamedItem(SOURCE) == null) {
			this.output.addError("This message " + 
			"does not have a defined source element.", flow.getId());
		} else {
			String sourceId = attributes.getNamedItem(SOURCE).getNodeValue();
			Object object = this.diagram.getObject(sourceId);
			if ((object == null) || !(object instanceof Activity)) {
				this.output.addError("A source object with the Id " + 
						sourceId + " does not exist for this message flow", flow.getId());
			} else {
				flow.setSource((Activity)object);
			}
		}
		
		if (attributes.getNamedItem(TARGET) == null) {
			this.output.addError("There is a message flow without " +
			"a defined target element.", flow.getId());
		} else {
			String targetId = attributes.getNamedItem(TARGET).getNodeValue();
			Object object = this.diagram.getObject(targetId);
			if ((object == null) || !(object instanceof Activity)) {
				this.output.addError("A target object with the Id " + 
						targetId + " does not exist for this association", flow.getId());
			} else {
				flow.setTarget((Activity)object);
			}
		}
	}
	
	/**
	 * Parses the child elements of a message flow node and adds the
	 * information to a given message flow.
	 * 
	 * If the message flow does not define a message with a certain name,
	 * an error is added to the output.
	 * 
	 * @param flow            The message flow object to store the information
	 *                        in.
	 * @param messageFlowNode The message flow node to be parsed.
	 */
	private void parseMessageFlowElements(MessageFlow flow, Node messageFlowNode) {
		Node messageNode = XMLUtil.getChildWithName(messageFlowNode, MESSAGE);
		if (messageNode != null) {
			Node messageNameNode = messageNode.getAttributes().getNamedItem(NAME);
			if (messageNameNode != null) {
				flow.setMessageName(messageNameNode.getNodeValue());
			}
		}
		if (flow.getMessageName() == null) {
			this.output.addError("This message flow " + 
					" must define a message name.", flow.getId());
		}
	}

	/**
	 * Parses a message flow from a message flow node.
	 * 
	 * Creates a new message flow object and adds the parsed
	 * information to it.
	 * 
	 * @param messageFlowNode The message flow node to be parsed.
	 * 
	 * @return The created and filled message flow object.
	 */
	public MessageFlow parseMessageFlow(Node messageFlowNode) {
		MessageFlow flow = new MessageFlow(this.output);
		GraphicalObjectParser.parse(flow, messageFlowNode, this.output);
		parseMessageFlowAttributes(flow, messageFlowNode);
		parseMessageFlowElements(flow, messageFlowNode);
		return flow;
	}
	
	/**
	 * Parses the attributes of transition node and adds the information
	 * to a given transition.
	 * 
	 * If the transition defines a source or a target object
	 * that is not contained in the diagram, an error is added to the output. 
	 * 
	 * @param trans          The transition to store the information in.
	 * @param transitionNode The transition node to be parsed.
	 */
	private void parseTransitionAttributes(Transition trans, Node transitionNode) {
		NamedNodeMap attributes = transitionNode.getAttributes();
		
		Node attribute = attributes.getNamedItem(NAME);
		if (attribute != null) {
			trans.setName(attribute.getNodeValue());
		}
		
		attribute = attributes.getNamedItem(TRANSITION_SOURCE);
		if (attribute == null) {
			this.output.addError("There is an transition without " +
				"a defined source element.", trans.getId());
		} else {
			String sourceId = attribute.getNodeValue();
			Object object = this.diagram.getObject(sourceId);
			if ((object == null) || !(object instanceof Activity)) {
				this.output.addError("A source activity with the Id " + 
					sourceId + " does not exist for this transition", trans.getId());
			} else {
				trans.setSource((Activity)object, this.output);
			}
		}
		
		attribute = attributes.getNamedItem(TRANSITION_TARGET);
		if (attribute == null) {
			this.output.addError("There is an transition without " +
				"a defined target element.", trans.getId());
		} else {
			String targetId = attribute.getNodeValue();
			Object object = this.diagram.getObject(targetId);
			if ((object == null) || !(object instanceof Activity)) {
				this.output.addError("A target activity with the Id " + 
					targetId + " does not exist for this transition", trans.getId());
			} else {
				trans.setTarget((Activity)object, this.output);
			}
		}
	}
	
	/**
	 * Parses the condition node of a transition and adds the
	 * condition to the defined transition.
	 * 
	 * @param trans         The transition to add the condition to.
	 * @param conditionNode The condition node to be parsed.
	 */
	private void parseCondition(Transition trans, Node conditionNode) {
		Node typeAttr = 
			conditionNode.getAttributes().getNamedItem(CONDITION_TYPE);
		if (typeAttr != null) {
			if (typeAttr.getNodeValue().equals(Transition.TYPE_OTHERWISE)) {
				trans.setConditionType(Transition.TYPE_OTHERWISE);
			} else if (typeAttr.getNodeValue().equals(Transition.TYPE_EXPRESSION)) {
				trans.setConditionType(Transition.TYPE_EXPRESSION);
			}
		}
		
		Node expressionNode = XMLUtil.getChildWithName(conditionNode, EXPRESSION);
		if (expressionNode != null) {
			trans.setConditionExpression(
					SupportingParser.parseExpression(expressionNode, this.output));
		}
	}
	
	/**
	 * Parses the child elements of a transition node and adds the parsed
	 * information to a transition object.
	 * 
	 * @param trans          The transition to store the information in.
	 * @param transitionNode The transition node to be parsed.
	 */
	private void parseTransitionElements(Transition trans, Node transitionNode) {
		Node condition = XMLUtil.getChildWithName(transitionNode, CONDITION);
		if (condition != null) {
			parseCondition(trans, condition);
		}
	}
	
	/**
	 * Parses a transition from a transition node.
	 * 
	 * Creates a new transition object and adds the parsed
	 * information to it.
	 * 
	 * @param transitionNode The transition node to be parsed.
	 * 
	 * @return The created and filled transition object.
	 */
	public Transition parseTransition(Node transitionNode) {
		Transition transition = new Transition(this.output);
		GraphicalObjectParser.parse(transition, transitionNode, this.output);
		parseTransitionAttributes(transition, transitionNode);
		parseTransitionElements(transition, transitionNode);
		return transition;
	}
}
