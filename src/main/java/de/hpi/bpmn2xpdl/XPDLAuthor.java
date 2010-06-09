package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("Author")
public class XPDLAuthor extends XMLConvertible {

	@Text
	protected String content;

	public String getContent() {
		return content;
	}

	public void readJSONauthor(JSONObject modelElement) {
		setContent(modelElement.optString("author"));
	}
	
	public void readJSONauthorunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "authorunknowns");
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void writeJSONauthor(JSONObject modelElement) throws JSONException {
		modelElement.put("author", getContent());
	}
	
	public void writeJSONauthorunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "authorunknowns");
	}
}
