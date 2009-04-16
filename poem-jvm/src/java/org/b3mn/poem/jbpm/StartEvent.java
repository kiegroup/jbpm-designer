package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;

public class StartEvent extends Node {

	public StartEvent(JSONObject startEvent) {

		this.name = JsonToJpdl.readAttribute(startEvent, "name");
		this.bounds = JsonToJpdl.readBounds(startEvent);
		this.outgoings = JsonToJpdl.readOutgoings(startEvent);

	}
	
	public StartEvent(org.w3c.dom.Node startEvent) {
		this.uuid = UUID.randomUUID().toString();
		NamedNodeMap attributes = startEvent.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.bounds = JpdlToJson.getBounds(attributes.getNamedItem("g"));
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<start");
		jpdl.write(JsonToJpdl.transformAttribute("name", name));

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException(
					"Invalid Start Event. Bounds is missing.");
		}

		if (outgoings.size() > 0) {
			jpdl.write(" >\n");
			for (Transition t : outgoings) {
				jpdl.write(t.toJpdl());
			}
			jpdl.write("</start>\n");
		} else {
			jpdl.write(" />\n");
		}

		return jpdl.toString();
	}

	@Override
	public JSONObject toJson() throws JSONException {

		JSONObject stencil = new JSONObject();
		stencil.put("id", "StartEvent");

		JSONArray outgoing = JpdlToJson.setTransitions(outgoings);

		JSONObject properties = new JSONObject();
		properties.put("bgcolor", "#ffffff");
		if (name != null)
			properties.put("name", name);

		JSONArray childShapes = new JSONArray();

		return JpdlToJson.createJsonObject(uuid, stencil, outgoing, properties,
				childShapes, bounds.toJson());
	}
}
