package de.hpi.bpel4chor.model.activities;

import de.hpi.bpel4chor.util.Output;

/**
 * An intermediate event is an event that is located with the sequence flow of
 * the process. Thus it has one incoming and one outgoing transition. 
 * Intermediate events can be attached to the boundaries of tasks or scopes.
 * In this case there is no incoming sequence flow.
 * 
 * Intermediate events can be have the trigger message, timer, error, 
 * compensation or termination. Termination intermediate events must be
 * attached.
 */
public class IntermediateEvent extends Event {
	
	public static final String TRIGGER_MESSAGE = "Message";
	public static final String TRIGGER_TIMER = "Timer";
	public static final String TRIGGER_ERROR = "Error";
	public static final String TRIGGER_COMPENSATION = "Compensation";
	public static final String TRIGGER_TERMINATION = "Termination";
	
	private Activity target = null;
	private boolean createInstance = false;
	
	/**
	 * Constructor. Initializes the intermediate event and generates a unique id.
	 * 
	 * @param id          The id of the intermediate event.
	 * @param name        The name of the intermediate event.
	 * @param triggerType The trigger type of the intermediate event.
	 * @param trigger     The trigger object for the appropriate trigger type.
	 * @param output      The output to print errors to.
	 */
	public IntermediateEvent(String id, String name, String triggerType, Trigger trigger, Output output) {
		super(id, name, trigger);
		if (triggerType == null) {
			this.setTriggerType(TRIGGER_MESSAGE, output);
		} else {
			this.setTriggerType(triggerType, output);
		}
	}
	
	/**
	 * Constructor. Initializes the intermediate event and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public IntermediateEvent(Output output) {
		super(output);
		this.setTriggerType(TRIGGER_MESSAGE, output);
	}
	
	/**
	 * Determines the handler that is connected with the event.
	 * The handler must be the target of the outgoing transition 
	 * of the event.
	 * 
	 * @return The determined handler or null, if the event is not
	 * connected with a handler.
	 */
	public Handler getConnectedHandler() {
		if (!this.sourceFor.isEmpty()) {
			Activity target = this.sourceFor.get(0).getTarget();			
			if (target instanceof Handler) {
				Handler handler = (Handler)target;
				
				if (this.getTriggerType().equals(TRIGGER_ERROR) && 
						handler.getHandlerType().equals(Handler.TYPE_FAULT)) {
					return handler;
				} else if (this.getTriggerType().equals(TRIGGER_TERMINATION) && 
						handler.getHandlerType().equals(Handler.TYPE_TERMINATION)) {
					return handler;
				}
			}
		}
		return null;
	}

	/**
	 * @return The target activity the event is attached to. If the event is 
	 * not attached the result is null.
	 */
	public Activity getTarget() {
		return this.target;
	}
	
	
	/**
	 * @return True, if the event instantiates the process, false otherwise.
	 */
	public boolean getCreateInstance() {
		return this.createInstance;
	}
	
	/**
	 * Sets the create instance property of the event, that specifies if
	 * the event instantiates the process.
	 * 
	 * @param createInstance True, if the event instantiates the process, 
	 * false otherwise.
	 */
	public void setCreateInstance(boolean createInstance) {
		this.createInstance = createInstance;
	}

	/**
	 * Sets the activity the event is attached to.
	 * 
	 * @param target The new activity, the event is attached to.
	 */
	public void setTarget(Activity target) {
		this.target = target;
	}

	/**
	 * Sets the trigger type of the event.
	 * 
	 * @param triggerType The new trigger type of the event 
	 * ({@link #TRIGGER_COMPENSATION}, {@link #TRIGGER_ERROR}, 
	 * {@link #TRIGGER_MESSAGE}, {@link #TRIGGER_TERMINATION} or {@link #TRIGGER_TIMER}).
	 */
	@Override
	public void setTriggerType(String triggerType, Output output) {
		if (triggerType.equals(TRIGGER_COMPENSATION) || 
				triggerType.equals(TRIGGER_ERROR) || 
				triggerType.equals(TRIGGER_MESSAGE) || 
				triggerType.equals(TRIGGER_TERMINATION) || 
				triggerType.equals(TRIGGER_TIMER)) {
			super.setTriggerType(triggerType, output);
		} else {
			output.addError(
				"The trigger type " + triggerType + 
				" is not allowed for the intermediate event", this.getId());
		}
	}
}
