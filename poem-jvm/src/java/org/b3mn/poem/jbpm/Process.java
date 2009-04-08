package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Process {
	
	private String name;
	private String key;
	private String version;
	private String description;
	private List<Node> childNodes;
	
	public Process(JSONObject process) {
		try {
			this.name = process.getJSONObject("properties").getString("name");
		} catch (JSONException e) {}
		
		try {
			this.key = process.getJSONObject("properties").getString("key");
		} catch (JSONException e) {}
		
		try {
			this.version = process.getJSONObject("properties").getString("version");
		} catch (JSONException e) {}
		
		try {
			this.description = process.getJSONObject("properties").getString("description");
		} catch (JSONException e) {}
		
		childNodes = new ArrayList<Node>();
		
		try {
			JSONArray processElements = process.getJSONArray("childShapes");
			
			// Create all process nodes
			for(int i = 0; i < processElements.length(); i++) {
				JSONObject currentElement = processElements.getJSONObject(i);
				String currentElementID = currentElement.getJSONObject("stencil").getString("id");
				Node item = null;
				if(currentElementID.equals("StartEvent"))
					item = new StartEvent(currentElement);
				else if(currentElementID.equals("EndEvent"))
					item = new EndEvent(currentElement);
				else if(currentElementID.equals("EndErrorEvent"))
					item = new EndErrorEvent(currentElement);
				else if(currentElementID.equals("EndCancelEvent"))
					item = new EndCancelEvent(currentElement);
				else if(currentElementID.equals("Task"))
					item = new Task(currentElement);
				else if(currentElementID.equals("wait"))
					item = new State(currentElement);
				else if(currentElementID.equals("java"))
					item = new Java(currentElement);
				else if(currentElementID.equals("esb"))
					item = new Esb(currentElement);
				else if(currentElementID.equals("sql"))
					item = new Sql(currentElement);
				else if(currentElementID.equals("hql"))
					item = new Hql(currentElement);
				else if(currentElementID.equals("script"))
					item = new Script(currentElement);
				else if(currentElementID.equals("AND_Gateway"))
					item = new And(currentElement);
				else if(currentElementID.equals("Exclusive_Databased_Gateway"))
					item = new Xor(currentElement);
				if (item != null)
					childNodes.add(item);
			}
		} catch (JSONException e) {}
	}
	
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		
		jpdl.write("<process");
		jpdl.write(JsonToJpdl.transformAttribute("name", name)); 
		jpdl.write(JsonToJpdl.transformAttribute("key", key)); 
		jpdl.write(JsonToJpdl.transformAttribute("version", version)); 
		jpdl.write(JsonToJpdl.transformAttribute("description", description)); 
		jpdl.write(JsonToJpdl.transformAttribute("xmlns", "http://jbpm.org/4/jpdl"));
		jpdl.write(" >\n");
		
		for(int i = 0; i < childNodes.size(); i++) {
			jpdl.write(childNodes.get(i).toJpdl());
		}
		
		jpdl.write("</process>");
		return jpdl.toString();
	}
	
	
	
}
