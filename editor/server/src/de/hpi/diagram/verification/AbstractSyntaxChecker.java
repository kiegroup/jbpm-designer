package de.hpi.diagram.verification;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

abstract public class AbstractSyntaxChecker implements SyntaxChecker {
	protected Map<String,String> errors;
	
	public AbstractSyntaxChecker(){
		this.errors = new HashMap<String,String>();
	}

	abstract public boolean checkSyntax();

	public Map<String, String> getErrors() {
		return errors;
	}
	
	public void clearErrors(){
		errors.clear();
	}
	
	public boolean errorsFound(){
		return errors.size() > 0;
	}

	public JSONObject getErrorsAsJson() {
		JSONObject jsonObject = new JSONObject();
		
		for (Entry<String,String> error: this.getErrors().entrySet()) {
			try {
				jsonObject.put(error.getKey(), error.getValue());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return jsonObject;
	}
}
