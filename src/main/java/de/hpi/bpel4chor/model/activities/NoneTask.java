package de.hpi.bpel4chor.model.activities;

import de.hpi.bpel4chor.util.Output;

/**
 * An non-typed task hides the details about its implementation.
 */
public class NoneTask extends Task {
	
	/**
	 * Constructor. Initializes the task and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public NoneTask(Output output) {
		super(output);
	}

}
