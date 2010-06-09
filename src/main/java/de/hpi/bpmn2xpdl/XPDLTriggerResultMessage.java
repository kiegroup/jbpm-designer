package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("TriggerResultMessage")
public class XPDLTriggerResultMessage  extends XMLConvertible {

	@Attribute("CatchThrow")
	protected String catchThrow;
	@Element("Message")
	protected XPDLMessage message;
	
	public String getCatchThrow() {
		return catchThrow;
	}
	
	public XPDLMessage getMessage() {
		return message;
	}
	
	public void readJSONmessage(JSONObject modelElement) throws JSONException {
		passInformationToMessage(modelElement, "message");		
	}
	
	public void readJSONmessageunknowns(JSONObject modelElement) throws JSONException {
		passInformationToMessage(modelElement, "messageunknowns");
	}
	
	public void readJSONtriggerresultunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "triggerresultunknowns");
	}
	
	public void setCatchThrow(String catchThrow) {
		this.catchThrow = catchThrow;
	}
	
	public void setMessage(XPDLMessage message) {
		this.message = message;
	}
	
	public void writeJSONmessage(JSONObject modelElement) throws JSONException {
		XPDLMessage messageObject = getMessage();
		if (messageObject != null) {
			initializeProperties(modelElement);
			messageObject.write(getProperties(modelElement));
		}
	}
	
	public void writeJSONtriggerresultunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "triggerresultunknowns");
	}
	
	protected JSONObject getProperties(JSONObject modelElement) {
		return modelElement.optJSONObject("properties");
	}
	
	protected void initializeMessage(JSONObject modelElement) {
		if (getMessage() == null) {
			setMessage(new XPDLMessage());
		}
	}
	
	protected void initializeProperties(JSONObject modelElement) throws JSONException {
		JSONObject properties = modelElement.optJSONObject("properties");
		if (properties == null) {
			JSONObject newProperties = new JSONObject();
			modelElement.put("properties", newProperties);
			properties = newProperties;
		}
	}
	
	protected void passInformationToMessage(JSONObject modelElement, String key) throws JSONException {
		initializeMessage(modelElement);
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		
		getMessage().parse(passObject);
	}
	
	protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
		initializeProperties(modelElement);
		
		getProperties(modelElement).put(key, value);
	}
}
