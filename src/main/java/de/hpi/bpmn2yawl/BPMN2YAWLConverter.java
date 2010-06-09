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

import de.hpi.yawl.*;
import de.hpi.yawl.YMultiInstanceParam.CreationMode;
import de.hpi.yawl.resourcing.DistributionSet;
import de.hpi.yawl.resourcing.ResourcingType;
import de.hpi.yawl.resourcing.InitiatorType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.Activity;
import de.hpi.bpmn.Assignment;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.EndTerminateEvent;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.IntermediateErrorEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.IntermediatePlainEvent;
import de.hpi.bpmn.IntermediateTimerEvent;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.Property;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Task;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.XOREventBasedGateway;
import de.hpi.bpmn.DataObject;
import de.hpi.bpmn.SequenceFlow.ConditionType;

/**
 * converts a given BPMN diagram to a YAWL diagram
 */
public class BPMN2YAWLConverter {

	/**
	 * counter to give each node a unique name for the YAWL engine
	 */
	private int nodeCount = 0;
	
	/**
	 * hash map for looping activities
	 */
	private HashMap<YDecomposition, LinkedList<Node>> loopingActivities = new HashMap<YDecomposition, LinkedList<Node>>();
	private HashMap<Node, ResourcingType> resourcingNodeMap;

	/**
	 * translates a BPMN Diagram to a YAWL model
	 * @param diagram the BPMN diagram
	 * @param poolIndex index of pool to get the appropiate pool in the diagram
	 * @param resourcingNodeMap node map for YAWL resourcing info
	 * @return the mapped diagram serialized to XML
	 */
	public String translate(BPMNDiagram diagram, int poolIndex, HashMap<Node, ResourcingType> resourcingNodeMap) {
		Container pool = diagram.getProcesses().get(poolIndex);
		YModel model = new YModel("mymodel" + poolIndex);
		model.setDataTypeDefinition(diagram.getDataTypeDefinition());
		this.resourcingNodeMap = resourcingNodeMap;
			
		// YAWL
		mapDecomposition(diagram, model, pool);
			
		return model.writeToYAWL();
	}

	/**
	 * mappes pools or subprocesses to decompositions (contain net information)
	 * @param diagram the BPMN diagram
	 * @param model the YAWL model to which the decomposition is added 
	 * @param graph the BPMN element that should be mapped to a decomposition
	 * @return the mapped decomposition
	 */
	private YDecomposition mapDecomposition(BPMNDiagram diagram, YModel model, Container graph) {

		YDecomposition dec = null;
		HashMap<Node, YNode> nodeMap = new HashMap<Node, YNode>();
		LinkedList<Node> gateways = new LinkedList<Node>();
		LinkedList<Activity> withEventHandlers = new LinkedList<Activity>();
		LinkedList<EndTerminateEvent> terminateEvents = new LinkedList<EndTerminateEvent>();
		
		//check for Subprocess
		if (graph instanceof SubProcess) {
			String subProcessLabel = ((SubProcess)graph).getLabel().replace(" ", "");
			dec = model.createDecomposition(generateId(subProcessLabel));
		} else{
			//if it is not a Subprocess, it is the main decomposition
			dec = model.createDecomposition("OryxBPMNtoYAWL_Net");
			dec.setRootNet(true);
		}
		
		// Map process elements
		for (Node node : graph.getChildNodes()) {
			if (node instanceof Activity && ((Activity)node).getAttachedEvents().size() > 0) {
				withEventHandlers.add((Activity)node);
			}
			
			if(node instanceof IntermediateEvent){
				if(((IntermediateEvent) node).isAttached())
					continue;
			}
			
			YNode ynode = mapProcessElement(diagram, model, dec, node, nodeMap);
			if ((ynode == null) && (node instanceof Gateway))
				gateways.add(node);
		}		
		
		// Map gateways
		for (Node node : gateways){
			mapGateway(model, dec, node, nodeMap);
		}
		
		//check decomposition's input and output condition
		if (dec.getOutputCondition() == null)
			dec.createOutputCondition(generateId("Output"), "Output Condition");
		
		if (dec.getInputCondition() == null){
			dec.createInputCondition(generateId("Input"), "Input Condition");
			if(nodeMap.isEmpty())
				dec.connectInputToOutput();	
		}
		
		// Map data objects
		for (DataObject dataObject : diagram.getDataObjects()) {
			mapDataObject(model, dec, dataObject, nodeMap);
		}
		
		// Map links and edges
		linkYawlElements(nodeMap, dec, terminateEvents);

		// Event handlers
		for (Activity act : withEventHandlers)
			mapExceptions(model, dec, act, nodeMap);
		
		rewriteLoopingTasks(nodeMap);
		
		for(EndTerminateEvent terminate : terminateEvents){
			YNode sourceTask = nodeMap.get(terminate);
			mapEndTerminateToCancellationSet(sourceTask, nodeMap);
		}
		
		return dec;
	}

	/**
	 * Graph rewriting to deal with looping activities
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 */
	private void rewriteLoopingTasks(HashMap<Node, YNode> nodeMap) {
		for (YDecomposition decomposition : loopingActivities.keySet()) {
			LinkedList<Node> activities = loopingActivities.get(decomposition);
			for (Node activityNode : activities) {
				YTask task = (YTask)nodeMap.get(activityNode);
				
				//add a splitting task and seperate the task from split rules, if it is an AND split
				if (task.getSplitType() == SplitJoinType.AND) {
					// Factor out the split decorator to allow a self loop
					YTask split = decomposition.createTask(generateId(), "SplitTask");

					for (YEdge edge : task.getOutgoingEdges())
						decomposition.createEdge(split, edge.getTarget(), false, "", 0);

					task.getOutgoingEdges().clear();
					
					decomposition.createEdge(task, split, false, "", 0);					
				}

				//add a joining task and seperate the task from joining rules, if it is an AND join
				if (task.getJoinType() == SplitJoinType.AND) {
					// Factor out the split decorator to allow a self loop
					YTask join = decomposition.createTask(generateId(), "JoinTask", 
							SplitJoinType.AND, SplitJoinType.AND);

					for (YEdge edge : task.getIncomingEdges())
							decomposition.createEdge(edge.getSource(), join, false, "", 0);
						
					task.getIncomingEdges().clear();
					
					decomposition.createEdge(join, task, false, "", 0);					
				}
				Activity activity = (Activity)activityNode;
				String predicate = "";
				if(activity.getLoopType() == Activity.LoopType.Standard)
					predicate = activity.getLoopCondition();
				else if (isLoopingActivityBySequenceFlow(activityNode))
					predicate = getExpressionForLoopingActivityBySequenceFlow(activityNode);

				// Self loop edge
				decomposition.createEdge(task, task, false, predicate, 1);
				task.setSplitType(SplitJoinType.XOR);
				task.setJoinType(SplitJoinType.XOR);
			}
		}
	}

