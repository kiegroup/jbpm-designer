package de.hpi.bpel4chor.model.supporting;

/**
 * A loop expresses that an activity is processed multiple times. 
 * It can be a standard (while, repeat) or a multi-instance (forEach) loop.
 */
public class Loop {
	
	public static final String TYPE_STANDARD = "Standard";
	public static final String TYPE_MULITPLE = "MultiInstance";
	public static final String TEST_TIME_AFTER = "After";
	public static final String TEST_TIME_BEFORE = "Before";
	
	public static final String ORDERING_SEQUENTIAL = "Sequential";
	public static final String ORDERING_PARALLEL = "Parallel";
	
	private String loopType = null;
	
	// standard attributes
	private Expression loopCondition = null;
	private String testTime = TEST_TIME_AFTER;
	
	// multi-instance attributes
	private String successfulBranchesOnly = null;
	private String ordering = ORDERING_SEQUENTIAL;
	private Expression startCounterValue = null;
	private Expression finalCounterValue = null;
	private Expression completionCondition = null;
	
	/**
	 * Constructor. Initializes the loop object.
	 */
	public Loop() {}

	/**
	 * @return The completion condition that defines the early termination
	 * of multi-instance branches.
	 */
	public Expression getCompletionCondition() {
		return this.completionCondition;
	}

	/**
	 * @return The final counter value that defines the termination
	 * of the loop.
	 */
	public Expression getFinalCounterValue() {
		return this.finalCounterValue;
	}

	/**
	 * @return The condition for the standard loop.
	 */
	public Expression getLoopCondition() {
		return this.loopCondition;
	}

	/**
	 * @return The type of the loop ({@link #TYPE_MULITPLE} or 
	 * {@link #TYPE_STANDARD}).
	 */
	public String getLoopType() {
		return this.loopType;
	}

	/**
	 * @return The ordering of a multi-instance loop (
	 * {@link #ORDERING_PARALLEL} or {@link #ORDERING_SEQUENTIAL}).
	 */
	public String getOrdering() {
		return this.ordering;
	}

	/**
	 * @return The start value of the counter in a multi-instance loop.
	 */
	public Expression getStartCounterValue() {
		return this.startCounterValue;
	}

	/**
	 * @return "yes" if only successful branches should be considered
	 * in the completion condition, "no" otherwise.
	 */
	public String isSuccessfulBranchesOnly() {
		return this.successfulBranchesOnly;
	}

	/**
	 * @return The test time of a standard loop, that determines when the 
	 * loop condition will be evaluated ({@link #TEST_TIME_AFTER} or 
	 * {@link #TEST_TIME_BEFORE}). 
	 */
	public String getTestTime() {
		return this.testTime;
	}

	/**
	 * Sets the value that determines if only successful branches should be
	 * considered in the completion condition.
	 * @param successfulBranchesOnly "yes" if only successful branches should
	 * be considered in the completion condition, "no" otherwise.
	 */
	public void setSuccessfulBranchesOnly(String successfulBranchesOnly) {
		this.successfulBranchesOnly = successfulBranchesOnly;
	}

	/**
	 * Sets the completion condition that defines the early termination
	 * of multi-instance branches.
	 * 
	 * @param completionCondition The completion condition to set.
	 */
	public void setCompletionCondition(Expression completionCondition) {
		this.completionCondition = completionCondition;
	}

	/**
	 * Sets the final counter value that defines the termination
	 * of the loop.
	 * 
	 * @param finalCounterValue The final counter value to set.
	 */
	public void setFinalCounterValue(Expression finalCounterValue) {
		this.finalCounterValue = finalCounterValue;
	}

	/**
	 * Sets the condition for the standard loop. If the loop
	 * has a test time of the value {@link #TEST_TIME_AFTER} the loop
	 * terminates if the expression evaluates to true. If the loop has
	 * a test time of the value {@link #TEST_TIME_BEFORE} the loop
	 * terminates if the expression evaluates to false.
	 * 
	 * @param loopCondition The loop condition to set.
	 */
	public void setLoopCondition(Expression loopCondition) {
		this.loopCondition = loopCondition;
	}

	/**
	 * Sets the type of the loop.
	 * 
	 * @param loopType The type of the loop ({@link #TYPE_MULITPLE} or 
	 * {@link #TYPE_STANDARD}).
	 */
	public void setLoopType(String loopType) {
		this.loopType = loopType;
	}

	/**
	 * Sets the ordering of a multi-instance loop.
	 * 
	 * @param ordering The ordering to set
	 * ({@link #ORDERING_PARALLEL} or {@link #ORDERING_SEQUENTIAL}).
	 */
	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}

	/**
	 * Sets the start value of the counter in a multi-instance loop.
	 * 
	 * @param startCounterValue The expression for determining the start value.
	 */
	public void setStartCounterValue(Expression startCounterValue) {
		this.startCounterValue = startCounterValue;
	}

	/**
	 * Sets the test time of a standard loop, that determines when the 
	 * loop condition will be evaluated
	 * 
	 * @param testTime The test time to set ({@link #TEST_TIME_AFTER} or 
	 * {@link #TEST_TIME_BEFORE}). 
	 */
	public void setTestTime(String testTime) {
		this.testTime = testTime;
	}
}
