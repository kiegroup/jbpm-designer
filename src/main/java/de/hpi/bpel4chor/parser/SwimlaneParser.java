package de.hpi.bpel4chor.parser;

import java.util.Iterator;
import java.util.List;
import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.Pool;
import de.hpi.bpel4chor.model.PoolSet;
import de.hpi.bpel4chor.model.Process;
import de.hpi.bpel4chor.model.Swimlane;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.activities.BlockActivity;
import de.hpi.bpel4chor.model.supporting.Import;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import de.hpi.bpel4chor.util.BPELUtil;
import de.hpi.bpel4chor.util.Output;
import de.hpi.bpel4chor.util.XMLUtil;

/**
 * This class creates swimlane objects parsed from the appropriate
 * XPDL4Chor nodes. Swimlanes are pools and pool sets.
 * 
 * A swimlane parser instance can only be used for one diagram.
 */
public class SwimlaneParser {
	
	// swimlane
	private static final String NAME = "Name";
	private static final String TARGET_NAMESPACE = "TargetNamespace";
	private static final String PREFIX = "Prefix";
	private static final String IMPORTS = "Imports";
	private static final String IMPORT = "Import";
	private static final String PROCESS = "Process";
	
	// pool
	private static final String PARTICIPANT_NAME = "Participant";
	private static final String CONTAINMENT = "Containment";
	private static final String SELECTS = "Selects";
	private static final String CONTAINMENT_REQUIRED = "Required";
	private static final String CONTAINMENT_MUST_ADD = "MustAdd";
	private static final String CONTAINMENT_ADD_IF_NOT_EXISTS = "AddIfNotExists";
	
	private Diagram diagram = null;
	private Output output;
	
	/**
	 * Constructor. Initializes the swimlane parser. 
	 * 
	 * @param diagram The diagram the swimlanes belong to.
	 * @param output  The Output to print the errors to.
	 */
	public SwimlaneParser(Diagram diagram, Output output) {
		this.diagram = diagram;
		this.output = output;
	}
	
	/**
	 * Sets the parent swimlane for the given activities. If an activity already
	 * has a parent swimlane specified, an error is added to the output.
	 * 
	 * @param swimlane   The parent swimlane for the given activities.
	 * @param activities The activities to set the parent swimlane for.
	 */
	private void setSwimlaneForActivities(Swimlane swimlane, List<Activity> activities) {
		for (Iterator<Activity> it = activities.iterator(); it.hasNext();) {
			Activity activity = it.next();
			if (activity.getParentSwimlane() == null) {
				if (activity instanceof BlockActivity) {
					setSwimlaneForActivities(swimlane, 
							((BlockActivity)activity).getSubProcess().getActivities());
				}
				activity.setParentSwimlane(swimlane);
			} else {
				this.output.addError("Activity " + 
						" is located in multiple swimlanes.", activity.getId());
			}
		}
	}
	
