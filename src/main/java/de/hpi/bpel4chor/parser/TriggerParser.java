package de.hpi.bpel4chor.parser;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hpi.bpel4chor.util.Output;

import de.hpi.bpel4chor.model.activities.ResultCompensation;
import de.hpi.bpel4chor.model.activities.ResultError;
import de.hpi.bpel4chor.model.activities.Trigger;
import de.hpi.bpel4chor.model.activities.TriggerResultMessage;
import de.hpi.bpel4chor.model.activities.TriggerTimer;
import de.hpi.bpel4chor.model.supporting.Expression;

/**
 * This class is used for parsing the trigger elements that can be
 * defined for events.
 */
public class TriggerParser {
	
	// triggers
	private static final String TRIGGER_RESULT_MESSAGE = "TriggerResultMessage";
	private static final String TRIGGER_TIMER = "TriggerTimer";
	private static final String RESULT_ERROR = "ResultError";
	private static final String RESULT_COMPENSATION = "ResultCompensation";
	
	// message
	private static final String OPAQUE_OUTPUT = "OpaqueOutput";
	private static final String MESSAGE_EXCHANGE = "MessageExchange";
	private static final String FROM_PARTS = "FromParts";
	
	// timer
	private static final String TIME_CYCLE = "TimeCycle";
	private static final String TIME_DATE = "TimeDate";
	private static final String TIME_LANGUAGE = "TimeLanguage";
	private static final String REPEAT_EVERY = "RepeatEvery";
	
	// error
	private static final String ERROR_CODE = "ErrorCode";
	
	// compensation
	private static final String ACTIVITY_ID = "ActivityId";
	
	// correlations
	private static final String CORRELATIONS = "Correlations";
	
