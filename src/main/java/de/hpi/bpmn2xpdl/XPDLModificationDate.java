package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("ModificationDate")
public class XPDLModificationDate extends XMLConvertible {

	@Text
	protected String content;

	public String getContent() {
		return content;
	}

	public void readJSONmodificationdate(JSONObject modelElement) {
		setContent(modelElement.optString("modificationdate"));
	}
	
	public void readJSONmodificationdateunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "modificationdateunknowns");
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void writeJSONmodificationdate(JSONObject modelElement) throws JSONException {
		modelElement.put("modificationdate", getContent());
	}
	
	public void writeJSONmodificationdateunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "modificationdateunknowns");
	}
}