package org.b3mn.poem.jbpm;

import java.io.StringWriter;

import org.json.JSONObject;

public class EndErrorEvent extends EndEvent{

	public EndErrorEvent(JSONObject endEvent) {
		super(endEvent);
	}
	
	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<end-error");
		jpdl.write(writeAttributes());
		return jpdl.toString();

	}

}