	/**
	 * Parses the attributes of a message trigger node. The parsed information
	 * is added to the given TriggerResultMessage object.
	 *   
	 * @param trigger     The TriggerResultMessage object to add the parsed
	 * 	                  information to.
	 * @param triggerNode The message trigger node to be parsed.
	 */
	private static void parseMessageAttributes(TriggerResultMessage trigger, Node triggerNode) {
		NamedNodeMap attributes = triggerNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(OPAQUE_OUTPUT)) {
				trigger.setOpaqueOutput( 
					new Boolean(attribute.getNodeValue()).booleanValue());
			} else if (attribute.getLocalName().equals(MESSAGE_EXCHANGE)) {
				trigger.setMessageExchange(attribute.getNodeValue());
			}
		}
	}
	
	/**
	 * Parses the child elements of a message trigger node. The parsed information
	 * is added to the given TriggerResultMessage object.
	 *   
	 * @param trigger     The TriggerResultMessage object to add the parsed
	 * 	                  information to.
	 * @param triggerNode The message trigger node to be parsed.
	 * @param output      The output to print errors to.
	 */
	private static void parseMessageElements(TriggerResultMessage trigger, Node triggerNode, Output output) {
		NodeList childs = triggerNode.getChildNodes();
		
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(CORRELATIONS)) {
				trigger.setCorrelations(SupportingParser.parseCorrelations(child, output));
			} else if (child.getLocalName().equals(FROM_PARTS)) {
				trigger.setFromParts(SupportingParser.parseFromParts(child));
			}
		}
	}
	
	/** 
	 * Parses the attributes and child elements of a message trigger node. A 
	 * new TriggerResultMessage object is created the parsed information will
	 * be added to.
	 * 
	 * @param triggerNode The message trigger node to be parsed.
	 * @param output      The output to print errors to.
	 * 
	 * @return The created and filled TriggerResultMessage object.
	 */
	private static TriggerResultMessage parseTriggerResultMessage(Node triggerNode, Output output) {
		TriggerResultMessage trigger = new TriggerResultMessage();
		parseMessageAttributes(trigger, triggerNode);
		parseMessageElements(trigger, triggerNode, output);
		return trigger;
	}
	
	/**
	 * Parses the child elements of a timer trigger node. The parsed information
	 * is added to the given TriggerTimer object.
	 *   
	 * @param trigger     The TriggerTimer object to add the parsed
	 * 	                  information to.
	 * @param triggerNode The timer trigger node to be parsed.
	 * @param output      The output to print errors to.
	 */
	private static void parseTimerElements(TriggerTimer trigger, Node triggerNode, Output output) {
		NodeList childs = triggerNode.getChildNodes();
		
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(REPEAT_EVERY)) {
				trigger.setRepeatEveryExpression(SupportingParser.parseExpression(child, output));
			}
		}
	}
	
	/**
	 * Parses the attributes of a timer trigger node. The parsed information
	 * is added to the given TriggerTimer object.
	 *   
	 * @param trigger     The TriggerTimer object to add the parsed
	 * 	                  information to.
	 * @param triggerNode The timer trigger node to be parsed.
	 */
	private static void parseTimerAttributes(TriggerTimer trigger, Node triggerNode) {
		NamedNodeMap attributes = triggerNode.getAttributes();
		
		String timeCycle = null;
		String timeDate = null;
		String timeLanguage = null;
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName() == null) {
				continue;
			}
			if (attribute.getLocalName().equals(TIME_CYCLE)) {
				timeCycle = attribute.getNodeValue();
			} else if (attribute.getLocalName().equals(TIME_DATE)) {				
				timeDate = attribute.getNodeValue();
			} else if (attribute.getLocalName().equals(TIME_LANGUAGE)) {
				timeLanguage = attribute.getNodeValue();
			}
		}
		
		if (timeCycle != null) {
			trigger.setTimeDurationExpression(new Expression(timeCycle, timeLanguage));
		} else {
			trigger.setTimeDeadlineExpression(new Expression(timeDate, timeLanguage));
		}
	}
	
	/** 
	 * Parses the attributes and child elements of a timer trigger node. A 
	 * new TriggerTimer object is created the parsed information will
	 * be added to.
	 * 
	 * @param triggerNode The timer trigger node to be parsed.
	 * @param output      The output to print errors to.
	 * 
	 * @return The created and filled TriggerTimer object.
	 */
	private static TriggerTimer parseTriggerTimer(Node triggerNode, Output output) {
		TriggerTimer trigger = new TriggerTimer();
		parseTimerElements(trigger, triggerNode, output);
		parseTimerAttributes(trigger, triggerNode);
		return trigger;
	}
	
	/**
	 * Parses the attributes of a error trigger node. The parsed information
	 * is added to the given ResultError object.
	 *   
	 * @param result      The ResultError object to add the parsed
	 * 	                  information to.
	 * @param triggerNode The error trigger node to be parsed.
	 */
	private static void parseResultErrorAttributes(ResultError result, Node triggerNode) {
		Node errorCodeAttr = 
			triggerNode.getAttributes().getNamedItem(ERROR_CODE);
		if (errorCodeAttr != null) {
			result.setErrorCode(errorCodeAttr.getNodeValue());
		}
	}
	
	/** 
	 * Parses the attributes of an error trigger node. A new ResultError object
	 * is created the parsed information will be added to.
	 * 
	 * @param triggerNode The error trigger node to be parsed.
	 * 
	 * @return The created and filled ResultError object.
	 */
	private static ResultError parseResultError(Node triggerNode) {
		ResultError result = new ResultError();
		parseResultErrorAttributes(result, triggerNode);
		return result;
	}
	
	/** 
	 * Parses the attributes of a compensation trigger node. A new 
	 * ResultCompensation object is created the parsed information 
	 * will be added to.
	 * 
	 * @param triggerNode The compensation trigger node to be parsed.
	 * 
	 * @return The created and filled ResultCompensation object.
	 */
	private static ResultCompensation parseResultCompensation(Node triggerNode) {
		
		Node activityId = 
			triggerNode.getAttributes().getNamedItem(ACTIVITY_ID);
		
		if (activityId != null) {
			return new ResultCompensation(activityId.getNodeValue());
		}
		return new ResultCompensation();
	}
	
	/**
	 * Parses the given trigger node and creates a trigger object
	 * depending on the type of the trigger node. The parsed information
	 * will be added to the created object. 
	 * 
	 * @param triggerNode The trigger node to be parsed.
	 * @param output      The output to print errors to.
	 * 
	 * @return The created and filled Trigger object
	 */
	public static Trigger parseTrigger(Node triggerNode, Output output) {
		Trigger result = null;
		if (triggerNode != null) {
			if (triggerNode.getLocalName().equals(TRIGGER_RESULT_MESSAGE)) {
				result = parseTriggerResultMessage(triggerNode, output);
			} else if (triggerNode.getLocalName().equals(TRIGGER_TIMER)) {
				result = parseTriggerTimer(triggerNode, output);
			} else if (triggerNode.getLocalName().equals(RESULT_ERROR)) {
				result = parseResultError(triggerNode);
			} else if (triggerNode.getLocalName().equals(RESULT_COMPENSATION)) {
				result = parseResultCompensation(triggerNode);
			}
		}
		return result;
	}
}
