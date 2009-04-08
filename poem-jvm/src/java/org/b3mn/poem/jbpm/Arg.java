package org.b3mn.poem.jbpm;

import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class Arg {
	private WireObjectGroup child = null;
	
	public Arg (JSONObject arg) {
		try {
			if(arg.getString("type").toLowerCase().equals("string")) {
				String sName = arg.getString("name");
				String sValue = arg.getString("value");
				this.child = new WireString(sName, sValue);
			}
			if(arg.getString("type").toLowerCase().equals("object")) {
				String oName = arg.getString("name");
				this.child = new WireObjectType(oName);
			}
		} catch (JSONException e) {}
	}
	
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<arg>\n");
		if(child != null) {
			jpdl.write(child.toJpdl());
		} else {
			throw new InvalidModelException("Invalid Arg. Object or String is missing");
		}
		jpdl.write("</arg>\n");
		return jpdl.toString();
	}

	public WireObjectGroup getChild() {
		return child;
	}

	public void setChild(WireObjectGroup child) {
		this.child = child;
	}
}
