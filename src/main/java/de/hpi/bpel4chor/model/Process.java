package de.hpi.bpel4chor.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import de.hpi.bpel4chor.model.supporting.CorrelationSet;

/**
 * A process is a container that holds activities and transitions that are
 * directly contained within a swimlane.
 */
public class Process extends Container {
	
	private String name = null;
	private boolean suppressJoinFailure = false;
	private boolean enableInstanceCompensation = false;
	private URI expressionLanguage = null;
	private URI queryLanguage = null;
	private boolean exitOnStandardFault = false;
	
	private List<SubProcess> subProcesses = new ArrayList<SubProcess>();
	private List<String> messageExchanges = new ArrayList<String>();
	private List<CorrelationSet> correlations = new ArrayList<CorrelationSet>();
	
	/**
	 * Adds a sub-process to the list of sub-processes contained in the process.
	 * 
	 * @param subProcess The subprocess to add.
	 */
	public void addSubProcess(SubProcess subProcess) {
		this.subProcesses.add(subProcess);
	}
	
	/**
	 * Adds a message exchange value to the list of message exchanges of the
	 * process.
	 * 
	 * @param messageExchange The message exchange to add.
	 */
	public void addMessageExchange(String messageExchange) {
		this.messageExchanges.add(messageExchange);
	}
	
	/**
	 * Adds a correlation set the list of correlation sets of the process.
	 * 
	 * @param correlationSet The correlation set to add.
	 */
	public void addCorrelationSet(CorrelationSet correlationSet) {
		this.correlations.add(correlationSet);
	}

	/**
	 * @return The list of correlation sets defined for the process.
	 */
	public List<CorrelationSet> getCorrelationSets() {
		return this.correlations;
	}

	/**
	 * @return True, if the process allows instance compensationsm, 
	 * false otherwise.
	 */
	public boolean isEnableInstanceCompensation() {
		return this.enableInstanceCompensation;
	}

	/**
	 * @return True, if the process exists on standard faults, 
	 * false otherwise.
	 */
	public boolean isExitOnStandardFault() {
		return this.exitOnStandardFault;
	}

	/**
	 * @return The expression language defined for the process or null if
	 * no expression language defined.
	 */
	public URI getExpressionLanguage() {
		return this.expressionLanguage;
	}

	/**
	 * @return The list with message exchanges defined for the process.
	 */
	public List<String> getMessageExchanges() {
		return this.messageExchanges;
	}

	/**
	 * @return The name of the process or null if no name for the process
	 * defined.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The query language defined for the process or null if no
	 * query language defined.
	 */
	public URI getQueryLanguage() {
		return this.queryLanguage;
	}

	/**
	 * @return True, if join failures in the process will be suppressed,
	 * false otherwise.
	 */
	public boolean isSuppressJoinFailure() {
		return this.suppressJoinFailure;
	}

	/**
	 * @return The list of sub-processes contained in the process.
	 */
	public List<SubProcess> getSubProcesses() {
		return this.subProcesses;
	}

	/**
	 * Sets the enablement of instance compensation for the process.
	 * 
	 * @param enableInstanceCompensation True, if instance compensation is enabled,
	 * false otherwise.
	 */
	public void setEnableInstanceCompensation(boolean enableInstanceCompensation) {
		this.enableInstanceCompensation = enableInstanceCompensation;
	}

	/**
	 * Sets the exit on standard fault property of a process.
	 * 
	 * @param exitOnStandardFault True, if the process should exit on 
	 * standard faults, false otherwise.
	 */
	public void setExitOnStandardFault(boolean exitOnStandardFault) {
		this.exitOnStandardFault = exitOnStandardFault;
	}

	/**
	 * Sets the expression language of the process.
	 * 
	 * @param expressionLanguage The new expression language.
	 */
	public void setExpressionLanguage(URI expressionLanguage) {
		this.expressionLanguage = expressionLanguage;
	}

	/**
	 * Sets the name of the process.
	 * 
	 * @param name The new process name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the query language of the process.
	 * 
	 * @param queryLanguage The new query language.
	 */
	public void setQueryLanguage(URI queryLanguage) {
		this.queryLanguage = queryLanguage;
	}

	/**
	 * Sets the suppress join failure property of the process.
	 * 
	 * @param suppressJoinFailure True, if join failures should be suppressed, 
	 * false otherwise.
	 */
	public void setSuppressJoinFailure(boolean suppressJoinFailure) {
		this.suppressJoinFailure = suppressJoinFailure;
	}
}
