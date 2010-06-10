package de.hpi.bpel4chor.parser;

import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.Process;
import de.hpi.bpel4chor.model.SubProcess;
import de.hpi.bpel4chor.model.activities.BlockActivity;
import de.hpi.bpel4chor.model.artifacts.Artifact;
import de.hpi.bpel4chor.model.artifacts.ParticipantReferenceDataObject;
import de.hpi.bpel4chor.model.artifacts.ParticipantSetDataObject;
import de.hpi.bpel4chor.model.artifacts.VariableDataObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hpi.bpel4chor.util.BPELUtil;
import de.hpi.bpel4chor.util.Output;
import de.hpi.bpel4chor.util.XMLUtil;

/**
 * This class parses the diagram artifacts from the xpdl4chor input.
 * The parsed artifacts will be added to a specified diagram.
 * 
 * Before the artifacts can be parsed all processes and sub-process of the diagram
 * must be added to the diagram objects.
 * 
 * An artifact parser instance can only be used for one diagram. 
 */
public class ArtifactParser {
	
	private static final String NAME = "Name";
	private static final String SUB_PROCESS = "SubProcess";
	private static final String PROCESS = "Process";
	
	// participant reference data object
	private static final String COPY_TO = "CopyTo";
	private static final String SCOPE = "Scope";
	private static final String CONTAINMENT = "Containment";
	private static final String SELECTS = "Selects";
	private static final String CONTAINMENT_REQUIRED = "Required";
	private static final String CONTAINMENT_MUST_ADD = "MustAdd";
	private static final String CONTAINMENT_ADD_IF_NOT_EXISTS = "AddIfNotExists";
	
	// variable
	private static final String TYPE = "Type";
	private static final String VARIABLE_TYPE = "VariableType";
	private static final String VARIABLE_TYPE_VALUE = "VariableTypeValue";
	private static final String FROM_SPEC = "FromSpec";
	
	private Diagram diagram;	
	private Output output;
	
	/**
	 * Constructor. Initializes the artifact parser. 
	 * 
	 * @param diagram The diagram, to store the information in.
	 * @param output  The Output to print the errors to. 
	 */
	public ArtifactParser(Diagram diagram, Output output) {
		this.diagram = diagram;
		this.output = output;
	}
	
