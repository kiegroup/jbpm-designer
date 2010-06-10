package de.hpi.bpel4chor.model;

import de.hpi.bpel4chor.model.activities.BlockActivity;

/**
 * A sub-process is a container for activities and transitions.
 * Each sub-process is located in a block activity.
 */
public class SubProcess extends Container {

	private BlockActivity activity = null;
	
	/**	
	 * @return The block activity the sub-process is located in.
	 */
	public BlockActivity getBlockActivity() {
		return this.activity;
	}
	
	/**
	 * Sets the block activity the sub-process is located in.
	 * 
	 * @param blockActivity The block activity the sub-process is located in.
	 */
	public void setBlockActivity(BlockActivity blockActivity) {
		this.activity = blockActivity;
	}
}
