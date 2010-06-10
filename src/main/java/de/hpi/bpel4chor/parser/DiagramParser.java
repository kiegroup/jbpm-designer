package de.hpi.bpel4chor.parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.Pool;
import de.hpi.bpel4chor.model.PoolSet;
import de.hpi.bpel4chor.model.Process;
import de.hpi.bpel4chor.model.artifacts.ParticipantReferenceDataObject;
import de.hpi.bpel4chor.model.artifacts.ParticipantSetDataObject;
import de.hpi.bpel4chor.model.artifacts.VariableDataObject;
import de.hpi.bpel4chor.model.connections.Association;
import de.hpi.bpel4chor.model.connections.MessageFlow;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hpi.bpel4chor.util.Output;
import de.hpi.bpel4chor.util.XMLUtil;

/**
 * This class parses a diagram and its contained elements from the XPDL4Chor
 * input.
 */
public class DiagramParser {
	
	private static final String ID = "Id";
	private static final String NAME = "Name";
	private static final String EXPRESSION_LANGUAGE = "ExpressionLanguage";
	private static final String QUERY_LANGUAGE = "QueryLanguage";
	private static final String TARGET_NAMESPACE = "TargetNamespace";
	private static final String GROUNDING_FILE = "GroundingFile";
	private static final String REDEFINABLE_HEADER = "RedefinableHeader";
	
	private static final String POOLS = "Pools";
	private static final String POOL_SETS = "PoolSets";
	private static final String POOL = "Pool";
	private static final String POOL_SET = "PoolSet";
	private static final String ASSOCIATIONS = "Associations";
	private static final String ASSOCIATION = "Association";
	private static final String MESSAGE_FLOWS = "MessageFlows";
	private static final String MESSAGE_FLOW = "MessageFlow";
	private static final String ARTIFACTS = "Artifacts";
	private static final String ARTIFACT = "Artifact";
	private static final String DATA_OBJECT = "DataObject";
	private static final String PROCESSES = "WorkflowProcesses";
	private static final String PROCESS = "WorkflowProcess";
	
	private Diagram diagram = null;
	private Output output;
	
	/**
	 * Constructor. Initializes the diagram parser. 
	 * 
	 * @param output  The Output to print the errors to. 
	 */
	public DiagramParser(Output output) {
		this.output = output;
	}
	
	/**
	 * Parses the diagram and its contained elemenents from the given diagram
	 * element. Creates a new diagram object and adds the parsed information
	 * to it.
	 * 
	 * @param diagramElement The diagram element to be parsed.
	 * 
	 * @return The diagram containing the information and graphical
	 * elements parsed from the given element.
	 */
	public Diagram parseDiagram(Element diagramElement) {
		this.diagram = new Diagram();
		parse(diagramElement);
		return this.diagram;
	}
	
