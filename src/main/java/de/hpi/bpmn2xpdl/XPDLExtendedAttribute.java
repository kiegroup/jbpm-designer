package de.hpi.bpmn2xpdl;

import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("ExtendedAttribute")
public class XPDLExtendedAttribute {

	@Attribute("Name")
	protected String name;
	@Attribute("Value")
	protected String value;
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setName(String nameParameter) {
		name = nameParameter;
	}
	
	public void setValue(String valueParameter) {
		value = valueParameter;
	}
}
