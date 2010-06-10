package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

public abstract class Activity extends Node {
	
	public enum LoopType {
		None,Standard,Multiinstance
	}

	public enum TestTime {
		Before,After
	}

	public enum MIOrdering {
		Sequential,Parallel
	}

	public enum MIFlowCondition {
		None,One,All,Complex
	}
	
	protected List<IntermediateEvent> attachedEvents;
	
	protected LoopType loopType = LoopType.None;
	
	protected String loopCondition = "";

	protected String miCondition = "";
	
	protected MIOrdering miOrdering = MIOrdering.Sequential;

	protected TestTime testTime = TestTime.After;
	
	protected MIFlowCondition miFlowCondition = MIFlowCondition.All;
	
	protected String complexMIFlowCondition = "";
	
	protected List<Property> properties;
	
	protected List<Assignment> assignments;
	
	public List<IntermediateEvent> getAttachedEvents() {
		if (attachedEvents == null)
			attachedEvents = new ArrayList<IntermediateEvent>();
		return attachedEvents;
	}
	
	public List<Property> getProperties() {
		if (properties == null)
			properties = new ArrayList<Property>();
		return properties;
	}
	
	public List<Assignment> getAssignments() {
		if (assignments == null)
			assignments = new ArrayList<Assignment>();
		return assignments;
	}

	public LoopType getLoopType() {
		return loopType;
	}

	public void setLoopType(LoopType loopType) {
		this.loopType = loopType;
	}

	public String getLoopCondition() {
		return loopCondition;
	}

	public void setLoopCondition(String loopCondition) {
		this.loopCondition = loopCondition;
	}

	public TestTime getTestTime() {
		return testTime;
	}

	public void setTestTime(TestTime testTime) {
		this.testTime = testTime;
	}

	public String getComplexMIFlowCondition() {
		return complexMIFlowCondition;
	}

	public void setComplexMIFlowCondition(String complexMIFlowCondition) {
		this.complexMIFlowCondition = complexMIFlowCondition;
	}

	public String getMiCondition() {
		return miCondition;
	}

	public void setMiCondition(String miCondition) {
		this.miCondition = miCondition;
	}

	public MIFlowCondition getMiFlowCondition() {
		return miFlowCondition;
	}

	public void setMiFlowCondition(MIFlowCondition miFlowCondition) {
		this.miFlowCondition = miFlowCondition;
	}

	public MIOrdering getMiOrdering() {
		return miOrdering;
	}

	public void setMiOrdering(MIOrdering miOrdering) {
		this.miOrdering = miOrdering;
	}
	
	public boolean isMultipleInstance(){
		return loopType == LoopType.Multiinstance;
	}
	public void setMultipleInstance(){
		loopType = LoopType.Multiinstance;
	}
	
	@Override
	public Node getCopy() {
		Activity newnode = (Activity)super.getCopy();
		newnode.setLoopCondition(this.getLoopCondition());
		newnode.setLoopType(this.getLoopType());
		return newnode;
	}

}
