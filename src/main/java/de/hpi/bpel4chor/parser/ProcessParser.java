package de.hpi.bpel4chor.parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.Process;
import de.hpi.bpel4chor.model.SubProcess;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.activities.BlockActivity;
import de.hpi.bpel4chor.model.activities.IntermediateEvent;
import de.hpi.bpel4chor.model.activities.ResultCompensation;
import de.hpi.bpel4chor.model.activities.Task;
import de.hpi.bpel4chor.model.connections.Transition;
import de.hpi.bpel4chor.model.supporting.CorrelationSet;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import de.hpi.bpel4chor.util.BPELUtil;
import de.hpi.bpel4chor.util.Output;
import de.hpi.bpel4chor.util.XMLUtil;

/**
 * This class creates a new process and fills it with the information parsed
 * from a process node in the XPDL4Chor input. Parsing a process means to
 * parse also the contained activities and transitions.
 * 
 * A process parser instance can only be used for one diagram.
 */
public class ProcessParser {
	
	private static final String ID = "Id";
	private static final String NAME = "Name";
	private static final String SUPPRESS_JOIN_FAILURE = "SuppressJoinFailure";
	private static final String ENABLE_INSTANCE_COMPENSATION = "EnableInstanceCompensation";
	private static final String QUERY_LANGUAGE = "QueryLanguage";
	private static final String EXPRESSION_LANGUAGE = "ExpressionLanguage";
	private static final String EXIT_ON_STANDARD_FAULT = "ExitOnStandardFault";
	
	private static final String MESSAGE_EXCHANGES = "MessageExchanges";
	private static final String MESSAGE_EXCHANGE = "MessageExchange";

	private Diagram diagram = null;
	private Process process = null;
	private Output output;
	
	/**
	 * Constructor. Initializes the process parser. 
	 * 
	 * @param diagram The diagram the process belongs to.
	 * @param output  The Output to print the errors to.
	 */
	public ProcessParser(Diagram diagram, Output output) {
		this.diagram = diagram;
		this.output = output;
	}

	/**
	 * Parses the process node and the contained activities and transitions.
	 * For this purpose a new process object is created where the parsed
	 * information will be stored in.
	 * 
	 * @param processNode The process node to parse.
	 * 
	 * @return The created and filled process object.
	 */
	public Process parseProcess(Node processNode) {
		this.process = new Process();
		parse(processNode);
		return this.process;
	}
	
	/**
	 * Parses the attributes of the process node. The parsed
	 * information is added to the process object created by this class. 
	 * 
	 * If the process does not define an id, an error is added to the output.
	 * If the defined query or expression language is not an URI, an error
	 * is added to the output, too.
	 * 
	 * @param processNode The process node to be parsed.
	 */
	private void parseAttributes(Node processNode) {
		NamedNodeMap attributes = processNode.getAttributes();
		
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			String name = attribute.getLocalName();
			String value = attribute.getNodeValue();
			if (name.equals(ID)) {
				this.process.setId(value);
			} else if (name.equals(NAME)) {
				if (!value.equals("")) {
					this.process.setName(value);
				}
			} else if (name.equals(SUPPRESS_JOIN_FAILURE)) {
				this.process.setSuppressJoinFailure(
						new Boolean(value).booleanValue());
			} else if (name.equals(ENABLE_INSTANCE_COMPENSATION)) {
				this.process.setEnableInstanceCompensation(
						new Boolean(value).booleanValue());
			} else if (name.equals(QUERY_LANGUAGE)) {
				if (!value.equals("")) {
					try {
						this.process.setQueryLanguage(new URI(value));
					} catch (URISyntaxException e) {
						this.output.addError(
								"The query language defined for process "+ 
								"is not a valid URI", this.process.getId() );
					}
				}
			} else if (name.equals(EXPRESSION_LANGUAGE)) {
				if (!value.equals("")) {
					try {
						this.process.setExpressionLanguage(new URI(value));
					} catch (URISyntaxException e) {
						this.output.addError(
								"The expression language defined for process "+ 
								" is not a valid URI", this.process.getId());
					}
				}
			} else if (name.equals(EXIT_ON_STANDARD_FAULT)) {
				this.process.setExitOnStandardFault(
						new Boolean(value).booleanValue());
			}
		}
		
