package de.hpi.bpel4chor.transformation.factories;

import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.supporting.Expression;

/**
 * Link that was generated during the mapping of a component that matches the 
 * generalized flow pattern. A link has a source and a target activity. The 
 * name of the link is generated automatically during the instantiation. For 
 * this purpose a static variable index is used to identify the index of the 
 * last created link. In this way the name is unique within all created links. 
 * The name has the pattern: "generatedLink_" + index
 */
public class Link {
	
	private Activity source;
	private Activity target;
	private Expression expression;
	private String name;
	
	/**
	 * Constructor. Initializes the link with its
	 * source and target and creates the name of the link.
	 * 
	 * @param source The source activity
	 * @param target The target activity
	 * @param index  The index of the link to create the id with.
	 */
	public Link(Activity source, Activity target, int index) {
		this.source = source;
		this.target = target;
		this.name="generatedLink_" + index;
	}
	
	/**
	 * Constructor. Initializes the link with its source and target
	 * and expression. Moreover it creates the name of the link.
	 * 
	 * @param source     The source activity
	 * @param target     The target activity
	 * @param expression The expression that defines the transition condition
	 *                   of the link
	 * @param index      The index of the link to create the id with.
	 */
	public Link(Activity source, Activity target, Expression expression, int index) {
		this.source = source;
		this.target = target;
		this.expression = expression;
		this.name="generatedLink_" + index;
	}
	
	/**
	 * @return        The source activity of the link
	 */
	public Activity getSource() {
		return this.source;
	}
	
	/**
	 * @return        The target activity of the link
	 */
	public Activity getTarget() {
		return this.target;
	}
	
	/**
	 * @return        The name of the link
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return The expression that specifies the transition condition
	 *         of the link
	 */
	public Expression getExpression() {
		return this.expression;
	}
	
	/**
	 * Sets the expression of the link that specifies the transition
	 * condition.
	 * 
	 * @param exp The Expression to set.
	 */
	public void setExpression(Expression exp) {
		this.expression = exp;
	}
}
