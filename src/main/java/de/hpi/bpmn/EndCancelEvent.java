package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class EndCancelEvent extends EndEvent {

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
