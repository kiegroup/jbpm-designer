package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("TriggerConditional")
public class XPDLTriggerConditional  extends XMLConvertible {

	@Text
	protected String condition;

	public String getCondition() {
		return condition;
	}
	
	public void readJSONcondition(JSONObject modelElement) {
		setCondition(modelElement.optString("condition"));
	}
	
	public void readJSONtriggerresultunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "triggerresultunknowns");
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public void writeJSONconditionref(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "conditionref", getCondition());
	}
	
	public void writeJSONtriggerresultunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "triggerresultunknowns");
	}
	
	protected JSONObject getProperties(JSONObject modelElement) {
		return modelElement.optJSONObject("properties");
	}
	
	protected void initializeProperties(JSONObject modelElement) throws JSONException {
		JSONObject properties = modelElement.optJSONObject("properties");
		if (properties == null) {
			JSONObject newProperties = new JSONObject();
			modelElement.put("properties", newProperties);
			properties = newProperties;
		}
	}
	
	protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
		initializeProperties(modelElement);
		
		getProperties(modelElement).put(key, value);
	}
}
