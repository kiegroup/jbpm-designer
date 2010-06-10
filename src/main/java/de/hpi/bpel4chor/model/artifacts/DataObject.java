package de.hpi.bpel4chor.model.artifacts;

import de.hpi.bpel4chor.util.Output;

/**
 * A data objects represents variables, participant references and participant
 * sets in the diagram and can be associated with activities. 
 */
public abstract class DataObject extends Artifact {

	/**
	 * Constructor. Initializes the data object and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public DataObject(Output output) {
		super(output);
	}	
}
