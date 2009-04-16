package org.b3mn.poem.jbpm;

import org.json.JSONException;
import org.json.JSONObject;

public class EndCancelEvent extends EndEvent{

	public EndCancelEvent(JSONObject endEvent) {
		super(endEvent);
	}
	
	public EndCancelEvent(org.w3c.dom.Node endEvent) {
		super(endEvent);
	}
	
	@Override
	public String toJpdl() throws InvalidModelException {
		String id = "end-cancel";
		return writeJpdlAttributes(id).toString();

	}
	
	@Override
	public JSONObject toJson() throws JSONException {
		String id = "EndCancelEvent";
		
		return writeJsonAttributes(id);
	}

}
