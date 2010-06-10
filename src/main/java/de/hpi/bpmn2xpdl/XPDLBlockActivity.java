package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("BlockActivity")
public class XPDLBlockActivity extends XMLConvertible {
	
	@Attribute("ActivitySetId")
	protected String activitySetId;
	@Attribute("View")
	protected String view;
	
	protected XPDLActivitySet activitySet;
	
	public String getActivitySetId() {
		return activitySetId;
	}
	
	public XPDLActivitySet getActivitySet() {
		return activitySet;
	}
	
	public String getView() {
		return view;
	}
	
	public void readJSONblockunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "blockunknowns");
	}
	
	public void readJSONid(JSONObject modelElement) {
		setActivitySetId(modelElement.optString("id") + "-activitySet");
	}
	
	public void readJSONstencil(JSONObject modelElement) {
		if (modelElement.optString("stencil").contains("Collapsed")) {
			setView("COLLAPSED");
		} else {
			setView("EXTENDED");
		}
	}
	
	public void readJSONsubprocesstype(JSONObject modelElement) {
	}
	
	public void setActivitySetId(String activitySetId) {
		this.activitySetId = activitySetId;
	}
	
	public void setActivitySet(XPDLActivitySet set) {
		activitySet = set;
	}
	
	public void setView(String view) {
		this.view = view;
	}
	
	public void writeJSONactivitySet(JSONObject modelElement) {
		if (getActivitySet() != null) {
			getActivitySet().write(modelElement);
		}
	}
	
	public void writeJSONblockunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "blockunknowns");
	}
	
	public void writeJSONstencil(JSONObject modelElement) throws JSONException {
		String stencil = "CollapsedSubprocess";
		if ("EXTENDED".equalsIgnoreCase(getView())) {
			stencil = "Subprocess";
		}
		JSONObject stencilObject = new JSONObject();
		stencilObject.put("id", stencil);
		putProperty(modelElement, "subprocesstype", "Embedded");
		modelElement.put("stencil", stencilObject);
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
