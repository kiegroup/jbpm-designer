package de.hpi.bpel4chor.model.activities;

import java.util.ArrayList;
import java.util.List;
import de.hpi.bpel4chor.model.supporting.Correlation;
import de.hpi.bpel4chor.model.supporting.FromPart;
import de.hpi.bpel4chor.model.supporting.ToPart;
import de.hpi.bpel4chor.util.Output;

/**
 * A service sends synchronous calls to other activities. This means it sends a
 * message and waits for a return message. Thus, it can be the source or target 
 * activity of a message flow. Service tasks can define input and output variables.
 */
public class ServiceTask extends Task {
	
	private boolean opaqueInput = false;
	private boolean opaqueOutput = false;
	
	private List<Correlation> correlations = new ArrayList<Correlation>();
	private List<FromPart> fromParts = new ArrayList<FromPart>();
	private List<ToPart> toParts = new ArrayList<ToPart>();
	
	/**
	 * Constructor. Initializes the task and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public ServiceTask(Output output) {
		super(output);
	}

	/**
	 * @return The correlations defined for the task.
	 */
	public List<Correlation> getCorrelations() {
		return this.correlations;
	}

	/**
	 * @return The fromParts defined for the service task. FromParts can
	 * be defined instead of an output variable.
	 */
	public List<FromPart> getFromParts() {
		return this.fromParts;
	}

	/**
	 * @return True, if the input variable of the task is omitted, 
	 * false otherwise.
	 */
	public boolean isOpaqueInput() {
		return this.opaqueInput;
	}

	/**
	 * @return True, if the output variable of the task is omitted, 
	 * false otherwise.
	 */
	public boolean isOpaqueOutput() {
		return this.opaqueOutput;
	}

	/**
	 * @return The toParts defined for the service task. ToParts can
	 * be defined instead of an input variable.
	 */
	public List<ToPart> getToParts() {
		return this.toParts;
	}

	/**
	 * Defines whether the input variable of the service task is omitted.
	 * 
	 * @param opaqueInput True if the input variable should be omitted, 
	 * false otherwise.
	 */
	public void setOpaqueInput(boolean opaqueInput) {
		this.opaqueInput = opaqueInput;
	}

	/**
	 * Defines whether the output variable of the service task is omitted.
	 * 
	 * @param opaqueOutput True if the output variable should be omitted, 
	 * false otherwise.
	 */
	public void setOpaqueOutput(boolean opaqueOutput) {
		this.opaqueOutput = opaqueOutput;
	}

	/**
	 * Sets the correlations for the service task. Already existing
	 * correlations will be removed.
	 * 
	 * @param correlations The new correlations.
	 */
	public void setCorrelations(List<Correlation> correlations) {
		this.correlations = correlations;
	}

	/**
	 * Sets the fromParts for the service task. FromParts can be
	 * defined instead of an output variable. The determine where the 
	 * data of the received message is stored.
	 * 
	 * @param fromParts The fromParts of the service task.
	 */
	public void setFromParts(List<FromPart> fromParts) {
		this.fromParts = fromParts;
	}

	/**
	 * Sets the toParts for the service task. ToParts can be
	 * defined instead of an input variable. The determine where the 
	 * data of the send message is taken from and where it will be
	 * stored in the message.
	 * 
	 * @param toParts The toParts of the service task.
	 */
	public void setToParts(List<ToPart> toParts) {
		this.toParts = toParts;
	}
}
