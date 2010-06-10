package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("XPDLVersion")
public class XPDLXPDLVersion extends XMLConvertible {

	@Text
	protected String content = "2.1";

	public String getContent() {
		return content;
	}

	public void readJSONxpdlversionunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "xpdlversionunknowns");
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void writeJSONxpdlversionunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "xpdlversionunknowns");
	}
}
