package org.b3mn.poem.jbpm;

import java.io.StringWriter;

import org.json.JSONObject;

public class EndCancelEvent extends EndEvent{

	public EndCancelEvent(JSONObject endEvent) {
		super(endEvent);
	}
	
	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<end-cancel");
		jpdl.write(writeAttributes());
		return jpdl.toString();

	}

}
