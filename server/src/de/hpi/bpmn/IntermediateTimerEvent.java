package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class IntermediateTimerEvent extends IntermediateEvent {

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
