package de.hpi.bpel4chor.model.activities;

import java.util.ArrayList;
import java.util.List;
import de.hpi.bpel4chor.model.supporting.Copy;
import de.hpi.bpel4chor.util.Output;

/**
 * An assign task can assign the value of one variable to another variable.
 * This assignment is specified in a Copy object.
 */
public class AssignTask extends Task {
		
	private String validate = null;
	private List<Copy> copyObjects = new ArrayList<Copy>();
	
	/**
	 * Constructor. Initializes the assign task.
	 * 
	 * @param output The output to print errors to.
	 */
	public AssignTask(Output output) {
		super(output);
	}

	/**
	 * @return The copy objects that define the assignments.
	 */
	public List<Copy> getCopyElements() {
		return this.copyObjects;
	}

	/**
	 * @return "yes", if the variable values should be validated against 
	 * their definition, "no" otherwise. The result is null, if the validate
	 * value is not specified.
	 */
	public String getValidate() {
		return this.validate;
	}

	/**
	 * Sets the validate value of the assign task.
	 * 
	 * @param validate "yes" if the variables should be validated, "no" otherwise.
	 */
	public void setValidate(String validate) {
		this.validate = validate;
	}

	/**
	 * Adds a copy object to the list of copy object.
	 * 
	 * @param copy The new copy object
	 */
	public void addCopyElement(Copy copy) {
		this.copyObjects.add(copy);
	}
}
