/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
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

package de.hpi.bpmn2_0.transformation;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.factory.IntermediateCatchEventFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Collaboration;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.SubProcess;
import de.hpi.bpmn2_0.model.choreography.Choreography;
import de.hpi.bpmn2_0.model.choreography.ChoreographyActivity;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.DataAssociation;
import de.hpi.bpmn2_0.model.connector.DataInputAssociation;
import de.hpi.bpmn2_0.model.connector.DataOutputAssociation;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.conversation.Conversation;
import de.hpi.bpmn2_0.model.conversation.ConversationLink;
import de.hpi.bpmn2_0.model.conversation.ConversationNode;
import de.hpi.bpmn2_0.model.diagram.AssociationConnector;
import de.hpi.bpmn2_0.model.diagram.BpmnConnector;
import de.hpi.bpmn2_0.model.diagram.BpmnDiagram;
import de.hpi.bpmn2_0.model.diagram.BpmnNode;
import de.hpi.bpmn2_0.model.diagram.ChoreographyCompartment;
import de.hpi.bpmn2_0.model.diagram.ChoreographyDiagram;
import de.hpi.bpmn2_0.model.diagram.CollaborationDiagram;
import de.hpi.bpmn2_0.model.diagram.ConversationDiagram;
import de.hpi.bpmn2_0.model.diagram.ConversationLinkConnector;
import de.hpi.bpmn2_0.model.diagram.ConversationParticipantShape;
import de.hpi.bpmn2_0.model.diagram.LaneCompartment;
import de.hpi.bpmn2_0.model.diagram.MessageFlowConnector;
import de.hpi.bpmn2_0.model.diagram.PoolCompartment;
import de.hpi.bpmn2_0.model.diagram.ProcessDiagram;
import de.hpi.bpmn2_0.model.diagram.SequenceFlowConnector;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayWithDefaultFlow;
import de.hpi.bpmn2_0.model.participant.Lane;
import de.hpi.bpmn2_0.model.participant.LaneSet;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.diagram.OryxUUID;
import de.hpi.util.reflection.ClassFinder;

/**
 * Converter class for Diagram to BPMN 2.0 transformation.
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 * 
 */
public class Diagram2BpmnConverter {
	/* Hash map of factories for BPMN 2.0 element to enable lazy initialization */
	private HashMap<String, AbstractBpmnFactory> factories;
	private HashMap<String, BPMNElement> bpmnElements;
	private Diagram diagram;
	private List<BPMNElement> diagramChilds;
	private List<Process> processes;
	private Definitions definitions;
	private LaneCompartment defaultLaneCompartment;

	private Collaboration collaboration;
	private CollaborationDiagram collaborationDiagram;

	private Conversation conversation;
	private ConversationDiagram conversationDiagram;

	private List<Choreography> choreography;
	private ChoreographyDiagram choreographyDiagram;

	/* Define edge ids */
	private final static String[] edgeIdsArray = { "SequenceFlow",
			"Association_Undirected", "Association_Unidirectional",
			"Association_Bidirectional", "MessageFlow", "ConversationLink" };

	public final static HashSet<String> edgeIds = new HashSet<String>(Arrays
			.asList(edgeIdsArray));

	/* Define data related objects ids */
	private final static String[] dataObjectIdsArray = { "DataObject",
			"DataStore", "Message", "ITSystem" };

	public final static HashSet<String> dataObjectIds = new HashSet<String>(
			Arrays.asList(dataObjectIdsArray));

	public Diagram2BpmnConverter(Diagram diagram) {
		this.factories = new HashMap<String, AbstractBpmnFactory>();
		this.bpmnElements = new HashMap<String, BPMNElement>();
		this.definitions = new Definitions();
		this.definitions.setId(OryxUUID.generate());
		this.diagram = diagram;
	}

	/**
	 * Retrieves the stencil id related hashed factory.
	 * 
	 * @param stencilId
	 *            The stencil id
	 * @return The related factory
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private AbstractBpmnFactory getFactoryForStencilId(String stencilId)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		/* Create a new factory instance if necessary */
		if (!factories.containsKey(stencilId)) {
			this.factories.put(stencilId, createFactoryForStencilId(stencilId));
		}

