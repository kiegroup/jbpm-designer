package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Esb extends Node {

	private String category;
	private String service;
	private List<Part> part;

	public Esb(JSONObject esb) {

		this.name = JsonToJpdl.readAttribute(esb, "name");
		this.category = JsonToJpdl.readAttribute(esb, "category");
		this.service = JsonToJpdl.readAttribute(esb, "service");
		this.bounds = JsonToJpdl.readBounds(esb);

		this.part = new ArrayList<Part>();

		try {
			JSONArray parameters = esb.getJSONObject("properties")
					.getJSONObject("part").getJSONArray("items");
			for (int i = 0; i < parameters.length(); i++) {
				JSONObject item = parameters.getJSONObject(i);
				part.add(new Part(item));
			}
		} catch (JSONException e) {}

		this.outgoings = JsonToJpdl.readOutgoings(esb);
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<Part> getPart() {
		return part;
	}

	public void setPart(List<Part> part) {
		this.part = part;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
	
	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<esb");

		jpdl.write(JsonToJpdl.transformAttribute("name", name));

		try {
			jpdl.write(JsonToJpdl.transformRequieredAttribute("category",
					category));
			jpdl.write(JsonToJpdl.transformRequieredAttribute("service",
					service));
		} catch (InvalidModelException e) {
			throw new InvalidModelException("Invalid Esb activity. "
					+ e.getMessage());
		}

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException(
					"Invalid ESB activity. Bounds is missing.");
		}

		jpdl.write(" >\n");
		
		for (Part p : part) {
			jpdl.write(p.toJpdl());
		}
		
		for (Transition t : outgoings) {
			jpdl.write(t.toJpdl());
		}
		
		jpdl.write("</esb>\n");
		
		return jpdl.toString();
	}

}
