package de.hpi.bpel4chor.model.activities;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpel4chor.model.supporting.CorrelationSet;
import de.hpi.bpel4chor.util.Output;

/**
 * A scope is a special sub-process that limits the context of its contained
 * activities. It defines correlation sets and message exchanges that can be
 * used from activities within the scope. 
 * 
 */
public class Scope extends BlockActivity {
	
	private List<String> messageExchanges = new ArrayList<String>();
	private List<CorrelationSet> correlations = new ArrayList<CorrelationSet>();
	
	/**
	 * Constructor. Initializes the scope and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public Scope(Output output) {
		super(output);
	}

	/**
	 * @return The message exchanges defined for the scope.
	 */
	public List<String> getMessageExchanges() {
		return this.messageExchanges;
	}
	
	/** 
	 * Adds a message exchange to the list of message exchanges 
	 * defined for the scope.
	 * 
	 * @param messageExchange The message exchange to add.
	 */
	public void addMessageExchange(String messageExchange) {
		this.messageExchanges.add(messageExchange);
	}
	
	/**
	 * Adds a correlation set the list of correlation sets of the scope.
	 * 
	 * @param correlationSet The correlation set to add.
	 */
	public void addCorrelationSet(CorrelationSet correlationSet) {
		this.correlations.add(correlationSet);
	}

	/**
	 * @return The list of correlation sets defined for the scope.
	 */
	public List<CorrelationSet> getCorrelationSets() {
		return this.correlations;
	}
}
