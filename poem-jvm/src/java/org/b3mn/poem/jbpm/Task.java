package org.b3mn.poem.jbpm;

import java.io.StringWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Task extends Node {

	private String assignee;
	private String candidateGroups;
	private String candidateUsers;
	private String swimlane;

	public Task(JSONObject task) {

		this.name = JsonToJpdl.readAttribute(task, "name");
		this.assignee = JsonToJpdl.readAttribute(task, "assignee");
		if (assignee == null) {
			this.candidateGroups = JsonToJpdl.readAttribute(task,
					"candidate-groups");
			this.candidateUsers = JsonToJpdl.readAttribute(task,
					"candidate-users");
			this.swimlane = JsonToJpdl.readAttribute(task, "swimlane");
		}
		this.bounds = JsonToJpdl.readBounds(task);
		this.outgoings = JsonToJpdl.readOutgoings(task);

	}

	public String getSwimlane() {
		return swimlane;
	}

	public void setSwimlane(String swimlane) {
		this.swimlane = swimlane;
	}

	public String getCandidateGroups() {
		return candidateGroups;
	}

	public void setCandidateGroups(String candidateGroups) {
		this.candidateGroups = candidateGroups;
	}

	public String getCandidateUsers() {
		return candidateUsers;
	}

	public void setCandidateUsers(String candidateUsers) {
		this.candidateUsers = candidateUsers;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<task");

		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		if (assignee != null)
			jpdl.write(JsonToJpdl.transformAttribute("assignee", assignee));
		else if (candidateGroups != null && candidateUsers != null) {
			jpdl.write(JsonToJpdl.transformAttribute("candidate-groups",
					candidateGroups));
			jpdl.write(JsonToJpdl.transformAttribute("candidate-users",
					candidateUsers));
		} else if (swimlane != null) {
			jpdl.write(JsonToJpdl.transformAttribute("swimlane", swimlane));
		}

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException("Invalid Task. Bounds is missing.");
		}

		if (outgoings.size() > 0) {
			jpdl.write(" >\n");
			for (Transition t : outgoings) {
				jpdl.write(t.toJpdl());
			}
			jpdl.write("</task>\n");
		} else {
			jpdl.write(" />\n");
		}

		return jpdl.toString();
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", "Task");

		JSONArray outgoing = new JSONArray();
		// TODO add outgoings

		JSONObject properties = new JSONObject();
		properties.put("bgcolor", "#ffffcc");
		if (name != null)
			properties.put("name", name);
		if (candidateGroups != null)
			properties.put("candidate-groups", candidateGroups);
		if (candidateUsers != null)
			properties.put("candidate-users", candidateUsers);
		if (swimlane != null)
			properties.put("swimlane", swimlane);

		JSONArray childShapes = new JSONArray();

		return JpdlToJson.createJsonObject(uuid, stencil, outgoing, properties,
				childShapes, bounds.toJson());
	}

}
