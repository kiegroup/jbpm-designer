package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;

public class Transition {
	private String uuid;
	private String name;
	private String target;
	private String condition;
	private Node targetNode;
	private Docker start;
	private Docker end;

	public Docker getStart() {
		return start;
	}

	public void setStart(Docker start) {
		this.start = start;
	}

	public Docker getEnd() {
		return end;
	}

	public void setEnd(Docker end) {
		this.end = end;
	}

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
		this.uuid = "oryx_" + UUID.randomUUID().toString();
		NamedNodeMap attributes = transition.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.condition = JpdlToJson.getAttribute(attributes, "condition");
		this.target = JpdlToJson.getAttribute(attributes, "to");
		this.targetNode = JpdlToJson.getProcess().getTarget(target);
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

	public JSONObject toJson() throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", "SequenceFlow");

		JSONObject targetAsJson = new JSONObject();
		targetAsJson.put("resourceId", targetNode.getUuid());

		JSONArray outgoing = new JSONArray();
		outgoing.put(targetAsJson);

		JSONObject properties = new JSONObject();
		if (name != null)
			properties.put("name", name);
		if (condition != null) {
			properties.put("conditionexpression", condition);
			properties.put("conditiontype", "Expression");
			properties.put("showdiamondmarker", "true");
		} else {
			properties.put("conditiontype", "None");
			properties.put("showdiamondmarker", "false");
		}
		JSONArray childShapes = new JSONArray();

		if (EndEvent.class.isAssignableFrom(targetNode.getClass())) {
			end = new Docker(15, 15);
		} else {
			end = new Docker(50, 40);
		}

		Bounds bounds = new Bounds();
		
		JSONArray dockers = new JSONArray();
		dockers.put(start.toJson());
		dockers.put(end.toJson());
		
		JSONObject node = new JSONObject();
		node.put("resourceId", uuid);
		node.put("stencil", stencil);
		node.put("outgoing", outgoing);
		node.put("properties", properties);
		node.put("childShapes", childShapes);
		node.put("dockers", dockers);
		node.put("bounds", bounds.toJson());
		return node;
	}

}
