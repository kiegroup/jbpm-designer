package de.hpi.bpel4chor.model.supporting;

/**
 * A ToPart specifies the variable a value should be taken from
 * and the message part it should be copied to.
 * 
 * ToParts can be used for activities instead of input variables.
 */
public class ToPart {
	
	private String part = "##opaque";
	private String fromVariable = "##opaque";
	
	/**
	 * Constructor. Initializes the ToPart object.
	 */
	public ToPart() {}
	
	/**
	 * @return The part of a message a value should be copied to.
	 */
	public String getPart() {
		return this.part;
	}

	/**
	 * @return The name of a variable the value should be taken from.
	 */
	public String getFromVariable() {
		return this.fromVariable;
	}

	/**
	 * Sets the name of a variable the value should be taken from.
	 * 
	 * @param fromVariable The variable name to set.
	 */
	public void setFromVariable(String fromVariable) {
		this.fromVariable = fromVariable;
	}

	/**
	 * Sets the part of a message a value should be copied to.
	 * 
	 * @param part The name of the part.
	 */
	public void setPart(String part) {
		this.part = part;
	}

}
