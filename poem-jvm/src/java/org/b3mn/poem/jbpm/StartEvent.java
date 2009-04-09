package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.UUID;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StartEvent extends Node {

	public StartEvent(JSONObject startEvent) {
		
		this.name = JsonToJpdl.readAttribute(startEvent, "name");
		this.bounds = JsonToJpdl.readBounds(startEvent);
		this.outgoings = JsonToJpdl.readOutgoings(startEvent);

	}
	
	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<start");
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		
		if(bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException("Invalid Start Event. Bounds is missing.");
		}

		if(outgoings.size() > 0) {
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
	
	public JSONObject toJson() throws JSONException {
		
		JSONObject stencil = new JSONObject();
		stencil.put("id", "StartEvent");
		
		JSONArray outgoing = new JSONArray();
		// TODO add outgoings
		
		JSONObject properties = new JSONObject();
		properties.put("bgcolor", "#ffffff");
		if( name != null)
			properties.put("name", name);
		
		JSONArray childShapes = new JSONArray();
		
		JSONObject startEvent = new JSONObject();

		startEvent.put("bounds", bounds.toJson());
		startEvent.put("resourceId", "oryx_" + UUID.randomUUID().toString());
		startEvent.put("stencil", stencil);
		startEvent.put("outgoing", outgoing);
		startEvent.put("properties", properties);
		startEvent.put("childShapes", childShapes);
		
		return startEvent;
	}
}

