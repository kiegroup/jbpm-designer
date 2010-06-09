package de.hpi.cpn.globbox;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNList
{
	// Elements which are neither important for the Export nor the Import, but these elements
	// are necessary for XStream otherwise XStream raises an error
	private transient Object with;
	
	private String id;
	
	// ------------------------------------------ Mapping ------------------------------------------
	public static void registerMapping(XStream xstream)
	{
	   xstream.alias("list", CPNList.class);
	   
	   xstream.aliasField("with", CPNList.class, "with");
	}
	
	// -------------------------------------------- Helper ----------------------------------------
	
	public String getLayoutText(JSONObject modelElement) throws JSONException
	{
		String layoutText = "colset ";
		layoutText = layoutText + modelElement.getString("name");
		layoutText = layoutText + " = ";
		layoutText = layoutText + modelElement.getString("type");
		layoutText = layoutText + ";";
		
		return layoutText;		
	}

	// ------------------------------------------ Accessory --------------------------------------
	public void setId(String id)
	{
		this.id = id;
	}
	public String getId()
	{
		return this.id;
	}
}
