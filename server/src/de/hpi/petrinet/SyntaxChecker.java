package de.hpi.petrinet;

import java.util.Map;

public interface SyntaxChecker {
	
	/**
	 * 
	 * @return true if there are no syntax errors
	 */
	boolean checkSyntax();
	
	/**
	 * returns the errors if any were found
	 * @return key = resource ID, value = error text
	 */
	Map<String,String> getErrors();

}
