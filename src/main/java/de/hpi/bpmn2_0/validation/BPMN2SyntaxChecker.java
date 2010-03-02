package de.hpi.bpmn2_0.validation;

import java.util.HashMap;
import java.util.List;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.RootElement;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.SubProcess;
import de.hpi.bpmn2_0.model.activity.type.ReceiveTask;
import de.hpi.bpmn2_0.model.choreography.Choreography;
import de.hpi.bpmn2_0.model.choreography.ChoreographyActivity;
import de.hpi.bpmn2_0.model.connector.DataInputAssociation;
import de.hpi.bpmn2_0.model.connector.DataOutputAssociation;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.conversation.Conversation;
import de.hpi.bpmn2_0.model.data_object.DataInput;
import de.hpi.bpmn2_0.model.data_object.DataOutput;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.ConditionalEventDefinition;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.event.IntermediateCatchEvent;
import de.hpi.bpmn2_0.model.event.MessageEventDefinition;
import de.hpi.bpmn2_0.model.event.SignalEventDefinition;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.event.TimerEventDefinition;
import de.hpi.bpmn2_0.model.gateway.EventBasedGateway;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayDirection;
import de.hpi.bpmn2_0.model.participant.Lane;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.diagram.verification.AbstractSyntaxChecker;

/**
 * Copyright (c) 2009 Philipp Giese
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

public class BPMN2SyntaxChecker extends AbstractSyntaxChecker {

	protected static final String NO_SOURCE = "BPMN_NO_SOURCE";
	protected static final String NO_TARGET = "BPMN_NO_TARGET";
	protected static final String MESSAGE_FLOW_NOT_CONNECTED = "BPMN_MESSAGE_FLOW_NOT_CONNECTED";
	protected static final String DIFFERENT_PROCESS = "BPMN_DIFFERENT_PROCESS";
	protected static final String SAME_PROCESS = "BPMN_SAME_PROCESS";
	protected static final String FLOWOBJECT_NOT_CONTAINED_IN_PROCESS = "BPMN_FLOWOBJECT_NOT_CONTAINED_IN_PROCESS";
	protected static final String ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "BPMN_ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW";
	protected static final String STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "BPMN_STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW";
//	protected static final String INTERMEDIATEEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "BPMN_INTERMEDIATEEVENT_WITHOUT_INCOMING_CONTROL_FLOW";
	protected static final String STARTEVENT_WITH_INCOMING_CONTROL_FLOW = "BPMN_STARTEVENT_WITH_INCOMING_CONTROL_FLOW";
	protected static final String ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW = "BPMN_ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW";
	protected static final String ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "BPMN_ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW";
	protected static final String ENDEVENT_WITH_OUTGOING_CONTROL_FLOW = "BPMN_ENDEVENT_WITH_OUTGOING_CONTROL_FLOW";
	protected static final String EVENTBASEDGATEWAY_BADCONTINUATION = "BPMN_EVENTBASEDGATEWAY_BADCONTINUATION";
	protected static final String NODE_NOT_ALLOWED = "BPMN_NODE_NOT_ALLOWED";
	protected static final String MESSAGE_FLOW_NOT_ALLOWED = "BPMN_MESSAGE_FLOW_NOT_ALLOWED";	
	
	// BPMN 2.0 Specific
	protected static final String DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION = "BPMN2_DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION";
	protected static final String DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION = "BPMN2_DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION";
	protected static final String EVENT_BASED_WITH_TOO_LESS_OUTGOING_SEQUENCE_FLOWS = "BPMN2_EVENT_BASED_WITH_TOO_LESS_OUTGOING_SEQUENCE_FLOWS";
	protected static final String EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS = "BPMN2_EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS";
	protected static final String EVENT_BASED_EVENT_TARGET_CONTRADICTION = "BPMN2_EVENT_BASED_EVENT_TARGET_CONTRADICTION";
	protected static final String EVENT_BASED_WRONG_TRIGGER = "BPMN2_EVENT_BASED_WRONG_TRIGGER";
	protected static final String EVENT_BASED_WRONG_CONDITION_EXPRESSION = "BPMN2_EVENT_BASED_WRONG_CONDITION_EXPRESSION";
	protected static final String EVENT_BASED_NOT_INSTANTIATING = "BPMN2_EVENT_BASED_NOT_INSTANTIATING";
	protected static final String EVENT_BASED_WITH_TOO_LESS_INCOMING_SEQUENCE_FLOWS = "BPMN2_EVENT_BASED_WITH_TOO_LESS_INCOMING_SEQUENCE_FLOWS";
	protected static final String RECEIVE_TASK_WITH_ATTACHED_EVENT = "BPMN2_RECEIVE_TASK_WITH_ATTACHED_EVENT";
	
	protected static final String GATEWAYDIRECTION_MIXED_FAILURE = "BPMN2_GATEWAYDIRECTION_MIXED_FAILURE";
	protected static final String GATEWAYDIRECTION_CONVERGING_FAILURE = "BPMN2_GATEWAYDIRECTION_CONVERGING_FAILURE";
	protected static final String GATEWAYDIRECTION_DIVERGING_FAILURE = "BPMN2_GATEWAYDIRECTION_DIVERGING_FAILURE";
	protected static final String GATEWAY_WITH_NO_OUTGOING_SEQUENCE_FLOW = "BPMN2_GATEWAY_WITH_NO_OUTGOING_SEQUENCE_FLOW";
	
	protected static final String EVENT_SUBPROCESS_BAD_CONNECTION = "BPMN2_EVENT_SUBPROCESS_BAD_CONNECTION";
	
	// CHOREOGRAPHY
	protected static final String TOO_MANY_INITIATING_MESSAGES = "BPMN2_TOO_MANY_INITIATING_MESSAGES";
	protected static final String TOO_MANY_INITIATING_PARTICIPANTS = "BPMN2_TOO_MANY_INITIATING_PARTICIPANTS";
	protected static final String TOO_FEW_INITIATING_PARTICIPANTS = "BPMN2_TOO_FEW_INITIATING_PARTICIPANTS";
	
	private Definitions defs;
		
//	public HashSet<String> allowedNodes;
//	public HashSet<String> forbiddenNodes;
	
	public BPMN2SyntaxChecker(Definitions defs) {
		this.defs = defs;
		this.errors = new HashMap<String, String>();
		
//		this.allowedNodes = new HashSet<String>();
//		this.forbiddenNodes = new HashSet<String>();
	}

	@Override
	public boolean checkSyntax() {
		
		errors.clear();
		
		this.checkEdges();
		this.checkNodes();
		
		return errors.size() == 0;
	}
	
	private void checkEdges() {	
		for(Edge edge : this.defs.getEdges()) {	
			
			if(edge.getSourceRef() == null) {
				this.addError(edge, NO_SOURCE);
				
			} else if(edge.getTargetRef() == null) {
				this.addError(edge, NO_TARGET);
			
			} else {
			
				if(edge instanceof MessageFlow) {			
													
					if(edge.getSourceRef().getProcess() == edge.getTargetRef().getProcess())	
						this.addError(edge, SAME_PROCESS);
										
//					if(edge.getSourceRef().getPool() == edge.getTargetRef().getPool() &&
//							edge.getSourceRef().getLane() != edge.getTargetRef().getLane()) 
//						this.addError(edge, MESSAGE_FLOW_NOT_ALLOWED);
					
					if(edge.getSourceRef() instanceof Lane || edge.getTargetRef() instanceof Lane)
						this.addError(edge, MESSAGE_FLOW_NOT_ALLOWED);					
					
				} else if(edge instanceof SequenceFlow) {
						
					if(edge.getSourceRef().getProcess() != edge.getTargetRef().getProcess()) 
						this.addError(edge, DIFFERENT_PROCESS);						
					
				}
			}
		}
	}
	
	private void checkNodes() {		
	
		for(RootElement rootElement : this.defs.getRootElement()) {
			
			/*
			 * Checking of Regular BPMN2.0 Diagrams
			 */
			if(rootElement instanceof Process) {
				
				for(FlowElement flowElement : ((Process) rootElement).getFlowElement()) {			
					
					if(!(flowElement instanceof Edge)) {
					
						this.checkNode(flowElement);	
					}
				}
			/*
			 * Checking of Choroegraphy Diagrams
			 */
			} else if(rootElement instanceof Choreography) {
				
				for(FlowElement flowElement : ((Choreography) rootElement).getFlowElement()) {
					
					if(!(flowElement instanceof Edge)) {
						
						this.checkChoreographyNode(flowElement);
					}
					
				}
				
			} else if(rootElement instanceof Conversation) {
				
				(new BPMN2ConversationChecker(this)).checkConversation((Conversation)rootElement);
				
			}
		}
	}

	private void checkChoreographyNode(FlowElement flowElement) {
		this.checkNode(flowElement);
		
		if(flowElement instanceof ChoreographyActivity) {
			Integer instantiationMessageCounter = this.checkForInitiatingMessages((ChoreographyActivity) flowElement);			
			Integer instantiatingCounter = 0;
						
			for(Participant participant : ((ChoreographyActivity) flowElement).getParticipants()) {
				if(participant.isInitiating()) {
					instantiatingCounter++;
					
					if(instantiatingCounter > 1) 
						this.addError(participant, TOO_MANY_INITIATING_PARTICIPANTS);
				}
				
				instantiationMessageCounter += this.checkForInitiatingMessages(participant);
			}
			
			if(instantiatingCounter == 0) 
				this.addError(flowElement, TOO_FEW_INITIATING_PARTICIPANTS);
			
			if(instantiationMessageCounter > 1) 
				this.addError(flowElement, TOO_MANY_INITIATING_MESSAGES);
		}
		
	}

	private Integer checkForInitiatingMessages(FlowElement flowElement) {
		Integer initiatingCounter = 0;
		
		// Check outgoing edges
		for(Edge outgoing : flowElement.getOutgoing())			
			if(outgoing.getTargetRef() instanceof Message) 				
				if(((Message) outgoing.getTargetRef()).isInitiating()) 
					initiatingCounter++;				
		
		// Check incoming edges
		for(Edge incoming : flowElement.getIncoming()) 			
			if(incoming.getSourceRef() instanceof Message) 				
				if(((Message) incoming.getSourceRef()).isInitiating())
					initiatingCounter++;
							
		return initiatingCounter;
		
	}

	private void checkNode(FlowElement node) {
				
//		this.checkForAllowedAndForbiddenNodes(node);
		
		if((node instanceof Activity || node instanceof Event || node instanceof Gateway) && node.getProcess() == null) {			
			this.addError(node, FLOWOBJECT_NOT_CONTAINED_IN_PROCESS);			
		}
		
		// Events
		if(node instanceof EndEvent && !this.hasIncomingControlFlow((FlowNode) node))
			this.addError(node, ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW);
		
		if(node instanceof EndEvent && this.hasOutgoingControlFlow((FlowNode) node))
			this.addError(node, ENDEVENT_WITH_OUTGOING_CONTROL_FLOW);
		
		if(node instanceof StartEvent && this.hasIncomingControlFlow((FlowNode) node))
			this.addError(node, STARTEVENT_WITH_INCOMING_CONTROL_FLOW);
		
		if(node instanceof StartEvent && !this.hasOutgoingControlFlow((FlowNode) node))
			this.addError(node, STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW);
		
		if(node instanceof BoundaryEvent)
			this.checkBoundaryEvent((BoundaryEvent) node);
				
		// Gateways
		if(node instanceof Gateway) {
			this.checkGateway((Gateway) node);
		}
				
		//Data Objects
		if(node instanceof DataInput)
			for(Edge edge : ((DataInput) node).getIncoming()) 
				if(edge instanceof DataInputAssociation || edge instanceof DataOutputAssociation)
					this.addError(node, DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION);
		
		if(node instanceof DataOutput)
			for(Edge edge : ((DataOutput) node).getOutgoing())
				if(edge instanceof DataInputAssociation || edge instanceof DataOutputAssociation)
					this.addError(node, DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION);
		
		// Subprocesses
		if(node instanceof SubProcess) 
			this.checkSubProcess((SubProcess) node);
		
	}
	
	private void checkSubProcess(SubProcess node) {
		
		/*
		 * this node is an event subprocess
		 */
		if(node.isTriggeredByEvent())
			if(node.getIncomingSequenceFlows().size() > 0
					|| node.getOutgoingSequenceFlows().size() > 0) {
				
				// SPEC: P. 188
				this.addError(node, EVENT_SUBPROCESS_BAD_CONNECTION);
				
			}
	}

	private void checkGateway(Gateway node) {
		/* 
		 * Eventbased Gateways can instantiate processes, thus 
		 * we have to handle them differently 
		 */
		if(node instanceof EventBasedGateway)
			this.checkEventBasedGateway((EventBasedGateway) node);
		else 
			// SPEC: P. 298
			this.checkCommomGateway(node);
		
	}

	private void checkCommomGateway(Gateway node) {
		GatewayDirection direction = node.getGatewayDirection();
		
		/*
		 * must have both multiple incoming and 
		 * outgoing sequence flows
		 */
		if(direction.equals(GatewayDirection.MIXED)) {
			
			if(node.getIncomingSequenceFlows().size() < 2
					|| node.getOutgoingSequenceFlows().size() < 2) {
				
				this.addError(node, GATEWAYDIRECTION_MIXED_FAILURE);
			}
		
		/* 
		 * must have multiple incoming sequence flows and must NOT have
		 * multiple outgoing sequence flows
		 */
		} else if (direction.equals(GatewayDirection.CONVERGING)) {
			
			if(!(node.getIncomingSequenceFlows().size() > 1 
					&& node.getOutgoingSequenceFlows().size() == 1)) {
				
				this.addError(node, GATEWAYDIRECTION_CONVERGING_FAILURE);
			}
		
		/*
		 * must have multiple outgoing sequence flows and must NOT have
		 * multiple incoming sequence flows
		 */
		} else if(direction.equals(GatewayDirection.DIVERGING)) {
			
			if(!(node.getIncomingSequenceFlows().size() == 1
					&& node.getOutgoingSequenceFlows().size() > 1)) {
				
				this.addError(node, GATEWAYDIRECTION_DIVERGING_FAILURE);
			}
		
		/*
		 * gateways must have a minimum of one outgoing sequence flow
		 */
		} else if(node.getOutgoingSequenceFlows().size() == 0) {
			
			this.addError(node, GATEWAY_WITH_NO_OUTGOING_SEQUENCE_FLOW);
			
		}
	}

	private void checkEventBasedGateway(EventBasedGateway node) {
		List<SequenceFlow> outEdges = node.getOutgoingSequenceFlows();
		
		boolean receiveTaskFlag = false;
		boolean messageEventFlag = false;
		
		if(outEdges.size() < 2)
			// SPEC: P. 307
			this.addError(node, EVENT_BASED_WITH_TOO_LESS_OUTGOING_SEQUENCE_FLOWS);
		
		if(node.isInstantiate() && !checkInstatiateCondition(node))
			// SPEC: P. 309
			this.addError(node, EVENT_BASED_NOT_INSTANTIATING);				
		
		if(!node.isInstantiate() && node.getIncomingSequenceFlows().size() == 0)
			this.addError(node, EVENT_BASED_WITH_TOO_LESS_INCOMING_SEQUENCE_FLOWS);
		
		for(SequenceFlow edge : outEdges) {
			
			/*
			 * outgoing sequence flows must NOT have a 
			 * condition expression
			 */
			if(edge.getConditionExpression() != null)
				// SPEC: P. 308
				// TODO: Look out for the correct implementation
				this.addError(edge, EVENT_BASED_WRONG_CONDITION_EXPRESSION);
			
			if(!(edge.getTargetRef() instanceof IntermediateCatchEvent || edge.getTargetRef() instanceof ReceiveTask)) {
				
				this.addError(node, EVENTBASEDGATEWAY_BADCONTINUATION);
			
			} else if(edge.getTargetRef() instanceof IntermediateCatchEvent) {				
				
				if(((IntermediateCatchEvent) edge.getTargetRef()).getEventDefinitionOfType(MessageEventDefinition.class) != null)
					messageEventFlag = true;
				
				/* Check for correct Events triggers */
				if(!checkTrigger((IntermediateCatchEvent) edge.getTargetRef()))
					// SPEC: P. 308
					this.addError(edge.getTargetRef(), EVENT_BASED_WRONG_TRIGGER);
				
				if(((IntermediateCatchEvent) edge.getTargetRef()).getIncomingSequenceFlows().size() > 1)
					// SPEC: P. 308
					this.addError(edge.getTargetRef(), EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS);
				
			} else if(edge.getTargetRef() instanceof ReceiveTask) {
				
				receiveTaskFlag = true;
				
				if(((ReceiveTask) edge.getTargetRef()).getIncomingSequenceFlows().size() > 1)
					// SPEC: P. 308
					this.addError(edge.getTargetRef(), EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS);
				
				if(((ReceiveTask) edge.getTargetRef()).getBoundaryEventRefs().size() != 0)
					// SPEC: P. 308
					this.addError(edge.getTargetRef(), RECEIVE_TASK_WITH_ATTACHED_EVENT);
			}
		}
		
		if(receiveTaskFlag && messageEventFlag)
			// SPEC: P. 308
			this.addError(node, EVENT_BASED_EVENT_TARGET_CONTRADICTION);
	}

	// TODO: Check if this Method and the invoked one are really necessary
