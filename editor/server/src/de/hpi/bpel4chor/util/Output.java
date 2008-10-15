package de.hpi.bpel4chor.util;

/**
 * Simple singleton class to store errors that occured during the parsing of
 * transformation. The errors are combined in a string and separated with new
 * lines.
 */
public interface Output {
	
	/**
	 * Appends a string to the output.
	 * 
	 * @param newOutput the string to add
	 */
	public abstract void addError(String newOutput);
	
	/**
	 * Adds the stack trace of the exception to the output.
	 * 
	 * @param e The exception
	 */
	public abstract void addError(Exception e);
	
	/**
	 * Adds the stack trace of the error to the output.
	 * 
	 * @param e The error
	 */
	public abstract void addError(Error e);
	
	/**
	 * Clears the output. After the call of this method, 
	 * the Output holds an empty string.
	 */
	public abstract void clear();	
	/**
	 * @return     The current Output as String.
	 */
	public abstract String getErrors();
	
	/**
	 * @return True if there was no error added to the output before,
	 * false otherwise. 
	 */
	public abstract boolean isEmpty();
}
