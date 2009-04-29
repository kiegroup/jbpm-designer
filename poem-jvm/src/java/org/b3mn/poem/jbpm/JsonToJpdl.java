package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonToJpdl {

	private static JsonToJpdl instance = null;
	private HashMap<String, JSONObject> children;
	private JSONObject processData;

	public static JsonToJpdl getInstance() {
		return instance;
	}

	public static JsonToJpdl createInstance(JSONObject process) {
		instance = new JsonToJpdl(process);
		return instance;
	}

	private JsonToJpdl(JSONObject process) {
		this.processData = process;
		this.children = new HashMap<String, JSONObject>();
		try {
			JSONArray processElements = process.getJSONArray("childShapes");
			// Collect all children for direct access
			for (int i = 0; i < processElements.length(); i++) {
				JSONObject currentElement = processElements.getJSONObject(i);
				this.children.put(currentElement.getString("resourceId"),
						currentElement);
			}
		} catch (JSONException e) {
		}
	}

	public String transform() throws InvalidModelException {
		// trigger for transformation
		
		// Check if model is of type BPMN 1.2 + jBPM
		try {
			JSONArray extensions = this.processData.getJSONArray("ssextensions");
			for (int i = 0; i < extensions.length(); i++)
				if (extensions.getString(i).equals(
						"http://oryx-editor.org/stencilsets/extensions/jbpm#")) {
					Process process = new Process(this.processData);
					return process.toJpdl();
				}
			throw new InvalidModelException(
					"Invalid model type. BPMN 1.2 with jBPM extension is required.");
		} catch (JSONException e) {
			throw new InvalidModelException(
					"Invalid model type. BPMN 1.2 with jBPM extension is required.");
		}
	}

	public static String transformAttribute(String name, String value) {
		if (value == null)
			return "";
		if (value.equals(""))
			return "";

		StringWriter jpdl = new StringWriter();

		jpdl.write(" ");
		jpdl.write(name);
		jpdl.write("=\"");
		jpdl.write(value);
		jpdl.write("\"");

		return jpdl.toString();
	}

	public static String transformRequieredAttribute(String name, String value)
			throws InvalidModelException {
		if (value == null)
			throw new InvalidModelException("Attribute " + name
					+ " is missing.");

		StringWriter jpdl = new StringWriter();

		jpdl.write(" ");
		jpdl.write(name);
		jpdl.write("=\"");
		jpdl.write(value);
		jpdl.write("\"");

		return jpdl.toString();
	}

	public static String getAttribute(JSONObject node, String name) {
		try {
			return node.getJSONObject("properties").getString(name);
		} catch (JSONException e) {
			return null;
		}
	}

	public static Bounds getBounds(JSONObject node) {
		try {
			return new Bounds(node.getJSONObject("bounds"));
		} catch (JSONException e) {
			return null;
		}
	}

	public static List<Transition> getOutgoings(JSONObject node) {
		List<Transition> outgoings = new ArrayList<Transition>();
		try {
			JSONArray outs = node.getJSONArray("outgoing");
			for (int i = 0; i < outs.length(); i++) {
				String id = outs.getJSONObject(i).getString("resourceId");
				JSONObject out = JsonToJpdl.getInstance().getChild(id);
				outgoings.add(new Transition(out));
			}
		} catch (JSONException e) {
		}
		return outgoings;
	}

	public String getTargetName(String targetId) {
		JSONObject target = children.get(targetId);
		try {
			return target.getJSONObject("properties").getString("name");
		} catch (JSONException e) {
			return null;
		}
	}

	public JSONObject getChild(String childId) {
		return children.get(childId);
	}

}
