package de.hpi.epc.validation;

import de.hpi.bpt.hypergraph.abs.Vertex;
import de.hpi.epc.Marking;

public class MarkingNode extends Vertex {
	protected Marking marking;
	
	public MarkingNode(Marking m){
		marking = m;
	}
	
	public boolean equals(Object o){
		//System.out.println("Calling MarkingNode#equals");
		return marking.equals(((MarkingNode)o).getMarking());
	}

	public Marking getMarking() {
		return marking;
	}

	public void setMarking(Marking marking) {
		this.marking = marking;
	}
}
