package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.List;

public class UserCode {
	private String name;
	private String clazz;
	private List<Field> fields;
	private List<Property> properties;
	
	public UserCode() {
		
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<" + name);
		jpdl.write(JsonToJpdl.transformAttribute("class", clazz));
		jpdl.write(" >\n");
		
		for(Property p : properties)
			jpdl.write(p.toJpdl());
		
		for(Field f : fields)
			jpdl.write(f.toJpdl());
		
		jpdl.write("</" + name + ">\n");
		
		return jpdl.toString();
	}
	
	
}
