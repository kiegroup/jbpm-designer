package de.hpi.bpel4chor.model.activities;

import java.util.ArrayList;
import java.util.List;
import de.hpi.bpel4chor.model.supporting.Correlation;
import de.hpi.bpel4chor.model.supporting.ToPart;
import de.hpi.bpel4chor.util.Output;

/**
 * A send task can send messages. Thus, it can be the source activity
 * of a message flow. Send tasks can define input variables the message 
 * data is taken from.
 */
public class SendTask extends Task {
	
	private boolean opaqueInput = false;
	private String faultName = null;
	private String messageExchange = null;
	
	private List<Correlation> correlations = new ArrayList<Correlation>();
	private List<ToPart> toParts = new ArrayList<ToPart>();
	
	/**
	 * Constructor. Initializes the task and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public SendTask(Output output) {
		super(output);
	}

	/**
	 * @return The correlations defined for the task.
	 */
	public List<Correlation> getCorrelations() {
		return this.correlations;
	}

	/**
	 * @return True, if the input variable of the task is omitted, 
	 * false otherwise.
	 */
	public boolean isOpaqueInput() {
		return this.opaqueInput;
	}

	/**
	 * @return The toParts defined for the send task. ToParts can
	 * be defined instead of an input variable.
	 */
	public List<ToPart> getToParts() {
		return this.toParts;
	}

	/**
	 * A send task that is connected with a service task by a message flow
	 * can indicate that an error has occured.
	 * 
	 * @return The name of error that will be replied.
	 */
	public String getFaultName() {
		return this.faultName;
	}

	/**
	 * @return The message exchange defined for the receive task.
	 */
	public String getMessageExchange() {
		return this.messageExchange;
	}

	/**
	 * Sets the fault name that will be replied by the send task. A fault
	 * name should only be defined if the send task is connected with a 
	 * service task by a message flow.
	 * 
	 * @param faultName The name of the error that will be replied.
	 */
	public void setFaultName(String faultName) {
		this.faultName = faultName;
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
	 * Defines whether the input variable of the send task is omitted.
	 * 
	 * @param opaqueInput True if the input variable should be omitted, 
	 * false otherwise.
	 */
	public void setOpaqueInput(boolean opaqueInput) {
		this.opaqueInput = opaqueInput;
	}

	/**
	 * Sets the toParts for the send task. ToParts can be
	 * defined instead of an input variable. The determine where the 
	 * data of the send message is taken from and where it will be
	 * stored in the message.
	 * 
	 * @param toParts The toParts of the send task.
	 */
	public void setToParts(List<ToPart> toParts) {
		this.toParts = toParts;
	}

	/**
	 * Sets the correlations for the send task. Already existing
	 * correlations will be removed.
	 * 
	 * @param correlations The new correlations.
	 */
	public void setCorrelations(List<Correlation> correlations) {
		this.correlations = correlations;
	}
}
