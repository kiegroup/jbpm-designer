package de.hpi.bpel4chor.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.SubProcess;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.connections.Transition;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hpi.bpel4chor.util.Output;
import de.hpi.bpel4chor.util.XMLUtil;

/**
 * This class creates a new sub-process and fills it with the information parsed
 * from a sub-process node in the XPDL4Chor input. Parsing a sub-process means to
 * parse also the contained activities and transitions.
 * 
 * A sub-process parser instance can only be used for one diagram.
 */
public class SubProcessParser {
	
	private static final String ID = "Id";
	
	private Diagram diagram = null;
	
	// necessary to find the node again for parsing additional information later 
	private static Map<String, Node> nodes = new HashMap<String, Node>();
	private Output output;
	
	/**
	 * Constructor. Initializes the sub-process parser. 
	 * 
	 * @param diagram The diagram the sub-process belongs to.
	 * @param output  The Output to print the errors to.
	 */
	public SubProcessParser(Diagram diagram, Output output) {
		this.diagram = diagram;
		this.output = output;
	}
	
	/**
	 * Determines and sets the Id of the sub-process, represented by the 
	 * given sub-process node. Moreover, the sub-process node is added to the
	 * list of sub-process nodes, parsed by this instance. These nodes can be
	 * referenced by the determined id.
	 * 
	 * @param subProcess     The sub-process to set the Id for.
	 * @param subProcessNode The sub-process node to be parsed.
	 */
	private void parseId(SubProcess subProcess, Node subProcessNode) {
		NamedNodeMap attributes = subProcessNode.getAttributes();
		if (attributes.getNamedItem(ID) == null) {
			this.output.addParseError("An activity set does " +
					"not have a specified Id.", subProcessNode);			
		} else {
			String id = attributes.getNamedItem(ID).getNodeValue();
			subProcess.setId(id);
			nodes.put(id, subProcessNode);
		}
	}

	/**
	 * Creates a new sub-process. The id of the sub-process is set
	 * to the id value defined in the given sub-process node.
	 * 
	 * @param subProcessNode The sub-process node to be parsed.
	 * 
	 * @return The created sub-process.
	 */
	public SubProcess parseSubProcessId(Node subProcessNode) {
		// parse id so that it is available for later referencing
		SubProcess subProcess = new SubProcess();
		parseId(subProcess, subProcessNode);
		return subProcess;
	}
	
	/**
	 * Parses the activity nodes contained in the given activities node.
	 * It creates an activity for each activity node and adds this activity
	 * to the sub-process activities and to the diagram.
	 *  
	 * @param subProcess     The sub process to add the parsed activities to.
	 * @param activitiesNode The activities node to be parse.
	 */
	private void parseActivities(SubProcess subProcess, Node activitiesNode) {
		NodeList activityNodes = activitiesNode.getChildNodes();
		ActivityParser parser = new ActivityParser(this.diagram, this.output);
		for (int i = 0; i < activityNodes.getLength(); i++) {
			Node activityNode = activityNodes.item(i);
			if ((activityNode.getLocalName() != null) &&
					activityNode.getLocalName().equals("Activity")) {
				Activity activity = 
					parser.parseActivity(activityNodes.item(i));
				if (activity != null) {
					activity.setParentContainer(subProcess);
					subProcess.addActivity(activity);
					this.diagram.putObject(activity.getId(), activity);
				}
			}
		}
	}
	
	/**
	 * Parses the transition nodes contained in the given transitions node.
	 * It creates a transition for each transition node and adds this transition
	 * to the sub-process transitions and to the diagram.
	 *  
	 * @param subProcess      The sub-process to add the transitions to.
	 * @param transitionsNode The transitions node to be parse.
	 */
	private void parseTransitions(SubProcess subProcess, Node transitionsNode) {
		NodeList transitionNodes = transitionsNode.getChildNodes();
		ConnectionsParser parser = new ConnectionsParser(this.diagram, this.output);
		for (int i = 0; i < transitionNodes.getLength(); i++) {
			Node transitionNode = transitionNodes.item(i);
			if ((transitionNode.getLocalName() != null) &&
					transitionNode.getLocalName().equals("Transition")) {
				Transition transition = 
					parser.parseTransition(transitionNodes.item(i));
				subProcess.addTransition(transition);
				this.diagram.putObject(transition.getId(), transition);
			}
		}
	}
	
	/**
	 * Parses the child elements of the sub-process node. This means to parse 
	 * the contained activities and transitions. These activities and 
	 * transitions will be added to the sub-process.
	 * 
	 * @param subProcess     The sub-process to add the activities and
	 *                       transitions to.
	 * @param subProcessNode The sub-process node to be parsed.
	 */
	private void parseElements(SubProcess subProcess, Node subProcessNode) {		
		Node node = XMLUtil.getChildWithName(subProcessNode, "Activities");
		if (node != null) { 
			parseActivities(subProcess, node);
		}
		
		node = XMLUtil.getChildWithName(subProcessNode, "Transitions");
		if (node != null) { 
			parseTransitions(subProcess, node);
		}
	}
	
	/**
	 * Builds up the graph structure based on the sub-process transitions. 
	 * For each transition the source and target object will be determined.
	 * If the source or target object does not exist in the diagram or is
	 * not an activity, an error is added to the output.
	 * 
	 * @param subProcess The sub-process to build the graph structure for.
	 */
	private void buildGraphStructure(SubProcess subProcess) {
		for (Iterator<Transition> it = 
			subProcess.getTransitions().iterator(); it.hasNext();) {
			Transition transition = it.next();
			if (transition.getSource() != null) {
				Object source = 
					this.diagram.getObject(transition.getSource().getId());
				if ((source != null) && (source instanceof Activity)) {
					((Activity)source).addSourceFor(transition, this.output);
				}
			}
			if (transition.getTarget() != null) {
				Object target = 
					this.diagram.getObject(transition.getTarget().getId());
				if ((target != null) && (target instanceof Activity)) {
					((Activity)target).addTargetFor(transition, this.output);
				}
			}
		}
	}
	
	/**
	 * Parses a sub-process node and adds the parsed information to the 
	 * given sub-process object. The sub-process node to be parsed is
	 * determined by the id of the given sub-process. That is why the
	 * sub-process must be created with {@link #parseSubProcessId(Node)}
	 * before this methode is called.
	 * 
	 * @param subProcess The sub-process whose node should be parsed.
	 */
	public void parseSubProcess(SubProcess subProcess) {
		Node subProcessNode = nodes.get(subProcess.getId());
		if (subProcessNode == null) {
			this.output.addGeneralError(
					"Tried to parse a sub process" +
					" that was not introduced to the parser before.");
		} else {
			parseElements(subProcess, subProcessNode);
			buildGraphStructure(subProcess);
		}
	}
}
