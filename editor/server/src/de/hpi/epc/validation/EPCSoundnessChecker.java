package de.hpi.epc.validation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.epc.Marking;

public class EPCSoundnessChecker {
	ReachabilityGraph rg;
	IEPC diag;
	
	public EPCSoundnessChecker(IEPC diag){
		this.diag = diag;
		rg = new ReachabilityGraph(diag);
	}
	
	public List<IControlFlow> badStartArcs;
	public List<IControlFlow> badEndArcs;
	public List<Marking> goodInitialMarkings;
	public List<Marking> goodFinalMarkings;
	
	protected void clear(){
		badStartArcs = new LinkedList<IControlFlow>();
		badEndArcs = new LinkedList<IControlFlow>();
		goodInitialMarkings = new LinkedList<Marking>();
		goodFinalMarkings = new LinkedList<Marking>();
	}
	
	public void calculate(){
		clear();
		rg.calculate();
		
		List<Marking> badLeaves = new LinkedList<Marking>();
		List<Marking> goodLeaves = new LinkedList<Marking>();
		List<Marking> goodRoots = rg.getRoots();
		
		// Phase 1: Check for soundness
		for(Marking m : rg.getLeaves()){
			if(!m.isFinalMarking(diag)){
				badLeaves.add(m);
			}
		}
		if(badLeaves.size() == 0){
			//EPC is sound
			goodInitialMarkings = rg.getLeaves();
			goodFinalMarkings = rg.getRoots();
			return;
		}
		
		// Phase 2: Determine all predecessor markings of a deadlock
		Set<Marking> badLeavesPredecessors = new HashSet<Marking>();
		Stack<Marking> predecessorStack = new Stack<Marking>();
		predecessorStack.addAll(badLeaves);
		
		while(predecessorStack.size() != 0){
			Marking current = predecessorStack.pop();
			if(rg.isRoot(current)){
				goodRoots.remove(current);
			} else {
				for(Marking pre : rg.getPredecessors(current)){
					if(!badLeavesPredecessors.contains(pre)){
						predecessorStack.push(pre);
					}
				}
				badLeavesPredecessors.add(current);
			}
		}
		
		// Phase 3: Determine those leaves of rg that can be reached from good nodes
		Set<Marking> goodRootSuccessors = new HashSet<Marking>();
		Stack<Marking> successorStack = new Stack<Marking>();
		successorStack.addAll(goodRoots);
		
		while(successorStack.size() != 0){
			Marking current = successorStack.pop();
			if(rg.isLeaf(current)){
				goodLeaves.add(current);
			} else {
				for(Marking post : rg.getSuccessors(current)){
					if(goodRootSuccessors.contains(post)){
						successorStack.push(post);
					}
				}
				goodRootSuccessors.add(current);
			}
		}
		
		goodInitialMarkings = goodRoots;
		goodFinalMarkings = goodLeaves;
		badStartArcs = missingStartArcs(goodLeaves);
		badEndArcs = missingEndArcs(goodRoots);
	}
	
	public boolean isSound(){
		return badStartArcs.size() == 0 && badEndArcs.size() == 0;
	}
	
	public List<IControlFlow> missingStartArcs(List<Marking> markings){
		LinkedList<IControlFlow> missings = new LinkedList<IControlFlow>();
		
		// Initialize list with all start and end arcs
		for(IControlFlow cf : diag.getControlFlow()){
			if(diag.getIncomingControlFlow(cf.getSource()).size() == 0){
				missings.add(cf);
			}
		}
		
		return missings(markings, missings);
	}
	
	public List<IControlFlow> missingEndArcs(List<Marking> markings){
		LinkedList<IControlFlow> missings = new LinkedList<IControlFlow>();
		
		// Initialize list with all start and end arcs
		for(IControlFlow cf : diag.getControlFlow()){
			if(diag.getOutgoingControlFlow(cf.getTarget()).size() == 0){
				missings.add(cf);
			}
		}
		
		return missings(markings, missings);
	}
	
	// Returns a list of nodes that do not have a positive
	// token in any marking of the MarkingList.
	public List<IControlFlow> missings(List<Marking> markings, LinkedList<IControlFlow> missings){
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
