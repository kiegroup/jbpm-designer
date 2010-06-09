package de.hpi.cpn.globbox;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNInteger
{
	// Elements which are neither important for the Export nor the Import, but these elements
	// are necessary for XStream otherwise XStream raises an error
	private transient Object with;
	
	// ------------------------------------------ Mapping ------------------------------------------
	public static void registerMapping(XStream xstream)
	{
	   xstream.alias("integer", CPNInteger.class);
	   
	   xstream.aliasField("with", CPNInteger.class, "with");
	}
	
	// -------------------------------------------- Helper ----------------------------------------
	
	public String getLayoutText(JSONObject modelElement) throws JSONException
	{
		String layoutText = "colset ";
		layoutText = layoutText + modelElement.getString("name");
		layoutText = layoutText + " = int;";
		
		return layoutText;		
	}
}
