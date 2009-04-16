package org.b3mn.poem.jbpm;

import java.io.StringWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Xor extends Node {

	private String expression;
	private String handler;

	public Xor(JSONObject xor) {

		this.name = JsonToJpdl.readAttribute(xor, "name");
		this.expression = JsonToJpdl.readAttribute(xor, "expr");
		this.handler = JsonToJpdl.readAttribute(xor, "handler");
		this.bounds = JsonToJpdl.readBounds(xor);
		this.outgoings = JsonToJpdl.readOutgoings(xor);

	}
	
	public Xor(org.w3c.dom.Node xor) {
		
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<exclusive");

		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(JsonToJpdl.transformAttribute("expr", expression));

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException(
					"Invalid Exclusive gateway. Bounds is missing.");
		}

		jpdl.write(" >\n");

		if (handler != null)
			jpdl.write("<handler class=\"" + handler + "\" />\n");

		for (Transition t : outgoings) {
			jpdl.write(t.toJpdl());
		}

		jpdl.write("</exclusive>\n");

		return jpdl.toString();
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", "Exclusive_Databased_Gateway");

		JSONArray outgoing = new JSONArray();
		// TODO add outgoings

		JSONObject properties = new JSONObject();
		properties.put("bgcolor", "#ffffff");
		if (name != null)
			properties.put("name", name);
		if (expression != null)
			properties.put("expr", expression);
		if (handler != null)
			properties.put("handler", handler);

		JSONArray childShapes = new JSONArray();

		return JpdlToJson.createJsonObject(uuid, stencil, outgoing, properties,
				childShapes, bounds.toJson());
	}

}
