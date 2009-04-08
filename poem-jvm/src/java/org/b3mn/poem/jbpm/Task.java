package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import org.json.JSONObject;

public class Task extends Node {
	
	private String assignee;
	
	public Task(JSONObject task) {
		
		this.name = JsonToJpdl.readAttribute(task, "name");
		this.assignee = JsonToJpdl.readAttribute(task, "assignee");
		this.bounds = JsonToJpdl.readBounds(task);
		this.outgoings = JsonToJpdl.readOutgoings(task);

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
		jpdl.write(JsonToJpdl.transformAttribute("assignee", assignee));
		
		if(bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException("Invalid Task. Bounds is missing.");
		}
			
		if(outgoings.size() > 0) {
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

}
