package de.hpi.cpn.converter;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import de.hpi.cpn.CPNWorkspaceElement;

public class CPNConverter
{	
	public static String convertToCPNFile(String json) throws JSONException 
	{		
		try
		{
			XStream xstream = new XStream(new DomDriver());		
		
			CPNWorkspaceElement workElement = new CPNWorkspaceElement();
			
			// Setting up the workSpaceElement
			workElement.parse(new JSONObject(json));
			
			// Register importing mapping rules
			CPNWorkspaceElement.registerMapping(xstream);
			
			// Converting the object into a XML string
			String Result = xstream.toXML(workElement);
			
			return Result;
		}		
		catch (Exception e)
		{
			e.printStackTrace();
			return "error:" + e.getMessage();
		}		
	}
	
	public static String importFirstPage(String xml)
	{
		try
		{
			XStream xstream = new XStream(new DomDriver());
			
			CPNWorkspaceElement.registerMapping(xstream);
			
			// Extract an object out of the XML
			CPNWorkspaceElement workElement = (CPNWorkspaceElement) xstream.fromXML(xml);
			
			CPNToolsTranslator translator = new CPNToolsTranslator(workElement);
			
			// Putting the name of the first page into the String[]
			String[] pagesToImport = { workElement.getCpnet().getPage(0).getPageattr().getName()};
			
			String oryxJson = translator.translatePagesIntoDiagrams(pagesToImport);
			
			return oryxJson;			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "error:" + e.getMessage();
		}
	}
	
	public static String importPagesNamed(String xml, String[] pagesToImport)
	{
		try
		{
			xml = xml.substring(xml.indexOf("<workspaceElements>"));
			
			XStream xstream = new XStream(new DomDriver());
			
			CPNWorkspaceElement.registerMapping(xstream);
			
			// Extract an object out of the XML
			CPNWorkspaceElement workElement = (CPNWorkspaceElement) xstream.fromXML(xml);
			
			CPNToolsTranslator translator = new CPNToolsTranslator(workElement);
			
			String oryxJson = translator.translatePagesIntoDiagrams(pagesToImport);
	
			return oryxJson;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "error:" + e.getMessage();
		}
	}
}