	/**
	 * mappes BPMN exceptions - events attached to activities - to YAWL
	 * @param model containing model
	 * @param decomposition containing decomposition
	 * @param activity activity that has attached events
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 */
	private void mapExceptions(YModel model, YDecomposition decomposition, Activity activity,
			HashMap<Node, YNode> nodeMap) {
		YTask compositeTask = (YTask) nodeMap.get(activity);
		YTask sourceTask = compositeTask;
		boolean splitAttached = false;
		LinkedList<IntermediateTimerEvent> timers = new LinkedList<IntermediateTimerEvent>();
		
		//seperate between timer and other attached events
		for (IntermediateEvent eventHandler : activity.getAttachedEvents()) {
			if (eventHandler instanceof IntermediateTimerEvent)
				timers.add((IntermediateTimerEvent)eventHandler);
			else
				splitAttached = true;
		}
		
		if (splitAttached) {
			if (compositeTask.getOutgoingEdges().size() > 1) {
				//create a new splitting task after composite task
				YTask newSplit = decomposition.createTask(generateId(), "newSplitTask");
				
				for (YEdge edge : compositeTask.getOutgoingEdges())
					decomposition.createEdge(newSplit, edge.getTarget(), false, "", 0);
					
				compositeTask.getOutgoingEdges().clear();
				
				decomposition.createEdge(compositeTask, newSplit, false, "", 0);
				sourceTask = newSplit;
				newSplit.setSplitType(compositeTask.getSplitType());
			}
			compositeTask.setSplitType(SplitJoinType.XOR);
		}
		
		for (IntermediateEvent eventHandler : activity.getAttachedEvents()) {
			if (eventHandler instanceof IntermediateErrorEvent) {
				mapErrorException(model, decomposition, nodeMap, compositeTask, sourceTask,
						(IntermediateErrorEvent) eventHandler);
			} else if (eventHandler instanceof IntermediateTimerEvent) {
				mapTimerException(model, decomposition, nodeMap, compositeTask, sourceTask,
						(IntermediateTimerEvent)eventHandler, timers);				
			}
		}
	}

	/**
	 * mappes attached Intermediate Timer Events as Timer Exceptions with the composite task in the cancellation set of the mapped timer task
	 * and the timer task in the cancellation set of the composite task
	 * @param model containing model
	 * @param decomposition containing decomposition
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 * @param compositeTask the composite task
	 * @param sourceTask the source task
	 * @param eventHandler the BPMN event
	 * @param timers linked list of BPMN Intermediate Timer Events attached to an activity
	 */
	private void mapTimerException(YModel model, YDecomposition decomposition,
			HashMap<Node, YNode> nodeMap, YTask compositeTask, YTask sourceTask,
			IntermediateTimerEvent eventHandler, LinkedList<IntermediateTimerEvent> timers) {
		
		YTask timerEventTask = (YTask)mapTimerEvent(model, decomposition, eventHandler, nodeMap, true);
		YNode targetTask = nodeMap.get(eventHandler.getOutgoingSequenceFlows().get(0).getTarget());
		decomposition.createEdge(timerEventTask, targetTask, false, "", 1);
		
		if (timers.size() == 0) 
			return;

		YNode predecesor = null;
		boolean needsLinking = false;
		if (compositeTask.getIncomingEdges().size() > 1) {
			predecesor = decomposition.createTask(generateId(), "Task");
			Task predecesorTask = new Task();
			nodeMap.put(predecesorTask, predecesor);
			needsLinking = true;
		} else {
			predecesor = (YNode)compositeTask.getIncomingEdges().get(0).getSource();

			if (predecesor instanceof YCondition) {
				YNode gw = decomposition.createTask(generateId(), "Task");

				YEdge edge = (YEdge) predecesor.getOutgoingEdges().get(0);
				decomposition.removeEdge(edge);

				decomposition.createEdge(predecesor, gw, false, "", 1);
				predecesor = gw;
				Task predecesorTask = new Task();
				nodeMap.put(predecesorTask, predecesor);
				needsLinking = true;
			} else if ((predecesor.getOutgoingEdges().size() > 1 && ((YTask)predecesor).getSplitType() != SplitJoinType.AND)) {
				; // TODO: factor out a AND split
			}
		}

		for (IntermediateTimerEvent timer : timers) {
			YTask timerTask = (YTask)nodeMap.get(timer);
			compositeTask.getCancellationSet().add(timerTask);
			timerTask.getCancellationSet().add(compositeTask);
			for (IntermediateTimerEvent another : timers) {
				if (!timer.equals(another))
					timerTask.getCancellationSet().add((YTask)nodeMap.get(another));
			}

			decomposition.createEdge(predecesor, timerTask, false, "", 1);
		}

		if (needsLinking) {
			decomposition.createEdge(predecesor, compositeTask, false, "", 1);
		}
	}