		if (this.process.getId() == null) {
			this.output.addParseError("A process does not have a specified Id.", processNode);
		}
	}
	
	/**
	 * Creates the sub-processes for each activity set contained in
	 * the given activity sets element. The created sub-process only contains
	 * the id defined for the activity set element. Other information must
	 * be added using {@link #parseActivitySets()}.
	 * 
	 * It is necessary to create the sub-processes before the actual information
	 * will be added, because a sub-process may be referenced from
	 * activities in another sub-processes.
	 * 
	 * The created sub-processes will be added to the process and the diagram.
	 * 
	 * @param activitySetsNode
	 */
	private void createActivitySets(Node activitySetsNode) {
		NodeList subProcessNodes = activitySetsNode.getChildNodes();
		SubProcessParser parser = new SubProcessParser(this.diagram, this.output);
		for (int i = 0; i < subProcessNodes.getLength(); i++) {
			Node subProcessNode = subProcessNodes.item(i);
			if ((subProcessNode.getLocalName() != null) && 
					subProcessNode.getLocalName().equals("ActivitySet")) {
				
				SubProcess subProcess = parser.parseSubProcessId(subProcessNode);
				this.process.addSubProcess(subProcess);
				this.diagram.putObject(subProcess.getId(), subProcess);
			}
		}
	}
	
	/**
	 * Parses the additional information (e.g. activities and transitions) 
	 * for each sub-process, that was created before using 
	 * {@link #createActivitySets(Node)}. 
	 */
	private void parseActivitySets() {
		SubProcessParser parser = 
			new SubProcessParser(this.diagram, this.output);
		for (Iterator<SubProcess> it = 
				this.process.getSubProcesses().iterator(); it.hasNext();) {
			SubProcess subProcess = it.next();
			parser.parseSubProcess(subProcess);
		}
	}
	
	/**
	 * Parses the activity nodes contained in the given activities node.
	 * It creates an activity for each activity node and adds this activity
	 * to the process activities and to the diagram.
	 *  
	 * @param activitiesNode The activities node to be parse.
	 */
	private void parseActivities(Node activitiesNode) {
		NodeList activityNodes = activitiesNode.getChildNodes();
		ActivityParser parser = new ActivityParser(this.diagram, this.output);
		for (int i = 0; i < activityNodes.getLength(); i++) {
			Node activityNode = activityNodes.item(i);
			if ((activityNode.getLocalName() != null) && 
					activityNode.getLocalName().equals("Activity")) {
				
				Activity activity = parser.parseActivity(activityNodes.item(i));
				if (activity != null) {
					activity.setParentContainer(this.process);
					this.process.addActivity(activity);
					this.diagram.putObject(activity.getId(), activity);
				}
			}
		}
	}
	
	/**
	 * Parses the transition nodes contained in the given transitions node.
	 * It creates a transition for each transition node and adds this transition
	 * to the process transitions and to the diagram.
	 *  
	 * @param transitionsNode The transitions node to be parse.
	 */
	private void parseTransitions(Node transitionsNode) {
		NodeList transitionNodes = transitionsNode.getChildNodes();
		ConnectionsParser parser = 
			new ConnectionsParser(this.diagram, this.output);
		for (int i = 0; i < transitionNodes.getLength(); i++) {
			Node transitionNode = transitionNodes.item(i);
			if ((transitionNode.getLocalName() != null) &&
					transitionNode.getLocalName().equals("Transition")) {
				Transition transition = 
					parser.parseTransition(transitionNodes.item(i));
				this.process.addTransition(transition);
				this.diagram.putObject(transition.getId(), transition);
			}
		}
	}
	
	/**
	 * Parses the message exchange nodes contained in the message exchanges 
	 * node. The parsed information is added to the list of message exchanges
	 * defined for the process. 
	 * 
	 * @param messageExchangesNode The message exchanges node to be parsed.
	 */
	private void parseMessageExchanges(Node messageExchangesNode) {
		NodeList messageExchangeNodes = messageExchangesNode.getChildNodes();
		for (int i = 0; i < messageExchangeNodes.getLength(); i++) {
			Node messageExchangeNode = messageExchangeNodes.item(i);
			if ((messageExchangeNode.getLocalName() != null) && 
					messageExchangeNode.getLocalName().equals(MESSAGE_EXCHANGE)) {
				String messageExchange = XMLUtil.getNodeValue(
						messageExchangeNodes.item(i), this.output);
				this.process.addMessageExchange(
						BPELUtil.stringToNCName(messageExchange));
			}
		}
	}
	
	/**
	 * Parses the data field nodes contained in the given data fields node.
	 * These data fields are used to define the correlation sets of a process.
	 * So the parsed information added to the list of correlation sets defined 
	 * for the process.
	 *  
	 * @param dataFieldsNode The data fields node to be parse.
	 */
	private void parseDataFields(Node dataFieldsNode) {
		NodeList dataFieldNodes = dataFieldsNode.getChildNodes();
		for (int i = 0; i < dataFieldNodes.getLength(); i++) {
			Node dataFieldNode = dataFieldNodes.item(i);
			if ((dataFieldNode.getLocalName() != null) && 
					dataFieldNode.getLocalName().equals("DataField")) {
				Node correlation = 
					dataFieldNode.getAttributes().getNamedItem("Correlation");
				if ((correlation != null) && 
						new Boolean(correlation.getNodeValue()).booleanValue()) {
					CorrelationSet correlationSet = 
						SupportingParser.parseCorrelationSet(dataFieldNode, this.output);
					this.process.addCorrelationSet(correlationSet);
				}
			}
		}
	}
	
	/**
	 * Parses the child elements of a process node. This means to parse 
	 * sub-processes, activities, transitions, message exchanges
	 * and data fields.
	 * 
	 * @param processNode The process node to be parsed.
	 */
	private void parseElements(Node processNode) {		
		Node node = XMLUtil.getChildWithName(processNode, "ActivitySets");
		if (node != null) {
			// create before parsing because they may be referenced within other activitiy sets
			createActivitySets(node);
			parseActivitySets();
		}
		
		node = XMLUtil.getChildWithName(processNode, "Activities");
		if (node != null) { 
			parseActivities(node);
		}
		
		node = XMLUtil.getChildWithName(processNode, "Transitions");
		if (node != null) { 
			parseTransitions(node);
		}
		
		node = XMLUtil.getChildWithName(processNode, MESSAGE_EXCHANGES);
		if (node != null) { 
			parseMessageExchanges(node);
		}
		
		node = XMLUtil.getChildWithName(processNode, "DataFields");
		if (node != null) { 
			parseDataFields(node);
		}
	}
	
	/**
	 * After all activities within the process have been parsed the activities
	 * to compensate can be determined. These activities are referenced in 
	 * intermediate compensation events. If the activity to compensate does
	 * not exist in the process, an error is added to the output.
	 */
	private void determineCompensationActivity() {
		List<IntermediateEvent> events = this.process.getIntermediateEvents(
				IntermediateEvent.TRIGGER_COMPENSATION);
		for (Iterator<SubProcess> it = 
				this.process.getSubProcesses().iterator(); it.hasNext();) {
			events.addAll(it.next().getIntermediateEvents(
					IntermediateEvent.TRIGGER_COMPENSATION));
		}
		for (Iterator<IntermediateEvent> it = events.iterator(); it.hasNext(); ) {
			IntermediateEvent event = it.next();
			
			if ((event.getTrigger() != null) && 
				(event.getTrigger() instanceof ResultCompensation)) {
				ResultCompensation trigger = (ResultCompensation)event.getTrigger();
				String id = trigger.getActivityId();
				if (id != null) {
					Object object = this.diagram.getObject(id);
					if (object == null) {
						this.output.addError(
								"An activity with id " + id + 
								" defined for a compensation " +
								"intermediate event does not exists in the diagram.", event.getId());
					} else if ((object instanceof BlockActivity) || (object instanceof Task)) {
						trigger.setActivity((Activity)object);
					} else {
						this.output.addError(
								"A scope or task with id " + id + 
								" defined for a compensation " +
								"intermediate event does not exists in the diagram.", event.getId());
					}
				}
			}
		}
	}
	
	/**
	 * Builds up the graph structure based on the process transitions. 
	 * For each transition the source and target object will be determined.
	 * If the source or target object does not exist in the diagram or is
	 * not an activity, an error is added to the output.
	 */
	private void buildGraphStructure() {
		for (Iterator<Transition> it = 
				this.process.getTransitions().iterator(); it.hasNext();) {
			Transition transition = it.next();
			Activity sourceAct = transition.getSource();
			if (sourceAct != null) {	
				Object source = 
					this.diagram.getObject(sourceAct.getId());
				if ((source != null) && (source instanceof Activity)) {
					((Activity)source).addSourceFor(transition, this.output);
				} else {
					this.output.addError(
							"The source activity of this transition" + 
							" does not exist in the process.", transition.getId());
				}
			}
			Activity targetAct = transition.getTarget();
			if (targetAct != null) {
				Object target = 
					this.diagram.getObject(targetAct.getId());
				if ((target != null) && (target instanceof Activity)) {
					((Activity)target).addTargetFor(transition, this.output);
				} else {
					this.output.addError(
							"The target activity of this transition" + 
							" does not exist in the process.", transition.getId());
				}
			}
		}
	}
	
	/**
	 * Parses a process node and adds the parsed information to the process 
	 * object created by this class.
	 * 
	 * @param processNode The process node to be parsed.
	 */
	private void parse(Node processNode) {
		parseAttributes(processNode);
		parseElements(processNode);
		
		determineCompensationActivity();
		buildGraphStructure();
	}
}
