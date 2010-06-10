package de.hpi.bpmn2yawl;

/**
 * Copyright (c) 2010, Armin Zamani
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
 * s
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Vector;

import de.hpi.bpmn.Activity;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.IntermediateTimerEvent;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.SequenceFlow.ConditionType;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;


public class BPMN2YAWLSyntaxChecker extends BPMNSyntaxChecker{

	private static final String COMPLEXGATEWAY_NOT_SUPPORTED = "Complex gateways are not supported in the mapping";
	private static final String ADHOCSUBPROCESS_NOT_SUPPORTED = "Adhoc subprocesses are not supported in the mapping";
	private static final String GATEWAY_WITHOUT_DEFAULTFLOW = "XORDataBasedGateways and ORGateways must have one outgoing default flow.";
	private static final String EXPRESSION_MISSING = "Outgoing flows of XORDataBasedGateways and ORGateways must either have an expression or be a default flow.";
	private static final String MORE_THAN_TWO_DEFAULTFLOWS_PER_GATEWAY_NOT_SUPPORTED = "XORDataBasedGateways and ORGateways must have only one outgoing default flow.";
	
	/**
	 * constructor of class 
	 */
	public BPMN2YAWLSyntaxChecker(BPMNDiagram diagram) {
		super(diagram);
		
		forbiddenNodes.add("ComplexGateway");
		forbiddenNodes.add("StartMessageEvent");
		forbiddenNodes.add("StartConditionalEvent");
		forbiddenNodes.add("StartSignalEvent");
		forbiddenNodes.add("StartMultipleEvent");
		forbiddenNodes.add("IntermediateCancelEvent");
		forbiddenNodes.add("IntermediateCompensationEvent");
		forbiddenNodes.add("IntermediateConditionalEvent");
		forbiddenNodes.add("IntermediateSignalEvent");
		forbiddenNodes.add("IntermediateMultipleEvent");
		forbiddenNodes.add("IntermediateLinkEvent");
		forbiddenNodes.add("EndCompensationEvent");
		forbiddenNodes.add("EndSignalEvent");
		forbiddenNodes.add("EndMultipleEvent");
	}
	
	/**
	 * @see de.hpi.bpmn.validation.BPMNSyntaxChecker#checkNode(de.hpi.bpmn.Node)
	 */
	@Override
	protected boolean checkNode(Node node) {
		boolean isOk = super.checkNode(node);
		
		if (node instanceof ComplexGateway)
			isOk = isOk && addErrorForComplexGateway(node);
		else if(node instanceof SubProcess)
			isOk = isOk && handleSubProcess(node);
		else if((node instanceof ORGateway) || (node instanceof XORDataBasedGateway))
			isOk = isOk && checkGateway(node);
		else if (node instanceof Lane)
			isOk = isOk && checkLane((Lane)node);
		
		return isOk;
	}

	/**
	 * checks a given gateway if it conforms to BPMN syntax rules including condition types 
	 * of outgoing edges and the number of default flows
	 * @param node
	 * @return
	 */
	private boolean checkGateway(Node node) {
		boolean isOk = true;
		if(node.getOutgoingSequenceFlows().size() > 1){
			int numberOfOutgoingDefaultFlows = 0;
			for(SequenceFlow sequenceFlow : node.getOutgoingSequenceFlows()){
				if(sequenceFlow.getConditionType() == ConditionType.DEFAULT)
					numberOfOutgoingDefaultFlows++;
				else if(sequenceFlow.getConditionType() == ConditionType.NONE)
					isOk &= checkSequenceFlowConditionTypeNone(sequenceFlow);
				else
					isOk &= checkSequenceFlowConditionTypeExpression(sequenceFlow);
			}
			
			if(numberOfOutgoingDefaultFlows == 0){
				isOk = false;
				addError(node, GATEWAY_WITHOUT_DEFAULTFLOW);
			}else if(numberOfOutgoingDefaultFlows > 1){
				isOk = false;
				addError(node, MORE_THAN_TWO_DEFAULTFLOWS_PER_GATEWAY_NOT_SUPPORTED);
			}
		}
		return isOk;
	}

	/**
	 * check if the given sequence flow has a condition expression
	 * @param sequenceFlow edge to be checked
	 * @return result of check
	 */
	private boolean checkSequenceFlowConditionTypeExpression(SequenceFlow sequenceFlow) {
		boolean isOk = true;
		if(sequenceFlow.getConditionExpression().isEmpty()){
			isOk = false;
			addError(sequenceFlow, EXPRESSION_MISSING);
		}
		return isOk;
	}

	/**
	 * check the given sequence flow with condition type none if it has a condition expression
	 * and set the conditiontype to Expression if it has (modeller may have forgotten it)
	 * @param sequenceFlow edge to be checked
	 * @return result of check
	 */
	private boolean checkSequenceFlowConditionTypeNone(SequenceFlow sequenceFlow) {
		boolean isOk = true;
		if(sequenceFlow.getConditionExpression().isEmpty()){
			isOk = false;
			addError(sequenceFlow, EXPRESSION_MISSING);
		}else
			sequenceFlow.setConditionType(ConditionType.EXPRESSION);
		
		return isOk;
	}

	/**
	 * check if the given subprocess is of type adhoc
	 * @param node subprocess to be checked
	 * @return result of check
	 */
	private boolean handleSubProcess(Node node) {
		boolean isOk = true;
		SubProcess subprocess = (SubProcess)node;
		if(subprocess.isAdhoc())
		{
			isOk = false;
			addError(node, ADHOCSUBPROCESS_NOT_SUPPORTED);
		}
		return isOk;
	}

	/**
	 * adds an error for a complex gateway
	 * @param node complex gateway node
	 * @return diagram not ok
	 */
	private boolean addErrorForComplexGateway(Node node) {
		addError(node, COMPLEXGATEWAY_NOT_SUPPORTED);
		return false;
	}
	
	/**
	 * checks if the tasks following the start events are executable tasks
	 * @param diagram BPMN diagram
	 * @return result of check
	 */
	public boolean checkForNonEmptyTasks(BPMNDiagram diagram){
		boolean isOk = true;
		Vector<StartEvent> startEvents = getStartEventsFromDiagram(diagram);
		isOk = isOk && checkStartEvents(startEvents);
		
		return isOk;
	}

	/**
	 * check all start events of the list of start events
	 * @param startEvents list of start events
	 * @return result of check
	 */
	private boolean checkStartEvents(Vector<StartEvent> startEvents) {
		boolean isOk = true;
		for(StartEvent start : startEvents){
			if(start.getOutgoingSequenceFlows().size() > 1){
				for (SequenceFlow flow : start.getOutgoingSequenceFlows()){
					Node node = (Node)flow.getTarget();
					isOk = isOk && checkNodeFollowingStartEvent(node);
				}
			}
		}
		return isOk;
	}

	/**
	 * checks if the node following a start event is an activity, intermediate message event or intermediate Timer event
	 * @param node node to be checked
	 * @return result of check
	 */
	private boolean checkNodeFollowingStartEvent(Node node) {
		boolean isOk = true;
		if(!((node instanceof Activity) || (node instanceof IntermediateMessageEvent) || (node instanceof IntermediateTimerEvent))){
			isOk = false;
			addError(node, "Nodes that follow start events have to be instances of Activities, Intermediate Message Events or Intermediate Timer Events only");
		}
		return isOk;
	}

	/**
	 * returns all start events of a given diagram
	 * @param diagram
	 * @return list of start events
	 */
	private Vector<StartEvent> getStartEventsFromDiagram(BPMNDiagram diagram) {
		Vector<StartEvent> startEvents = new Vector<StartEvent>();
		for (Container process : diagram.getProcesses()) {
			for (Node node : process.getChildNodes()) {
				if (node instanceof StartEvent)
					startEvents.add((StartEvent) node);
			}
		}
		return startEvents;
	}
	
	/**
	 * checks if the given lane has a resourcing type
	 * @param lane lane to be checked
	 * @return result of check
	 */
	public boolean checkLane(Lane lane){
		boolean isOk = true;
		if (lane.getResourcingType() == null || lane.getResourcingType().isEmpty()){
			isOk = false;
			addError(lane, "Please load the YAWL stencilset extension and choose a resourcing type for this lane.");
		}
		
		return isOk;
	}
}