	/**
	 * mappes an Intermediate Error event to an exception task that sets the exception variable to true
	 * if executed
	 * @param model containing model
	 * @param decomposition containing decomposition
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 * @param compositeTask the composite task
	 * @param sourceTask the source task
	 * @param eventHandler the BPMN event (Intermediate Error Event)
	 */
	private void mapErrorException(YModel model, YDecomposition decomposition,
			HashMap<Node, YNode> nodeMap, YTask compositeTask, YTask sourceTask,
			IntermediateErrorEvent eventHandler) {
		
		YNode targetTask = nodeMap.get(eventHandler.getOutgoingSequenceFlows().get(0).getTarget());

		// PREDICATE & Mapping
		String varName =  sourceTask.getID();
		String predicate = String.format("/%s/%s_%s_exception/text()", decomposition.getID(), compositeTask.getID(), varName);
		String tag = String.format("%s_%s_exception", compositeTask.getID(), varName);
		String query = String.format("&lt;%s&gt;{%s}&lt;/%s&gt;", tag, predicate, tag);

		//TODO: defaultFlow!!!
		if(!sourceTask.getOutgoingEdges().isEmpty()){
			int edgeCounter = 2;
			for(YEdge edge : sourceTask.getOutgoingEdges()){
				if(edge.getOrdering() == 0){
					edge.setOrdering(edgeCounter++);
				}
				if(edge.getPredicate().isEmpty()){
					edge.setDefault(true);
				}
			}
		}
		decomposition.createEdge(sourceTask, targetTask, false, predicate, 1);

		//create the exception variable
		YVariable local = new YVariable();
		local.setName(tag);
		local.setType("boolean");
		local.setInitialValue("false");
		decomposition.getLocalVariables().add(local);

		YVariableMapping mapping = new YVariableMapping(query, local);

		compositeTask.getCompletedMappings().add(mapping);
		
		// Add control flow variables to composite task decomposition
		YVariable localVariable = new YVariable();
		localVariable.setName("_"+varName+"_exception");
		localVariable.setType("boolean");
		compositeTask.getDecomposesTo().getLocalVariables().add(localVariable);
		
		YVariable outputParam = new YVariable();
		outputParam.setName("_"+varName+"_exception");
		outputParam.setType("boolean");
		compositeTask.getDecomposesTo().getOutputParams().add(outputParam);
		
		for (YNode exceptionNode : compositeTask.getDecomposesTo().getNodes()) {
			if(exceptionNode instanceof YTask){
				YTask exceptionTask = (YTask)exceptionNode;
				
				if (exceptionTask.getID().contains("ErrorEvent")) {
					//variable mapping to set the exception variable to true
					String anotherQuery = String.format("&lt;%s&gt;true&lt;/%s&gt;", localVariable.getName(), localVariable.getName());
					YVariableMapping anotherMapping = new YVariableMapping(anotherQuery, localVariable);

					exceptionTask.getCompletedMappings().add(anotherMapping);
					
					// create an executable decomposition
					YDecomposition exceptionDec = null;
					exceptionDec = setTaskDecomposition(model, exceptionDec, exceptionTask);
					break;
				}
			}
		}
	}

	/**
	 * mappes AND, OR, Data Xor gateways to a splitting or joining task or adds a splitting
	 * or joining decorator to the predecessor or successor task
	 * first the task that gets the decorators is determined, then the decorators are added to task
	 * @param model containing model
	 * @param decomposition containing decomposition
	 * @param node BPMN node to be mapped
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 */
	private void mapGateway(YModel model, YDecomposition decomposition, Node node,
			HashMap<Node, YNode> nodeMap) {
		YTask task = null;
		boolean split = false, join = false;
		//determine to which task the decorator should be added
		if (node.getOutgoingSequenceFlows().size() > 1 && node.getIncomingSequenceFlows().size() > 1) {
			// Split and Join roles
			task = decomposition.createTask(generateId(), "Task");

			split = true; join = true;
		} else if (node.getOutgoingSequenceFlows().size() > 1) {
			// SPLIT role
			Node predNode = (Node) node.getIncomingSequenceFlows().get(0).getSource();
			YNode predTask = nodeMap.get(predNode);
			
			if (predTask == null || (predNode.getOutgoingSequenceFlows() != null && predNode.getOutgoingSequenceFlows().size() > 1) ||
					predTask instanceof YCondition)
				task = decomposition.createTask(generateId(), "Task");
			else
				task = (YTask)predTask;
			split = true;
		} else if (node.getIncomingSequenceFlows().size() > 1) {
			// JOIN role			
			Node succNode = (Node) node.getOutgoingSequenceFlows().get(0).getTarget();
			YNode succTask = nodeMap.get(succNode);
			
			if (succTask == null || (succNode.getIncomingSequenceFlows() != null && succNode.getIncomingSequenceFlows().size() > 1) ||
					succTask instanceof YCondition)
				task = decomposition.createTask(generateId(), "Task");
			else
				task = (YTask)succTask;
			join = true;
		}
		//set the split and join type of task
		if (node instanceof XORDataBasedGateway) {
			if (split)
				task.setSplitType(SplitJoinType.XOR);
			if (join)
				task.setJoinType(SplitJoinType.XOR);
		} else if (node instanceof ANDGateway) {
			if (split)
				task.setSplitType(SplitJoinType.AND);
			if (join)
				task.setJoinType(SplitJoinType.AND);
		} else if (node instanceof ORGateway) {
			if (split)
				task.setSplitType(SplitJoinType.OR);
			if (join)
				task.setJoinType(SplitJoinType.OR);
		}
		
		nodeMap.put(node, task);
	}
	
	/**
	 * determines if the given node is a looping node through control flow.
	 * The node is looping, if the third successor node of the given node is itself.
	 * The nodes between the given and the third successor node may only be AND, OR or
	 * Data-based Xor Gateways
	 * @param node the node to be tested if it is looping
	 * @return is node looping?
	 */
	private boolean isLoopingActivityBySequenceFlow(Node node){
		
		for(SequenceFlow firstSequenceFlow: node.getOutgoingSequenceFlows()){
			Node firstSuccessorNode = (Node) firstSequenceFlow.getTarget();
			if((firstSuccessorNode instanceof ANDGateway) || (firstSuccessorNode instanceof ORGateway) || (firstSuccessorNode instanceof XORDataBasedGateway)){
				for(SequenceFlow secondSequenceFlow: firstSuccessorNode.getOutgoingSequenceFlows()){
					Node secondSuccessorNode = (Node) secondSequenceFlow.getTarget();
					if((secondSuccessorNode instanceof ANDGateway) || (secondSuccessorNode instanceof ORGateway) || (secondSuccessorNode instanceof XORDataBasedGateway)){
						for(SequenceFlow thirdSequenceFlow: secondSuccessorNode.getOutgoingSequenceFlows()){
							Node thirdSuccessorNode = (Node) thirdSequenceFlow.getTarget();
							
							if(thirdSuccessorNode == node)
								return true;
						}
					}
				}
			}
		}	
		return false;
	}

