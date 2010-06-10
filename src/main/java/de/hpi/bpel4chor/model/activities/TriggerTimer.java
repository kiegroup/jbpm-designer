package de.hpi.bpel4chor.model.activities;

import de.hpi.bpel4chor.model.supporting.Expression;

/**
 * The timer trigger is used by timer start or intermediate events.
 * It provides the information about the time duration or deadline
 * that triggers the event.
 * 
 * Either a deadline or a duration should be defined for the trigger.
 */
public class TriggerTimer extends Trigger {
	 
	private Expression timeDurationExpression = null;
	private Expression timeDeadlineExpression = null;
	private Expression repeatEveryExpression = null;
	
	/**
	 * Constructor. Initializes the trigger.
	 */
	public TriggerTimer() {}

	/**
	 * @return The expression that defines a repeated triggering.
	 */
	public Expression getRepeatEveryExpression() {
		return this.repeatEveryExpression;
	}

	/**
	 * @return The expression that defines the triggering deadline.
	 */
	public Expression getTimeDeadlineExpression() {
		return this.timeDeadlineExpression;
	}

	/**
	 * @return The expression that defines the triggering time duration.
	 */
	public Expression getTimeDurationExpression() {
		return this.timeDurationExpression;
	}

	/**
	 * Sets the expression that defines a repeated triggering.
	 * 
	 * @param repeatEveryExpression The new expression.
	 */
	public void setRepeatEveryExpression(Expression repeatEveryExpression) {
		this.repeatEveryExpression = repeatEveryExpression;
	}

	/**
	 * Sets the expression that defines the triggering deadline.
	 * 
	 * @param timeDeadlineExpression The new deadline expression.
	 */
	public void setTimeDeadlineExpression(Expression timeDeadlineExpression) {
		this.timeDeadlineExpression = timeDeadlineExpression;
	}

	/**
	 * Sets the expression that defines the triggering time duration.
	 * 
	 * @param timeDurationExpression The new duration expression.
	 */
	public void setTimeDurationExpression(Expression timeDurationExpression) {
		this.timeDurationExpression = timeDurationExpression;
	}
}
