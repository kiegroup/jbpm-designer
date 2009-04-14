package org.b3mn.poem.jbpm;

import java.io.StringWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EndEvent extends Node {

	protected String state;
	protected String ends;

	public EndEvent(JSONObject endEvent) {

		this.name = JsonToJpdl.readAttribute(endEvent, "name");
		this.ends = JsonToJpdl.readAttribute(endEvent, "ends");
		this.state = JsonToJpdl.readAttribute(endEvent, "state");
		this.bounds = JsonToJpdl.readBounds(endEvent);

	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getEnds() {
		return ends;
	}

	public void setEnds(String ends) {
		this.ends = ends;
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		String id = "end";
		return writeJpdlAttributes(id).toString();

	}

	@Override
	public JSONObject toJson() throws JSONException {
		String id = "EndEvent";

		return writeJsonAttributes(id);
	}

	protected JSONObject writeJsonAttributes(String id) throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", id);

		JSONArray outgoing = new JSONArray();

		JSONObject properties = new JSONObject();
		properties.put("bgcolor", "#ffffff");
		if (name != null)
			properties.put("name", name);
		if (state != null)
			properties.put("state", state);
		if (ends != null)
			properties.put("ends", ends);
		else
			properties.put("ends", "processinstance"); // default value

		JSONArray childShapes = new JSONArray();

		return JpdlToJson.createJsonObject(uuid, stencil, outgoing, properties,
				childShapes, bounds.toJson());
	}

	protected String writeJpdlAttributes(String id)
			throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<" + id);
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		if (!ends.equals("processinstance")) // processinstance is default value
			jpdl.write(JsonToJpdl.transformAttribute("ends", ends));
		jpdl.write(JsonToJpdl.transformAttribute("state", state));

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException(
					"Invalid End Event. Bounds is missing.");
		}

		jpdl.write(" />\n");

		return jpdl.toString();
	}
}
