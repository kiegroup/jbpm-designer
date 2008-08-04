package de.hpi.bpmn;

public class SequenceFlow extends Edge {
	
	public enum ConditionType {
		NONE, DEFAULT, EXPRESSION
	}
	
	protected ConditionType conditionType = ConditionType.NONE;

	public ConditionType getConditionType() {
		return conditionType;
	}

	public void setConditionType(ConditionType conditionType) {
		this.conditionType = conditionType;
	}

}
