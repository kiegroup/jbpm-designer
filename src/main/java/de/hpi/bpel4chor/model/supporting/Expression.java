package de.hpi.bpel4chor.model.supporting;

/**
 * An expression object represents expressions (e.g. boolean, integer expression)
 * that can be defined in a special language. 
 */
public class Expression {
	
	private String expression = null;
	private String expressionLanguage = null;

	/**
	 * Constructor. Initializes the expression object.
	 */
	public Expression() {}
	
	/**
	 * Constructor. Initializes the expression object.
	 * 
	 * @param expression The expression represented by this object.
	 * @param language   The language the expression is defined in.
	 */
	public Expression(String expression, String language) {
		this.expression = expression;
		this.expressionLanguage = language;
	}

	/**
	 * @return The expression represented by this object.
	 */
	public String getExpression() {
		return this.expression;
	}

	/**
	 * @return The language the expression is defined in. The result is null
	 * if the language was not specified.
	 */
	public String getExpressionLanguage() {
		return this.expressionLanguage;
	}

	/**
	 * Sets the expression represented by this object.
	 * 
	 * @param expression The expression to set.
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * Sets the language the expression is defined in.
	 * 
	 * @param expressionLanguage The language to set.
	 */
	public void setExpressionLanguage(String expressionLanguage) {
		this.expressionLanguage = expressionLanguage;
	}
}
