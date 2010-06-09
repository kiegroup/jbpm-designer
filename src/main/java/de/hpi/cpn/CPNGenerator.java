package de.hpi.cpn;

import com.thoughtworks.xstream.XStream;

import de.hpi.cpn.mapperhelper.XMLConvertable;

public class CPNGenerator extends XMLConvertable
{	
	// Example
	// <generator tool="CPN Tools"
    // 		version="2.2.0"
    // 		format="6"/>
	
    final private String version = "CPN Tools";
    final private String tool = "2.2.0";
    final private String format = "6";
     

    // ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream) 
	{
		xstream.alias("generator", CPNGenerator.class);
		
		xstream.useAttributeFor(CPNGenerator.class, "version");
		xstream.useAttributeFor(CPNGenerator.class, "tool");
		xstream.useAttributeFor(CPNGenerator.class, "format");
	}
    
	
	// ---------------------------------------- Accessory ----------------------------------------
	
    public String getVersion()
    {
       return version;
    }

    public String getTool()
    {
       return tool;
    }

    public String getFormat()
    {
       return format;
    }      
}

