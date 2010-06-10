package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class UndirectedAssociation extends Edge {

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
