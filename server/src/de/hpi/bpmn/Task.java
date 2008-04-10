package de.hpi.bpmn;

public class Task extends Activity {
	protected boolean skipable;

	public boolean isSkipable() {
		return skipable;
	}

	public void setSkipable(boolean skipable) {
		this.skipable = skipable;
	}

}
