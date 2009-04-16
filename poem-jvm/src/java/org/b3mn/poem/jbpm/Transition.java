package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;

public class Transition {
	private String uuid;
	private String name;
	private String target;
	private String condition;
	private Node targetNode;

	public Transition(JSONObject transition) {
		try {
			this.name = transition.getJSONObject("properties")
					.getString("name");
		} catch (JSONException e) {
		}

		try {
			this.condition = transition.getJSONObject("properties").getString(
					"conditionexpression");
		} catch (JSONException e) {
		}

		try {
			this.target = JsonToJpdl.getInstance().getTargetName(
					transition.getJSONObject("target").getString("resourceId"));
		} catch (JSONException e) {
		}
	}

	public Transition(org.w3c.dom.Node transition) {
		this.uuid = UUID.randomUUID().toString();
		NamedNodeMap attributes = transition.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.condition = JpdlToJson.getAttribute(attributes, "condition");
		String targetName = JpdlToJson.getAttribute(attributes, "to");
		this.targetNode = JpdlToJson.getProcess().getTarget(targetName);
	}

	public Node getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(Node targetNode) {
		this.targetNode = targetNode;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<transition");

		if (name != null) {
			jpdl.write(JsonToJpdl.transformAttribute("name", name));
		}

		if (target != null) {
			jpdl.write(JsonToJpdl.transformAttribute("to", target));
		} else {
			throw new InvalidModelException("Invalid edge. Target is missing.");
		}

		if (condition != null) {
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
