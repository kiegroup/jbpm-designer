package de.hpi.bpel4chor.parser;

import java.io.PrintWriter;
import java.io.StringWriter;

import de.hpi.bpel4chor.util.Output;

/**
 * Simple class to store errors that have occured 
 * during the transformation. The errors are combined in a
 * string and separated with new lines.
 */
public class ParserErrors implements Output  {
	
	private String errors = null;
	private static final String newLine = System.getProperty("line.separator");
	private static final String PREFIX = "Parser Error: ";
	
	/**
	 * Constructor. Initializes the output with an empty string.
	 */
	public ParserErrors() {
		this.errors = "";
	}
	
	/**
	 * Appends a string to the output.
	 * 
	 * @param newOutput the string to add
	 */
	public void addError(String newOutput) {
		this.errors += PREFIX + newOutput + newLine;
	}
	
	/**
	 * Adds the stack trace of the exception to the output.
	 * 
	 * @param e The exception
	 */
	public void addError(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		this.errors += PREFIX + sw.toString() + newLine;
	}
	
	/**
	 * Adds the stack trace of the error to the output.
	 * 
	 * @param e The error to add.
	 */
	public void addError(Error e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		this.errors += PREFIX + sw.toString() + newLine;
	}
	
	/**
	 * Clears the output. After the call of this method, 
	 * the Output holds an empty string.
	 */
	public void clear() {
		this.errors = "";
	}
	
	/**
	 * @return The current Output as String.
	 */
	public String getErrors() {
		return this.errors;
	}
	
	/**
	 * @return Returns true, if no error was reported, 
	 * false otherwise
	 */
	public boolean isEmpty() {
		return (this.errors.equals(""));
	}
}
