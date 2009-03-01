package de.hpi.bpel4chor.model.activities;

import de.hpi.bpel4chor.util.Output;

/**
 * A start event start the sequence flow of a process. Thus it can not have
 * incoming transitions. Start events can have triggers of the type none, message
 * or timer.
 */
public class StartEvent extends Event {
	
	public static final String TRIGGER_NONE = "None";
	public static final String TRIGGER_MESSAGE = "Message";
	public static final String TRIGGER_TIMER = "Timer";
	
	/**
	 * Constructor. Initializes the event and generates a unique id. If the
	 * trigger type is "None", the trigger parameter should be null. 
	 * 
	 * @param triggerType The trigger type of the intermediate event.
	 * @param trigger     The trigger object for the appropriate trigger type.
	 * @param generated   True, if the event was generated during the transformation, 
	 *                    false if it was parsed from the input.
	 * @param output      The output to print errors to.
	 */
	public StartEvent(String triggerType, Trigger trigger, boolean generated, Output output) {
		super(generated, trigger, output);
		if (triggerType == null) {
			this.setTriggerType(TRIGGER_NONE, output);
		} else {
			this.setTriggerType(triggerType, output);
		}
	}
	
	/**
	 * Constructor. Initializes the event and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public StartEvent(Output output) {
		super(output);
		this.setTriggerType(TRIGGER_NONE, output);
	}
	
	
	/**
	 * Sets the trigger type of the event. If the trigger type is
	 * not allowed, an error will be added to the output.
	 * 
	 * @param triggerType The new trigger type of the event 
	 * ({@link #TRIGGER_NONE}, {@link #TRIGGER_MESSAGE} or 
	 * {@link #TRIGGER_TIMER}).	
	 */
	@Override
	public void setTriggerType(String triggerType, Output output) {
		if (triggerType.equals(TRIGGER_MESSAGE) || 
				triggerType.equals(TRIGGER_TIMER) || 
				triggerType.equals(TRIGGER_NONE)) { 
			super.setTriggerType(triggerType, output);
		} else {
			output.addError(
					"The trigger type " + triggerType + 
					" is not allowed for this start event", this.getId());
		}
	}
}
