package org.b3mn.poem.jbpm;

import org.json.JSONException;
import org.json.JSONObject;

public class EndErrorEvent extends EndEvent{

	public EndErrorEvent(JSONObject endEvent) {
		super(endEvent);
	}
	
	public EndErrorEvent(org.w3c.dom.Node endEvent) {
		super(endEvent);
	}
	
	@Override
	public String toJpdl() throws InvalidModelException {
		String id = "end-error";
		return writeJpdlAttributes(id).toString();

	}
	
	@Override
	public JSONObject toJson() throws JSONException {
		String id = "EndErrorEvent";
		
		return writeJsonAttributes(id);
	}

}
