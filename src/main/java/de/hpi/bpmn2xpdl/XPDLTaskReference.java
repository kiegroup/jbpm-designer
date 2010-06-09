package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("TaskReference")
public class XPDLTaskReference extends XMLConvertible {

	@Attribute("TaskRef")
	protected String taskref;

	public String getTaskref() {
		return taskref;
	}
	
	public void readJSONimplementation(JSONObject modelElement) {
	}
	
	public void readJSONinstantiate(JSONObject modelElement) {
	}
	
	public void readJSONtaskref(JSONObject modelElement) {
		setTaskref(modelElement.optString("taskref"));
	}
	
	public void readJSONtasktypeunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "tasktypeunknowns");
	}

	public void setTaskref(String taskref) {
		this.taskref = taskref;
	}
	
	public void writeJSONtaskref(JSONObject modelElement) throws JSONException {
		modelElement.put("taskref", getTaskref());
	}
	
	public void writeJSONtasktype(JSONObject modelElement) throws JSONException {
		modelElement.put("tasktype", "Reference");
	}
	
	public void writeJSONtasktypeunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "tasktypeunknowns");
	}
}
