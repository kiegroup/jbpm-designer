package org.b3mn.poem.jbpm;

import java.io.StringWriter;
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
	
	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<exclusive");
		
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(JsonToJpdl.transformAttribute("expr", expression));
		
		if(bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException("Invalid Exclusive gateway. Bounds is missing.");
		}
			
		jpdl.write(" >\n");
		
		if(handler != null)
			jpdl.write("<handler class=\"" + handler + "\" />\n");
		
		for (Transition t : outgoings) {
			jpdl.write(t.toJpdl());
		}
		
		jpdl.write("</exclusive>\n");
		
		return jpdl.toString();
	}

}
