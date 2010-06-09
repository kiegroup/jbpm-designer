package de.hpi.cpn.attributes;

import com.thoughtworks.xstream.XStream;

public class CPNLineattr
{
	// Example
	// <lineattr colour="Black"
    //    	thick="0"
    //    	type="Solid"/>
	
	private String colour;	
	private String thick;
	private String type;	
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("lineattr", CPNLineattr.class);
		
		xstream.useAttributeFor(CPNLineattr.class, "colour");
		xstream.useAttributeFor(CPNLineattr.class, "thick");
		xstream.useAttributeFor(CPNLineattr.class, "type");
	}

	
	public void setdefaultCPNLineattr()
	{
		String defaultColour = "Black";
		String defaultThick = "1";
		String defaultType = "Solid";
		
		setColour(defaultColour);
		setThick(defaultThick);
		setType(defaultType);
	}
	
	public static CPNLineattr getdefaultLineattr()
	{
		CPNLineattr tempLineattr = new CPNLineattr();
		
		tempLineattr.setdefaultCPNLineattr();
		
		return tempLineattr;
	}

	// ------------------------------ Accessory ------------------------------
	
	public void setColour(String colour) 
	{
		this.colour = colour;
	}
	public String getColour()
	{
		return colour;
	}

	public void setThick(String thick) 
	{
		this.thick = thick;
	}
	public String getThick() 
	{
		return thick;
	}

	public void setType(String type)
	{
		this.type = type;
	}
	public String getType()
	{
		return type;
	}
}
