package de.hpi.epc.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.IFlowObject;
import de.hpi.epc.Marking;
import de.hpi.epc.Marking.NodeNewMarkingPair;

/*
 * TODO title thesis?
 * Implementation as proposed by Jan Mendling, p. 90 
 */
public class ReachabilityGraph {
	IEPC diag;
	
	// Simple map which maps from a fromMarking to several toMarkings (children)
	public Map<Marking, List<Marking>> tree;
	
	public ReachabilityGraph(IEPC diag){
		this.diag = diag;
		tree = new HashMap<Marking, List<Marking>>();
	}
	
	// Calculates RG for all possible initial markings
	public void calculate(){
		//TODO this calculation should be made public in a graph algo class
		List<IFlowObject> startNodes = new LinkedList<IFlowObject>();
		for(IFlowObject fo : diag.getFlowObjects()){
			if(diag.getIncomingControlFlow(fo).size() == 0){
				startNodes.add(fo);
			}
		}
		
		for(List<IFlowObject> initialNodes : (List<List<IFlowObject>>)de.hpi.bpmn.analysis.Combination.findCombinations(startNodes) ){
			if(initialNodes.size() > 0)
				calculate(Marking.getInitialMarking(diag, initialNodes));
		}
	}
	
	public void calculate(Marking marking){
		Stack<Marking> toBePropagated = new Stack<Marking>();
		toBePropagated.push(marking);
		
		Set<Marking> propagated = new HashSet<Marking>();
		
		while(!toBePropagated.isEmpty()){
			Marking currentMarking = toBePropagated.pop();
			Marking oldMarking = currentMarking.clone();
			List<NodeNewMarkingPair> nodeNewMarkings = currentMarking.propagate(diag);
			propagated.add(oldMarking);
			for(NodeNewMarkingPair nodeNewMarking : nodeNewMarkings){
				add(oldMarking, nodeNewMarking.newMarking);
				if(!propagated.contains(nodeNewMarking.newMarking)){
					toBePropagated.push(nodeNewMarking.newMarking);
				}
			}
		}
	}
	
	public void add(Marking fromMarking, Marking toMarking){
		if(tree.get(fromMarking) == null){
			tree.put(fromMarking, new LinkedList<Marking>());
		}
		tree.get(fromMarking).add(toMarking);
	}
	
	public boolean isRoot(Marking m){
		return this.getRoots().contains(m);
	}
	
	public boolean isLeaf(Marking m){
		return this.getLeaves().contains(m);
	}
	
	//TODO expensive search!!
	public List<Marking> getPredecessors(Marking m){
		List<Marking> predecessors = new LinkedList<Marking>();
		
		for(Marking marking : tree.keySet()){
			if(tree.get(marking).contains(m)){
				predecessors.add(marking);
				predecessors.addAll(getPredecessors(marking));
			}
		}
		return predecessors;
	}
	
	public List<Marking> getSuccessors(Marking m){
		List<Marking> successors = new LinkedList<Marking>();
		
		if(tree.get(m) != null) {
			successors.addAll(tree.get(m));
			for(Marking sucMarking : tree.get(m)){
				successors.addAll(getSuccessors(sucMarking));
			}
		}
		return successors;
	}
	
	public List<Marking> getRoots(){
		// Calc all keys which don't appears as values
		Set<Marking> keys = (Set<Marking>) new HashSet<Marking>(tree.keySet()).clone();
		for(List<Marking> value : tree.values()){
			keys.removeAll(value);
		}
		return new LinkedList<Marking>(keys);
	}
	
	public List<Marking> getLeaves(){
		// Calc all values which don't appears as keys
		Set<List<Marking>> valueLists = (Set<List<Marking>>) new HashSet<List<Marking>>(tree.values()).clone();
		Set<Marking> values = new HashSet<Marking>();
		for(List<Marking> value : valueLists){
			values.addAll(value);
		}
		values.removeAll(tree.keySet());
		return new LinkedList<Marking>(values);
	}
	
	// Returns a list of nodes that do not have a positive
	// token in any marking of the MarkingList.
	public List<IControlFlow> missing(List<Marking> markings){
		LinkedList<IControlFlow> missings = new LinkedList<IControlFlow>();
		
		// Initialize list with all start and end arcs
		for(IControlFlow cf : diag.getControlFlow()){
			if(diag.getIncomingControlFlow(cf.getSource()).size() == 0){
				missings.add(cf);
			} else if (diag.getOutgoingControlFlow(cf.getTarget()).size() == 0){
				missings.add(cf);
			}
		}
		
		// For each marking, those arcs with a positive token are deleted
		for(Marking marking : markings) {
			List<IControlFlow> missingsClone = (List<IControlFlow>)missings.clone();
			for(IControlFlow cf : missingsClone){
				if(marking.state.get(cf) == Marking.State.POS_TOKEN){
					missings.remove(cf);
				}
			}
		}
		
		return missings;
	}
}
