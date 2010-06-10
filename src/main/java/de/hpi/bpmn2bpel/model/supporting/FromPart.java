package de.hpi.bpmn2bpel.model.supporting;

/**
 * A FromPart specifies the part of a message a value should be taken from
 * and the variable it should be stored in.
 * 
 * FromParts can be used for activities instead of output variables.
 */
public class FromPart {
	
	private String part = "##opaque";
	private String toVariable = "##opaque";
	
	/**
	 * Constructor. Initializes the FromPart object.
	 */
	public FromPart() {}

	/**
	 * @return The part of a message a value should be taken from.
	 */
	public String getPart() {
		return this.part;
	}

	/**
	 * @return The name of a variable the value should be stored in.
	 */
	public String getToVariable() {
		return this.toVariable;
	}

	/**
	 * Sets the part of a message a value should be taken from.
	 * 
	 * @param part The name of the part.
	 */
	public void setPart(String part) {
		this.part = part;
	}

	/**
	 * Sets the name of a variable the value should be stored in.
	 * 
	 * @param toVariable The variable name to set.
	 */
	public void setToVariable(String toVariable) {
		this.toVariable = toVariable;
	}
}
