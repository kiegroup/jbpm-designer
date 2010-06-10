package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class ComplexGateway extends Gateway {

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}

}
