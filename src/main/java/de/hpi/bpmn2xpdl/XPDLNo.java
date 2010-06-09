package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;

import org.xmappr.RootElement;

@RootElement("No")
public class XPDLNo extends XMLConvertible {

	public void readJSONnounknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "nounknowns");
	}
	
	public void writeJSONnounknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "nounknowns");
	}
	
	public void writeJSONtasktype(JSONObject modelElement) throws JSONException {
		modelElement.put("tasktype", "None");
	}
}
