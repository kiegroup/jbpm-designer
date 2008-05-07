package de.hpi.bpmn;

public class Task extends Activity {
	protected boolean skippable;
	protected String form;

	public boolean isSkippable() {
		return skippable;
	}

	public void setSkippable(boolean skippable) {
		this.skippable = skippable;
	}
	
	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

}
