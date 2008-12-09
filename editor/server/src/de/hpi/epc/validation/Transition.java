package de.hpi.epc.validation;

import de.hpi.bpt.graph.abs.AbstractDirectedEdge;
import de.hpi.bpt.graph.abs.AbstractDirectedGraph;
import de.hpi.bpt.process.epc.IFlowObject;

public class Transition extends AbstractDirectedEdge<MarkingNode> {
	IFlowObject flowObject;
	
	@SuppressWarnings("unchecked")
	protected Transition(AbstractDirectedGraph g, MarkingNode source,
			MarkingNode target) {
		super(g, source, target);
	}
	
	public IFlowObject getFlowObject() {
		return flowObject;
	}

	public void setFlowObject(IFlowObject flowObject) {
		this.flowObject = flowObject;
	}	
}
