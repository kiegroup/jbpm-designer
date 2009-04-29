package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
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
	private List<Docker> dockers;

	public Transition(JSONObject transition) {
		this.dockers = new ArrayList<Docker>();
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
		try {
			JSONArray dockerArray = transition.getJSONArray("dockers");
			// Create path dockers. Start and end will be ignored.
			if (dockerArray.length() > 2)
				for (int i = 1; i < dockerArray.length() - 1; i++) {
					try {
						JSONObject docker = dockerArray.getJSONObject(i);
						int x = Math.round(Float.parseFloat(docker
								.getString("x")));
						int y = Math.round(Float.parseFloat(docker
								.getString("y")));
						dockers.add(new Docker(x, y));
					} catch (JSONException e) {
					}
				}
		} catch (JSONException f) {
		}
	}

	public Transition(org.w3c.dom.Node transition) {
		this.uuid = "oryx_" + UUID.randomUUID().toString();
		NamedNodeMap attributes = transition.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.condition = JpdlToJson.getAttribute(attributes, "condition");
		this.target = JpdlToJson.getAttribute(attributes, "to");
		this.targetNode = JpdlToJson.getProcess().getTarget(target);
		this.dockers = new ArrayList<Docker>();
		String g = JpdlToJson.getAttribute(attributes, "g");
		if (g != null) {
			// Create path dockers. Start and end are missing.
			String[] pathDockers = g.split(":")[0].split(";");
			for (int i = 0; i < pathDockers.length; i++) {
				if (pathDockers[i].length() > 1) {
					String[] dockerPosition = pathDockers[i].split(",");
					if (dockerPosition.length == 2) {
						Docker d = new Docker(Integer
								.parseInt(dockerPosition[0]), Integer
								.parseInt(dockerPosition[1]));
						dockers.add(d);
					}
				}
			}
		}

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
		
		if (dockers.size() > 0) {
			// g="120,42;120,45:0,0"
			String dockerString = "";
			for(Docker d : dockers) {
				dockerString += d.toJpdl();
				if(dockers.indexOf(d) == dockers.size() - 1)
					dockerString += ":";
				else
					dockerString += ";";
			}
			jpdl.write(JsonToJpdl.transformAttribute("g", dockerString));
		}

		if (condition != null && !condition.equals("")) {
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

		end = new Docker(targetNode.getBounds().getWidth() / 2, targetNode
				.getBounds().getHeight() / 2);

		Bounds bounds = new Bounds();

		JSONArray allDockers = new JSONArray();
		allDockers.put(start.toJson());
		for (Docker d : dockers) {
			allDockers.put(d.toJson());
		}
		allDockers.put(end.toJson());

		JSONObject node = new JSONObject();
		node.put("resourceId", uuid);
		node.put("stencil", stencil);
		node.put("outgoing", outgoing);
		node.put("properties", properties);
		node.put("childShapes", childShapes);
		node.put("dockers", allDockers);
		node.put("bounds", bounds.toJson());
		return node;
	}

}
