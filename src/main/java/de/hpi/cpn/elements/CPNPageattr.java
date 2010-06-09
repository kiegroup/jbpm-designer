package de.hpi.cpn.elements;

import com.thoughtworks.xstream.XStream;

public class CPNPageattr
{	
	//	Example
	//	<page id="ID6">
	//    	<pageattr name="Tutorial"/>
	//    ...    
	
	private String name;	
	
	// ---------------------------------------- Mapping ----------------------------------------
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("pageattr", CPNPageattr.class);
		
		xstream.useAttributeFor(CPNPageattr.class, "name");
	}	
	
	// ---------------------------------------- Accessory ----------------------------------------
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
}