	/**
	 * Parses an artifact from the given artifact node. 
	 * 
	 * The node can specify a sub-process or process attribute, that
	 * identifies the sub-process or process, the artifact belongs to.
	 * If a sub-process or process with the specified id was not parsed before,
	 * an error is added to the output.	 
	 * 
	 * If an artifact does not specify a name, an appropriate error is added
	 * to the output.
	 * 
	 * @param artifact          The artifact to store the information in.
	 * @param artifactNode      The artifact node to be parsed.
	 * @param mustHaveContainer True, if the artifact has to define a parent container,
	 *                          false otherwise.
	 */
	private void parseArtifact(Artifact artifact, Node artifactNode, 
			boolean mustHaveContainer) {
		GraphicalObjectParser.parse(artifact, artifactNode, this.output);
		
		NamedNodeMap attributes = artifactNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(NAME)) {
				
				artifact.setName(BPELUtil.stringToNCName(
						attributes.getNamedItem(NAME).getNodeValue()));
				
			} else if (attribute.getLocalName().equals(SUB_PROCESS))  {
				
				String subprocessId = attribute.getNodeValue();
				Object object = this.diagram.getObject(subprocessId);
				if (object == null || !(object instanceof SubProcess)) {
					this.output.addError("A sub process with the Id " + 
							subprocessId + " does not exist for this artifact", 
							artifact.getId());
				} else {
					artifact.setContainer((SubProcess)object);
				}
				
			} else if (attribute.getLocalName().equals(PROCESS))  {
				String processId = attribute.getNodeValue();
				Object object = this.diagram.getObject(processId);
				if ((object == null) || !(object instanceof Process)) {
					this.output.addError("A process with the Id " + 
							processId + " does not exist for this artifact", 
							artifact.getId());
				} else {
					artifact.setContainer((Process)object);
				}
			}
		}
		
		if (artifact.getName() == null) {
			this.output.addError("An artifact element does " +
					"not have a specified Name.", artifact.getId());
		}	
		
		if (mustHaveContainer) {
			if (artifact.getContainer() == null) {
				this.output.addError("The data object " + 
						" does not have a process or" +
						" subprocess defined.", artifact.getId());
			}
		}
	}
	
	/**
	 * Determines the value of the containment attribute for a participant
	 * reference in BPEL4Chor from the value of the containment attribute in
	 * the XPDL4Chor representation.
	 * 
	 * @param containment The containment attribute value in XPDL4Chor.
	 * @return The containment attribute value in BPEL4Chor.
	 */
	private String getContainment(String containment) {
		if (containment != null) {
			if (containment.equals(CONTAINMENT_REQUIRED)) {
				return ParticipantReferenceDataObject.CONTAINMENT_REQUIRED;
			} else if (containment.equals(CONTAINMENT_MUST_ADD)) {
				return ParticipantReferenceDataObject.CONTAINMENT_MUST_ADD;
			} else if (containment.equals(CONTAINMENT_ADD_IF_NOT_EXISTS)) {
				return ParticipantReferenceDataObject.CONTAINMENT_ADD_IF_NOT_EXISTS;
			}
		}
		return null;
	}
	
	/**
	 * Parses the attributes of a participant reference data object.
	 * 
	 * If the participant reference data object defines a scope and
	 * the scope does not exist, an error is added to the output.
	 * 
	 * @param ref                          The participant reference data
	 *                                     object to store the information in.
	 * @param participantRefDataObjectNode The participant reference data
	 *                                     object node to be parsed.
	 */
	private void parseReferenceAttributes(ParticipantReferenceDataObject ref, 
			Node participantRefDataObjectNode) {
		NamedNodeMap attributes = 
			participantRefDataObjectNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(COPY_TO)) {
				ref.setCopyTo(attribute.getNodeValue());
				break;
			} else if (attribute.getLocalName().equals(SCOPE)) {
				Object object = 
					this.diagram.getObject(attribute.getNodeValue());
				if ((object == null) || !(object instanceof BlockActivity)) {
					this.output.addError(
							"The participant reference data object " + 
							" defines a scope that does not exist.", ref.getId());
				} else {
					ref.setScope((BlockActivity)object);
				}
			} else if (attribute.getLocalName().equals(CONTAINMENT)) {
				ref.setContainment(getContainment(
						attributes.getNamedItem(CONTAINMENT).getNodeValue()));
			}
		}
	}
	
	/**
	 * Parses the child elements of a participant reference data object node.
	 * 
	 * @param ref                          The participant reference data
	 *                                     object to store the information in.
	 * @param participantRefDataObjectNode The participant reference data
	 *                                     object node to be parsed.
	 */
	private void parseReferenceElements(ParticipantReferenceDataObject ref,
			Node participantRefDataObjectNode) {
		NodeList childs = participantRefDataObjectNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if ((child.getLocalName() != null) && 
					child.getLocalName().equals(SELECTS)) {
				ref.addSelect(XMLUtil.getNodeValue(child, this.output));
			}
		}
	}
	
	/**
	 * Parses a participant reference data object from an artifact node
	 * and a participant reference data object node.
	 * 
	 * Creates a new participant reference data object and adds the parsed
	 * information to it.
	 * 
	 * @param artifactNode            The artifact node to be parsed.
	 * @param referenceDataObjectNode The participant reference data object
	 *                                node to be parsed.
	 * 
	 * @return The created and filled participant reference data object.
	 */
	public ParticipantReferenceDataObject parseReferenceDataObject(
			Node artifactNode, Node referenceDataObjectNode) {
		ParticipantReferenceDataObject result = 
			new ParticipantReferenceDataObject(this.output);
		parseArtifact(result, artifactNode, false);
		parseReferenceAttributes(result, referenceDataObjectNode);
		parseReferenceElements(result, referenceDataObjectNode);
		return result;
	}
	
	/**
	 * Parses the attributes of a participant set data object.
	 * 
	 * If the participant set data object defines a scope and
	 * the scope does not exist, an error is added to the output.
	 * 
	 * @param set                          The participant set data object to
	 *                                     store the information in.
	 * @param participantSetDataObjectNode The participant set data object node
	 *                                     to be parsed.
	 */
	private void parseSetAttributes(ParticipantSetDataObject set, 
			Node participantSetDataObjectNode) {
		NamedNodeMap attributes = 
			participantSetDataObjectNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(COPY_TO)) {
				set.setCopyTo(attribute.getNodeValue());
				break;
			} else if (attribute.getLocalName().equals(SCOPE)) {
				Object object = 
					this.diagram.getObject(attribute.getNodeValue());
				if ((object == null) || !(object instanceof BlockActivity)) {
					this.output.addError(
							"The participant set data object " +
							"defines a scope that does not exist.", set.getId());
				} else {
					set.setScope((BlockActivity)object);
				}
			}
		}
	}
	
	/**
	 * Parses a participant set data object from an artifact node
	 * and a participant set data object node.
	 * 
	 * Creates a new participant set data object and adds the parsed
	 * information to it.
	 * 
	 * @param artifactNode      The artifact node to be parsed.
	 * @param setDataObjectNode The participant set data object node to be
	 *                          parsed.
	 * 
	 * @return The create and filled participant set data object.
	 */
	public ParticipantSetDataObject parseSetDataObject(
			Node artifactNode, Node setDataObjectNode) {
		ParticipantSetDataObject result = 
			new ParticipantSetDataObject(this.output);
		parseArtifact(result, artifactNode, false);
		parseSetAttributes(result, setDataObjectNode);
		return result;
	}
	
	/**
	 * Parses the attributes of a variabel data object node.
	 * 
	 * If the variable data object does not define a variable type or
	 * a variable type value, an error is added to the output.
	 * 
	 * @param var                    The variable data object to store the
	 *                               information in.
	 * @param variableDataObjectNode The variable data object node to be parsed
	 */
	private void parseVariableAttributes(VariableDataObject var,
			Node variableDataObjectNode) {
		NamedNodeMap attributes = variableDataObjectNode.getAttributes();
		
		if (attributes.getNamedItem(TYPE) != null) {
			var.setType(attributes.getNamedItem(TYPE).getNodeValue());
		}
		
		String type = var.getType();
		if (type.equals(VariableDataObject.TYPE_STANDARD) || 
				type.equals(VariableDataObject.TYPE_FAULT) || 
				type.equals(VariableDataObject.TYPE_MESSAGE)) {
			if (attributes.getNamedItem(VARIABLE_TYPE) == null) {
				this.output.addError("The variable data object " +
					"does not have a variable type defined", var.getId());			
			} else {
				var.setVariableType(attributes.getNamedItem(VARIABLE_TYPE).getNodeValue());
			}
			
			if (attributes.getNamedItem(VARIABLE_TYPE_VALUE) == null) {
				this.output.addError("The variable data object " +
						"does not have a variable type value defined", var.getId());
			} else {
				var.setVariableTypeValue(attributes.getNamedItem(VARIABLE_TYPE_VALUE).getNodeValue());
			}
		}
	}
	
	/**
	 * Parses the child elements of a variable data object node.
	 * 
	 * @param var                    The variable data object to store the
	 *                               information in.
	 * @param variableDataObjectNode The variable data object node to be parsed
	 */
	private void parseVariableElements(VariableDataObject var, 
			Node variableDataObjectNode) {
		NodeList childs = variableDataObjectNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(FROM_SPEC)) {
				var.setFromSpec(SupportingParser.parseFromSpec(child, this.output));
			}
		}
	}
	
	/**
	 * Parses a variable data object from an artifact node
	 * and a variable data object node.
	 * 
	 * Creates a new variable data object and adds the parsed
	 * information to it.
	 * 
	 * @param artifactNode            The artifact node to be parsed.
	 * @param variableDataObjectNode  The variable data object node to be
	 *                                parsed.
	 * 
	 * @return The create and filled variable data object.
	 */
	public VariableDataObject parseVariableDataObject(
			Node artifactNode, Node variableDataObjectNode) {
		VariableDataObject result = 
			new VariableDataObject(this.output);
		parseArtifact(result, artifactNode, true);
		parseVariableAttributes(result, variableDataObjectNode);
		parseVariableElements(result, variableDataObjectNode);
		return result;
	}
}
