package de.hpi.bpel4chor.model.artifacts;

import de.hpi.bpel4chor.model.supporting.FromSpec;
import de.hpi.bpel4chor.util.Output;

/**
 * <p>A variable data object represents data that is processed by the 
 * activities. It can be a standard, counter, fault or message variable 
 * data object.</p>
 * 
 * <p>The different variable types have different restrictions concerning
 * their definition (e.g. a counter variable does not define a variable type,
 * a variableTypeValue and a fromSpec).</p>
 */
public class VariableDataObject extends DataObject {
	
	public static final String TYPE_STANDARD = "Standard";
	public static final String TYPE_COUNTER = "Counter";
	public static final String TYPE_FAULT = "Fault";
	public static final String TYPE_MESSAGE = "Message";
	
	public static final String VARIABLE_TYPE_MESSAGE = "MessageType";
	public static final String VARIABLE_TYPE_XML_TYPE = "XMLType";
	public static final String VARIABLE_TYPE_XML_ELEMENT = "XMLElement";
	
	private String type = "Standard";
	private String variableType = null;
	private String variableTypeValue = null;
	private FromSpec fromSpec = null;

	/**
	 * Constructor. Initializes the data object and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public VariableDataObject(Output output) {
		super(output);
	}

	/**
	 * @return The type of the variable data object ({@link #TYPE_COUNTER}, 
	 * {@link #TYPE_FAULT}, {@link #TYPE_MESSAGE} or {@link #TYPE_STANDARD}).
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @return The type of the variable definition ({@link #VARIABLE_TYPE_MESSAGE}, 
	 * {@link #VARIABLE_TYPE_XML_ELEMENT} or {@link #VARIABLE_TYPE_XML_TYPE}).
	 */
	public String getVariableType() {
		return this.variableType;
	}
	
	/**
	 * @return The value of the variable definition.
	 */
	public String getVariableTypeValue() {
		return this.variableTypeValue;
	}

	/**
	 * @return The FromSpec defined that initializes the variable with a value.
	 * The result is null if no FromSpec was defined.
	 */
	public FromSpec getFromSpec() {
		return this.fromSpec;
	}

	/**
	 * Sets the FromSpec that initializes the variable with a value.
	 * 
	 * @param fromSpec The new FromSpec of the variable. 
	 */
	public void setFromSpec(FromSpec fromSpec) {
		this.fromSpec = fromSpec;
	}

	/**
	 * Sets the type of the variable.
	 * 
	 * @param type The type of the variable ({@link #TYPE_COUNTER}, 
	 * {@link #TYPE_FAULT}, {@link #TYPE_MESSAGE} or {@link #TYPE_STANDARD}).
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Sets the type of the variable definition.
	 *  
	 * @param variableType The type of the variable definition 
	 * ({@link #VARIABLE_TYPE_MESSAGE}, {@link #VARIABLE_TYPE_XML_ELEMENT} or 
	 * {@link #VARIABLE_TYPE_XML_TYPE}).
	 */
	public void setVariableType(String variableType) {
		this.variableType = variableType;
	}
	
	/**
	 * Sets the value for the variable type definition. This must be a String
	 * with a prefix defined for an imported file followed by the name of a type 
	 * defined in this file: "prefix:typeName".
	 * 
	 * @param variableTypeValue The value of the variable type definition.
	 */
	public void setVariableTypeValue(String variableTypeValue) {
		this.variableTypeValue = variableTypeValue;
	}
}
