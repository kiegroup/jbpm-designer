package de.hpi.bpel4chor.model;

import de.hpi.bpel4chor.util.Output;

/**
 * A pool set is a special swimlane that represents a multiple participants in
 * the choreography. The process contained in the pool set defines the
 * participant behaviour description of these participants. The actual 
 * participants represented by a pool set must be expressed using participant
 * set data objects.
 * 
 */
public class PoolSet extends Swimlane{

	/**
	 * Constructor. Initializes the pool set and generates a unique id.
	 * 
	 * @param output The output to print errors to.
	 */
	public PoolSet(Output output) {
		super(output);
	}	
}
