package org.b3mn.poem.jbpm;

import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class Field {
	private WireObjectGroup child = null;
	private String name;
	
	public Field (JSONObject field) {
		try {
			this.name = field.getString("f_name");
		} catch (JSONException e) {}
		
		try {
			if(field.getString("type").toLowerCase().equals("string")) {
				String sName = field.getString("name");
				String sValue = field.getString("value");
				this.child = new WireString(sName, sValue);
			}
			if(field.getString("type").toLowerCase().equals("object")) {
				String oName = field.getString("name");
				this.child = new WireObjectType(oName);
			}
		} catch (JSONException e) {}
	}
	
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<field ");
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(" >\n");
		if(child != null) {
			jpdl.write(child.toJpdl());
		} else {
			throw new InvalidModelException("Invalid Field. Object or String is missing");
		}
		jpdl.write("</field>\n");
		return jpdl.toString();
	}

	public WireObjectGroup getChild() {
		return child;
	}

	public void setChild(WireObjectGroup child) {
		this.child = child;
	}
}
