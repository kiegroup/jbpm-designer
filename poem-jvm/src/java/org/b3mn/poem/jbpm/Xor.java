package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;

public class Xor extends Node {

	private String expression;
	private String handler;

	public Xor(JSONObject xor) {

		this.name = JsonToJpdl.getAttribute(xor, "name");
		this.expression = JsonToJpdl.getAttribute(xor, "expr");
		this.handler = JsonToJpdl.getAttribute(xor, "handler");
		this.bounds = JsonToJpdl.getBounds(xor);
		this.outgoings = JsonToJpdl.getOutgoings(xor);

	}

	public Xor(org.w3c.dom.Node xor) {
		this.uuid = "oryx_" + UUID.randomUUID().toString();
		NamedNodeMap attributes = xor.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.expression = JpdlToJson.getAttribute(attributes, "expression");
		if (xor.hasChildNodes())
			for (org.w3c.dom.Node a = xor.getFirstChild(); a != null; a = a.getNextSibling())
				if(a.getNodeName().equals("handler")) {
					this.handler = a.getAttributes().getNamedItem("class").getNodeValue();
					break;
				}
		this.bounds = JpdlToJson.getBounds(attributes.getNamedItem("g"));
		this.bounds.setWidth(40);
		this.bounds.setHeight(40);
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

		JSONArray outgoing = JpdlToJson.getTransitions(outgoings);

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
