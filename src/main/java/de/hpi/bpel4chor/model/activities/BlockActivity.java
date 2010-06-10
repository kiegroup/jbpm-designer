package de.hpi.bpel4chor.model.activities;

import de.hpi.bpel4chor.model.SubProcess;
import de.hpi.bpel4chor.model.connections.Transition;
import de.hpi.bpel4chor.util.Output;

/**
 * A block acitivity is an activity that contains a sub-process.
 * BlockActivity is the base class for scopes and handlers.
 */
public abstract class BlockActivity extends Activity {
		
	private String isolated = null;
	private String exitOnStandardFault = null;
	private SubProcess subProcess = null;
	
	/**
	 * Constructor. Initializes the block activity and generates a unique id.
	 * 
	 * @param output The output to print errors to.
	 */
	protected BlockActivity(Output output) {
		super(output);
	}

	/**
	 * @return The sub-process containing the activities and transitions
	 * contained in the block activity.
	 */
	public SubProcess getSubProcess() {
		return this.subProcess;
	}
	
	@Override
	/**
	 * Adds in outgoing transition to the block activity. If there
	 * is already an outgoing transition, an error is added to the output.
	 */
	public void addSourceFor(Transition transition, Output output) {
		if (!this.sourceFor.isEmpty()) {
			output.addError(
					"This block activity " +
					"is not allowed to have multiple outgoing transitions.",
					getId());
		} else {
			super.addSourceFor(transition, output);
		}
	}
	
	@Override
	/**
	 * Adds in incoming transition to the block activity. If there
	 * is already an incoming transition, an error is added to the output.
	 */
	public void addTargetFor(Transition transition, Output output) {
		if (!this.targetFor.isEmpty()) {
			output.addError(
					"Thois block activity " +
					"is not allowed to have multiple incoming transitions.",
					getId());
		} else {
			super.addTargetFor(transition, output);
		}
	}

	/**
	 * @return "yes", if the block activity exists on standard faults, 
	 * "no" otherwise. The result is null, if the exit on standard fault 
	 * value is not specified.
	 */
	public String getExitOnStandardFault() {
		return this.exitOnStandardFault;
	}

	/**
	 * Sets the exit on standard fault value.
	 * 
	 * @param exitOnStandardFault "yes" if the block activity should
	 * exit on standard faults, "no" otherwise.
	 */
	public void setExitOnStandardFault(String exitOnStandardFault) {
		this.exitOnStandardFault = exitOnStandardFault;
	}

	/**
	 * @return "yes", if the block activity is executed isolated, 
	 * "no" otherwise. The result is null, if the isolated 
	 * value is not specified.
	 */
	public String getIsolated() {
		return this.isolated;
	}

	/**
	 * Sets the isolated value of the block activity.
	 * 
	 * @param isolated "yes" if the block acitivity is executed
	 * isolated, "no" otherwise.
	 */
	public void setIsolated(String isolated) {
		this.isolated = isolated;
	}

	/**
	 * Sets the sub-process that containes the activities and
	 * transitions located in the block activity.
	 * 
	 * @param subProcess The sub-process of the block activity.
	 */
	public void setSubProcess(SubProcess subProcess) {
		this.subProcess = subProcess;
	}
}