	/**
	 * Parses the attributes of a swimlane node. A swimlane node can be a pool
	 * or a pool set node. The parsed information is added to the given 
	 * Swimlane object.
	 * 
	 * @param swimlane     The Swimlane object to add the parsed information to
	 * @param swimlaneNode The swimlane node to be parsed.
	 */
	private void parseSwimlaneAttributes(Swimlane swimlane, Node swimlaneNode) {
		NamedNodeMap attributes = swimlaneNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(NAME)) {
				swimlane.setName(BPELUtil.stringToNCName(attribute.getNodeValue()));
			} else if (attribute.getLocalName().equals(TARGET_NAMESPACE)) {
				swimlane.setTargetNamespace(attribute.getNodeValue());
			} else if (attribute.getLocalName().equals(PREFIX)) {
				swimlane.setPrefix(attribute.getNodeValue());
			} else if (attribute.getLocalName().equals(PROCESS)) {
				String processId = attribute.getNodeValue();
				Object object = this.diagram.getObject(processId);
				if ((object == null) || !(object instanceof Process)) {
					this.output.addError("The process with the id " +
							processId + " referenced by the swimlane with the id "+ 
							" does not exist.", swimlane.getId());
				} else {
					Process process = (Process)object;
					swimlane.setProcess(process);
					setSwimlaneForActivities(swimlane, process.getActivities());
				}
			}
		}
		
		if (swimlane.getName() == null) {
			this.output.addError("A swimlane element does " +
					"not have a specified Name.", swimlane.getId());
		}
		
		if (swimlane.getProcess() == null) {
			this.output.addError("The swimlane" +
				" does not have a specified process.", swimlane.getId());
		}
	}
	
	/**
	 * Parses the imports node defined in a swimlane node. The parsed 
	 * information is added to the given Swimlane object 
	 * (see {@link SupportingParser#parseImport(Node, Output)}.
	 * 
	 * @param swimlane    The Swimlane object to add the parsed information to.
	 * @param importsNode The imports node to be parsed.
	 */
	private void parseImports(Swimlane swimlane, Node importsNode) {
		NodeList importNodes = importsNode.getChildNodes();
		for (int i = 0; i < importNodes.getLength(); i++) {
			Node importNode = importNodes.item(i);
			if ((importNode.getLocalName() != null) && 
					importNode.getLocalName().equals(IMPORT)) {
				Import imp = SupportingParser.parseImport(importNodes.item(i), this.output);
				swimlane.addImport(imp);
			}
		}
	}
	
	/**
	 * Parses the child elements of a swimlane node. A swimlane node can be a 
	 * pool or a pool set node. The parsed information is added to the given 
	 * Swimlane object.
	 * 
	 * @param swimlane     The Swimlane object to add the parsed information to
	 * @param swimlaneNode The swimlane node to be parsed.
	 */
	private void parseSwimlaneElements(Swimlane swimlane, Node swimlaneNode) {
		NodeList childs = swimlaneNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if ((child.getLocalName() != null) && 
					child.getLocalName().equals(IMPORTS)) {
				parseImports(swimlane, child);
				break;
			}
		}
	}
	
	/**
	 * Parses the attributes and child elements of a swimlane node and adds
	 * the parsed information to the given Swimlane object.
	 * 
	 * @param swimlane     The Swimlane object to add the parsed information to
	 * @param swimlaneNode The swimlane node to be parsed.
	 */
	private void parseSwimlane(Swimlane swimlane, Node swimlaneNode) {
		GraphicalObjectParser.parse(swimlane, swimlaneNode, this.output);
		parseSwimlaneAttributes(swimlane, swimlaneNode);
		parseSwimlaneElements(swimlane, swimlaneNode);
	}
	
	/**
	 * Parses the attributes that are special to a pool node. The parsed
	 * information is added to the given Pool object.
	 * 
	 * @param pool     The Pool object to add the parsed information to.
	 * @param poolNode The pool node to be parsed.
	 */
	private void parsePoolAttributes(Pool pool, Node poolNode) {
		NamedNodeMap attributes = poolNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(PARTICIPANT_NAME)) {
				pool.setParticipantName(attribute.getNodeValue());
				break;
			}
		}
	}
	
	/**
	 * Determines the BPEL4Chor containment value from the XPDL4Chor 
	 * containment attribute value.
	 * 
	 * @param containment The XPDL4Chor containment attribute value.
	 * 
	 * @return The BPEL4Chor containment value.
	 */
	private String getContainment(String containment) {
		if (containment != null) {
			if (containment.equals(CONTAINMENT_REQUIRED)) {
				return "required";
			} else if (containment.equals(CONTAINMENT_MUST_ADD)) {
				return "must-add";
			} else if (containment.equals(CONTAINMENT_ADD_IF_NOT_EXISTS)) {
				return "add-if-not-exists";
			}
		}
		return null;
	}
	
	/**
	 * Parses the child elements that only occur in pool nodes. These are
	 * elements that specify the participant reference information the pool 
	 * represents. The parsed information is added the given Pool object.
	 *  
	 * @param pool     The Pool object to add the parsed information to.
	 * @param poolNode The pool node to be parsed.
	 */
	private void parseParticipantReference(Pool pool, Node poolNode) {
		NodeList childs = poolNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if ((child.getLocalName() != null) && 
					child.getLocalName().equals(SELECTS)) {
				pool.addSelect(XMLUtil.getNodeValue(child, this.output));
			}
		}
		
		NamedNodeMap attributes = 
			poolNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(CONTAINMENT)) {
				pool.setContainment(getContainment(
						attribute.getNodeValue()));
			}
		}
	}
	
	/**
	 * Parsed the attributes and child elements of a pool node. A new
	 * Pool object is created the parsed information will be added to.
	 * 
	 * @param poolNode The pool node to be parsed.
	 * 
	 * @return The created and filled Pool object.
	 */
	public Pool parsePool(Node poolNode) {
		Pool pool = new Pool(this.output);
		parseSwimlane(pool, poolNode);
		parsePoolAttributes(pool, poolNode);
		parseParticipantReference(pool, poolNode);
		return pool;
	}
	
	/**
	 * Parsed the attributes and child elements of a pool set node. A new
	 * PoolSet object is created the parsed information will be added to.
	 * 
	 * @param poolSetNode The pool node to be parsed.
	 * 
	 * @return The created and filled PoolSet object.
	 */
	public PoolSet parsePoolSet(Node poolSetNode) {
		PoolSet poolSet = new PoolSet(this.output);
		parseSwimlane(poolSet, poolSetNode);
		return poolSet;
	}
}
