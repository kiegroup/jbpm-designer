package de.hpi.cpn.globbox;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNProduct
{
	// Example
	//  <product>
	//    <id>Name</id>
	//    <id>Alter</id>
	//  </product>
	
	// Elements which are neither important for the Export nor the Import, but these elements
	// are necessary for XStream otherwise XStream raises an error
	private transient Object with;
	
	private ArrayList<String> ids;
	
	// ------------------------------------------ Mapping ------------------------------------------
	public static void registerMapping(XStream xstream)
	{
	   xstream.alias("product", CPNProduct.class);	   
	   xstream.alias("id", String.class);
	   
	   xstream.aliasField("with", CPNProduct.class, "with");
	   
	   xstream.addImplicitCollection(CPNProduct.class, "ids", String.class);
	}
	
	// -------------------------------------------- Helper ----------------------------------------
	
	
	public String getLayoutText(JSONObject modelElement) throws JSONException
	{
		String layoutText = "colset ";
		layoutText = layoutText + modelElement.getString("name");
		layoutText = layoutText + " = product ";
		layoutText = layoutText + modelElement.getString("type");
		layoutText = layoutText + ";";
		
		return layoutText;		
	}

	// ------------------------------------------ Accessory --------------------------------------
	
	public ArrayList<String> getIds()
	{
		if (ids == null)
			ids = new ArrayList<String>();
		return ids;
	}
	public void addId(String id)
	{
		getIds().add(id);
	}
	public String getId(int index)
	{
		return getIds().get(index);
	}
}
