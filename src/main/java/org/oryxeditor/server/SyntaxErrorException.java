package org.oryxeditor.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Gero.Decker
 */
public class SyntaxErrorException extends Exception {
	
	private static final long serialVersionUID = 4122105347895713305L;
	private Map<String,String> errors = new HashMap<String,String>();
	
	/**
	 * 
	 * @param errors key = resourceID, value = error text
	 */
	public SyntaxErrorException(Map<String,String> errors) {
		this.errors = errors;
	}

	public Map<String,String> getErrors() {
		return errors;
	}
	
	@Override
	public String getMessage() {
		String message = "";
		boolean isFirstEntry = true;
		for (Entry<String,String> error: errors.entrySet()) {
			if (isFirstEntry) isFirstEntry = false;
			else message = message+", ";
			
			message = message+error.getValue()+" ("+error.getKey()+")";
		}
		return message;
	}

}