	/**
	 * returns the condition expression of the outgoing sequence flow of the splitting gateway
	 * if the node is looping by control flow (see above)
	 * @param node the looping node
	 * @return the condition expression of the outgoing sequence flow of the splitting gateway
	 */
	private String getExpressionForLoopingActivityBySequenceFlow(Node node){
		String result = "";
		
		for(SequenceFlow firstSequenceFlow: node.getOutgoingSequenceFlows()){
			Node firstSuccessorNode = (Node) firstSequenceFlow.getTarget();
			if((firstSuccessorNode instanceof ANDGateway) || (firstSuccessorNode instanceof ORGateway) || (firstSuccessorNode instanceof XORDataBasedGateway)){
				for(SequenceFlow secondSequenceFlow: firstSuccessorNode.getOutgoingSequenceFlows()){
					if(secondSequenceFlow.getConditionType() == SequenceFlow.ConditionType.EXPRESSION){
						Node secondSuccessorNode = (Node) secondSequenceFlow.getTarget();
						if((secondSuccessorNode instanceof ANDGateway) || (secondSuccessorNode instanceof ORGateway) || (secondSuccessorNode instanceof XORDataBasedGateway)){
							for(SequenceFlow thirdSequenceFlow: secondSuccessorNode.getOutgoingSequenceFlows()){
								Node thirdSuccessorNode = (Node) thirdSequenceFlow.getTarget();
							
								if(thirdSuccessorNode == node)
									return secondSequenceFlow.getConditionExpression();
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * generates a unique id
	 * @return generated unique id
	 */
	private String generateId() {
		return generateId("gw");
	}
	
	/**
	 * generates a unique node id for the YAWL engine
	 * @param infix String that describes the node and becomes part of the name
	 * @return unique identifier
	 */
	private String generateId(String infix) {
		return "Node_" + infix + "_" + (nodeCount++);
	}

	/**
	 * mappes the given BPMN node to the according YAWL node
	 * Subprocess -> Composite Task
	 * Activity -> Task
	 * Start Plain Event -> Input Condition
	 * End Plain Event -> Output Condition
	 * XOR Event BasedGateway -> Condition
	 * Intermediate Timer Event -> Timer task
	 * Intermediate Message Event (predecessor node is an Event-based Gateway) -> Task
	 * Intermediate Event (predecessor node is a Gateway) -> Task
	 * Intermediate Plain Event -> Condition
	 * End Error Event -> Task
	 * End Terminate Event -> Task
	 * @param diagram the BPMN diagram
	 * @param model containing model
	 * @param node the BPMN node to be mapped
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 * @return the mapped YAWL node
	 */
	private YNode mapProcessElement(BPMNDiagram diagram, YModel model, YDecomposition dec, Node node, HashMap<Node, YNode> nodeMap) {
		YNode ynode = null;
		
		if (node instanceof SubProcess)
			ynode = mapCompositeTask(diagram, model, dec, (Activity)node, nodeMap);
		
		else if (node instanceof Activity)
			ynode = mapTask(model, dec, (Activity)node, nodeMap);
		
		else if (node instanceof StartPlainEvent)
			ynode = dec.createInputCondition(generateId("input"), "inputCondition");
		
		else if (node instanceof EndPlainEvent)
			ynode = dec.createOutputCondition(generateId("output"), "outputCondition");

		else if (node instanceof XOREventBasedGateway)
			ynode = mapConditionFromEventBased(model, dec, node, nodeMap);
		
		else if (node instanceof IntermediateTimerEvent)
			ynode = mapTimerEvent(model, dec, node, nodeMap, false);
		
		else if (node instanceof IntermediateMessageEvent && node.getIncomingSequenceFlows().get(0).getSource() instanceof XOREventBasedGateway)
			ynode = mapIntermediateMessageEvent(model, dec, node, nodeMap);
		
		else if (node instanceof IntermediateEvent && node.getIncomingSequenceFlows().get(0).getSource() instanceof Gateway)
			ynode = mapIntermediateEvent(model, dec, node, nodeMap);
		
		else if (node instanceof IntermediatePlainEvent && node.getOutgoingSequenceFlows().get(0).getTarget() instanceof Gateway) 
			ynode = dec.createCondition(generateId("plain"), "ConditionMappedFromIntermediatePlainEvent");
		
		else if (node instanceof EndErrorEvent)
			ynode = dec.createTask(generateId("ErrorEvent"), "TaskMappedFromErrorEvent");

		else if (node instanceof EndTerminateEvent)
			ynode = dec.createTask(generateId("endTerminate"), "CancellationTask");
		
		if (ynode != null)
			nodeMap.put(node, ynode);
			
		return ynode;
	}

	/**
	 * mappes data objects to YAWL variables and defines the task parameters according to
	 * associations
	 * @param model containing model
	 * @param decomposition containing decomposition
	 * @param dataObject the data object to be mapped
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 */
	private void mapDataObject(YModel model, YDecomposition decomposition, DataObject dataObject,
			HashMap<Node, YNode> nodeMap) {
		if((dataObject.getIncomingEdges().size() == 0) && (dataObject.getOutgoingEdges().size() == 0))
			return;
		
		//check, if the new variable name is used already
		//TODO: Take it out, if a dataSyntaxChecker exists
		for(YVariable variable : decomposition.getLocalVariables()){
			if(variable.getName().equalsIgnoreCase(dataObject.getLabel()))
				return;
		}
		
		//define the local variable and add it to decomposition
		YVariable localVar = new YVariable();
		localVar.setName(dataObject.getLabel());
		localVar.setType(dataObject.getDataType());
		localVar.setInitialValue(dataObject.getValue());
		decomposition.getLocalVariables().add(localVar);
		
		//set the input parameters for nodes that the data object references
		for (Edge edge : dataObject.getOutgoingEdges()){
			Node targetNode = (Node)edge.getTarget();
			YTask targetTask = (YTask) nodeMap.get(targetNode);
			
			String startQuery = "&lt;" + localVar.getName() + "&gt;{/" + decomposition.getID() + "/" + localVar.getName() + "/text()}&lt;/" + localVar.getName() +"&gt;";
			YVariableMapping localVarMapping = new YVariableMapping(startQuery, localVar);
			targetTask.getStartingMappings().add(localVarMapping);
			if(targetTask.getDecomposesTo() != null){
				targetTask.getDecomposesTo().getInputParams().add(localVar);
			}
		}
		
		//set the output parameters for nodes that reference the data object
		for (Edge edge : dataObject.getIncomingEdges()){
			Node sourceNode = (Node)edge.getSource();
			YTask sourceTask = (YTask) nodeMap.get(sourceNode);
			
			String completeQuery = "&lt;" + localVar.getName() + "&gt;{/" + sourceTask.getID() + "/" + localVar.getName() + "/text()}&lt;/" + localVar.getName() +"&gt;";
			YVariableMapping localVarMapping = new YVariableMapping(completeQuery, localVar);
			sourceTask.getCompletedMappings().add(localVarMapping);
			if(sourceTask.getDecomposesTo() != null){
				sourceTask.getDecomposesTo().getOutputParams().add(localVar);
			}
		}
		
	}

	/**
	 * mappes a BPMN timer event to a YAWL timer task
	 * @param model containing model
	 * @param decomposition containing decomposition
	 * @param node the Intermediate Timer Event to be mapped
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 * @param attached whether the node is attached to a activity (for error handling)
	 * @return the timer task
	 */
	private YNode mapTimerEvent(YModel model, YDecomposition decomposition, Node node,
			HashMap<Node, YNode> nodeMap, Boolean attached) {
		YTask timerTask = decomposition.createTask(generateId("timer"), "TimerTask");
		IntermediateTimerEvent timerEvent = (IntermediateTimerEvent)node;
		Date timeDate = null;
		
		//try to get the date (if it is in a appropiate format)
		try {
			if(!timerEvent.getTimeDate().isEmpty()){
				DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");
				timeDate = dateFormatter.parse(timerEvent.getTimeDate());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//create a YAWL timer object and attach it to the timer task
		YTimer timer = new YTimer(YTimer.Trigger.OnEnabled, timerEvent.getTimeCycle(), timeDate);
		timerTask.setTimer(timer);
		if(attached)
			timer.setTrigger(YTimer.Trigger.OnExecuting);
		
		//create a local timer variable
		YVariable timerVariable = new YVariable();
		timerVariable.setName(timerTask.getID() + "_timer");
		timerVariable.setType("string");
		timerVariable.setReadOnly(false);
		decomposition.getLocalVariables().add(timerVariable);
		
		//create mappings from the local timer variable to the timer task
		String timerQuery = "&lt;" + timerVariable.getName() + "&gt;{/" + decomposition.getID() + "/" + timerVariable.getName() + "/text()}&lt;/" + timerVariable.getName() + "&gt;";			
		YVariableMapping timerStartVarMap = new YVariableMapping(timerQuery, timerVariable);			
		timerTask.getStartingMappings().add(timerStartVarMap);
		
		//add the timer variable as a input parameter to the decomposition of the timer task
		YDecomposition taskDecomposition = null;
		taskDecomposition = setTaskDecomposition(model, taskDecomposition, timerTask);
		taskDecomposition.getInputParams().add(timerVariable);
		
		return timerTask;
	}

	/**
	 * sets edges between the mapped YAWL elements in the nodeMap
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 * @param decomposition containing decomposition
	 * @param terminateEvents list of End Terminate Events (are handled differently)
	 */
	private void linkYawlElements(HashMap<Node, YNode> nodeMap, YDecomposition decomposition,
			LinkedList<EndTerminateEvent> terminateEvents) {		
		Map<YNode, Integer> counter = new HashMap<YNode, Integer>();
		
		for (Node node : nodeMap.keySet()) {
			YEdge defaultEdge = null;
			YNode defaultSourceTask = null;
			YNode sourceTask;
			
			if ((node instanceof EndErrorEvent) || (node instanceof EndTerminateEvent)){
				//link the node directly to the decomposition's output condition
				sourceTask = nodeMap.get(node);				
				decomposition.createEdge(sourceTask, decomposition.getOutputCondition(), false, "", 1);
				if (node instanceof EndTerminateEvent)
					terminateEvents.add((EndTerminateEvent) node);
				continue;
			}
			for (SequenceFlow edge : node.getOutgoingSequenceFlows()) {
				String predicate = "";
				
				Node target = (Node) edge.getTarget();
				YNode targetTask = nodeMap.get(target);
				sourceTask = nodeMap.get(node);
				
				if (sourceTask == null || targetTask == null)
					continue;
				
				if ((sourceTask != targetTask) || (node instanceof Gateway && node == target)) {
					if (!counter.containsKey(sourceTask))
						counter.put(sourceTask, 0);
						
					Integer order = counter.get(sourceTask) + 1;
					counter.put(sourceTask, order);
					
					if(edge.getConditionType() == ConditionType.EXPRESSION){
						predicate = edge.getConditionExpression();
					} else if(edge.getConditionType() == ConditionType.DEFAULT){
						predicate = "";
						
						defaultSourceTask = sourceTask;
						order--;
						counter.put(sourceTask, order);
						defaultEdge = new YEdge(sourceTask, targetTask, true, predicate, 0);
						continue;
					}
					
					decomposition.createEdge(sourceTask, targetTask, false, predicate, order);
				}
			}
			if(defaultEdge != null){
				Integer order = counter.get(defaultSourceTask) + 1;
				counter.put(defaultSourceTask, order);
				
				defaultEdge.setOrdering(order);
				
				decomposition.addEdge(defaultEdge);
			}
		}
	}

	/**
	 * adds each node in the nodeMap to the terminateNode's cancellation set
	 * @param terminateNode the task mapped from Terminate Event
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 */
	private void mapEndTerminateToCancellationSet(YNode terminateNode, HashMap<Node, YNode> nodeMap) { 
		YTask terminateTask = (YTask)terminateNode;
		
		for(YNode ynode : nodeMap.values()){
			if((ynode instanceof YInputCondition) || (ynode instanceof YOutputCondition) 
					|| (ynode.equals(terminateNode))){
				continue;
			}
			
			terminateTask.getCancellationSet().add(ynode);
		}
	}

	/**
	 * maps an Event Based XOR Gateway to a YAWL condition
	 * @param model containing YAWL model
	 * @param decomposition containing decomposition
	 * @param node node to be mapped (Event Based XOR Gateway)
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 * @return the mapped YAWL condition
	 */
	private YNode mapConditionFromEventBased(YModel model, YDecomposition decomposition,
			Node node, HashMap<Node, YNode> nodeMap) {
		
		YCondition cond = null;
	
		YNode preYNode = nodeMap.get((Node)node.getIncomingSequenceFlows().get(0).getSource());
		if(preYNode instanceof YCondition)
			cond = (YCondition)preYNode;
		else
			cond = decomposition.createCondition(generateId("EXorGW"), "Condition");
		
		return cond;
	}
	
	/**
	 * maps a Intermediate Event to a YAWL Task
	 * @param model containing YAWL model
	 * @param decomposition containing decomposition
	 * @param node node to be mapped (Intermediate Event)
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 * @return the mapped YAWL Task
	 */
	private YNode mapIntermediateEvent(YModel model, YDecomposition decomposition, Node node,
			HashMap<Node, YNode> nodeMap) {
		
		YTask task = decomposition.createTask(generateId("intermediate"), "TaskMappedFromIntermediateEvent");
		
		YDecomposition taskDecomposition = null;
		taskDecomposition = setTaskDecomposition(model, taskDecomposition, task);
		
		return task;
	}
	
	/**
	 * maps an Intermediate Message Event to a YAWL task
	 * @param model containing YAWL model
	 * @param decomposition containing decomposition
	 * @param node node to be mapped (Intermediate Message Event)
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 * @return the mapped task
	 */
	private YNode mapIntermediateMessageEvent(YModel model, YDecomposition decomposition, 
			Node node, HashMap<Node, YNode> nodeMap) {
		
		YTask task = decomposition.createTask(generateId("msg"), "TaskMappedFromIntermediateMessageEvent");
		
		YDecomposition taskDecomposition = null;
		taskDecomposition = setTaskDecomposition(model, taskDecomposition, task);
		
		return task;
	}

	/**
	 * maps a BPMN Activity to a YAWL task
	 * @param model containing YAWL model
	 * @param decomposition containing decomposition
	 * @param activity BPMN Activity to be mapped
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 * @return the mapped task
	 */
	private YNode mapTask(YModel model, YDecomposition decomposition, Activity activity,
			HashMap<Node, YNode> nodeMap) {
		YDecomposition taskDecomposition = null;
		YNode task = mapTask(model, decomposition, activity, taskDecomposition);		
		return task;
	}

	/**
	 * maps a BPMN Subprocess to a YAWL composite task and maps 
	 * the given subprocess to a decomposition
	 * @param diagram BPMN Diagram
	 * @param model containing YAWL model
	 * @param decomposition containing decomposition
	 * @param activity BPMN activity to be mapped
	 * @param nodeMap the hashmap for BPMN and YAWL nodes
	 * @return mapped composite task
	 */
	private YNode mapCompositeTask(BPMNDiagram diagram, YModel model, YDecomposition decomposition,
			Activity activity, HashMap<Node, YNode> nodeMap) {
	
		YDecomposition subdec = mapDecomposition(diagram, model, (SubProcess)activity);
		YTask task = (YTask)mapTask(model, decomposition, activity, subdec);
		
		return task;
	}

	/**
	 * maps a BPMN activity to a YAWL task
	 * @param model containing model
	 * @param decomposition containing decomposition
	 * @param activity BPMN activity to be mapped
	 * @param subDecomposition already defined decomposition of the task to be mapped
	 * @return mapped task
	 */
	private YNode mapTask(YModel model, YDecomposition decomposition, Activity activity, 
			YDecomposition subDecomposition) {
		ArrayList<YVariable> taskVariablesLocal = new ArrayList<YVariable>();
		ArrayList<YVariable> taskVariablesInput = new ArrayList<YVariable>();
		ArrayList<YVariable> taskVariablesOutput = new ArrayList<YVariable>();
		
		YTask task = decomposition.createTask(generateId("task"), activity.getLabel());
		if (!activity.isMultipleInstance()){

			mapActivityProperties(decomposition, activity, task, taskVariablesLocal);

			mapAllActivityAssignments(decomposition, activity, taskVariablesLocal,
					taskVariablesInput, taskVariablesOutput, task);

			assignParametersToContainingDecomposition(decomposition, taskVariablesLocal,
					taskVariablesInput, taskVariablesOutput);

			if(subDecomposition == null){
				//add a new decomposition for the task to the model
				subDecomposition = setTaskDecomposition(model, subDecomposition, task);
				assignParametersToSubDecomposition(subDecomposition, taskVariablesLocal,
						taskVariablesInput, taskVariablesOutput);
			}
			//assign the subdecomposition to the task
			task.setDecomposesTo(subDecomposition);
		}
		else {
			// Decomposition
			subDecomposition = setTaskDecomposition(model, subDecomposition, task);
			
			mapMultipleInstanceInfo(decomposition, activity, task);	
		}
		
		if (activity.getLoopType() == Activity.LoopType.Standard) {
			if (!loopingActivities.containsKey(decomposition))
				loopingActivities.put(decomposition, new LinkedList<Node>());
			loopingActivities.get(decomposition).add(activity);
		}
		
		if(isLoopingActivityBySequenceFlow(activity)){
			if (!loopingActivities.containsKey(decomposition))
				loopingActivities.put(decomposition, new LinkedList<Node>());
			loopingActivities.get(decomposition).add(activity);
		}
		if (activity instanceof Task)
			mapTaskResourcingInfo((Task)activity, task);
		
		return task;
	}

	/**
	 * sets and maps multiple instance information from the given BPMN activity to the 
	 * given YAWL task
	 * @param decomposition containing decomposition
	 * @param activity BPMN multiple instance activity
	 * @param task the YAWL task to become a multiple instance task
	 */
	private void mapMultipleInstanceInfo(YDecomposition decomposition, Activity activity,
			YTask task) {
		task.setIsMultipleTask(true);
		task.setXsiType("MultipleInstanceExternalTaskFactsType");
			
		YMultiInstanceParam miParam = mapMultiInstanceParameters(activity);
		task.setMiParam(miParam);
		
		YVariable local = defineInputStringVariable(task);
		//decomposition.getInputParams().add(local);
		decomposition.getLocalVariables().add(local);
			
		YVariable inputParam = defineInputStringVariable(task);
		task.getDecomposesTo().getInputParams().add(inputParam);
		
		miParam.setMiDataInput(mapMiDataInput(decomposition, local));
		miParam.setMiDataOutput(mapMiDataOutput(decomposition, local));
	}

	/**
	 * generates a YAWl MIDataOutput object with variable mapping rules
	 * @param decomposition containing decomposition
	 * @param local variable to which data is mapped
	 * @return YAWL MIDataOutput object
	 */
	private YMIDataOutput mapMiDataOutput(YDecomposition decomposition, YVariable local) {
		YMIDataOutput miDataOutput = new YMIDataOutput();
		miDataOutput.setFormalOutputExpression("/" + decomposition.getID() + "/" + local.getName());
		miDataOutput.setOutputJoiningExpression(" ");
		miDataOutput.setResultAppliedToLocalVariable(local);
		return miDataOutput;
	}

	/**
	 * generates a YAWl MIDataInput object with variable mapping rules
	 * @param decomposition
	 * @param input input parameter to which data is mapped 
	 * @return YAWL MIDataInput object
	 */
	private YMIDataInput mapMiDataInput(YDecomposition decomposition, YVariable input) {
		YMIDataInput miDataInput = new YMIDataInput();
		miDataInput.setExpression("/" + decomposition.getID() + "/" + input.getName());
		miDataInput.setSplittingExpression(" ");
		miDataInput.setFormalInputParam(input);
		return miDataInput;
	}

	/**
	 * sets a input variable of type string for given task
	 * @param task YAWL task
	 * @return YAWL variable
	 */
	private YVariable defineInputStringVariable(YTask task) {
		YVariable local = new YVariable();
		local.setName(task.getID() + "_input");
		local.setType("string");
		return local;
	}

	/**
	 * creates an executable subdecomposition for a given task
	 * @param model YAWL model that will contain the new decomposition
	 * @param subDecomposition decomposition to be created, if not already created
	 * @param task YAWL task that refers to the subdecomposition
	 * @return task's decomposition
	 */
	private YDecomposition setTaskDecomposition(YModel model,
			YDecomposition subDecomposition, YTask task) {
		if(subDecomposition == null){
			subDecomposition = model.createDecomposition(task.getID());
			subDecomposition.setXSIType(XsiType.WebServiceGatewayFactsType);
			task.setDecomposesTo(subDecomposition);
		}
		return subDecomposition;
	}

	/**
	 * creates a YAWL Multi Instance Parameter object according to BPMN activity setting
	 * @param activity BPMN Multiple instance activity
	 * @return YAWL Multi Instance Parameter object
	 */
	private YMultiInstanceParam mapMultiInstanceParameters(Activity activity) {
		//the number 2147483647 stands for infinite in YAWL
		YMultiInstanceParam param = new YMultiInstanceParam();
		param.setMinimum(1);
		param.setMaximum(2147483647);

		mapMultipleInstanceThreshold(activity, param);

		param.setCreationMode(CreationMode.STATIC);
		return param;
	}

	/**
	 * sets the threshold for a multiple instance activity
	 * @param activity BPMN multi instance activity
	 * @param param YAWL Multi Instance Parameter object
	 */
	private void mapMultipleInstanceThreshold(Activity activity,
			YMultiInstanceParam param) {
		// the number 2147483647 stands for infinite in YAWL
		if(activity.getMiFlowCondition() == Activity.MIFlowCondition.One){
			param.setThreshold(1);
		} else if (activity.getMiFlowCondition() == Activity.MIFlowCondition.All){
			param.setThreshold(2147483647);
		} else if (activity.getMiFlowCondition() == Activity.MIFlowCondition.Complex){
			param.setThreshold(2147483647);
		}
	}

	/**
	 * copies all variables, input and output parameters of a task to the containing decomposition
	 * @param decomposition containing decomposition
	 * @param taskVariablesLocal list of local variables of a task
	 * @param taskVariablesInput list of input parameters of a task
	 * @param taskVariablesOutput list of output parameters of a task
	 */
	private void assignParametersToContainingDecomposition(YDecomposition decomposition,
			ArrayList<YVariable> taskVariablesLocal,
			ArrayList<YVariable> taskVariablesInput,
			ArrayList<YVariable> taskVariablesOutput) {
		decomposition.getLocalVariables().addAll(taskVariablesLocal);
		decomposition.getInputParams().addAll(taskVariablesInput);
		decomposition.getOutputParams().addAll(taskVariablesOutput);
	}

	/**
	 * attaches YAWL resourcing information from the BPMN activity to the YAWL task
	 * @param bpmnTask BPMN task
	 * @param task YAWL task
	 */
	private void mapTaskResourcingInfo(Task bpmnTask, YTask task) {
		YResourcing resourcingParam = new YResourcing();

		mapOfferInfo(bpmnTask, resourcingParam);
		mapAllocateInfo(bpmnTask, resourcingParam);
		mapStartInfo(bpmnTask, resourcingParam);

		task.setResourcing(resourcingParam);

		if (bpmnTask.getParent() instanceof Lane){
			//get the resourcing object corresponding to the lane
			ResourcingType resource = resourcingNodeMap.get(bpmnTask.getParent());
			DistributionSet distributionSet = new DistributionSet();
			distributionSet.getInitialSetList().add(resource);

			if(resourcingParam.getOffer().equals(InitiatorType.SYSTEM))
				resourcingParam.setOfferDistributionSet(distributionSet);

			if(resourcingParam.getAllocate().equals(InitiatorType.SYSTEM))
				resourcingParam.setAllocateDistributionSet(distributionSet);
		}
	}

	/**
	 * maps the initiator type for the starting the task
	 * @param bpmnTask BPMN task
	 * @param resourcingParam Resourcing Parameter object
	 */
	private void mapStartInfo(Task bpmnTask, YResourcing resourcingParam) {
		if((bpmnTask.getYawl_startedBy() != null) && bpmnTask.getYawl_startedBy().toLowerCase().equals("system"))
			resourcingParam.setStart(InitiatorType.SYSTEM);
		else
			// by default set it to user
			resourcingParam.setStart(InitiatorType.USER);
	}

	/**
	 * maps the initiator type for the allocating the task
	 * @param bpmnTask BPMN task
	 * @param resourcingParam Resourcing Parameter object
	 */
	private void mapAllocateInfo(Task bpmnTask, YResourcing resourcingParam) {
		if((bpmnTask.getYawl_allocatedBy() != null) && bpmnTask.getYawl_allocatedBy().toLowerCase().equals("system"))
			resourcingParam.setAllocate(InitiatorType.SYSTEM);
		else
			// by default set it to user
			resourcingParam.setAllocate(InitiatorType.USER);
	}

	/**
	 * maps the initiator type for the offering the task
	 * @param bpmnTask BPMN task
	 * @param resourcingParam Resourcing Parameter object
	 */
	private void mapOfferInfo(Task bpmnTask, YResourcing resourcingParam) {
		if((bpmnTask.getYawl_offeredBy() != null) && bpmnTask.getYawl_offeredBy().toLowerCase().equals("system"))
			resourcingParam.setOffer(InitiatorType.SYSTEM);
		else
			// by default set it to user
			resourcingParam.setOffer(InitiatorType.USER);
	}

	/**
	 * assigns the task variables to the decomposition of the task
	 * @param taskDecomposition decomposition of the task
	 * @param taskVariablesLocal local variables
	 * @param taskVariablesInput input parameters
	 * @param taskVariablesOutput output parameters
	 */
	private void assignParametersToSubDecomposition(YDecomposition taskDecomposition,
			ArrayList<YVariable> taskVariablesLocal,
			ArrayList<YVariable> taskVariablesInput,
			ArrayList<YVariable> taskVariablesOutput) {
		
		taskDecomposition.getInputParams().addAll(taskVariablesLocal);
		taskDecomposition.getOutputParams().addAll(taskVariablesLocal);
		
		taskDecomposition.getInputParams().addAll(taskVariablesInput);

		taskDecomposition.getOutputParams().addAll(taskVariablesOutput);
	}

	/**
	 * maps all assignments of an activity to input and output parameters
	 * @param decomposition containing decomposition
	 * @param activity activity containing assignments
	 * @param taskVariablesLocal list of local variables
	 * @param taskVariablesInput list of input parameters
	 * @param taskVariablesOutput list of output parameters
	 * @param task YAWL task
	 */
	private void mapAllActivityAssignments(YDecomposition decomposition,
			Activity activity, ArrayList<YVariable> taskVariablesLocal,
			ArrayList<YVariable> taskVariablesInput,
			ArrayList<YVariable> taskVariablesOutput, YTask task) {
		if(activity.getAssignments().size() > 0){

			for(Assignment assignment : activity.getAssignments()){
				 
				if(assignment.getAssignTime() == Assignment.AssignTime.Start){
					mapActivityAssignments(decomposition, taskVariablesLocal,
							taskVariablesInput, task, assignment,
							task.getStartingMappings(), decomposition.getID());
				}
				
				if(assignment.getAssignTime() == Assignment.AssignTime.End){
					mapActivityAssignments(decomposition, taskVariablesLocal,
							taskVariablesOutput, task, assignment,
							task.getCompletedMappings(), task.getID());
				}
			}

		}
	}
	
	/**
	 * generic method, maps an assignment to a local variable, adds it as a parameter
	 *  and defines the variable mapping between parameter and local variable
	 * @param decomposition containing decomposition
	 * @param taskVariablesLocal list of local variables
	 * @param taskVariables list of parameters
	 * @param task YAWL task
	 * @param assignment BPMN assignment to be mapped
	 * @param taskMapping list of mappings of a task (starting or completing mappings)
	 * @param querySourceId id of the variable source
	 */
	private void mapActivityAssignments(YDecomposition decomposition,
			ArrayList<YVariable> taskVariablesLocal,
			ArrayList<YVariable> taskVariables, YTask task,
			Assignment assignment,
			ArrayList<YVariableMapping> taskMapping,
			String querySourceId) {
		Boolean propertyIsMapped = false;
		YVariable mappedVariable = null;
		
		//the mappings have to be accessed, because the task can still have no decomposition
		taskVariables.addAll(taskVariablesLocal);
		for (YVariable variable : taskVariables) {
			if(variable.getName().equalsIgnoreCase(assignment.getTo())){
				propertyIsMapped = true;
				mappedVariable = variable;
				break;
			}
		}
		
		if(!propertyIsMapped){
			//add a local variable in the given decomposition
			mappedVariable = new YVariable();
			
			mappedVariable.setName(assignment.getTo());
			mappedVariable.setType("string");
			
			taskVariablesLocal.add(mappedVariable);
		}
		
		//set the variable mappings for the task
		String query = "&lt;" + mappedVariable.getName() + "&gt;{/" + querySourceId + "/" + mappedVariable.getName() + "/text()}&lt;/" + mappedVariable.getName() +"&gt;";
		Boolean sameMappingExists = false;
		for(YVariableMapping mapping : taskMapping){
			if(mapping.getQuery().equalsIgnoreCase(query)){
				sameMappingExists = true;
				break;
			}
		}
		if(!sameMappingExists){
			YVariableMapping localVarMap = new YVariableMapping(query, mappedVariable);			
			taskMapping.add(localVarMap);
		}
	}

	/**
	 * maps the properties of a BPMN activity to a local variable
	 * and defines variable mappings for the given task
	 * @param decomposition containing decomposition
	 * @param activity activity containing properties
	 * @param task YAWL task
	 * @param taskVariablesLocal list of local variables
	 */
	private void mapActivityProperties(YDecomposition decomposition, Activity activity,
			YTask task, ArrayList<YVariable> taskVariablesLocal) {
		if(activity.getProperties().size() > 0){
			
			for(Property property : activity.getProperties()){
				//add a local variable in the given decomposition
				YVariable mappedVariable = new YVariable();
				
				mappedVariable.setName(property.getName());
				if(!property.getType().equalsIgnoreCase("null"))
					mappedVariable.setType(property.getType().toLowerCase());
				else
					//set string as the default type if no type specified
					mappedVariable.setType("string");
				mappedVariable.setInitialValue(property.getValue());
				
				taskVariablesLocal.add(mappedVariable);
				
				//set the variable mappings for the task
				String startQuery = "&lt;" + mappedVariable.getName() + "&gt;{/" + decomposition.getID() + "/" + mappedVariable.getName() + "/text()}&lt;/" + mappedVariable.getName() +"&gt;";			
				YVariableMapping localStartVarMap = new YVariableMapping(startQuery, mappedVariable);			
				task.getStartingMappings().add(localStartVarMap);
				
				String completeQuery = "&lt;" + mappedVariable.getName() + "&gt;{/" + task.getID() + "/" + mappedVariable.getName() + "/text()}&lt;/" + mappedVariable.getName() +"&gt;";
				YVariableMapping localCompleteVarMap = new YVariableMapping(completeQuery, mappedVariable);			
				task.getCompletedMappings().add(localCompleteVarMap);
			}			
		}
	}	
}