	/**
	 * Parses the attributes of the given diagram element and adds the 
	 * information to the diagram created by this class.
	 * 
	 * @param diagramElement The diagram element to be parsed.
	 */
	private void parseAttributes(Element diagramElement) {
		NamedNodeMap attributes = diagramElement.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attributeNode = attributes.item(i);
			String attributeName = attributeNode.getLocalName();
			if (attributeName.equals(ID)) {
				this.diagram.setId(attributeNode.getNodeValue());
			} else if (attributeName.equals(NAME)) {
				this.diagram.setName(attributeNode.getNodeValue());
			} else if (attributeName.equals(TARGET_NAMESPACE)) {				
				this.diagram.setTargetNamespace(attributeNode.getNodeValue());
			}  else if (attributeName.equals(GROUNDING_FILE)) {
				this.diagram.setGroundingFile(attributeNode.getNodeValue());
			}
		}
		if (this.diagram.getName() == null) {
			this.output.addError("Package element does not have a specified Name.", diagramElement.toString());
		}
	}
	
	/**
	 * Parses the pool nodes contained in the given pools node
	 * (see {@link SwimlaneParser#parsePool(Node)}. The parsed pool
	 * is added to the diagram created by this class.
	 * 
	 * @param poolsNode The pools node containing the pool nodes to parse.
	 */
	private void parsePools(Node poolsNode) {
		NodeList poolNodes = poolsNode.getChildNodes();
		SwimlaneParser parser = new SwimlaneParser(this.diagram, this.output);
		for (int i = 0; i < poolNodes.getLength(); i++) {
			Node poolNode = poolNodes.item(i);
			if ((poolNode.getLocalName() != null) &&
					poolNode.getLocalName().equals(POOL)) {
				Pool pool = parser.parsePool(poolNode);
				this.diagram.addPool(pool);
				this.diagram.putObject(pool.getId(), pool);
			}
		}
	}
	
	/**
	 * Parses the pool set nodes contained in the given pool sets node
	 * (see {@link SwimlaneParser#parsePoolSet(Node)}. The parsed pool set
	 * is added to the diagram created by this class.
	 * 
	 * @param poolSetsNode The pool sets node containing the pool set 
	 *                      nodes to parse.
	 */
	private void parsePoolSets(Node poolSetsNode) {
		NodeList poolSetNodes = poolSetsNode.getChildNodes();
		SwimlaneParser parser = new SwimlaneParser(this.diagram, this.output);
		for (int i = 0; i < poolSetNodes.getLength(); i++) {
			Node poolSetNode = poolSetNodes.item(i);
			if ((poolSetNode.getLocalName() != null) && 
					poolSetNode.getLocalName().equals(POOL_SET)) {
				PoolSet poolSet = parser.parsePoolSet(poolSetNode);
				this.diagram.addPoolSet(poolSet);
				this.diagram.putObject(poolSet.getId(), poolSet);
			}
		}
	}
	
	/**
	 * Parses an artifact node and data object node and adds the information
	 * to an artifact (see {@link ArtifactParser#parseReferenceDataObject(Node, Node)},
	 * {@link ArtifactParser#parseSetDataObject(Node, Node)}, 
	 * {@link ArtifactParser#parseVariableDataObject(Node, Node)}).
	 * 
	 * The artifact is added to the diagram created by this class. 
	 * 
	 * @param artifactNode   The artifact node to be parsed.
	 * @param dataObjectNode The data object node to be parsed.
	 */
	private void parseDataObject(Node artifactNode, Node dataObjectNode) {
		NodeList childs = dataObjectNode.getChildNodes();
		ArtifactParser parser = new ArtifactParser(this.diagram, this.output);
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getLocalName().equals("VariableDataObject")) {
				VariableDataObject dataObject = 
					parser.parseVariableDataObject(artifactNode, child);
				this.diagram.addVariableDataObject(dataObject);
				this.diagram.putObject(dataObject.getId(), dataObject);
				break;
			} else if (child.getLocalName().equals("ParticipantReferenceDataObject")) {
				ParticipantReferenceDataObject dataObject = 
					parser.parseReferenceDataObject(artifactNode, child);
				this.diagram.addParticipantReferenceDataObject(dataObject);
				this.diagram.putObject(dataObject.getId(), dataObject);
				break;
			} else if (child.getLocalName().equals("ParticipantSetDataObject")) {
				ParticipantSetDataObject dataObject = 
					parser.parseSetDataObject(artifactNode, child);
				this.diagram.addParticipantSetDataObject(dataObject);
				this.diagram.putObject(dataObject.getId(), dataObject);
				break;
			}
		}
	}
	
	/**
	 * Parses the data object nodes contained in the given artifacts node and
	 * adds the information to appropriate artifacts. The artifacts will be added
	 * to the diagram created by this class.
	 * 
	 * @param artifactsNode The artifact node to be parsed.
	 */
	private void parseArtifacts(Node artifactsNode) {
		NodeList artifactNodes = artifactsNode.getChildNodes();
		for (int i = 0; i < artifactNodes.getLength(); i++) {
			Node artifactNode = artifactNodes.item(i);
			if ((artifactNode.getLocalName() != null) && 
					artifactNode.getLocalName().equals(ARTIFACT)) {
				// check if artifact has a data object
				NodeList childs = artifactNode.getChildNodes();
				for (int j = 0; j < childs.getLength(); j++) {
					Node child = childs.item(j);
					if ((child.getLocalName() != null) && 
							child.getLocalName().equals(DATA_OBJECT)) {
						parseDataObject(artifactNode, child);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Parses the message flow nodes contained in the given message flows node
	 * (see {@link ConnectionsParser#parseMessageFlow(Node)}. The parsed 
	 * message flows will be to the diagram created by this class.
	 * 
	 * @param messageFlowsNode The message flows node containing the message
	 *                         flow nodes to parse.
	 */
	private void parseMessageFlows(Node messageFlowsNode) {
		NodeList messageFlowNodes = messageFlowsNode.getChildNodes();
		ConnectionsParser parser = new ConnectionsParser(this.diagram, this.output);
		for (int i = 0; i < messageFlowNodes.getLength(); i++) {
			Node messageFlowNode = messageFlowNodes.item(i);
			if ((messageFlowNode.getLocalName() != null) && messageFlowNode.getLocalName().equals(MESSAGE_FLOW)) {
				MessageFlow messageFlow = parser.parseMessageFlow(messageFlowNode);
				this.diagram.addMessageFlow(messageFlow);
				this.diagram.putObject(messageFlow.getId(), messageFlow);
			}
		}
	}
	
	/**
	 * Parses the association nodes contained in the given associations node
	 * (see {@link ConnectionsParser#parseAssociation(Node)}. The parsed 
	 * associations will be to the diagram created by this class.
	 * 
	 * @param associationsNode The associations node containing the association
	 *                         nodes to parse.
	 */
	private void parseAssociations(Node associationsNode) {
		NodeList associationNodes = associationsNode.getChildNodes();
		ConnectionsParser parser = new ConnectionsParser(this.diagram, this.output);
		for (int i = 0; i < associationNodes.getLength(); i++) {
			Node associationNode = associationNodes.item(i);
			if ((associationNode.getLocalName() != null) && 
					associationNode.getLocalName().equals(ASSOCIATION)) {
				Association association = parser.parseAssociation(associationNode);
				this.diagram.addAssociation(association);
				this.diagram.putObject(association.getId(), association);
			}
		}
	}
	
	/**
	 * Parses the process nodes contained in the given processes node
	 * (see {@link ProcessParser#parseProcess(Node)}. The parsed 
	 * processes will be to the diagram created by this class.
	 * 
	 * @param processesNode The processes node containing the process
	 *                      nodes to parse.
	 */
	private void parseProcesses(Node processesNode) {
		List<Process> processes = new ArrayList<Process>();
		NodeList processNodes = processesNode.getChildNodes();
		ProcessParser parser = new ProcessParser(this.diagram, this.output);
		for (int i = 0; i < processNodes.getLength(); i++) {
			Node processNode = processNodes.item(i);
			if ((processNode.getLocalName() != null) && 
					processNode.getLocalName().equals(PROCESS)) {
				Process process = parser.parseProcess(processNodes.item(i));
				processes.add(process);
				this.diagram.putObject(process.getId(), process);
			}
		}
	}
	
	/**
	 * Parses the redefinable header node of a package element.
	 * 
	 * If the defined expression or query language is not an URI,
	 * an error is added to the output.
	 * 
	 * @param headerNode The node to be parsed.
	 */
	private void parseRedefineableHeader(Node headerNode) {
		if (headerNode != null) {
			NamedNodeMap attributes = headerNode.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attribute = attributes.item(i);
				String value = attribute.getNodeValue();
				if (value.equals("")) {
					continue;
				}
				if (attribute.getLocalName().equals(QUERY_LANGUAGE)) {
					try {
						this.diagram.setQueryLanguage(new URI(value));
					} catch (URISyntaxException e) {
						this.output.addParseError("The query language defined for the "+
								"diagram is not a valid URI", headerNode);
					}
				} else if (attribute.getLocalName().equals(EXPRESSION_LANGUAGE)) {
					try {
						this.diagram.setExpressionLanguage(new URI(value));
					} catch (URISyntaxException e) {
						this.output.addParseError("The expression language defined for the "+
								"diagram is not a valid URI", headerNode);
					}
				}
			}
		}
	}
	
	/**
	 * Parses the child elements of the given diagram element.
	 * In this way the processes, pools, pool sets, artifacts,
	 * message flows, associations will be created and added
	 * to the diagram object.
	 * 
	 * If the diagram does not define any process, an error is added to the
	 * output. If the defined query or expression language is not an URI, 
	 * an error is added to the output, too.
	 * 
	 * @param diagramElement The diagram element to parse.
	 */
	private void parseElements(Element diagramElement) {
		// ordering is important
		Node node = XMLUtil.getChildWithName(diagramElement, PROCESSES);
		if (node != null) {
			parseProcesses(node);
		} else {
			this.output.addParseError(
				"There are no processes defined for the diagram.", diagramElement);
		}
		node = XMLUtil.getChildWithName(diagramElement, POOLS);
		if (node != null) {
			parsePools(node);
		}
		node = XMLUtil.getChildWithName(diagramElement, POOL_SETS);
		if (node != null) {
			parsePoolSets(node);
		}
		node = XMLUtil.getChildWithName(diagramElement, ARTIFACTS);
		if (node != null) {
			parseArtifacts(node);
		}
		node = XMLUtil.getChildWithName(diagramElement, MESSAGE_FLOWS);
		if (node != null) {
			parseMessageFlows(node);
		}
		node = XMLUtil.getChildWithName(diagramElement, ASSOCIATIONS);
		if (node != null) {
			parseAssociations(node);
		}
		node = XMLUtil.getChildWithName(diagramElement, REDEFINABLE_HEADER);
		parseRedefineableHeader(node);
	}
	
	/**
	 * Parses the attributes and child elements of the diagram element.
	 * (see {@link #parseAttributes(Element)}, {@link #parseElements(Element)}).
	 * 
	 * @param diagramElement The diagram element to parse.
	 */
	private void parse(Element diagramElement) {
		parseAttributes(diagramElement);
		parseElements(diagramElement);
	}
}
