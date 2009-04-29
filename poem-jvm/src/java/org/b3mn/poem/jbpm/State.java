package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;

public class State extends Node {

	public State(JSONObject state) {

		this.name = JsonToJpdl.getAttribute(state, "name");
		this.bounds = JsonToJpdl.getBounds(state);
		this.outgoings = JsonToJpdl.getOutgoings(state);

	}
	
	public State(org.w3c.dom.Node state) {
		this.uuid = "oryx_" + UUID.randomUUID().toString();
		NamedNodeMap attributes = state.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.bounds = JpdlToJson.getBounds(attributes.getNamedItem("g"));
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<state");

		jpdl.write(JsonToJpdl.transformAttribute("name", name));

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException(
					"Invalid Wait activity. Bounds is missing.");
		}

		if (outgoings.size() > 0) {
			jpdl.write(" >\n");
			for (Transition t : outgoings) {
				jpdl.write(t.toJpdl());
			}
			jpdl.write("</state>\n");
		} else {
			jpdl.write(" />\n");
		}

		return jpdl.toString();
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", "wait");

		JSONArray outgoing = JpdlToJson.getTransitions(outgoings);

		JSONObject properties = new JSONObject();
		properties.put("bgcolor", "#ffffcc");
		if (name != null)
			properties.put("name", name);

		JSONArray childShapes = new JSONArray();

		return JpdlToJson.createJsonObject(uuid, stencil, outgoing, properties,
				childShapes, bounds.toJson());
	}

}
