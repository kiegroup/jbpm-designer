package de.hpi.epc.validation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import de.hpi.bpt.graph.abs.AbstractDirectedGraph;
import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.IFlowObject;
import de.hpi.epc.Marking;
import de.hpi.epc.Marking.NodeNewMarkingPair;

/*
 * TODO title thesis?
 * Implementation as proposed by Jan Mendling, p. 90 
 */
public class ReachabilityGraph extends AbstractDirectedGraph<Transition, MarkingNode> {
	IEPC diag;
	DirectedGraphAlgorithms<Transition, MarkingNode> directedGraphAlgorithms;
	
	public ReachabilityGraph(IEPC diag){
		this.diag = diag;
		directedGraphAlgorithms = new DirectedGraphAlgorithms<Transition, MarkingNode>(); 
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
		
		List<Marking> initialMarkings = new LinkedList<Marking>();		
		for(List<IFlowObject> initialNodes : (List<List<IFlowObject>>)de.hpi.bpmn.analysis.Combination.findCombinations(startNodes) ){
			if(initialNodes.size() > 0)
				initialMarkings.add(Marking.getInitialMarking(diag, initialNodes));
		}
		calculate(initialMarkings);
	}
	
	public void clear(){
		this.getVertices().clear();
	}
	
	public void calculate(List<Marking> initialMarkings){
		clear();
				
		Stack<Marking> toBePropagated = new Stack<Marking>();
		System.out.println("InitialMarking: " + initialMarkings.get(0).toString());
		toBePropagated.addAll(initialMarkings);
		
		Set<Marking> propagated = new HashSet<Marking>();
		
		while(toBePropagated.size() > 0){
			Marking currentMarking = toBePropagated.pop();
			Marking oldMarking = currentMarking.clone();
			List<NodeNewMarkingPair> nodeNewMarkings = currentMarking.clone().propagate(diag);
			propagated.add(oldMarking);
			for(NodeNewMarkingPair nodeNewMarking : nodeNewMarkings){
				add(oldMarking, nodeNewMarking.newMarking, nodeNewMarking.node);
				
				//TODO why cannot be used propagate.contains? 
				boolean contains = false;
				for(Marking m : propagated){
					if(m.equals(nodeNewMarking.newMarking)){
						contains = true;
					}
				}
				//if(!propagated.contains(nodeNewMarking.newMarking)){
				if(!contains){
					toBePropagated.push(nodeNewMarking.newMarking);
				}
			}
		}
	}
	
	public void add(Marking fromMarking, Marking toMarking, IFlowObject node){
		if(!toMarking.hasToken(diag))
			return;
		
		MarkingNode fromMarkingNode = findByMarking(fromMarking);
		if(fromMarkingNode == null){
			fromMarkingNode = new MarkingNode(fromMarking);
			addVertex(fromMarkingNode);
		}
		MarkingNode toMarkingNode = findByMarking(toMarking);
		if(toMarkingNode == null){
			toMarkingNode = new MarkingNode(toMarking);
			addVertex(toMarkingNode);
		}
		
		// Only add new transition if there isn't already one
		if(this.getEdgesWithSourceAndTarget(fromMarkingNode, toMarkingNode).size() == 0){
			Transition transition = new Transition(fromMarkingNode, toMarkingNode, node);
			addEdge(transition);
		}
	}
	
	/*public void doubledMarkings(){
		for( Marking m1 : getMarkings() ){
			for( Marking m2 : getMarkings() ){
				if(m1 == m2)
					break;
				if(m1.equals(m2)){
					System.out.print("+"+m2.toString());
					return;
				}
			}
		}
	}*/
	
	public boolean contains(Marking m){
		return findByMarking(m) != null;
	}
	
	public List<Marking> getMarkings(){
		List<Marking> markings = new LinkedList<Marking>();
		for(MarkingNode mNode : this.getVertices()){
			markings.add(mNode.getMarking());
		}
		return markings;
	}
	
	public MarkingNode findByMarking(Marking m){
		for(MarkingNode fo : this.getVertices()){
			if(fo.getMarking().equals(m)){
				return fo;
			}
		}
		return null;
	}
	
	public boolean isRoot(Marking m){
		return this.getIncomingEdges(findByMarking(m)).size() == 0;
	}
	
	public boolean isLeaf(Marking m){
		return this.getOutgoingEdges(findByMarking(m)).size() == 0;
	}
	
	/*TODO should this return all predecessors or only direct ones???*/
	//TODO expensive search!! Avoid Marking => MarkingNode => Marking
	public List<Marking> getPredecessors(Marking m){
		List<Marking> list = new LinkedList<Marking>();
		for(MarkingNode markingNode : getPredecessors(findByMarking(m))){
			list.add(markingNode.getMarking());
		}
		return list;
	}
	
	/*TODO should this return all successors or only direct ones???*/
	//TODO expensive search!! Avoid Marking => MarkingNode => Marking
	public List<Marking> getSuccessors(Marking m){
		List<Marking> list = new LinkedList<Marking>();
		for(MarkingNode markingNode : getSuccessors(findByMarking(m))){
			list.add(markingNode.getMarking());
		}
		return list;
	}
	
	public List<Marking> getRoots(){
		List<Marking> list = new LinkedList<Marking>();
		for(MarkingNode node : directedGraphAlgorithms.getInputVertices(this)){
			list.add(node.getMarking());
		}
		return list;
	}
	
	public List<Marking> getLeaves(){
		List<Marking> list = new LinkedList<Marking>();
		for(MarkingNode node : directedGraphAlgorithms.getOutputVertices(this)){
			list.add(node.getMarking());
		}
		return list;
	}
}
