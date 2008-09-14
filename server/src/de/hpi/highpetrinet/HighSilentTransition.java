package de.hpi.highpetrinet;

import de.hpi.bpmn.DiagramObject;
import de.hpi.petrinet.SilentTransitionImpl;

public class HighSilentTransition extends SilentTransitionImpl {
	private DiagramObject BPMNObj;
	
	public DiagramObject getBPMNObj() {
		return BPMNObj;
	}

	public void setBPMNObj(DiagramObject obj) {
		BPMNObj = obj;
	}
}
