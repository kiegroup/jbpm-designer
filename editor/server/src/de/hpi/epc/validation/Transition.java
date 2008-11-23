package de.hpi.epc.validation;

import de.hpi.bpt.graph.abs.AbstractDirectedEdge;
import de.hpi.bpt.process.epc.IFlowObject;

public class Transition extends AbstractDirectedEdge<MarkingNode> {
	IFlowObject flowObject;
	
	public Transition(MarkingNode source, MarkingNode target) {
		super(source, target);
	}	
	
	public Transition(MarkingNode source, MarkingNode target, IFlowObject flowObject) {
		super(source, target);
		this.flowObject = flowObject;
	}

	public IFlowObject getFlowObject() {
		return flowObject;
	}

	public void setFlowObject(IFlowObject flowObject) {
		this.flowObject = flowObject;
	}	
}
