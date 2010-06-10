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

import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.TransitiveClosure;

public class BehaviouralProfile {
	
	public enum CharacteristicRelationType {
		StrictOrder,ReversedStrictOrder,Concurrency,Exclusive
	}

	protected PTNet pn;
	
	protected CharacteristicRelationType[][] matrix;

	public CharacteristicRelationType[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(CharacteristicRelationType[][] matrix) {
		this.matrix = matrix;
	}

	protected TrueConcurrencyRelation trueConcurrency;
	
	public BehaviouralProfile(PTNet pn) {
		this.pn = pn;
		this.matrix = null;
		deriveBehaviouralProfile();
	}
	
	public PTNet getNet() {
		return this.pn;
	}
	
	public TrueConcurrencyRelation getTrueConcurrency() {
		return this.trueConcurrency;
	}
	
	protected void deriveBehaviouralProfile() {
		this.matrix = new CharacteristicRelationType[this.pn.getNodes().size()][this.pn.getNodes().size()];

		TransitiveClosure closure = new TransitiveClosure(this.pn);
		trueConcurrency = new TrueConcurrencyRelation(this.pn);
		
		for(Node n1 : this.pn.getNodes()) {
			int index1 = this.pn.getNodes().indexOf(n1);
			for(Node n2 : this.pn.getNodes()) {
				int index2 = this.pn.getNodes().indexOf(n2);
				/*
				 * The matrix is symmetric. Therefore, we need to traverse only 
				 * half of the entries.
				 */
				if (index2 > index1)
					continue;
				/*
				 * What about the relation of a node to itself?
				 */
				if (index1 == index2) {
					if (closure.isPath(index1, index1)) {
						this.matrix[index1][index1] = CharacteristicRelationType.Concurrency;
					} else {
						this.matrix[index1][index1] = CharacteristicRelationType.Exclusive;
					}
				}
				else if (closure.isPath(index1, index2) && closure.isPath(index2, index1)) {
					setMatrixEntry(index1,index2,CharacteristicRelationType.Concurrency);
				}
				else if (trueConcurrency.areTrueConcurrent(index1,index2)) {
					setMatrixEntry(index1,index2,CharacteristicRelationType.Concurrency);
				}
				else if (!trueConcurrency.areTrueConcurrent(index1,index2) && !closure.isPath(index1, index2) && !closure.isPath(index2, index1)) {
					setMatrixEntry(index1,index2,CharacteristicRelationType.Exclusive);
				}
				else if (closure.isPath(index1, index2) && !closure.isPath(index2, index1)) {
					setMatrixEntryOrder(index1,index2);
				}
				else if (closure.isPath(index2, index1) && !closure.isPath(index1, index2)) {
					setMatrixEntryOrder(index2,index1);
				}
			}
		}
	}
	
	/**
	 * As the matrix of the behavioral profile is symmetric for
	 * the exclusive and concurrency relation, we use this procedure 
	 * to set these dependency between two nodes.
	 * 
	 * @param i
	 * @param j
	 * @param type
	 */
	protected void setMatrixEntry(int i, int j, CharacteristicRelationType type) {
		assert(type.equals(CharacteristicRelationType.Concurrency)||type.equals(CharacteristicRelationType.Exclusive));
		this.matrix[i][j] = type;
		this.matrix[j][i] = type;
	}
	
	protected void setMatrixEntryOrder(int from, int to) {
		this.matrix[from][to] = CharacteristicRelationType.StrictOrder;
		this.matrix[to][from] = CharacteristicRelationType.ReversedStrictOrder;
	}
	
	public boolean areConcurrent(Node n1, Node n2) {
		if (matrix == null)
			deriveBehaviouralProfile();
		int index1 = this.pn.getNodes().indexOf(n1);
		int index2 = this.pn.getNodes().indexOf(n2);
		return matrix[index1][index2].equals(CharacteristicRelationType.Concurrency);
	}
	
	public CharacteristicRelationType getRelationForNodes(Node n1, Node n2) {
		if (matrix == null)
			deriveBehaviouralProfile();
		int index1 = this.pn.getNodes().indexOf(n1);
		int index2 = this.pn.getNodes().indexOf(n2);
		return matrix[index1][index2];
	}
	
	public Collection<Node> getNodesInRelation(Node n, CharacteristicRelationType relationType) {
		if (matrix == null)
			deriveBehaviouralProfile();
		Collection<Node> nodes = new ArrayList<Node>();
		int index = this.pn.getNodes().indexOf(n);
		
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[index][i].equals(relationType)) {
				nodes.add(this.pn.getNodes().get(i));
			}
		}
		return nodes;
	}
	
	public void printAllNodes(CharacteristicRelationType relationType) {
		if (matrix == null)
			deriveBehaviouralProfile();
		for(Node n1 : this.pn.getNodes()) {
			int index1 = this.pn.getNodes().indexOf(n1);
			for(Node n2 : this.pn.getNodes()) {
				int index2 = this.pn.getNodes().indexOf(n2);
				if (index2 > index1)
					continue;
				if (matrix[index1][index2].equals(relationType))
					System.out.println(relationType + " -- " + n1 + " : " + n2);
			}
		}
	}
	
	public String toString(){
		if (matrix == null)
			deriveBehaviouralProfile();
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------\n");
		sb.append("Behavioural Profile Matrix\n");
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
	
	/**
	 * Checks equality for two behavioural profiles
	 * 
	 * Returns false, if both matrices are not based on the same
	 * Petri net.
	 * 
	 * @param profile that should be compared
	 * @return true, if the given profile is equivalent to this profile
	 */
	public boolean equals (BehaviouralProfile profile) {
		if (!this.pn.equals(profile.getNet()))
			return false;
		
		boolean equal = true;
		
		for(Node n1 : this.pn.getNodes()) {
			for(Node n2 : this.pn.getNodes()) {
				equal &= this.getRelationForNodes(n1, n2).equals(profile.getRelationForNodes(n1, n2));
			}
		}
		return equal;
	}

}
