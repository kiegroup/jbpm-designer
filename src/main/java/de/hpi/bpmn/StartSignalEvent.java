package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class StartSignalEvent extends StartEvent {

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
