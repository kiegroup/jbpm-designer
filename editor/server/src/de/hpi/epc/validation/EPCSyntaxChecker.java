package de.hpi.epc.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hpi.diagram.Diagram;
import de.hpi.diagram.DiagramEdge;
import de.hpi.diagram.DiagramNode;
import de.hpi.diagram.DiagramObject;
import de.hpi.diagram.verification.AbstractSyntaxChecker;

public class EPCSyntaxChecker extends AbstractSyntaxChecker {
/*	
	private static final String NO_SOURCE = "Each edge must have a source";
	private static final String NO_TARGET = "Each edge must have a target";
	private static final String NOT_CONNECTED = "Node must be connected with edges";
	private static final String NOT_CONNECTED_2 = "Node must be connected with more edges";
	private static final String TOO_MANY_EDGES = "Node has too many connected edges";
	private static final String NO_CORRECT_CONNECTOR = "Node is no correct connector";
	
	private static final String MANY_STARTS = "There must be only one start event";
	//private static final String MANY_ENDS = "There must be only one end event";
	
	private static final String FUNCTION_AFTER_OR = "There must be no functions after a splitting OR/XOR";
	private static final String PI_AFTER_OR = "There must be no process interface after a splitting OR/XOR";
	private static final String FUNCTION_AFTER_FUNCTION =  "There must be no function after a function";
	private static final String EVENT_AFTER_EVENT =  "There must be no event after an event";
	private static final String PI_AFTER_FUNCTION =  "There must be no process interface after a function";
	private static final String FUNCTION_AFTER_PI =  "There must be no function after a process interface";
*/
	private static final String NO_SOURCE = "EPC_NO_SOURCE";
	private static final String NO_TARGET = "EPC_NO_TARGET";
	private static final String NOT_CONNECTED = "EPC_NOT_CONNECTED";
	private static final String NOT_CONNECTED_2 = "EPC_NOT_CONNECTED_2";
	private static final String TOO_MANY_EDGES = "EPC_TOO_MANY_EDGES";
	private static final String NO_CORRECT_CONNECTOR = "EPC_NO_CORRECT_CONNECTOR";
	
	private static final String MANY_STARTS = "EPC_MANY_STARTS";
	//private static final String MANY_ENDS = "There must be only one end event";
	
	private static final String FUNCTION_AFTER_OR = "EPC_FUNCTION_AFTER_OR";
	private static final String PI_AFTER_OR = "EPC_PI_AFTER_OR";
	private static final String FUNCTION_AFTER_FUNCTION =  "EPC_FUNCTION_AFTER_FUNCTION";
	private static final String EVENT_AFTER_EVENT =  "EPC_EVENT_AFTER_EVENT";
	private static final String PI_AFTER_FUNCTION =  "EPC_PI_AFTER_FUNCTION";
	private static final String FUNCTION_AFTER_PI =  "EPC_FUNCTION_AFTER_PI";

	protected Diagram diagram;
	
	/**
	 * Here are some configuration options
	 */
	public boolean checkFunctionFollowsFunction;
	public boolean checkExactlyOneStartEvent;
	public boolean checkMustEndOnEvent;
	
	public EPCSyntaxChecker(Diagram diagram) {
		this.diagram = diagram;
		errors = new HashMap<String,String>();
		checkFunctionFollowsFunction = false;
		checkExactlyOneStartEvent = false;
		checkMustEndOnEvent = false;
	}

	public boolean checkSyntax() {
		errors.clear();
		if (diagram == null)
			return false;
		checkEdges();
		checkNodes();
		
		return errors.size() == 0;
	}
	
	protected void checkEdges() {
		for (DiagramEdge edge: diagram.getEdges()) {
			if (edge.getSource() == null)
				addError(edge, NO_SOURCE);
			if (edge.getTarget() == null) 
				addError(edge, NO_TARGET);
		}
	}
	
