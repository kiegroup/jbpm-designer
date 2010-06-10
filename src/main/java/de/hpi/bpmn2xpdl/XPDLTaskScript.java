package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.RootElement;

@RootElement("TaskScript")
public class XPDLTaskScript extends XMLConvertible {

	public void readJSONimplementation(JSONObject modelElement) {
	}
	
	public void readJSONinstantiate(JSONObject modelElement) {
	}
	
	public void readJSONtaskref(JSONObject modelElement) {
	}
	
	public void readJSONtasktypeunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "tasktypeunknowns");
	}
	
	public void writeJSONtasktype(JSONObject modelElement) throws JSONException {
		modelElement.put("tasktype", "Script");
	}
	
	public void writeJSONtasktypeunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "tasktypeunknowns");
	}
}
