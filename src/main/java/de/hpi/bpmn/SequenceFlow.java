package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class SequenceFlow extends Edge {
	
	public enum ConditionType {
		NONE, DEFAULT, EXPRESSION
	}
	
	protected ConditionType conditionType = ConditionType.NONE;

	protected String conditionExpression = "";
	
	public ConditionType getConditionType() {
		return conditionType;
	}

	public void setConditionType(ConditionType conditionType) {
		this.conditionType = conditionType;
	}

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}

	public String getConditionExpression() {
		return conditionExpression;
	}

	public void setConditionExpression(String conditionExpression) {
		this.conditionExpression = conditionExpression;
	}
	
}
