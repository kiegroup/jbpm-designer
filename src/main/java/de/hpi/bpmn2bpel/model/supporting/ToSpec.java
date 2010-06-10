package de.hpi.bpmn2bpel.model.supporting;

/**
 * A ToSpec object element is used to initialize a variable with a value
 * or to determine the target of an assign task a value should be copied to. 
 * It specifies where the value should be copied to.
 * This can be a variable, a variable property or an expression.
 * Moreover, the FromSpec can be left empty.
 */
public class ToSpec {
	
	public static final String TYPE_VARIABLE = "Variable";
	public static final String TYPE_VAR_PROPERTY = "VarProperty";
	public static final String TYPE_EXPRESSION = "Expression";
	public static final String TYPE_EMPTY = "Empty"; 
	
	public enum toTypes {
		VARIABLE,
		VARPROPERTY,
		EXPRESSION,
		EMPTY
	}
	
	private toTypes type = toTypes.EMPTY;
	private String variableName = null;
	private String part = null;
	private String property = null;
	private String queryLanguage = null;
	private String query = null;
	private String expressionLanguage = null;
	private String expression = null;
	private String header = null;
	
	/**
	 * Constructor. Initializes the ToSpec object.
	 */
	public ToSpec() {}

	/**
	 * @return The expression that determines the target.
	 */
	public String getExpression() {
		return this.expression;
	}

	/**
	 * @return The expression language the expression is defined in. 
	 */
	public String getExpressionLanguage() {
		return this.expressionLanguage;
	}

	/**
	 * @return The message part to copy the value to.
	 */
	public String getPart() {
		return this.part;
	}

	/**
	 * @return The variable property to copy the value to.
	 */
	public String getProperty() {
		return this.property;
	}

	/**
	 * @return The query to determine the target.
	 */
	public String getQuery() {
		return this.query;
	}

	/**
	 * @return The language the query is defined in.
	 */
	public String getQueryLanguage() {
		return this.queryLanguage;
	}

	/**
	 * @return The type of the ToSpec ({@link #TYPE_EMPTY}, 
	 * {@link #TYPE_EXPRESSION}, {@link #TYPE_VAR_PROPERTY} or 
	 * {@link #TYPE_VARIABLE}).
	 */
	public toTypes getType() {
		return this.type;
	}

	/**
	 * @return The name of the variable to store the value in.
	 */
	public String getVariableName() {
		return this.variableName;
	}

	/**
	 * Sets the expression that determines the target.
	 * 
	 * @param expression The expression to set.
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * Sets the expression language the expression is defined in.
	 *  
	 * @param expressionLanguage The expression language to set.
	 */
	public void setExpressionLanguage(String expressionLanguage) {
		this.expressionLanguage = expressionLanguage;
	}

	/**
	 * Sets the message part to copy the value to.
	 * 
	 * @param part The message part to set.
	 */
	public void setPart(String part) {
		this.part = part;
	}

	/**
	 * Sets the variable property to copy the value to.
	 * 
	 * @param property The variable property to set.
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * Sets the query to determine the target.
	 * 
	 * @param query The query to set.
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Sets the language the query is defined in.
	 * 
	 * @param queryLanguage The query language to set.
	 */
	public void setQueryLanguage(String queryLanguage) {
		this.queryLanguage = queryLanguage;
	}

	/**
	 * Sets the type of the ToSpec.
	 * 
	 * @param type The type to set ({@link #TYPE_EMPTY}, 
	 * {@link #TYPE_EXPRESSION}, {@link #TYPE_VAR_PROPERTY}, 
	 * {@link #TYPE_VARIABLE}).
	 */
	public void setType(toTypes type) {
		this.type = type;
	}

	/**
	 * Sets the name of the variable the value will be copied to.
	 * 
	 * @param variableName The variable name to set.
	 */
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getHeader() {
		return header;
	}
}
