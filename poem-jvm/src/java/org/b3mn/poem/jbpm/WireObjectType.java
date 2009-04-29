package org.b3mn.poem.jbpm;

import java.io.StringWriter;

public class WireObjectType extends WireObjectGroup {
	
	private String clazz;
	private String object;
	
	public WireObjectType(String name) {
		this.name = name;
	}
	
	@Override
	public String toJpdl() {
		
		StringWriter jpdl = new StringWriter();
		jpdl.write("<");
		jpdl.write(name);
		jpdl.write(" />");
		
		return jpdl.toString();
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}
}
