package de.hpi.cpn.attributes;

import com.thoughtworks.xstream.XStream;

public class CPNTextattr
{
	// Example
	// <textattr colour="Black"
    //     	bold="false"/>
	
	private String colour;
	private String bold;
		
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream) 
	{
		xstream.alias("textattr", CPNTextattr.class);
		
		xstream.useAttributeFor(CPNTextattr.class, "colour");
		xstream.useAttributeFor(CPNTextattr.class, "bold");		
	}


	public void setdefaultCPNTextattr()
	{
		String defaultColour = "Black";
		String defaultBold = "false";
		
		setColour(defaultColour);
		setBold(defaultBold);
	}
	
	public static CPNTextattr getdefaultTextattr()
	{
		CPNTextattr tempTextattr = new CPNTextattr();
		
		tempTextattr.setdefaultCPNTextattr();
		
		return tempTextattr;
	}
	
	
	// ------------------------------ Accessory ------------------------------
	
	public void setColour(String colour) {
		this.colour = colour;
	}
	public String getColour() {
		return colour;
	}

	public void setBold(String bold) {
		this.bold = bold;
	}
	public String getBold() {
		return bold;
	}
}
