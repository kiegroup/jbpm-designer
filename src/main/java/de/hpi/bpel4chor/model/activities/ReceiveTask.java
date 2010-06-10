package de.hpi.bpel4chor.model.activities;

import java.util.ArrayList;
import java.util.List;
import de.hpi.bpel4chor.model.supporting.Correlation;
import de.hpi.bpel4chor.model.supporting.FromPart;
import de.hpi.bpel4chor.util.Output;

/**
 * A receive task can receive messages. Thus, it can be the target activity
 * of a message flow. Receive tasks can define output variables the message is 
 * stored in and they can instantiate a process.
 */
public class ReceiveTask extends Task {
	
	private boolean opaqueOutput = false;
	private String messageExchange = null;
	private boolean instantiate = false;
	
	private List<Correlation> correlations = new ArrayList<Correlation>();
	private List<FromPart> fromParts = new ArrayList<FromPart>();
	
	/**
	 * Constructor. Initializes the task and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public ReceiveTask(Output output) {
		super(output);
	}

	/**
	 * @return The correlations defined for the task.
	 */
	public List<Correlation> getCorrelations() {
		return this.correlations;
	}

	/**
	 * @return True, if the output variable of the task is omitted, 
	 * false otherwise.
	 */
	public boolean isOpaqueOutput() {
		return this.opaqueOutput;
	}
	
	/**
	 * @return True, if the task instantiates the process, false otherwise.
	 */
	public boolean isInstantiate() {
		return this.instantiate;
	}

	/**
	 * @return The fromParts defined for the receive task. FromParts can
	 * be defined instead of an output variable.
	 */
	public List<FromPart> getFromParts() {
		return this.fromParts;
	}

	/**
	 * @return The message exchange defined for the receive task. 
	 */
	public String getMessageExchange() {
		return this.messageExchange;
	}

	/**
	 * Sets the correlations for the receive task. Already existing
	 * correlations will be removed.
	 * 
	 * @param correlations The new correlations.
	 */
	public void setCorrelations(List<Correlation> correlations) {
		this.correlations = correlations;
	}

	/**
	 * Sets the fromParts for the receive task. FromParts can be
	 * defined instead of an output variable. The determine where the 
	 * data of the received message is stored.
	 * 
	 * @param fromParts The fromParts of the receive task.
	 */
	public void setFromParts(List<FromPart> fromParts) {
		this.fromParts = fromParts;
	}

	/**
	 * Sets the message exchange for the receive task.
	 * 
	 * @param messageExchange The new message exchange.
	 */
	public void setMessageExchange(String messageExchange) {
		this.messageExchange = messageExchange;
	}

	/**
	 * Defines whether the output variable of the receive task is omitted.
	 * 
	 * @param opaqueOutput True if the output variable should be omitted, 
	 * false otherwise.
	 */
	public void setOpaqueOutput(boolean opaqueOutput) {
		this.opaqueOutput = opaqueOutput;
	}
	
	/**
	 * Defines if the receive task instantiates the process.
	 * 
	 * @param instantiate True if the task instantiates the process, 
	 * false otherwise.
	 */
	public void setInstantiate(boolean instantiate) {
		this.instantiate = instantiate;
	}
}
