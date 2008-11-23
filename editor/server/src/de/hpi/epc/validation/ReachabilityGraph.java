package de.hpi.epc.validation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import de.hpi.bpt.graph.abs.AbstractDirectedGraph;
import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;
import de.hpi.bpt.process.epc.IControlFlow;
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
	
	public void calculate(List<Marking> initialMarkings){
		Stack<Marking> toBePropagated = new Stack<Marking>();
		System.out.println("InitialMarking: " + initialMarkings.get(0).toString());
		toBePropagated.addAll(initialMarkings);
		
		Set<Marking> propagated = new HashSet<Marking>();
		
		while(toBePropagated.size() > 0){
			Marking currentMarking = toBePropagated.pop();
			Marking oldMarking = currentMarking.clone();
			List<NodeNewMarkingPair> nodeNewMarkings = currentMarking.propagate(diag);
			propagated.add(oldMarking);
			for(NodeNewMarkingPair nodeNewMarking : nodeNewMarkings){
				add(oldMarking, nodeNewMarking.newMarking, nodeNewMarking.node);
				if(!propagated.contains(nodeNewMarking.newMarking)){
					toBePropagated.push(nodeNewMarking.newMarking);
				}
			}
		}
	}
	
	public void add(Marking fromMarking, Marking toMarking, IFlowObject node){
		// Markings with no tokens anymore shouldn't be added
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
		
		Transition transition = new Transition(fromMarkingNode, toMarkingNode, node);
		addEdge(transition);
	}
	
	public boolean contains(Marking m){
		return findByMarking(m) != null;
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