//	private void checkForAllowedAndForbiddenNodes(FlowElement node) {
//		// Check for allowed and permitted nodes
//		if(!checkForAllowedNode(node, allowedNodes, true) || !checkForAllowedNode(node, forbiddenNodes, false)){
//			System.out.println("error");
//			addError(node, NODE_NOT_ALLOWED);
//		}
//	}
//	
//	private boolean checkForAllowedNode(FlowElement node, HashSet<String> classes, boolean allowed) {
//		// If checking for allowed classes, empty classes means all are allowed
//		if(allowed && classes.size() == 0)
//			return true;
//		
//		boolean containedInClasses = false;
//		String nodeClassName = node.getClass().getSimpleName();
//	
//		for(String clazz : classes){
//			//TODO this doesn't checks for superclasses!!!
//			// better would be "node instanceof Class.forName(clazz)" 
//			if(clazz.equals(nodeClassName)){
//				containedInClasses = true;
//			} else if(clazz.equals("MultipleInstanceActivity")){
//				containedInClasses = (node instanceof Activity) && ((Activity)node).isMultipleInstance();
//			}
//			
//			if(containedInClasses) break;
//		}
//		
//		return containedInClasses == allowed;
//	}
	
	private boolean checkInstatiateCondition(EventBasedGateway node) {
		
		boolean hasStartEvent = false;
					
			
		for(FlowElement flowElement : node.getProcess().getFlowElement()) {
			if(flowElement instanceof StartEvent)
				hasStartEvent = true;
		}
		
		/*
		 * if the process has no start events and the gateway has
		 * no incoming sequence flows it instantiates the process
		 */
		if(!hasStartEvent && node.getIncomingSequenceFlows().size() == 0)
			return true;
		
		/* OR */
		
		/*
		 * the gateway has an incoming sequence flow but its
		 * source is a none start event
		 */
		if(node.getIncomingSequenceFlows().size() == 1) {
			for(SequenceFlow sf : node.getIncomingSequenceFlows()) {
				if(sf.getSourceRef() instanceof StartEvent) {
					return true;
				}
			}
		}
			
		return false;
	}

	private boolean checkTrigger(IntermediateCatchEvent event) {
		// TODO: Add support multiple
		if(event.getEventDefinitionOfType(MessageEventDefinition.class) != null
				|| event.getEventDefinitionOfType(TimerEventDefinition.class) != null
				|| event.getEventDefinitionOfType(SignalEventDefinition.class) != null
				|| event.getEventDefinitionOfType(ConditionalEventDefinition.class) != null) {
			
			return true;
		}
		
		return false;
	}

	private void checkBoundaryEvent(BoundaryEvent node) {
		
		if(this.hasIncomingControlFlow(node))
			this.addError(node, ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW);
		
		if(node.getOutgoingSequenceFlows().size() != 1 && node.getEventDefinitionOfType(CompensateEventDefinition.class) == null)
			this.addError(node, ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW);
	}

	private boolean hasIncomingControlFlow(FlowNode node) {
		return node.getIncomingSequenceFlows().size() > 0;
	}
	
	private boolean hasOutgoingControlFlow(FlowNode node) {
		return node.getOutgoingSequenceFlows().size() > 0;
	}
		
	protected void addError(FlowElement elem, String errorText) {
		this.errors.put(elem.getId(), errorText);
	}	
}