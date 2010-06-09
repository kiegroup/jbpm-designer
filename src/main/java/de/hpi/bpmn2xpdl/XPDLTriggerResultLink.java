package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("TriggerResultLink")
public class XPDLTriggerResultLink extends XMLConvertible {

	@Attribute("CatchThrow")
	protected String catchThrow;
	@Text
	protected String link;
	
	public String getCatchThrow() {
		return catchThrow;
	}
	
	public String getLink() {
		return link;
	}
	
	public void readJSONlinkid(JSONObject modelElement) {
		setLink(modelElement.optString("linkid"));
	}
	
	public void readJSONtriggerresultunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "triggerresultunknowns");
	}
	
	public void setCatchThrow(String catchThrow) {
		this.catchThrow = catchThrow;
	}
	
	public void setLink(String linkValue) {
		link = linkValue;
	}
	
	public void writeJSONlinkid(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "linkid", getLink());
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
