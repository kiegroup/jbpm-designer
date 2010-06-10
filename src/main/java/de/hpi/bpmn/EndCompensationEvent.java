package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class EndCompensationEvent extends EndEvent {

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}

}
