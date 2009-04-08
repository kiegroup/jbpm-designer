package org.b3mn.poem.jbpm;

import java.io.StringWriter;

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
		StringWriter jpdl = new StringWriter();
		jpdl.write("<end");
		jpdl.write(writeAttributes());
		return jpdl.toString();

	}
	
	protected String writeAttributes() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		if(!ends.equals("processinstance")) // processinstance is default value
			jpdl.write(JsonToJpdl.transformAttribute("ends", ends));
		jpdl.write(JsonToJpdl.transformAttribute("state", state));
		
		if(bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException("Invalid End Event. Bounds is missing.");
		}

		jpdl.write(" />\n");

		return jpdl.toString();
	}
}

