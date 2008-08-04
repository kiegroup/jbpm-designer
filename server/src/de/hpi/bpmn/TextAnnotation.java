package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class TextAnnotation extends Node {

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
