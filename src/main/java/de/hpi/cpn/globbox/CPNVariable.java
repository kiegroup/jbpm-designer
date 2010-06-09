package de.hpi.cpn.globbox;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

import de.hpi.cpn.mapperhelper.XMLConvertable;

public class CPNVariable extends XMLConvertable
{	
	//	Example
	//	<var id="ID88708">
	//    <type>
	//      <id>int</id>
	//    </type>
	//    <id>y</id>
	//    <layout>var y: int;</layout>
	//  </var>
    
    
	private String idattri;
	private CPNVarType type;
	private String idtag;
	
	private String layout;
	
// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("var", CPNVariable.class);
		
		xstream.aliasField("id", CPNVariable.class, "idattri");
		xstream.aliasField("id", CPNVariable.class, "idtag");
		
		// the field idattri becomes an attribute of the variable - tag 
		xstream.useAttributeFor(CPNVariable.class, "idattri");
	}

// --------------------------------------- JSON Reader -----------------------------------
	
	public void readJSONname(JSONObject modelElement) throws JSONException
	{
		String name = modelElement.getString("name");
		
		setIdtag(name);
		
		String layout = getLayoutText(modelElement);
		
		setLayout(layout);
	}
	
	public void readJSONid(JSONObject modelElement) throws JSONException
	{
		String id = modelElement.getString("id");
		
		setIdattri(id);
	}
	
	public void readJSONtype(JSONObject modelElement) throws JSONException
	{
		CPNVarType tempVarType = new CPNVarType();
		
		String type = modelElement.getString("type");
		tempVarType.setId(type);
		
		setType(tempVarType);
	}
	
	// ---------------------------------------- Helper -----------------------------------
	
	public String getLayoutText(JSONObject modelElement) throws JSONException
	{
		String layoutText = "var ";
		
		layoutText = layoutText + modelElement.getString("name");
		layoutText = layoutText + ": ";
		layoutText = layoutText + modelElement.getString("type");
		layoutText = layoutText + ";";
		
		return layoutText;		
	}
	
	// ------------------------------ Accessor ------------------------------------------
	
	public void setIdtag(String idtag) {
		this.idtag = idtag;
	}
	public String getIdtag() {		
		return idtag;
	}

	public void setType(CPNVarType type) {
		this.type = type;
	}
	public CPNVarType getType() {
		return type;
	}

	public void setIdattri(String idattri) {
		this.idattri = idattri;
	}
	public String getIdattri() {
		return idattri;
	}
	
	public void setLayout(String layout) {
		this.layout = layout;
	}
	public String getLayout() {
		return layout;
	}
}
