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
	Map<Node,Collection<Node>> dominators;
	Container diag;
	
	public DominatorFinder(Container net){
		dominators = new HashMap<Node, Collection<Node>>();
		this.diag = net;
		
		Node startNode = this.getStartNode();
		
		for(Node n : net.getChildNodes()){
			setDominators((Node)n, net.getChildNodes());
		}
		
		List<Node> reversePostOrder = getReversePostOrder(net.getChildNodes());
		
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
	
	protected List<Node> depthFirstSearchIterative(Collection<Node> nodes){
		Node originNode = getStartNode();
		Node destinationNode = getEndNode();
		
		List<Node> openList = new LinkedList<Node>();
		List<Node> preOrderList = new LinkedList<Node>();
		
		//adding starting point to open list
		openList.add(originNode);
		
		while(openList.size() != 0){
			//take node from open list
			Node curNode = openList.get(0);
			openList.remove(curNode);
			
			
			//TODO if needed, check, if curNode is destinationNode
			
			for(Edge rel : curNode.getOutgoingEdges()){
				//put next node on the beginning of the list
				openList.add(0, (Node)rel.getTarget());
			}
			
			if(!preOrderList.contains(curNode))
				preOrderList.add(curNode);
		}
		
		return preOrderList;
	}
	
	protected List<Node> getReversePostOrder(Collection<Node> nodes){
		DepthFirstSearch DFS = new DepthFirstSearch(getStartNode());
		DFS.prepare();
		List<Node> reversePostOrder = DFS.getReversePostOrder();
		
		/*System.out.print("Reverse Post   ");
		for(Node n : reversePostOrder){
			System.out.print(n.getId() + " |");
		}
		System.out.print("\n");*/
		
		return reversePostOrder;
	}
	
	protected void addDominator(Node n, Node dominator){
		Collection<Node> doms = this.getDominators(n);
		doms.add(dominator);
		this.setDominators(n, doms);
	}
	
	public Collection<Node> getDominators(Node n){
		Collection<Node> set = dominators.get(n);
		if(set==null){
			set = new HashSet<Node>(); 
		}
		return set;
	}
	
	public void setDominators(Node n, Collection<Node> list){
		dominators.put(n, list);
	}
	
	//finds start node (one single node without any incoming edges)
	//TODO: cache start node?? move to petri net?? set while mapping bpmn2pn?
	public Node getStartNode(){
		for(Node n : diag.getChildNodes()){
			if(n.getIncomingEdges().size() == 0){
				return n;
			}
		}
		return null;
	}
	public Node getEndNode(){
		for(Node n : diag.getChildNodes()){
			if(n.getOutgoingEdges().size() == 0){
				return n;
			}
		}
		return null;
	}
}