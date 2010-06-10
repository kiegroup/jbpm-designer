package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Assignment")
public class XPDLAssignment extends XMLConvertible {
	
	@Attribute("AssignTime")
	protected String assignTime;
	
	public String getAssignTime() {
		return assignTime;
	}
	
	public void readJSONassigntime(JSONObject modelElement) {
		setAssignTime(modelElement.optString("assigntime"));
	}
	
	public void readJSONassignmentunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "assignmentunknowns");
	}
	
	public void setAssignTime(String time) {
		assignTime = time;
	}
	
	public void writeJSONassigntime(JSONObject modelElement) throws JSONException {
		modelElement.put("assigntime", getAssignTime());
	}
	
	public void writeJSONassignmentunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "assignmentunknowns");
	}
}
