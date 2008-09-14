package de.hpi.highpetrinet;

import de.hpi.bpmn.DiagramObject;
import de.hpi.petrinet.LabeledTransitionImpl;

public class HighLabeledTransition extends LabeledTransitionImpl {
	private DiagramObject BPMNObj;
	
	public DiagramObject getBPMNObj() {
		return BPMNObj;
	}

	public void setBPMNObj(DiagramObject obj) {
		BPMNObj = obj;
	}
}
