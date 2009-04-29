package org.b3mn.poem.jbpm;

import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class WireString extends WireObjectGroup {

	private String value;
	
	public WireString(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String toJpdl() {
		
		StringWriter jpdl = new StringWriter();
		jpdl.write("<string");
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(JsonToJpdl.transformAttribute("value", value));
		jpdl.write(" />");
		
		return jpdl.toString();
	}
	
	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject string = new JSONObject();
		if (name != null)
			string.put("name", name);
		if (value != null)
			string.put("value", value);
		string.put("type", "string");
		return string;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	

}
