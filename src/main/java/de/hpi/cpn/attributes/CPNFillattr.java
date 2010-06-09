package de.hpi.cpn.attributes;

import com.thoughtworks.xstream.XStream;

public class CPNFillattr
{
	// Example
	// <fillattr colour="White"
	//         pattern="Solid"
	//         filled="false"/>

	private String colour;	
	private String pattern;
	private String filled;
	
		
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("fillattr", CPNFillattr.class);
		
		xstream.useAttributeFor(CPNFillattr.class, "colour");
		xstream.useAttributeFor(CPNFillattr.class, "pattern");
		xstream.useAttributeFor(CPNFillattr.class, "filled");
	}

	public void setdefaultCPNFillattr()
	{
		String defaultColour = "White";
		String defaultPattern = "";
		String defaultFilled = "false";
		
		setColour(defaultColour);
		setPattern(defaultPattern);
		setFilled(defaultFilled);
	}
	
	public static CPNFillattr getdefaultFillattr()
	{
		CPNFillattr tempFillattr = new CPNFillattr();
		
		tempFillattr.setdefaultCPNFillattr();
		
		return tempFillattr;
	}

	// ------------------------------ Accessory ------------------------------
	
	public void setColour(String colour) {
		this.colour = colour;
	}
	public String getColour() {
		return colour;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getPattern() {
		return pattern;
	}

	public void setFilled(String filled) {
		this.filled = filled;
	}
	public String getFilled() {
		return filled;
	}

}
