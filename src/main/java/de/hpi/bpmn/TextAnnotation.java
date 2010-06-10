package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class TextAnnotation extends Node {
	
	protected String text = "";

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
