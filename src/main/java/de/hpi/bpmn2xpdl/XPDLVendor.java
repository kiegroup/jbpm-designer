package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("Vendor")
public class XPDLVendor extends XMLConvertible {

	@Text
	protected String content = "Hasso Plattner Institute";

	public String getContent() {
		return content;
	}
	
	public void readJSONvendorunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "vendorunknowns");
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void writeJSONvendorunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "vendorunknowns");
	}
}
