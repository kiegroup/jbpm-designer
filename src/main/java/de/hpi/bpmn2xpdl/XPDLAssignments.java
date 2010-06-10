package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Assignments")
public class XPDLAssignments extends XMLConvertible {

	@Element("Assignment")
	protected ArrayList<XPDLAssignment> assignments;

	public void add(XPDLAssignment newAssignment) {
		initializeAssignments();
		
		getAssignments().add(newAssignment);
	}
	
	public ArrayList<XPDLAssignment> getAssignments() {
		return assignments;
	}
	
	public void readJSONassignmentsunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "assignmentsunknowns");
	}

	public void setAssignments(ArrayList<XPDLAssignment> assignments) {
		this.assignments = assignments;
	}
	
	public void writeJSONassignmentsunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "assignmentsunknowns");
	}
	
	public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
//		ArrayList<XPDLAssignment> assignmentsList = getAssignments();
//		if (assignmentsList != null) {
//			JSONObject assignmentsObject = new JSONObject();
//			
//			JSONArray items = new JSONArray();
//			for (int i = 0; i < assignmentsList.size(); i++) {
//				XPDLAssignment singleAssignment = assignmentsList.get(i);
//				JSONObject item = new JSONObject();
//				
//				singleAssignment.write(item);
//				items.put(item);
//				assignmentsObject.put("totalCount", i);
//			}
//			modelElement.put("items", items);
//		}
	}
	
	protected void initializeAssignments() {
		if (getAssignments() == null) {
			setAssignments(new ArrayList<XPDLAssignment>());
		}
	}
}