		return this.factories.get(stencilId);
	}

	/**
	 * Creates a new factory instance for a stencil id.
	 * 
	 * @param stencilId
	 *            The stencil id
	 * @return The created factory
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 */
	private AbstractBpmnFactory createFactoryForStencilId(String stencilId)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		List<Class<? extends AbstractBpmnFactory>> factoryClasses = ClassFinder
				.getClassesByPackageName(AbstractBpmnFactory.class,
						"de.hpi.bpmn2_0.factory");

		/* Find factory for stencil id */
		for (Class<? extends AbstractBpmnFactory> factoryClass : factoryClasses) {
			StencilId stencilIdA = (StencilId) factoryClass
					.getAnnotation(StencilId.class);
			if (stencilIdA == null)
				continue;

			/* Check if appropriate stencil id is contained */
			List<String> stencilIds = Arrays.asList(stencilIdA.value());
			if (stencilIds.contains(stencilId)) {
				return (AbstractBpmnFactory) factoryClass.newInstance();
			}
		}

		throw new ClassNotFoundException("Factory for stencil id: '"
				+ stencilId + "' not found!");
	}

	/**
	 * Secures uniqueness of an BPMN Element.
	 * 
	 * @param el
	 * @throws InvalidKeyException
	 */
	private void addBpmnElement(BPMNElement el) throws InvalidKeyException {
		if (this.bpmnElements.containsKey(el.getId())) {
			throw new InvalidKeyException(
					"Key already exists for BPMN element!");
		}

		this.bpmnElements.put(el.getId(), el);
	}

	/**
	 * Creates the BPMN 2.0 elements for the parent's child shapes recursively.
	 * 
	 * @param childShapes
	 *            The list of parent's child shapes
	 * @param parent
	 *            The parent {@link BPMNElement}
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws BpmnConverterException
	 * @throws InvalidKeyException
	 */
	private BPMNElement createBpmnElementsRecursively(Shape shape)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, BpmnConverterException, InvalidKeyException {

		/* Build up the Elements of the current shape childs */
		ArrayList<BPMNElement> childElements = new ArrayList<BPMNElement>();

		/* Create BPMN elements from shapes */
		for (Shape childShape : shape.getChildShapes()) {
			childElements.add(this.createBpmnElementsRecursively(childShape));
		}

		if (shape.equals(this.diagram)) {
			this.diagramChilds = childElements;
			return null;
		}

		/* Get the appropriate factory and create the element */
		AbstractBpmnFactory factory = this.getFactoryForStencilId(shape
				.getStencilId());

		BPMNElement bpmnElement = factory.createBpmnElement(shape, null);

		/* Add element to flat list of all elements of the diagram */
		this.addBpmnElement(bpmnElement);

		/* Add childs to current BPMN element */
		for (BPMNElement child : childElements) {
			bpmnElement.addChild(child);
		}

		return bpmnElement;

	}

	/**
	 * Set the {@link Participant} references of each {@link ConversationNode}
	 */
	private void setConversationParticipants() {
		for (BPMNElement element : this.bpmnElements.values()) {
			if (!(element.getNode() instanceof ConversationNode))
				continue;
			ConversationNode conNode = (ConversationNode) element.getNode();

			for (String id : conNode.participantsIds) {
				conNode.getParticipantRef().add(
						(Participant) this.bpmnElements.get(id).getNode());
			}
		}
	}

	/**
	 * Finds catching intermediate event that are attached to an activities
	 * boundary.
	 */
	private void detectBoundaryEvents() {
		for (Shape shape : this.diagram.getShapes()) {
			if (edgeIds.contains(shape.getStencilId())) {
				continue;
			}

			for (Shape outShape : shape.getOutgoings()) {
				if (edgeIds.contains(outShape.getStencilId()))
					continue;
				IntermediateCatchEventFactory.changeToBoundaryEvent(
						this.bpmnElements.get(shape.getResourceId()),
						this.bpmnElements.get(outShape.getResourceId()));
			}
		}
	}

	/**
	 * Retrieves the edges and updates the source and target references.
	 */
	private void detectConnectors() {
		for (Shape shape : this.diagram.getShapes()) {
			if (!edgeIds.contains(shape.getStencilId())) {
				continue;
			}

			/* Retrieve connector element */
			BPMNElement bpmnConnector = this.bpmnElements.get(shape
					.getResourceId());

			BPMNElement source = null;

			/*
			 * Find source of connector. It is assumed that the first none edge
			 * element is the source element.
			 */
			for (Shape incomingShape : shape.getIncomings()) {
				if (edgeIds.contains(incomingShape.getStencilId())) {
					((Edge) bpmnConnector.getNode()).getIncoming().add(
							(Edge) this.bpmnElements.get(
									incomingShape.getResourceId()).getNode());
					continue;
				}

				source = this.bpmnElements.get(incomingShape.getResourceId());
				break;
			}

			/* Update outgoing references */
			for (Shape outgoingShape : shape.getOutgoings()) {
				if (!edgeIds.contains(outgoingShape.getStencilId()))
					continue;
				((Edge) bpmnConnector.getNode()).getOutgoing().add(
						(Edge) this.bpmnElements.get(
								outgoingShape.getResourceId()).getNode());

			}

			BPMNElement target = (shape.getTarget() != null) ? this.bpmnElements
					.get(shape.getTarget().getResourceId())
					: null;

			/* Update source references */
			if (source != null) {
				Edge edgeElement = (Edge) bpmnConnector.getNode();
				BpmnConnector edgeShape = (BpmnConnector) bpmnConnector
					.getShape();
				
				/* Correct the source reference if it is an expanded pool */
				if (source.getNode() instanceof LaneSet) {
					PoolCompartment poolShape = (PoolCompartment) source.getShape();
					edgeElement.setSourceRef(poolShape.getParticipantRef());
					edgeShape.setSourceRef(poolShape);
				} else {
					FlowElement sourceNode = (FlowElement) source.getNode();
					sourceNode.getOutgoing()
							.add((Edge) bpmnConnector.getNode());

					edgeElement.setSourceRef(sourceNode);

					edgeShape.setSourceRef(source.getShape());
				}
			}

			/* Update target references */
			if (target != null) {
				Edge edgeElement = (Edge) bpmnConnector.getNode();
				BpmnConnector edgeShape = (BpmnConnector) bpmnConnector
						.getShape();
				/* Correct the target reference if it is an expanded pool. */
				if (target.getNode() instanceof LaneSet) {
					PoolCompartment poolShape = (PoolCompartment) target.getShape();
					edgeElement.setTargetRef(poolShape.getParticipantRef());
					edgeShape.setTargetRef(poolShape);
				} else {
					FlowElement targetNode = (FlowElement) target.getNode();
					targetNode.getIncoming()
							.add((Edge) bpmnConnector.getNode());

					edgeElement.setTargetRef(targetNode);

					edgeShape.setTargetRef(target.getShape());
				}
			}
		}
	}

	/**
	 * A {@link DataAssociation} is a child element of an {@link Activity}. This
	 * method updates the references between activities and their data
	 * associations.
	 */
	private void updateDataAssociationsRefs() {
		/* Define edge ids */
		String[] associationIdsArray = { "Association_Undirected",
				"Association_Unidirectional", "Association_Bidirectional" };

		HashSet<String> associationIds = new HashSet<String>(Arrays
				.asList(associationIdsArray));

		for (Shape shape : this.diagram.getShapes()) {
			if (!associationIds.contains(shape.getStencilId())) {
				continue;
			}

			/* Retrieve connector element */
			BPMNElement bpmnConnector = this.bpmnElements.get(shape
					.getResourceId());

			/* Get related activity */
			Edge dataAssociation = (Edge) bpmnConnector.getNode();
			Activity relatedActivity = null;
			if (dataAssociation instanceof DataInputAssociation) {
				relatedActivity = (dataAssociation.getTargetRef() instanceof Activity ? (Activity) dataAssociation
						.getTargetRef()
						: null);
				if (relatedActivity != null)
					relatedActivity.getDataInputAssociation().add(
							(DataInputAssociation) dataAssociation);

			} else if (dataAssociation instanceof DataOutputAssociation) {
				relatedActivity = (dataAssociation.getSourceRef() instanceof Activity ? (Activity) dataAssociation
						.getSourceRef()
						: null);
				if (relatedActivity != null)
					relatedActivity.getDataOutputAssociation().add(
							(DataOutputAssociation) dataAssociation);
			}
		}
	}

	/**
	 * Identifies the default sequence flows after all sequence flows are set
	 * correctly.
	 */
	private void setDefaultSequenceFlowOfExclusiveGateway() {
		for (BPMNElement element : this.bpmnElements.values()) {
			BaseElement base = element.getNode();
			if (base instanceof GatewayWithDefaultFlow) {
				((GatewayWithDefaultFlow) base).findDefaultSequenceFlow();
			}
		}
	}

	/**
	 * Retrieves all elements related to a conversation and creates a diagram
	 * element to wrap them.
	 */
	private void identifyConversation() {

		for (BPMNElement element : this.diagramChilds) {
			/* Identify conversation elements */
			if (element.getNode() instanceof ConversationLink) {
				this.getConversation().getConversationLink().add(
						(ConversationLink) element.getNode());
				this.getConversationDiagram().getConnector().add(
						(ConversationLinkConnector) element.getShape());
			}

			else if (element.getNode() instanceof ConversationNode) {
				this.getConversation().getConversationNode().add(
						(ConversationNode) element.getNode());
				this.getConversationDiagram().getShape().add(
						(BpmnNode) element.getShape());
			}

			else if (element.getNode() instanceof Participant
					&& element.getShape() instanceof ConversationParticipantShape) {
				this.getConversation().getParticipant().add(
						(Participant) element.getNode());
				this.getConversationDiagram().getParticipant().add(
						(ConversationParticipantShape) element.getShape());
			}
		}

		if (this.conversation != null && this.conversationDiagram != null) {
			for (BPMNElement element : this.diagramChilds) {
				if (element.getNode() instanceof MessageFlow) {
					this.getConversation().getMessageFlow().add(
							(MessageFlow) element.getNode());
					this.getConversationDiagram().getConnector().add(
							(BpmnConnector) element.getShape());
				}
			}
		}

		if (this.conversationDiagram != null)
			this.getConversationDiagram().setConversation(
					this.getConversation());
	}

	/**
	 * Method to handle sub processes
	 * 
	 * @param subProcess
	 */
	private void handleSubProcess(SubProcess subProcess) {
		Process process = new Process();
		process.setId(OryxUUID.generate());
		process.setSubprocessRef(subProcess);

		List<BPMNElement> childs = this.getChildElements(this.bpmnElements
				.get(subProcess.getId()));
		for (BPMNElement ele : childs) {
			// process.getFlowElement().add((FlowElement) ele.getNode());
			subProcess.getFlowElement().add((FlowElement) ele.getNode());
			if (ele.getNode() instanceof SubProcess)
				this.handleSubProcess((SubProcess) ele.getNode());
		}

		this.processes.add(process);
	}

	/**
	 * Identifies sets of nodes, connected through SequenceFlows.
	 */
	private void identifyProcesses() {
		this.processes = new ArrayList<Process>();

		List<FlowNode> allNodes = new ArrayList<FlowNode>();
		this.getAllNodesRecursively(this.diagramChilds, allNodes);

		// handle subprocesses => trivial
		for (FlowNode flowNode : allNodes) {
			if (flowNode instanceof SubProcess)
				handleSubProcess((SubProcess) flowNode);
		}

		/* Handle pools, current solution: only one process per pool */
		for (BPMNElement element : this.diagramChilds) {
			if (element.getNode() instanceof LaneSet) {
				Process process = new Process();
				process.setId(OryxUUID.generate());
				process.getLaneSet().add((LaneSet) element.getNode());
				((LaneSet) element.getNode()).setProcess(process);

				process.getFlowElement().addAll(
						((LaneSet) element.getNode()).getChildFlowElements());

				this.processes.add(process);
			}

		}

		/* Identify components within allNodes */
		while (allNodes.size() > 0) {
			Process currentProcess = new Process();
			currentProcess.setId(OryxUUID.generate());
			this.processes.add(currentProcess);

			addNode(currentProcess,
					this.getBpmnElementForNode(allNodes.get(0)), allNodes);
		}

		this.addSequenceFlowsToProcess();
		
		/* Set processRefs */
		for(Process p : this.processes) {
			for(FlowElement el : p.getFlowElement()) {
				el.setProcess(p);
			}
		}
	}

	/**
	 * Adds {@link Edge} to the related process.
	 */
	private void addSequenceFlowsToProcess() {
		for (BPMNElement element : this.diagramChilds) {
			if (!(element.getNode() instanceof SequenceFlow))
				continue;

			Edge edge = (Edge) element.getNode();
			/* Find process for edge */
			for (Process process : this.processes) {
				List<FlowElement> flowElements;
				if (process.isSubprocess())
					flowElements = process.getSubprocessRef().getFlowElement();
				else
					flowElements = process.getFlowElement();

				if (flowElements.contains(edge.getSourceRef())
						|| flowElements.contains(edge.getTargetRef())) {
					flowElements.add(edge);
					break;
				}
			}
		}
	}

	/**
	 * Helper method to get the {@link BPMNElement} for the given
	 * {@link FlowNode} from the list of BPMN elements.
	 * 
	 * @param node
	 *            The concerning {@link FlowNode}
	 * @return The related {@link BPMNElement}
	 */
	private BPMNElement getBpmnElementForNode(FlowNode node) {
		return this.bpmnElements.get(node.getId());
	}

	/**
	 * Adds the node to the connected set of nodes.
	 * 
	 * @param process
	 * @param element
	 * @param allNodes
	 */
	private void addNode(Process process, BPMNElement element,
			List<FlowNode> allNodes) {
		if (!(element.getNode() instanceof FlowNode)
				|| !allNodes.contains(element.getNode())) {
			return;
		}
		FlowNode node = (FlowNode) element.getNode();

		allNodes.remove(node);

		node.setProcess(process);
		process.addChild(node);

		/* Handle sequence flows */
		/* Attention: navigate into both directions! */
		for (SequenceFlow seqFlow : node.getIncomingSequenceFlows()) {
			if (seqFlow.sourceAndTargetContainedInSamePool()) {
				addNode(process, this.getBpmnElementForNode((FlowNode) seqFlow
						.getSourceRef()), allNodes);
			}
		}

		for (SequenceFlow seqFlow : node.getOutgoingSequenceFlows()) {
			if (seqFlow.sourceAndTargetContainedInSamePool()) {
				addNode(process, this.getBpmnElementForNode((FlowNode) seqFlow
						.getTargetRef()), allNodes);
			}
		}

		/* Handle compensation flow */
		/* Attention: navigate into both directions! */
		for (Association compFlow : node.getIncomingCompensationFlows()) {
			if (compFlow.sourceAndTargetContainedInSamePool()) {
				addNode(process, this.getBpmnElementForNode((FlowNode) compFlow
						.getSourceRef()), allNodes);
			}
		}

		for (Association compFlow : node.getOutgoingCompensationFlows()) {
			if (compFlow.sourceAndTargetContainedInSamePool()) {
				addNode(process, this.getBpmnElementForNode((FlowNode) compFlow
						.getTargetRef()), allNodes);
			}
		}

		/* Handle boundary events */
		/* Attention: navigate into both directions! */
		if (node instanceof BoundaryEvent) {
			if (((BoundaryEvent) node).getAttachedToRef() != null) {
				addNode(process, this
						.getBpmnElementForNode(((BoundaryEvent) node)
								.getAttachedToRef()), allNodes);
			}
		} else if (node instanceof Activity) {
			for (BoundaryEvent event : ((Activity) node).getBoundaryEventRefs()) {
				addNode(process, this.getBpmnElementForNode(event), allNodes);
			}
		}
	}

	/**
	 * Retrieves all nodes included into the diagram and stop recursion at
	 * subprocesses.
	 * 
	 * @param elements
	 *            The child elements of a parent BPMN element
	 * @param allNodes
	 *            The list to store every element
	 */
	private void getAllNodesRecursively(List<BPMNElement> elements,
			List<FlowNode> allNodes) {
		for (BPMNElement element : elements) {
			if (element.getNode() instanceof Lane) {
				getAllNodesRecursively(this.getChildElements(element), allNodes);
				continue;
			}
			if (!(element.getNode() instanceof FlowNode)) {
				continue;
			}

			FlowNode node = (FlowNode) element.getNode();

			if (node instanceof Activity || node instanceof Event
					|| node instanceof Gateway) {
				allNodes.add(node);
			}
		}
	}

	/**
	 * Retrieve the child elements of a BPMN element from within all BPMN
	 * elements in the diagram.
	 * 
	 * @param element
	 *            The parent BPMN Element
	 * @return
	 */
	private List<BPMNElement> getChildElements(BPMNElement element) {
		List<BPMNElement> childElements = new ArrayList<BPMNElement>();
		for (Shape shape : this.diagram.getShapes()) {
			if (!shape.getResourceId().equals(element.getId())) {
				continue;
			}
			for (Shape child : shape.getChildShapes()) {
				childElements.add(this.bpmnElements.get(child.getResourceId()));
			}
		}

		return childElements;
	}

	private void insertSubprocessIntoDefinitions(ProcessDiagram processDia,
			Process process) {
		LaneCompartment lane = new LaneCompartment();
		lane.setId(OryxUUID.generate() + "_gui");
		lane.setIsVisible(false);

		/* Insert elements */
		for (FlowElement flowEle : process.getSubprocessRef().getFlowElement()) {
			if (flowEle instanceof FlowNode) {
				lane.getBpmnShape().add(
						(BpmnNode) this.bpmnElements.get(flowEle.getId())
								.getShape());
			}
			/* Insert sequence flows */
			else if (flowEle instanceof SequenceFlow) {
				processDia.getSequenceFlowConnector().add(
						(SequenceFlowConnector) this.bpmnElements.get(
								flowEle.getId()).getShape());
			}
		}

		processDia.getLaneCompartment().add(lane);

		/* Insert process into document */
		this.definitions.getDiagram().add(processDia);
	}

	/**
	 * Creates a process diagram for each identified process.
	 */
	private void insertProcessesIntoDefinitions() {
		for (Process process : this.processes) {
			if (process.isChoreographyProcess())
				continue;

			ProcessDiagram processDia = new ProcessDiagram();
			processDia.setProcessRef(process);
			processDia.setName(process.getName());
			processDia.setId(process.getId() + "_gui");

			/* Handle subprocesses */
			if (process.isSubprocess()) {
				this.insertSubprocessIntoDefinitions(processDia, process);
				continue;
			}

			/* Insert lane compartments */
			for (LaneSet laneSet : process.getLaneSet()) {
				PoolCompartment poolComp = (PoolCompartment) this.bpmnElements
						.get(laneSet.getId()).getShape();
				for (LaneCompartment laneComp : poolComp.getLane()) {
					processDia.getLaneCompartment().add(laneComp);
				}
			}

			/* Insert default lane set with one lane */
			if (process.getLaneSet().size() == 0) {

				LaneSet defaultLaneSet = new LaneSet();
				defaultLaneSet.setProcess(process);
				defaultLaneSet.setId(OryxUUID.generate());

				Lane defaultLane = new Lane();
				defaultLaneSet.getLanes().add(defaultLane);
				defaultLane.setId(OryxUUID.generate());
				defaultLane.setName("DefaultLane");
				defaultLane.setLaneSet(defaultLaneSet);

				LaneCompartment defaultLaneComp = new LaneCompartment();
				defaultLaneComp.setId(defaultLane.getId() + "_gui");
				defaultLaneComp.setIsVisible(false);
				defaultLaneComp.setName(defaultLane.getName());

				processDia.getLaneCompartment().add(defaultLaneComp);

				for (FlowElement node : process.getFlowElement()) {
					if (node instanceof FlowNode) {
						defaultLane.getFlowElementRef().add(node);
						defaultLaneComp.getBpmnShape().add(
								(BpmnNode) this.bpmnElements.get(node.getId())
										.getShape());
					}
				}

				process.getLaneSet().add(defaultLaneSet);
				this.defaultLaneCompartment = defaultLaneComp;
			}

			/* Insert Sequence Flow */
			for (FlowElement flowEle : process.getFlowElement()) {
				if (flowEle instanceof SequenceFlow) {
					processDia.getSequenceFlowConnector().add(
							(SequenceFlowConnector) this.bpmnElements.get(
									flowEle.getId()).getShape());
				}
			}

			/* Insert process into document */
			this.definitions.getRootElement().add(process);
			this.definitions.getDiagram().add(processDia);
		}
	}

	/**
	 * Set the reference to the activity related to the compensation.
	 */
	private void setCompensationEventActivityRef() {
		for (BPMNElement element : this.bpmnElements.values()) {
			/*
			 * Processing only necessary for events with compensation event
			 * definition
			 */
			if (!(element.getNode() instanceof Event))
				return;
			if (((Event) element.getNode())
					.getEventDefinitionOfType(CompensateEventDefinition.class) == null)

				if (element.getNode() instanceof BoundaryEvent
						&& ((BoundaryEvent) element.getNode())
								.getEventDefinitionOfType(CompensateEventDefinition.class) != null) {
					BoundaryEvent bEvent = (BoundaryEvent) element.getNode();
					((CompensateEventDefinition) bEvent
							.getEventDefinitionOfType(CompensateEventDefinition.class))
							.setActivityRef(bEvent.getAttachedToRef());
				}
		}
	}

	/**
	 * Set the initiating participant of a choreography activity.
	 */
	private void setInitiatingParticipant() {
		for (BPMNElement element : this.bpmnElements.values()) {
			if (element.getNode() instanceof ChoreographyActivity) {
				ChoreographyActivity activity = (ChoreographyActivity) element
						.getNode();
				for (Participant partici : activity.getParticipants()) {
					if (partici.isInitiating()) {
						activity.setInitiatingParticipantRef(partici);
						break;
					}
				}
			}
		}
	}

	/**
	 * Searches elements that are only allowed in a collaboration diagram and
	 * creates one if necessary.
	 */
	private void insertCollaborationElements() {
		boolean collaborationIncluded = false;
		for (BPMNElement element : this.diagramChilds) {
			if (element.getShape() instanceof PoolCompartment) {
				PoolCompartment pool = (PoolCompartment) element.getShape();
				this.setProcessForPool(pool);
				this.getCollaborationDiagram().getPool().add(pool);
				this.getCollaboration().getParticipant().add(
						pool.getParticipantRef());

				collaborationIncluded = true;
			}
			if (element.getNode() instanceof MessageFlow) {
				this.getCollaborationDiagram().getMessageFlowConnector().add(
						(MessageFlowConnector) element.getShape());
				this.getCollaboration().getMessageFlow().add(
						(MessageFlow) element.getNode());

				collaborationIncluded = true;
			}
		}

		if (collaborationIncluded && this.defaultLaneCompartment != null) {
			PoolCompartment poolComp = new PoolCompartment();
			poolComp.setIsVisible(false);
			poolComp.setId(OryxUUID.generate() + "_gui");
			poolComp.getLane().add(this.defaultLaneCompartment);

			Participant participant = new Participant();
			participant.setId(OryxUUID.generate());
			poolComp.setParticipantRef(participant);
			this.getCollaborationDiagram().getPool().add(poolComp);
			this.getCollaboration().getParticipant().add(participant);
		}

		/*
		 * Assure that the constrained of at least two pools in a collaboration
		 * diagram is fulfilled
		 */
		if (collaborationIncluded
				&& this.getCollaborationDiagram().getPool().size() >= 2) {
			this.definitions.getDiagram().add(this.getCollaborationDiagram());
			this.definitions.getRootElement().add(this.getCollaboration());
		}
	}
	
	/**
	 * Based on the passed pool, it searches for the appropriate process
	 * diagram, to retrieve the related process object.
	 * 
	 * @param pool
	 *            Resource pool
	 */
	private void setProcessForPool(PoolCompartment pool) {
		for (BpmnDiagram dia : this.definitions.getDiagram()) {
			if (!(dia instanceof ProcessDiagram))
				continue;
			for (LaneCompartment lane : ((ProcessDiagram) dia)
					.getLaneCompartment()) {
				for (LaneCompartment poolLane : pool.getLane()) {
					if (lane.equals(poolLane)) {
						pool.getParticipantRef().setProcessRef(
								((ProcessDiagram) dia).getProcessRef());
						return;
					}
				}
			}
		}
	}

	/**
	 * If the diagram contains conversation elements, this method appends them
	 * to in definitions element
	 */
	private void insertConversationIntoDefinitions() {
		this.identifyConversation();

		if (this.conversation != null && this.conversationDiagram != null) {
			this.definitions.getRootElement().add(this.conversation);
			this.definitions.getDiagram().add(this.conversationDiagram);
		}

	}

	/**
	 * If a process contains choreography elements the process will be inserted
	 * into a choreography element.
	 */
	private void insertChoreographyProcessesIntoDefinitions() {
		for (Process p : this.processes) {
			if (!p.isChoreographyProcess())
				continue;

			Choreography choreo = new Choreography();
			choreo.setId(OryxUUID.generate());

			ChoreographyCompartment choreoComp = new ChoreographyCompartment();
			choreoComp.setIsVisible(false);
			choreoComp.setId(choreo.getId() + "_gui");
			choreoComp.setChoreographyRef(choreo);

			for (FlowElement flowEle : p.getFlowElementsForChoreography()) {
				choreo.getFlowElement().add(flowEle);
				Object shape = this.bpmnElements.get(flowEle.getId())
						.getShape();
				if (shape instanceof BpmnNode)
					choreoComp.getBpmnShape().add((BpmnNode) shape);
				else if (shape instanceof SequenceFlowConnector)
					this.getChoreographyDiagram().getSequenceFlowConnector()
							.add((SequenceFlowConnector) shape);
				else if (shape instanceof AssociationConnector)
					this.getChoreographyDiagram().getAssociationConnector()
							.add((AssociationConnector) shape);
			}

			/* Insert into choreography diagram */
			this.getChoreographyDiagram().getChoreographyCompartment().add(
					choreoComp);
			this.getChoreography().add(choreo);

		}

		/* Insert into definitions */
		if (this.choreography == null || this.choreographyDiagram == null)
			return;

		this.definitions.getRootElement().addAll(this.choreography);
		this.definitions.getDiagram().add(this.choreographyDiagram);
	}

	/**
	 * Retrieves a BPMN 2.0 diagram and transforms it into the BPMN 2.0 model.
	 * 
	 * @param diagram
	 *            The BPMN 2.0 {@link Diagram} based on the ORYX JSON.
	 * @return The definitions root element of the BPMN 2.0 model.
	 * @throws BpmnConverterException
	 */
	public Definitions getDefinitionsFromDiagram()
			throws BpmnConverterException {

		/* Build-up the definitions as root element of the document */
		this.definitions.setTargetNamespace(diagram
				.getProperty("targetnamespace"));

		/* Convert shapes to BPMN 2.0 elements */

		try {
			createBpmnElementsRecursively(diagram);
		} catch (Exception e) {
			/* Pack exceptions in a BPMN converter exception */
			throw new BpmnConverterException(
					"Error while converting to BPMN model", e);
		}

		this.detectBoundaryEvents();
		this.detectConnectors();
		this.setInitiatingParticipant();
		this.updateDataAssociationsRefs();
		this.setDefaultSequenceFlowOfExclusiveGateway();
		this.setCompensationEventActivityRef();
		this.setConversationParticipants();

		this.identifyProcesses();

		this.insertChoreographyProcessesIntoDefinitions();
		this.insertConversationIntoDefinitions();

		this.insertProcessesIntoDefinitions();
		this.insertCollaborationElements();

		return definitions;
	}

	/* Getter & Setter */

	/**
	 * @return The list of BPMN 2.0 's stencil set edgeIds
	 */
	public static HashSet<String> getEdgeIds() {
		return edgeIds;
	}

	/**
	 * @return the collaborationDiagram
	 */
	private CollaborationDiagram getCollaborationDiagram() {
		if (this.collaborationDiagram == null) {
			this.collaborationDiagram = new CollaborationDiagram();
			this.collaborationDiagram.setId(this.getCollaboration().getId()
					+ "_gui");
			this.collaborationDiagram.setCollaborationRef(this
					.getCollaboration());
		}
		return collaborationDiagram;
	}

	/**
	 * @return the collaboration
	 */
	private Collaboration getCollaboration() {
		if (this.collaboration == null) {
			this.collaboration = new Collaboration();
			this.collaboration.setId(OryxUUID.generate());
		}
		return this.collaboration;
	}

	/**
	 * @return the choreography
	 */
	private List<Choreography> getChoreography() {
		if (this.choreography == null) {
			this.choreography = new ArrayList<Choreography>();
		}
		return this.choreography;
	}

	/**
	 * @return the choreographyDiagram
	 */
	private ChoreographyDiagram getChoreographyDiagram() {
		if (this.choreographyDiagram == null) {
			this.choreographyDiagram = new ChoreographyDiagram();
			this.choreographyDiagram.setId(OryxUUID.generate() + "_gui");
		}
		return this.choreographyDiagram;
	}

	/**
	 * @return the conversation
	 */
	private Conversation getConversation() {
		if (this.conversation == null) {
			this.conversation = new Conversation();
			this.conversation.setId(OryxUUID.generate());
		}
		return this.conversation;
	}

	/**
	 * @return the conversationDiagram
	 */
	private ConversationDiagram getConversationDiagram() {
		if (this.conversationDiagram == null) {
			this.conversationDiagram = new ConversationDiagram();
			this.conversationDiagram.setId(this.getConversation().getId()
					+ "_gui");
		}
		return this.conversationDiagram;
	}
}
