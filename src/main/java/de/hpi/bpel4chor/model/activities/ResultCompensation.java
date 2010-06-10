package de.hpi.bpel4chor.model.activities;

/**
 * The compensation result is used by compensation intermediate events and
 * defines the activity to be compensated.
 * 
 * The parser of the input data should determine the activity for the 
 * specified id, because this activity is used during the transformation.
 */
public class ResultCompensation extends Trigger {
	
	private Activity activity = null;
	private String activityId = null;
	
	/**
	 * Constructor. Initializes the result.
	 */
	public ResultCompensation() {}
	
	/**
	 * Constructor. Initializes the result and sets the activity to be 
	 * compensated.
	 * 
	 * @param id The id of the activity to compensate.
	 */
	public ResultCompensation(String id) {
		this.activityId = id;
	}

	/**
	 * @return The activity to compensate or null, if the activity
	 * was not specified.
	 */
	public Activity getActivity() {
		return this.activity;
	}
	
	/**
	 * @return The id of the activity to compensate or null, if the id
	 * was not specified.
	 */
	public String getActivityId() {
		return this.activityId;
	}

	/**
	 * Sets the activity to compensate. This activity should have the id 
	 * defined for this result.
	 * 
	 * @param activity The activity to compensate.
	 */
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
}
