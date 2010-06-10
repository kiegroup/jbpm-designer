package de.hpi.bpel4chor.model.activities;

import de.hpi.bpel4chor.model.connections.Transition;

import de.hpi.bpel4chor.util.Output;

/**
 * An Event is something that happens during the execution of the process. An 
 * event can be triggered by something (e.g. fault is caught) or it triggers 
 * something (e.g. fault is thrown). Event is the base class for start, 
 * intermediate and end events.
 */
public abstract class Event extends Activity {
	
	private Trigger trigger = null;
	private String triggerType = null;
	
	/**
	 * Constructor. Initializes the event and generates a unique id.
	 * 
	 * @param output The output to print errors to.
	 */
	protected Event(Output output) {
		super(output);
	}
	
	/**
	 * Constructor. Initializes the event and generates a unique id.
	 * 
	 * @param generated True, if the event was generated during the
	 * transformation, False if it is parsed from the input data.
	 * @param output The output to print errors to.
	 * @param trigger The trigger of the event.
	 */
	protected Event(boolean generated, Trigger trigger, Output output) {
		super(generated, output);
		this.trigger = trigger;
	}
	
	/**
	 * Constructor. Initializes the event and generates a unique id.
	 * 
	 * @param id   The id of the event.
	 * @param name The name of the event.
	 * @param trigger The trigger of the event.
	 */
	protected Event(String id, String name, Trigger trigger) {
		super(id, name);
		this.trigger = trigger;
	}
	
	@Override
	public void addSourceFor(Transition transition, Output output) {
		if (!this.sourceFor.isEmpty()) {
			output.addError(
					"This event " + 
					" is not allowed to have multiple outgoing transitions.",  getId());
		} else {
			super.addSourceFor(transition, output);
		}
	}
	
	@Override
	public void addTargetFor(Transition transition, Output output) {
		if (!this.targetFor.isEmpty()) {
			output.addError(
					"This event " +  
					" is not allowed to have multiple incoming transitions.",
					getId());
		} else {
			super.addTargetFor(transition, output);
		}
	}
	
	/**
	 * @return The trigger object of the event. 
	 */
	public Trigger getTrigger() {
		return this.trigger;
	}
	
	/**
	 * @return The trigger type of the event.
	 */
	public String getTriggerType() {
		return this.triggerType;
	}
	
	/**
	 * Sets the trigger object for the appropriate trigger type of the event.
	 * 
	 * @param trigger The new event trigger.
	 */
	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}
	
	/**
	 * Sets the trigger type of the event.
	 * 
	 * @param triggerType The new trigger type of the event.
	 */
	public void setTriggerType(String triggerType, Output output) {
		this.triggerType = triggerType;
	}
}
