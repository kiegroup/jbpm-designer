package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class Lane extends Node implements Container {
	
	protected List<Node> childNodes;
	/**
	 * BPMN extension for YAWL: resourcingType
	 */
	protected String resourcingType = "";
	
	public List<Node> getChildNodes() {
		if (childNodes == null)
			childNodes = new ArrayList<Node>();
		return childNodes;
	}

	/**
	 * the resourcingType getter
	 * @return resourcingType
	 */
	public String getResourcingType() {
		return resourcingType;
	}

	/**
	 * the resourcingType setter
	 * @param resourcingType
	 */
	public void setResourcingType(String resourcingType) {
		this.resourcingType = resourcingType;
	}

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
