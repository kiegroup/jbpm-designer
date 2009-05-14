/**
 * Copyright (c) 2009 Matthias Weidlich
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
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.hpi.bp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class TrueConcurrencyRelation {
	
	private class NodePair {
		
		private Node n1;
		private Node n2;
		
		public NodePair(Node n1, Node n2) {
			this.n1 = n1;
			this.n2 = n2;
		}
		
		public Node getFirstNode() {
			return this.n1;
		}
		
		public Node getSecondNode() {
			return this.n2;
		}
	}

	private PTNet pn;
	
	private boolean[][] matrix;
	
	public TrueConcurrencyRelation(PTNet pn) {
		this.pn = pn;
		this.matrix = null;
		calculateTrueConcurrencyMatrix();
	}
	
	/**
	 * Returns whether there exists a state in which 
	 * both nodes are enabled concurrently.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both nodes are concurrent.
	 */
	public boolean areTrueConcurrent(Node n1, Node n2) {
		assert(matrix != null);
		int index1 = this.pn.getNodes().indexOf(n1);
		int index2 = this.pn.getNodes().indexOf(n2);
		return matrix[index1][index2];
	}
	
	/**
	 * Returns whether there exists a state in which 
	 * both nodes are enabled concurrently. Both nodes 
	 * are identified by the index in the array of nodes
	 * of the respective Petri net.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both nodes are concurrent.
	 */
	public boolean areTrueConcurrent(int i, int j) {
		assert(matrix != null);
		return matrix[i][j];
	}
	
	protected boolean allNodesConcurrent(Collection<Node> nodes1) {
		boolean nok = false;
		
		for(Node n1 : nodes1) {
			int index1 = this.pn.getNodes().indexOf(n1);
			for(Node n2 : nodes1) {
				if (n1.equals(n2))
					continue;
				int index2 = this.pn.getNodes().indexOf(n2);
				if (!this.matrix[index1][index2])
					nok = true;
			}
		}
		return !nok;
	}
	
	protected boolean nodeConcurrentToNodes(Node n, Collection<Node> nodes) {
		boolean notConc = false;
		int i = this.pn.getNodes().indexOf(n);
		for(Node n2 : nodes) {
			if (n.equals(n))
				continue;
			int j = this.pn.getNodes().indexOf(n2);
			notConc |= (!this.matrix[i][j]);
		}
		return !notConc;
	}
	
	private void setAllNodesConcurrent(Collection<Node> nodes) {
		for(Node n : nodes) {
			setNodeConcurrentToNodes(n,nodes);
		}
	}
	
	protected void setNodeConcurrentToNodes(Node n, Collection<Node> nodes) {
		for(Node n2 : nodes) {
			setNodesConcurrent(n,n2);
		}
	}
	
	protected void setNodesConcurrent(Node n1, Node n2) {
		if (n1.equals(n2))
			return;
		int index1 = this.pn.getNodes().indexOf(n1);
		int index2 = this.pn.getNodes().indexOf(n2);
		this.matrix[index1][index2] = true;
		this.matrix[index2][index1] = true;
	}

	/**
	 * Helper method for calculating the concurrency 
	 * relation (see Kovalyov and Esparza (1996)).
	 */
	protected void processConcNodes(Set<NodePair> concNodes, boolean isFC) {
		for(NodePair pair : concNodes) {
			Node x = pair.getFirstNode();
			Node p = pair.getSecondNode();

			List<Node> outNodes = p.getSucceedingNodes();
			// optimization for free-choice nets
			if ((isFC) && (!outNodes.isEmpty())) {
				outNodes = new ArrayList<Node>();
				outNodes.add(p.getSucceedingNodes().get(0));
			}
			
			for (Node t : outNodes) {
				if (nodeConcurrentToNodes(x, t.getPrecedingNodes())) {
					
					Collection<Node> sucT = t.getSucceedingNodes();
					Set<NodePair> concNodes2 = new HashSet<NodePair>();
										
					for(Node s : sucT) {
						if (!areTrueConcurrent(x,s))
							concNodes2.add(new NodePair(x,s));
					}
					// not sure, whether this is needed
					if (x instanceof Place)
						concNodes2.add(new NodePair(t,x));
					
					setNodeConcurrentToNodes(x,sucT);
					setNodesConcurrent(x,t);
					processConcNodes(concNodes2, isFC);
				}
			}
		}
	}
	
	protected void addAllCombinations(Set<NodePair> combinations, List<Node> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = i + 1; j < nodes.size(); j++) {
				combinations.add(new NodePair(nodes.get(i), nodes.get(j)));
				combinations.add(new NodePair(nodes.get(j), nodes.get(i)));
			}
		}
	}

	/**
	 * Calculates the concurrency relation using the
	 * algorithm by Kovalyov and Esparza (1996).
	 * 
	 * Assumption: the net is live and bound!
	 */
	protected void calculateTrueConcurrencyMatrix() {
		
		// assert(live and bound);
		
		this.matrix = new boolean[this.pn.getNodes().size()][this.pn.getNodes().size()];

		// here we collect concurrent nodes 
		Set<NodePair> concNodes = new HashSet<NodePair>();
		
		/*
		 * Initialization of the algorithm
		 */
		List<Node> initialPlaces = new ArrayList<Node>(this.pn.getInitialMarking().getMarkedPlaces());
		setAllNodesConcurrent(initialPlaces);
		addAllCombinations(concNodes,initialPlaces);
		
		for(Transition t1 : this.pn.getTransitions()) {
			List<Node> outPlaces = t1.getSucceedingNodes();
			setAllNodesConcurrent(outPlaces);
			addAllCombinations(concNodes,outPlaces);
		}
		
		/*
		 * Actual algorithm to build up the matrix.
		 * It runs faster for free-choice nets than for arbitrary nets.
		 */
		processConcNodes(concNodes,this.pn.isFreeChoiceNet());
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------\n");
		sb.append("True Concurrency Matrix\n");
		sb.append("------------------------------------------\n");
		for (int k = 0; k < matrix.length; k++) {
			for (int row = 0; row < matrix.length; row++) {
				sb.append(matrix[row][k] + " , ");
			}
			sb.append("\n");
		}
		sb.append("------------------------------------------\n");
		return sb.toString();
	}
}
