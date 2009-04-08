package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import org.json.JSONObject;

public class And extends Node {
	
	public And(JSONObject and) {
		
		this.name = JsonToJpdl.readAttribute(and, "name");
		this.bounds = JsonToJpdl.readBounds(and);
		this.outgoings = JsonToJpdl.readOutgoings(and);

	}
	
	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<fork");
		
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		
		if(bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException("Invalid Wait activity. Bounds is missing.");
		}
			
		if(outgoings.size() > 0) {
			jpdl.write(" >\n");
			for (Transition t : outgoings) {
				jpdl.write(t.toJpdl());
			}
			jpdl.write("</fork>\n");
		} else {
			jpdl.write(" />\n");
		}

		return jpdl.toString();
	}

}
