package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class Pool extends Node implements Container {
	
	protected List<Node> childNodes;
	
	public List<Node> getChildNodes() {
		if (childNodes == null)
			childNodes = new ArrayList<Node>();
		return childNodes;
	}

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
