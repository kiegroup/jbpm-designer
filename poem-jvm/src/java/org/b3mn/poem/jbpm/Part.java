package org.b3mn.poem.jbpm;

import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class Part {
	private String expression = null;
	private String name = null;
	private WireObjectGroup child = null;

	public Part(JSONObject part) {
		try {
			this.name = part.getString("p_name");
		} catch (JSONException e) {}
		
		try {
			this.expression = part.getString("expr");
		} catch (JSONException e) {}
		
		try {
			if(part.getString("type").toLowerCase().equals("string")) {
				String sName = part.getString("name");
				String sValue = part.getString("value");
				this.child = new WireString(sName, sValue);
			}
			if(part.getString("type").toLowerCase().equals("object")) {
				String oName = part.getString("name");
				this.child = new WireObjectType(oName);
			}
		} catch (JSONException e) {}
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WireObjectGroup getChild() {
		return child;
	}

	public void setChild(WireObjectGroup child) {
		this.child = child;
	}
	
	public String toJpdl() {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<part ");
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(JsonToJpdl.transformAttribute("expr", expression));
		if(child != null) {
			jpdl.write(" >\n");
			jpdl.write(child.toJpdl());
			jpdl.write("</part>\n");
		} else {
			jpdl.write(" />\n");
		}
		return jpdl.toString();
	}

}
