package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("Version")
public class XPDLVersion extends XMLConvertible {

	@Text
	protected String content;

	public String getContent() {
		return content;
	}
	
	public void readJSONversion(JSONObject modelElement) {
		setContent(modelElement.optString("version"));
	}
	
	public void readJSONversionunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "versionunknowns");
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void writeJSONversion(JSONObject modelElement) throws JSONException {
		modelElement.put("version", getContent());
	}
	
	public void writeJSONversionunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "versionunknowns");
	}
}
