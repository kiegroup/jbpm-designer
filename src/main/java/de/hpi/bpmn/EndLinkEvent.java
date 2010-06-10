package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class EndLinkEvent extends EndEvent {

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}

}
