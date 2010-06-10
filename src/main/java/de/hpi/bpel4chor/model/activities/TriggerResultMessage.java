package de.hpi.bpel4chor.model.activities;

import java.util.ArrayList;
import java.util.List;
import de.hpi.bpel4chor.model.supporting.Correlation;
import de.hpi.bpel4chor.model.supporting.FromPart;

/**
 * The trigger/result message is used by message start or intermediate events.
 * It provides the information that is necessary for the receiving of a 
 * message (correlations, output variable and message exchange).
 */
public class TriggerResultMessage extends Trigger {
	
	private List<Correlation> correlations = new ArrayList<Correlation>();
	private List<FromPart> fromParts = new ArrayList<FromPart>();	
	private boolean opaqueOutput = false;
	private String messageExchange;
	
	/**
	 * Constructor. Initializes the trigger/result.
	 */
	public TriggerResultMessage() {	}

	/**
	 * @return The message exchange defined for the trigger/result. 
	 */
	public String getMessageExchange() {
		return this.messageExchange;
	}

	/**
	 * @return True, if the output variable of the trigger/result is omitted,
	 * false otherwise.
	 */
	public boolean isOpaqueOutput() {
		return this.opaqueOutput;
	}

	/**
	 * @return The correlations defined for the trigger/result.
	 */
	public List<Correlation> getCorrelations() {
		return this.correlations;
	}

	/**
	 * @return The fromParts defined for the trigger/result. FromParts can
	 * be defined instead of an output variable.
	 */
	public List<FromPart> getFromParts() {
		return this.fromParts;
	}

	/**
	 * Sets the correlations for the trigger/result. Already existing
	 * correlations will be removed.
	 * 
	 * @param correlations The new correlations.
	 */
	public void setCorrelations(List<Correlation> correlations) {
		this.correlations = correlations;
	}

	/**
	 * Sets the fromParts for the trigger/result. FromParts can be
	 * defined instead of an output variable. The determine where the 
	 * data of the received message is stored.
	 * 
	 * @param fromParts The fromParts of the trigger/result.
	 */
	public void setFromParts(List<FromPart> fromParts) {
		this.fromParts = fromParts;
	}

	/**
	 * Sets the message exchange for the trigger/result.
	 * 
	 * @param messageExchange The new message exchange.
	 */
	public void setMessageExchange(String messageExchange) {
		this.messageExchange = messageExchange;
	}

	/**
	 * Defines whether the output variable of the trigger/result is omitted.
	 * 
	 * @param opaqueOutput True if the output variable should be omitted, 
	 * false otherwise.
	 */
	public void setOpaqueOutput(boolean opaqueOutput) {
		this.opaqueOutput = opaqueOutput;
	}
}
