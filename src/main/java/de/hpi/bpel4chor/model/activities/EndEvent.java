package de.hpi.bpel4chor.model.activities;

import de.hpi.bpel4chor.util.Output;

/**
 * The end event is an event that finishes the control flow of a process or
 * sub-process. There are only end events with the trigger "None".
 */
public class EndEvent extends Event {
	
	public static final String TRIGGER_NONE = "None";
	
	/**
	 * Constructor. Initializes the end event and generates a unique id.
	 * 
	 * @param output The output to print errors to.
	 */
	public EndEvent(Output output) {
		super(output);
		this.setTriggerType(TRIGGER_NONE, output);
	}
	
	/**
	 * Sets the trigger type of the event. If the trigger type is
	 * not allowed, an error will be added to the output.
	 * 
	 * @param triggerType The new trigger type of the event 
	 * ({@link #TRIGGER_NONE}).	
	 */
	@Override
	public void setTriggerType(String triggerType, Output output) {
		if (triggerType.equals(TRIGGER_NONE)) { 
			super.setTriggerType(triggerType, output);
		} else {
			output.addError(
					"The trigger type " + triggerType + 
					" is not allowed for this end event", this.getId());
		}
	}
}
