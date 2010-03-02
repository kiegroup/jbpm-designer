/* http://en.wikipedia.org/wiki/Depth-first_search
 */

package de.hpi.bpmn.analysis;

import java.util.LinkedList;
import java.util.List;

import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Node;

public class DepthFirstSearch {
	protected List<Node> preOrder;
	protected List<Node> postOrder;
	protected List<Node> reversePostOrder;
	protected Node startNode;
	
	public DepthFirstSearch(Node startNode){
		this.startNode = startNode;		
	}
	
	public void prepare(){
		preOrder = new LinkedList<Node>();
		postOrder = new LinkedList<Node>();
		reversePostOrder = null;
		
		doDFS(startNode);
	}
	
	protected void doDFS(Node node){
		preOrder.add(node);
		
		List list = node.getOutgoingEdges();
		
		for(Object rel : list){
			if(!preOrder.contains(((Edge)rel).getTarget()))
				doDFS((Node)((Edge)rel).getTarget());
		}
		
		postOrder.add(node);
	}
	
	public List<Node> getPreOrder() {
		return preOrder;
	}

	public List<Node> getPostOrder() {
		return postOrder;
	}

	public List<Node> getReversePostOrder() {
		if(postOrder != null && reversePostOrder == null){
			reversePostOrder = new LinkedList<Node>();
			for(Node n : postOrder){
				reversePostOrder.add(0, n);
			}
		}
		return reversePostOrder;
	}
}
