package de.hpi.bpel4chor.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Node;

import de.hpi.bpel4chor.util.Output;

/**
 * Simple class to store errors that occurred during the transformation.  
 * The errors are combined in a string and separated with new lines.
 */
public class Output {

	private ArrayList<OutputElement> errors = new ArrayList<OutputElement>();

	/**
	 * Shortcut to create a new instance containing one error message
	 * 
	 * @param errorMsg the error message to add
	 */
	public Output(String errorMsg) {
		addGeneralError(errorMsg);
	}

	public Output() {
	}

	/**
	 * Appends a string to the output.
	 * 
	 * @param msg the message
	 * @param id the id of the element
	 */
	public void addError(String msg, String id) {
		this.errors.add(new OutputElement(msg, id));
	}
	
	/**
	 * Add a parse error resulting from direct XML representation
	 * @param msg
	 * @param node
	 */
	public void addParseError(String msg, Node node) {
		this.errors.add(new OutputElement(msg, node));
	}
	
	public void addGeneralError(String msg) {
		this.errors.add(new OutputElement(msg));
	}
	
	/**
	 * Adds the stack trace of the exception to the output.
	 * 
	 * @param e The exception
	 */
	public void addError(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		this.errors.add(new OutputElement(sw.toString()));
	}
	
	/**
	 * Adds the stack trace of the error to the output.
	 * 
	 * @param e The error
	 */
	public void addError(Error e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		this.errors.add(new OutputElement(sw.toString()));
	}
	
	/**
	 * Clears the stored data
	 */
	public void clear() {
		this.errors.clear();
	}
	
	/**
	 * @return     	Returns true, if no error was reported, 
	 * 				false otherwise
	 */
	public boolean isEmpty() {
		return this.errors.isEmpty();
	}
	
	public Iterator<OutputElement> iterator() {
		return errors.iterator();
	}

}
