package de.hpi.diagram.reachability;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;

import de.hpi.petrinet.Transition;

public class ReachabilityPath<FlowObject, Marking> implements Cloneable{
	LinkedList<ReachabilityTransition<FlowObject, Marking>> transitionPath;

	public ReachabilityPath() {
		this(new LinkedList<ReachabilityTransition<FlowObject, Marking>>());
	}

	protected ReachabilityPath(LinkedList<ReachabilityTransition<FlowObject, Marking>> transitionPath) {
		this.transitionPath = transitionPath;
	}

	public void append(ReachabilityTransition<FlowObject, Marking> flowObject) {
		transitionPath.add(flowObject);
	}

	public void prepend(ReachabilityTransition<FlowObject, Marking> flowObject) {
		transitionPath.addFirst(flowObject);
	}
	
	public boolean contains(ReachabilityTransition<FlowObject, Marking> flowObject){
		return transitionPath.contains(flowObject);
	}
	
	public ReachabilityPath<FlowObject, Marking> clone(){
		LinkedList<ReachabilityTransition<FlowObject, Marking>> clonedTransitionList = (LinkedList<ReachabilityTransition<FlowObject, Marking>>)this.transitionPath.clone();
		return new ReachabilityPath<FlowObject, Marking>(clonedTransitionList);
	}
	
	/**
	 * Calculates one examplary path leading from fromMarking to toMarking.
	 * @param <Diagram> Diagram class of interest
	 * @param <FlowObject> Flow object class of interest
	 * @param <Marking> Marking class of interest
	 * @param rg
	 * @param fromMarking
	 * @param toMarking
	 * @return The calculated examplary path, {@code null} is no path has been found.
	 */
	public static <Diagram, FlowObject, Marking> ReachabilityPath<FlowObject, Marking> calculate(
			ReachabilityGraph<Diagram, FlowObject, Marking> rg,
			Marking fromMarking, Marking toMarking) {
		ReachabilityNode<Marking> fromMarkingNode = rg.findByMarking(fromMarking);
		ReachabilityNode<Marking> toMarkingNode = rg.findByMarking(toMarking);
		return calculate(rg, new ReachabilityPath<FlowObject, Marking>(), fromMarkingNode, toMarkingNode);
	}
	
	protected static <Diagram, FlowObject, Marking> ReachabilityPath<FlowObject, Marking> calculate(
			ReachabilityGraph<Diagram, FlowObject, Marking> rg,
			ReachabilityPath<FlowObject, Marking> path,
			ReachabilityNode<Marking> fromMarking, ReachabilityNode<Marking> toMarking) {
		
		// End of search reached
		if(toMarking.getMarking().equals(fromMarking.getMarking()))
			return path;
		
		for(ReachabilityTransition<FlowObject, Marking> trans : rg.getIncomingEdges(toMarking)){
			if(!path.contains(trans)){
				path.prepend(trans);
				calculate(rg, path, fromMarking, trans.getSource());
				return path;
			}
		}
		return null;
	}
	
	public static <Diagram, FlowObject, Marking> List<ReachabilityPath<FlowObject, Marking>> calculateAll(
			ReachabilityGraph<Diagram, FlowObject, Marking> rg,
			Marking fromMarking, Marking toMarking) {
		ReachabilityNode<Marking> fromMarkingNode = rg.findByMarking(fromMarking);
		ReachabilityNode<Marking> toMarkingNode = rg.findByMarking(toMarking);
		return calculateAll(rg, new ReachabilityPath<FlowObject, Marking>(), fromMarkingNode, toMarkingNode);
	}
	
	protected static <Diagram, FlowObject, Marking> List<ReachabilityPath<FlowObject, Marking>> calculateAll(			
			ReachabilityGraph<Diagram, FlowObject, Marking> rg,
			ReachabilityPath<FlowObject, Marking> path,
			ReachabilityNode<Marking> fromMarking, ReachabilityNode<Marking> toMarking){
		List<ReachabilityPath<FlowObject, Marking>> paths = new LinkedList<ReachabilityPath<FlowObject, Marking>>();
		
		if(toMarking.getMarking().equals(fromMarking.getMarking())){ // End of search reached
			paths.add(path);
		} else {
			for(ReachabilityTransition<FlowObject, Marking> trans : rg.getOutgoingEdges(fromMarking)){
				if(!path.contains(trans)){
					ReachabilityPath<FlowObject, Marking> nextPath = path.clone();
					nextPath.append(trans);
					paths.addAll(calculateAll(rg, nextPath, trans.getTarget(), toMarking));
				}
			}
		}
		
		return paths;
	}
	
	public List<FlowObject> getFlowObjects(){
		List<FlowObject> path = new LinkedList<FlowObject>();
		
		for(ReachabilityTransition<FlowObject, Marking> trans : transitionPath){
			path.add(trans.getFlowObject());
		}
		
		return path;
	}
	
	/** Builds an array including flow objects of current path. If flow object is a 
	 * petri net transition, getResourceId() is called, else toString()
	 * @return JSON representation of path: {@code [ 'node1', 'node2' ]}
	 */
	public JSONArray toJson(){
		JSONArray path = new JSONArray();
		for(FlowObject flowObject : this.getFlowObjects()){
			if(flowObject instanceof Transition){ // if petri net transition
				path.put(((Transition)flowObject).getResourceId());
			} else {
				path.put(flowObject.toString());
			}
		}
		return path;
	}
}