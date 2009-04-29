package org.b3mn.poem.jbpm;

import org.json.JSONException;
import org.json.JSONObject;

public class WireObjectGroup {
	
	protected String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toJpdl() {
		return "";
	}

	public JSONObject toJson() throws JSONException {
		return new JSONObject();
	}
}
