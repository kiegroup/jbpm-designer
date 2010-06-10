package de.hpi.bpel4chor.model.activities;

import de.hpi.bpel4chor.model.connections.Transition;

import de.hpi.bpel4chor.util.Output;

/**
 * A task describes work that is done during the process. It
 * must be located within the sequence flow of the process. Thus,
 * a task must have one incoming and one outgoing sequence flow.
 */
public abstract class Task extends Activity {
	
	/**
	 * Constructor. Initializes the task and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	protected Task(Output output) {
		super(output);
	}
	
	/**
	 * Constructor. Initializes the event and generates a unique id.
	 * 
	 * @param generated   True, if the event was generated during the 
	 *                    transformation, false if it was parsed from the input
	 * @param output      The output to print errors to.
	 */
	protected Task(boolean generated, Output output) {
		super(generated, output);
	}
	
	@Override
	/**
	 * A task is not allowed to have multiple outgoing transitions. Thus,
	 * if there is already an outgoing transition an error is added to the 
	 * output.
	 */
	public void addSourceFor(Transition transition, Output output) {
		if (!this.sourceFor.isEmpty()) {
			output.addError(
					"This task " +
					"is not allowed to have multiple outgoing transitions.", getId());
		} else {
			super.addSourceFor(transition, output);
		}
	}
	
	@Override
	/**
	 * A task is not allowed to have multiple incoming transitions. Thus,
	 * if there is already an incoming transition an error is added to the 
	 * output.
	 */
	public void addTargetFor(Transition transition, Output output) {
		if (!this.targetFor.isEmpty()) {
			output.addError("This task " +
					"is not allowed to have multiple incoming transitions.", getId());
		} else {
			super.addTargetFor(transition, output);
		}
	}
}
