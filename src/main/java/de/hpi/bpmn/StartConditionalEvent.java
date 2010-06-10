package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class StartConditionalEvent extends StartEvent {

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
