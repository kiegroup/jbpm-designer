package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Java extends Node {
	
	private String clazz;
	private String method;
	private String var;
	private List<Arg> args;
	private List<Field> field;
	
	public Java(JSONObject java) {
		
		this.name = JsonToJpdl.readAttribute(java, "name");
		this.clazz = JsonToJpdl.readAttribute(java, "class");
		this.method = JsonToJpdl.readAttribute(java, "method");
		this.var = JsonToJpdl.readAttribute(java, "var");
		this.bounds = JsonToJpdl.readBounds(java);
		
		field = new ArrayList<Field>();
		try {
			JSONArray parameters = java.getJSONObject("properties")
					.getJSONObject("field").getJSONArray("items");
			for (int i = 0; i < parameters.length(); i++) {
				JSONObject item = parameters.getJSONObject(i);
				field.add(new Field(item));
			}
		} catch (JSONException e) {}
		
		args =  new ArrayList<Arg>();
		try {
			JSONArray parameters = java.getJSONObject("properties")
					.getJSONObject("arg").getJSONArray("items");
			for (int i = 0; i < parameters.length(); i++) {
				JSONObject item = parameters.getJSONObject(i);
				args.add(new Arg(item));
			}
		} catch (JSONException e) {}
		
		this.outgoings = JsonToJpdl.readOutgoings(java);

	}
	
	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<java");
		
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		
		try {
			jpdl.write(JsonToJpdl.transformRequieredAttribute("class", clazz));
			jpdl.write(JsonToJpdl.transformRequieredAttribute("method", method));
			jpdl.write(JsonToJpdl.transformRequieredAttribute("var", var));
		} catch (InvalidModelException e) {
			throw new InvalidModelException("Invalid Java activity. " + e.getMessage());
		}
		
		if(bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException("Invalid Java activity. Bounds is missing.");
		}
			
		jpdl.write(" >\n");
		
		for (Field f : field) {
			jpdl.write(f.toJpdl());
		}
		
		for (Arg a : args) {
			jpdl.write(a.toJpdl());
		}
		
		for (Transition t : outgoings) {
			jpdl.write(t.toJpdl());
		}
		
		jpdl.write("</java>\n");
		
		return jpdl.toString();
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

}
