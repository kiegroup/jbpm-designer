package de.hpi.bpel4chor.model.connections;

import de.hpi.bpel4chor.model.GraphicalObject;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.supporting.Expression;
import de.hpi.bpel4chor.util.Output;

/**
 * A transition connects flow objects to define the order of their execution.
 * In addition it can define conditions that prevent a transition from being 
 * taken.
 */
public class Transition extends GraphicalObject {
	
	public static final String TYPE_EXPRESSION = "CONDITION";
	public static final String TYPE_OTHERWISE = "OTHERWISE";
	
	private String name = null;
	private Activity source = null;
	private Activity target = null;
	private String conditionType = null;
	private Expression conditionExpression = null;
	
	/**
	 * Constructor. Initializes the transition and generates a unique id.
	 * 
	 * @param source The source activity of the transition
	 * @param target The target activity of the transition
	 * @param output The output to print errors to.
	 */
	public Transition(Activity source, Activity target, Output output) {
		super(output);
		setSource(source, output);
		source.addSourceFor(this, output);
		setTarget(target, output);
		target.addTargetFor(this, output);
	}
	
	/**
	 * Constructor. Initializes the transition and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public Transition(Output output) {
		super(output);
	}
	
	/**
	 * @return The name of the transition
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The condition expression defined for the transition.
	 */
	public Expression getConditionExpression() {
		return this.conditionExpression;
	}

	/**
	 * @return The condition type defined for the transitions 
	 * ({@link #TYPE_EXPRESSION} or {@link #TYPE_OTHERWISE}).
	 */
	public String getConditionType() {
		return this.conditionType;
	}

	/**
	 * @return The source activity of the transition.
	 */
	public Activity getSource() {
		return this.source;
	}
	
	/**
	 * Sets the source activity of the transition. If the source activity
	 * and the target activity are equal an error is added to the output
	 * and the source activity is not set.
	 * 
	 * @param source The source activity to set
	 * @param output The output to print the errors to.
	 */
	public void setSource(Activity source, Output output) {
		if ((this.target != null) && (this.target.equals(source))) {
			output.addError("The source and the target of this transition " +
					"have to be different.", getId());
			return;
		}
		this.source = source;
	}

	/**
	 * @return The target activity of the transition.
	 */
	public Activity getTarget() {
		return this.target;
	}
	
	/**
	 * Sets the target activity of the transition. If the target activity
	 * and the source activity are equal an error is added to the output
	 * and the target activity is not set.
	 * 
	 * @param target The source activity to set
	 * @param output The output to print the errors to.
	 */
	public void setTarget(Activity target, Output output) {
		if ((this.source != null) && (this.source.equals(target))) {
			output.addError(
					"The source and the target of this transition " +
					"have to be different.", getId());
			return;
		}
		this.target = target;
	}

	/**
	 * Sets the condition expression of the transition. If the transition
	 * evaluates to false, the transition will not be taken.
	 * 
	 * @param conditionExpression The condition expression to set
	 */
	public void setConditionExpression(Expression conditionExpression) {
		this.conditionExpression = conditionExpression;
	}

	/**
	 * Sets the condition type of the transition. Transitions of type
	 * {@link #TYPE_OTHERWISE} must not specify a condition expression.
	 * 
	 * @param conditionType The type of the condition 
	 * ({@link #TYPE_EXPRESSION} or {@link #TYPE_OTHERWISE})
	 */
	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}

	/**
	 * Sets the name of the transition.
	 * 
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
}
