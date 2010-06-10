package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Script")
public class XPDLScript extends XMLConvertible {

	@Attribute("Type")
	protected String scriptType;
	
	public String getScriptType() {
		return scriptType;
	}

	public void readJSONexpressionlanguage(JSONObject modelElement) {
		setScriptType(modelElement.optString("expressionlanguage"));
	}
	
	public void readJSONexpressionunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "expressionunknowns");
	}

	public void setScriptType(String typeValue) {
		scriptType = typeValue;
	}
	
	public void writeJSONexpressionlanguage(JSONObject modelElement) throws JSONException {
		modelElement.put("expressionlanguage", getScriptType());
	}
	
	public void writeJSONexpressionunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "expressionunknowns");
	}
}