	protected void checkNodes() {
		List<DiagramNode> startEvents = new ArrayList<DiagramNode>();
		for (DiagramNode node: diagram.getNodes()) {
			int inAll = node.getIncomingEdges().size();
			int outAll = node.getOutgoingEdges().size();
			if (inAll == 0 && outAll == 0){
				addError(node, NOT_CONNECTED);
				continue;
			}
			int in = numberOfControlFlows(node.getIncomingEdges());
			int out =  numberOfControlFlows(node.getOutgoingEdges());
			if ("Event".equals(node.getType())){
				//if (in == 1 && out == 0) endEvents.add(node);
				if (in == 0 && out == 1) startEvents.add(node);
				else if (in > 1 || out > 1) addError(node, TOO_MANY_EDGES);
				for (DiagramNode next : getNextEventsOrFunctions(node.getOutgoingEdges())){
					if ("Event".equals(next.getType())) addError(next, EVENT_AFTER_EVENT);
				}
			}
			else if ("Function".equals(node.getType())){
				if (in == 1 && out == 0){
					if (checkMustEndOnEvent) addError(node, NOT_CONNECTED_2);
				}
				else if (in > 1 || out > 1) addError(node, TOO_MANY_EDGES);
				else if (in == 0 && out == 1) addError(node, NOT_CONNECTED_2);
				for (DiagramNode next : getNextEventsOrFunctions(node.getOutgoingEdges())){
					if (checkFunctionFollowsFunction && "Function".equals(next.getType())) addError(next, FUNCTION_AFTER_FUNCTION);
					if (checkFunctionFollowsFunction && "ProcessInterface".equals(next.getType())) addError(next, PI_AFTER_FUNCTION);
				}
			}
			else if ("ProcessInterface".equals(node.getType())){
				if (in > 1 || out > 1) addError(node, TOO_MANY_EDGES);
				else if (in == 0 && out == 0) addError(node, NOT_CONNECTED_2);
				for (DiagramNode next : getNextEventsOrFunctions(node.getOutgoingEdges())){
					if (checkFunctionFollowsFunction && "Function".equals(next.getType())) addError(next, FUNCTION_AFTER_PI);
				}
				
			}
			else if ("XorConnector".equals(node.getType()) || "OrConnector".equals(node.getType())){
				if (in == 1 && out >= 2){
					for (DiagramNode next : getNextEventsOrFunctions(node.getOutgoingEdges())){
						if ("Function".equals(next.getType())){
							addError(node, FUNCTION_AFTER_OR);
							break;
						} else if ("ProcessInterface".equals(next.getType())){
							addError(next, PI_AFTER_OR);
							break;
						}
					}
				} else if (in >= 2 && out == 1){
					// nothing todo
				} else {
					addError(node, NO_CORRECT_CONNECTOR);
				}
			}
			else if ("AndConnector".equals(node.getType())){
				if ( ! ( (in >= 2 && out == 1) || (in == 1 && out >= 2) ) ){
					addError(node, NO_CORRECT_CONNECTOR);
				}
			}
		}
		if (checkExactlyOneStartEvent && startEvents.size() > 1){
			for (DiagramNode n : startEvents){
				addError(n, MANY_STARTS);
			}
		}
//		if (endEvents.size() > 1){
//			for (DiagramNode n : endEvents){
//				addError(n, MANY_ENDS);
//			}
//		}
	}
	
	protected void addError(DiagramObject obj, String errorCode) {
		String key = obj.getResourceId();
		String oldErrorCode = errors.get(key);
		if (oldErrorCode != null && oldErrorCode.startsWith("MULT_ERRORS: ")){
			errors.put(obj.getResourceId(), oldErrorCode+", "+errorCode);
		} else if (oldErrorCode != null){
			errors.put(obj.getResourceId(), "Multiple Errors: "+oldErrorCode+", "+errorCode);
		} else {
			errors.put(obj.getResourceId(), errorCode);
		}
	}
	
	private int numberOfControlFlows(List<DiagramEdge> edges){
		int result = 0;
		for (DiagramEdge edge : edges){
			if ("ControlFlow".equals(edge.getType())) result++;
		}
		return result;
	}
	
	private List<DiagramNode>getNextEventsOrFunctions(List<DiagramEdge> edges){
		List<DiagramEdge> newEdges = new ArrayList<DiagramEdge>();
		List<DiagramNode> result = new ArrayList<DiagramNode>();
		for (DiagramEdge edge : edges){
			newEdges.add(edge);
		}
		return getNextEventsOrFunctions(newEdges, result);
	}
	
	private List<DiagramNode>getNextEventsOrFunctions(List<DiagramEdge> edges, List<DiagramNode> result){
		List<DiagramEdge> newEdges = new ArrayList<DiagramEdge>();
		for (DiagramEdge edge : edges){
			if ("ControlFlow".equals(edge.getType())){
				DiagramNode target = edge.getTarget();
				// In broken diagrams, target can be null. Therefore
				// the syntax check shouldn't depend on control flow!!
				if(target == null)
					break;
				if ("Function".equals(target.getType()) || "Event".equals(target.getType()) || "ProcessInterface".equals(target.getType())){
					result.add(target);
				} else {
					newEdges.addAll(target.getOutgoingEdges());
				}
			}
		}
		if (newEdges.size() > 0){
			return getNextEventsOrFunctions(newEdges, result);
		}
		return result;
	}

}
