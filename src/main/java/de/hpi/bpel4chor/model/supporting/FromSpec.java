package de.hpi.bpel4chor.model.supporting;

/**
 * A FromSpec object element is used to initialize a variable with a value
 * or to determine the value of an assign task that should be copied.
 * The value is taken from a variable, a variable property, an expression,
 * or a literal. Moreover, the FromSpec can be left empty or it can be omitted.
 */
public class FromSpec {
	
	public static final String TYPE_VARIABLE = "Variable";
	public static final String TYPE_VAR_PROPERTY = "VarProperty";
	public static final String TYPE_EXPRESSION = "Expression";
	public static final String TYPE_LITERAL = "Literal";
	public static final String TYPE_OPAQUE = "Opaque";
	public static final String TYPE_EMPTY = "Empty"; 
	
	private String type = TYPE_EMPTY;
	private String variableName = null;
	private String part = null;
	private String property = null;
	private String literal = null;
	private String queryLanguage = null;
	private String query = null;
	private String expressionLanguage = null;
	private String expression = null;	
	
	/**
	 * Constructor. Initializes the FromSpec object.
	 */
	public FromSpec() {}

	/**
	 * @return The expression that determines the value.
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
	 * @return The literal that represents the value.
	 */
	public String getLiteral() {
		return this.literal;
	}

	/**
	 * @return The message part that contains the value.
	 */
	public String getPart() {
		return this.part;
	}

	/**
	 * @return The variable property that contains the value.
	 */
	public String getProperty() {
		return this.property;
	}

	/**
	 * @return The query to determine the value.
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
	 * @return The type of the FromSpec ({@link #TYPE_EMPTY}, 
	 * {@link #TYPE_EXPRESSION}, {@link #TYPE_LITERAL}, {@link #TYPE_OPAQUE}, 
	 * {@link #TYPE_VAR_PROPERTY}, {@link #TYPE_VARIABLE}).
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @return The name of the variable to take the value from.
	 */
	public String getVariableName() {
		return this.variableName;
	}

	/**
	 * Sets the expression that determines the value.
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
	 * Sets the literal that represents the value.
	 * 
	 * @param literal The literal to set 
	 */
	public void setLiteral(String literal) {
		this.literal = literal;
	}

	/**
	 * Sets the message part that contains the value.
	 * 
	 * @param part The message part to set.
	 */
	public void setPart(String part) {
		this.part = part;
	}

	/**
	 * Sets the variable property that contains the value.
	 * 
	 * @param property The variable property to set.
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * Sets the query to determine the value.
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
	 * Sets the type of the FromSpec.
	 * 
	 * @param type The type to set ({@link #TYPE_EMPTY}, 
	 * {@link #TYPE_EXPRESSION}, {@link #TYPE_LITERAL}, {@link #TYPE_OPAQUE}, 
	 * {@link #TYPE_VAR_PROPERTY}, {@link #TYPE_VARIABLE}).
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Sets the name of the variable the value will be taken from.
	 * 
	 * @param variableName The variable name to set.
	 */
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
}
