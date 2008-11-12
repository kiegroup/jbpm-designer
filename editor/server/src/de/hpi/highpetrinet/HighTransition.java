package de.hpi.highpetrinet;

import de.hpi.bpmn.DiagramObject;
import de.hpi.petrinet.Transition;

public interface HighTransition extends Transition {
	DiagramObject getBPMNObj();
	void setBPMNObj(DiagramObject obj);
}
