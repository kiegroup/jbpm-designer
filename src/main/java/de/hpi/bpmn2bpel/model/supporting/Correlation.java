package de.hpi.bpmn2bpel.model.supporting;

/**
 * Correlations are used to identify the istannce of a process a message is 
 * sent to.
 */
public class Correlation {
	
	public static final String INITIATE_YES = "Yes";
	public static final String INITIATE_NO = "No";
	public static final String INITIATE_JOIN = "Join";
	
	public static final String PATTERN_REQUEST = "Request";
	public static final String PATTERN_RESPONSE = "Response";
	public static final String PATTERN_REQUEST_RESPONSE = "Request-Response";
	
	private String set = null;
	private String initiate = INITIATE_NO;
	private String pattern = null;
	
	/**
	 * Constructor. Initializes the correlation object.
	 */
	public Correlation() {}

	/**
	 * @return "yes" if the correlation set will be initiated, "no" otherwise.
	 */
	public String getInitiate() {
		return this.initiate;
	}

	/**
	 * @return The pattern that indicates whether the correlation applies to
	 * the outgoing message, the incoming message or both. The result is null
	 * if no pattern was defined. 
	 */
	public String getPattern() {
		return this.pattern;
	}

	/**
	 * @return The name of the correlation set of the correlation object.
	 */
	public String getSet() {
		return this.set;
	}

	/**
	 * Sets the value that determines if the correlation set will be initiated.
	 * 
	 * @param initiate "yes" if the correlation set will be initiated, "no" otherwise.
	 */
	public void setInitiate(String initiate) {
		this.initiate = initiate;
	}

	/**
	 * Sets the pattern of the correlation that indicates whether the 
	 * correlation applies to the outgoing message, the incoming message or 
	 * both. Since only service tasks can have incoming and outgoing message
	 * flows, the pattern should only be set for this type of task.
	 * 
	 * @param pattern The pattern of the correlation ({@link #PATTERN_REQUEST_RESPONSE}, 
	 * {@link #PATTERN_REQUEST} or {@link #PATTERN_RESPONSE}).
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * Sets the name of the correlation set.
	 * 
	 * @param set The name of the correlation set.
	 */
	public void setSet(String set) {
		this.set = set;
	}
}
