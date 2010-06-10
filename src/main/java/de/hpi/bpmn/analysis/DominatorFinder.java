/* TODO: DFS isn't performant at all (use of data structures)
 * http://www.hipersoft.rice.edu/grads/publications/dom14.pdf
 * TODO: DFS isn't performant for immediate dominators
 */

package de.hpi.bpmn.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hpi.bpmn.Container;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Node;

public class DominatorFinder {
	public Map<Node,Collection<Node>> dominators;
	public Map<Node,Collection<Node>> postDominators;
	protected Container diag;
	protected List<Node> reversePostOrder;
	
	protected Node endNode;
	protected Node startNode;
	
	public DominatorFinder(Container net){
		dominators = new HashMap<Node, Collection<Node>>();
		postDominators = new HashMap<Node, Collection<Node>>();
		this.diag = net;
		
		calcReversePostOrder();
		calcDominators();
		calcPostDominators();
	}
	
	protected void calcDominators(){
		for(Node n : diag.getChildNodes()){
			setDominators((Node)n, diag.getChildNodes());
		}
		
		// iteratively eliminate nodes that are not dominators
		boolean changed = true;
		while(changed){
			changed = false;
			for(Node n : reversePostOrder){
				HashSet<Node> newSet = new HashSet<Node>();
				newSet.add(n); //add node himself
				
				//intersection
				if(n.getIncomingEdges().size() > 0){
					HashSet<Node> dominators = new HashSet<Node>();
					dominators.addAll(getDominators((Node)n.getIncomingEdges().get(0).getSource()));
					
					for(Edge incomingFlow : n.getIncomingEdges()){
						dominators.retainAll(getDominators((Node)incomingFlow.getSource()));
					}
					newSet.addAll(dominators);
				}

				if(!newSet.equals(getDominators(n))){
					setDominators(n, newSet);
					changed = true;
				}
			}
		}
	}
	
	/* 
	 * Algorithm taken from:
	 * http://www.eecs.umich.edu/~mahlke/483f03/lectures/483L16.pdf, slide 22
	 */
	protected void calcPostDominators(){
		// Initilize
		// All nodes gets all other nodes as post dominators
		for(Node n : diag.getChildNodes()){
			setPostDominators((Node)n, diag.getChildNodes());
		}
		
		// End node gets only itself as end node
		List<Node> endNodePostDominators = new LinkedList<Node>();
		endNodePostDominators.add(this.getEndNode());
		setPostDominators(this.getEndNode(), endNodePostDominators);
		
		// iteratively eliminate nodes that are not post dominators
		boolean changed = true;
		while(changed){
			changed = false;
			for(Node n : reversePostOrder){
				if(n.equals(this.getEndNode())) //end node isn't considered here
					continue;
				
				HashSet<Node> newSet = new HashSet<Node>();
				newSet.add(n); //add node himself
				
				//intersection
				if(n.getOutgoingEdges().size() > 0){
					HashSet<Node> postDominators = new HashSet<Node>();
					postDominators.addAll(getPostDominators((Node)n.getOutgoingEdges().get(0).getTarget()));
					
					for(Edge outgoingFlow : n.getOutgoingEdges()){
						postDominators.retainAll(getPostDominators((Node)outgoingFlow.getSource()));
					}
					newSet.addAll(postDominators);
				}

				if(!newSet.equals(getPostDominators(n))){
					setPostDominators(n, newSet);
					changed = true;
				}
			}
		}
	}
	
	protected void calcReversePostOrder(){
		DepthFirstSearch DFS = new DepthFirstSearch(getStartNode());
		DFS.prepare();
		reversePostOrder = DFS.getReversePostOrder();
	}
	
	public Collection<Node> getDominators(Node n){
		Collection<Node> set = dominators.get(n);
		if(set==null){
			set = new HashSet<Node>(); 
		}
		return set;
	}
	
	protected void setDominators(Node n, Collection<Node> list){
		dominators.put(n, list);
	}
	
	public Collection<Node> getPostDominators(Node n){
		Collection<Node> set = postDominators.get(n);
		if(set==null){
			set = new HashSet<Node>(); 
		}
		return set;
	}
	
	protected void setPostDominators(Node n, Collection<Node> list){
		postDominators.put(n, list);
	}
	
	//finds start node (one single node without any incoming edges)
	protected Node getStartNode(){
		if(startNode == null){
			for(Node n : diag.getChildNodes()){
				if( n.getIncomingSequenceFlows().size() == 0){
					startNode = n;
					return startNode;
				}
			}
			return null;
		} else {
			return startNode;
		}
	}
	protected Node getEndNode(){
		if(endNode == null){
			for(Node n : diag.getChildNodes()){
				if(n.getOutgoingSequenceFlows().size() == 0){
					endNode = n;
					return endNode;
				}
			}
			return null;
		} else {
			return endNode;
		}
	}
	
	/*public static void main(String [ ] args) {
		DominatorFinder domfind = new DominatorFinder(BPMNHelpers.loadRDFDiagram("bpmn.rdf"));
		for (Node n : domfind.dominators.keySet()){
			System.out.println("Dominators for: " + n.getId());
			for (Node innerN : domfind.dominators.get(n)){
				System.out.print(innerN.getId() + " | ");
			}
			System.out.print("\n\n");
		}
		
		for (Node n : domfind.postDominators.keySet()){
			System.out.println("Post dominators for: " + n.getId());
			for (Node innerN : domfind.postDominators.get(n)){
				System.out.print(innerN.getId() + " | ");
			}
			System.out.print("\n\n");
		}
	}*/
}