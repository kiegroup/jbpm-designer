package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;

public class EndEvent extends Node {

	protected String state;
	protected String ends;

	public EndEvent(JSONObject endEvent) {

		this.name = JsonToJpdl.getAttribute(endEvent, "name");
		this.ends = JsonToJpdl.getAttribute(endEvent, "ends");
		this.state = JsonToJpdl.getAttribute(endEvent, "state");
		this.bounds = JsonToJpdl.getBounds(endEvent);

	}
	
	public EndEvent(org.w3c.dom.Node endEvent) {
		this.uuid = "oryx_" + UUID.randomUUID().toString();
		NamedNodeMap attributes = endEvent.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.ends = JpdlToJson.getAttribute(attributes, "ends");
		this.state = JpdlToJson.getAttribute(attributes, "state");
		this.bounds = JpdlToJson.getBounds(attributes.getNamedItem("g"));
		this.bounds.setWidth(30);
		this.bounds.setHeight(30);
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
