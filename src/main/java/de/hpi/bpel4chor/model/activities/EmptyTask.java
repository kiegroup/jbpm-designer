package de.hpi.bpel4chor.model.activities;

import de.hpi.bpel4chor.util.Output;

/**
 * An emtpy task is a task that does nothing.
 */
public class EmptyTask extends Task {

	/**
	 * Constructor. Initializes the empty task and generates a unique id.
	 * 
	 * @param output The output to print errors to.
	 */
	public EmptyTask(Output output) {
		super(output);
	}
}
