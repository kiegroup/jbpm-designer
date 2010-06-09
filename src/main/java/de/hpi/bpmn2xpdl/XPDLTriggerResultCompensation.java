package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("TriggerResultCompensation")
public class XPDLTriggerResultCompensation  extends XMLConvertible {

	@Attribute("AttributeId")
	protected String attributeId;
	@Attribute("CatchThrow")
	protected String catchThrow;

	public String getAttributeId() {
		return attributeId;
	}
	
	public String getCatchThrow() {
		return catchThrow;
	}
	
	public void readJSONactivity(JSONObject modelElement) {
		setAttributeId(modelElement.optString("activity"));
	}
	
	public void readJSONtriggerresultunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "triggerresultunknowns");
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	
	public void setCatchThrow(String catchThrow) {
		this.catchThrow = catchThrow;
	}
	
	public void writeJSONattributeId(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "activityref", getAttributeId());
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
