package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class EndPlainEvent extends EndEvent {

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}

}
