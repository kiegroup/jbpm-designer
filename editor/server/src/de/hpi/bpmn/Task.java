package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class Task extends Activity {
	protected boolean skippable;
	protected String rolename;
	protected String rightInitProcess;
	protected String rightExecuteTask; 
	protected String rightSkipTask;
	protected String rightDelegateTask;
	protected String form;
	protected String color;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isSkippable() {
		return skippable;
	}

	public void setSkippable(boolean skippable) {
		this.skippable = skippable;
	}
	
	public String getRolename() {
		return rolename;
	}
	
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
