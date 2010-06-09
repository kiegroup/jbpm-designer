package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.RootElement;

@RootElement("TaskManual")
public class XPDLTaskManual extends XMLConvertible {

	public void readJSONimplementation(JSONObject modelElement) {
	}
	
	public void readJSONinstantiate(JSONObject modelElement) {
	}
	
	public void readJSONtaskref(JSONObject modelElement) {
	}
	
	public void writeJSONtasktype(JSONObject modelElement) throws JSONException {
		modelElement.put("tasktype", "Manual");
	}
	
	public void readJSONtasktypeunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "tasktypeunknowns");
	}
}
