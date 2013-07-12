package org.jbpm.designer.bpmn2.validation;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public interface SyntaxChecker {
	public void checkSyntax();
	public Map<String,List<BPMN2SyntaxChecker.ValidationSyntaxError>> getErrors();
	public JSONObject getErrorsAsJson();
	public boolean errorsFound();
	public void clearErrors();
}
