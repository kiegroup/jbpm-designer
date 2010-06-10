package de.hpi.petrinet;


/**
 * Calculates the transitive closure of the flow relation
 * for a given PetriNet.
 * 
 * Use the method isInLoop to get the information whether a 
 * node of the net is part of a cycle of the flow relation.
 * 
 * Use the method isPath to get the information whether there
 * is a path from one node to another node.
 * 
 * @author gero.decker,matthias.weidlich
 *
 */
public class TransitiveClosure {
	
	private PetriNet pn;
	
	private boolean[][] matrix;
	
	public TransitiveClosure(PetriNet pn) {
		this.pn = pn;
		this.matrix = null;
	}
	
	/**
	 * Checks whether the given node is part of a flow relation
	 * cycle.
	 * 
	 * @param node
	 * @return true, if the node is contained in a a flow relation cycle. false, otherwise.
	 */
	public boolean isInLoop(Node node) {
		if (matrix == null)
			calculateMatrix();
		int index = this.pn.getNodes().indexOf(node);
		return matrix[index][index];
	}

	protected void calculateMatrix() {
		this.matrix = new boolean[this.pn.getNodes().size()][this.pn.getNodes().size()];
		
		// setup relationships
		for (FlowRelationship f: this.pn.getFlowRelationships()) {
			int source = this.pn.getNodes().indexOf(f.getSource());
			int target = this.pn.getNodes().indexOf(f.getTarget());
			matrix[source][target] = true;
		}
		
		// compute transitive closure
	      for (int k = 0; k < matrix.length; k++) {
			for (int row = 0; row < matrix.length; row++) {
				// In Warshall's original paper, the inner-most loop is
				// guarded by the boolean value in [row][k] --- omitting
				// the loop on false and removing the "&" in the evaluation.
				if (matrix[row][k])
					for (int col = 0; col < matrix.length; col++)
						matrix[row][col] = matrix[row][col] | matrix[k][col];
			}
		}
	}

	public String toString(){
		if (matrix == null)
			calculateMatrix();
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------\n");
		sb.append("Transitive Closure\n");
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
	 * Checks whether there is a flow relation path from node1 to node2.
	 * 
	 * @param node1
	 * @param node2
	 * @return true, if there is a path from node1 to node2. false, otherwise.
	 */
	public boolean isPath(Node node1, Node node2) {
		if (matrix == null)
			calculateMatrix();
		int i = this.pn.getNodes().indexOf(node1);
		int j = this.pn.getNodes().indexOf(node2);
		return matrix[i][j];
	}
	
	/**
	 * Checks whether there is a flow relation path from the node with
	 * index i to the node with index j.
	 * 
	 * @param i
	 * @param j
	 * @return true, if there is a path from node with index i to node with index j. false, otherwise.
	 */
	public boolean isPath(int i, int j) {
		if (matrix == null)
			calculateMatrix();
		return matrix[i][j];
	}	
}
