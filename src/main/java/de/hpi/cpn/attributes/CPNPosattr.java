package de.hpi.cpn.attributes;

import com.thoughtworks.xstream.XStream;

import de.hpi.cpn.mapperhelper.XMLConvertable;

public class CPNPosattr extends XMLConvertable
{
	// Example
	// <posattr x="-122.500000"
    //    	y="-4.000000"/>
	
	private String x;	
	private String y;	
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("posattr", CPNModellingThing.class);
		
		xstream.useAttributeFor(CPNPosattr.class, "x");
		xstream.useAttributeFor(CPNPosattr.class, "y");
	}

	// ------------------------------ Accessor ------------------------------
	public void setX(String x) {
		this.x = x;
	}
	public String getX() {
		return x;
	}

	public void setY(String y) {
		this.y = y;
	}
	public String getY() {
		return y;
	}
}
