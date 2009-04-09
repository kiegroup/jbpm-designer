package org.b3mn.poem.jbpm;

import java.io.StringWriter;

public class Property extends WireObjectGroup {
	
	private String name;
	private String value;
	
	public Property(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String toJpdl() {
		
		StringWriter jpdl = new StringWriter();
		jpdl.write("<property");
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(JsonToJpdl.transformAttribute("value", value));
		jpdl.write(" />");
		
		return jpdl.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	

}
