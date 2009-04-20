package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.UUID;

import org.apache.xpath.XPathAPI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;

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
		this.uuid = "oryx_" + UUID.randomUUID().toString();
		NamedNodeMap attributes = xor.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.expression = JpdlToJson.getAttribute(attributes, "expression");
		try {
			org.w3c.dom.Node handlerNode = XPathAPI.selectSingleNode(xor,
					"/handler");
			this.handler = handlerNode.getAttributes().getNamedItem("class")
					.getNodeValue();
		} catch (Exception e) {
		}
		this.bounds = JpdlToJson.getBounds(attributes.getNamedItem("g"));
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

		JSONArray outgoing = JpdlToJson.setTransitions(outgoings);

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
