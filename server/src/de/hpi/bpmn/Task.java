package de.hpi.bpmn;

public class Task extends Activity {
	protected boolean skippable;

	public boolean isSkippable() {
		return skippable;
	}

	public void setSkippable(boolean skippable) {
		this.skippable = skippable;
	}

}
