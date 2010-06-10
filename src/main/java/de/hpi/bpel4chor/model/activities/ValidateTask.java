package de.hpi.bpel4chor.model.activities;

import de.hpi.bpel4chor.util.Output;

/**
 * A validate task validates variables against their definition. The
 * variables to validate must be associated with the task as input 
 * variables. 
 */
public class ValidateTask extends Task {
	
	/**
	 * Constructor. Initializes the task and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public ValidateTask(Output output) {
		super(output);
	}
}
