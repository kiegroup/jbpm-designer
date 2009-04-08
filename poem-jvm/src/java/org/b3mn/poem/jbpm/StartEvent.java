package org.b3mn.poem.jbpm;

import java.io.StringWriter;

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
}

