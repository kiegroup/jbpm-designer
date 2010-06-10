package de.hpi.bpmn;

public abstract class IntermediateEvent extends Event {
	
	protected Activity activity; // for attached intermediate events

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		if (this.activity != activity) {
			if (this.activity != null)
				this.activity.getAttachedEvents().remove(this);
			if (activity != null)
				activity.getAttachedEvents().add(this);
		}
		this.activity = activity;
	}
	
	/**
	 * 
	 * @return true if event is attached to an activity
	 */
	public boolean isAttached(){
		return activity != null;
	}

}