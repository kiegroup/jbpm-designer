package de.hpi.bpmn;

public class Task extends Activity {
	protected boolean skippable;
	protected String rolename;
	protected String rightInitProcess;
	protected String rightExecuteTask; 
	protected String rightSkipTask;
	protected String rightDelegateTask;
	protected String form;

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
	
	public String hasRightInitProcess() {
		return rightInitProcess;
	}
	
	public void setRightInitProcess(String rightInitProcess) {
		this.rightInitProcess = rightInitProcess;
	}
	
	public String hasRightExecuteTask() {
		return rightExecuteTask;
	}
	
	public void setRightExecuteTask(String rightExecuteTask) {
		this.rightExecuteTask = rightExecuteTask;
	}
	
	public String hasRightSkipTask() {
		return rightSkipTask;
	}
	
	public void setRightSkipTask(String rightSkipTask) {
		this.rightSkipTask = rightSkipTask;
	}
	
	public String hasRightDelegateTask() {
		return rightDelegateTask;
	}
	
	public void setRightDelegateTask(String rightDelegateTask) {
		this.rightDelegateTask = rightDelegateTask;
	}
	
	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

}
