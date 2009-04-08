package org.b3mn.poem.jbpm;

import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class Transition {
	private String name;
	private String target;
	private String condition;
	
	public Transition(JSONObject transition) {
		try {
			this.name = transition.getJSONObject("properties").getString("name");
		} catch (JSONException e) {}
		
		try {
			this.condition = transition.getJSONObject("properties").getString("conditionexpression");
		} catch (JSONException e) {}
	
		try {
			this.target = JsonToJpdl.getInstance().getTargetName(transition.getJSONObject("target").getString("resourceId"));
		} catch (JSONException e) {}
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<transition");
		
		if(name != null) {
			jpdl.write(JsonToJpdl.transformAttribute("name", name));
		}
		
		if(target != null) {
			jpdl.write(JsonToJpdl.transformAttribute("to", target));
		} else {
			throw new InvalidModelException("Invalid edge. Target is missing.");
		}
		
		if(condition != null) {
			jpdl.write(">\n");
			jpdl.write("<condition expr=\"");
			jpdl.write(condition);
			jpdl.write("\" />\n");
			jpdl.write("</transition>\n");
		} else {
			jpdl.write("/>\n");
		}
		
		return jpdl.toString();
	}
	
}
