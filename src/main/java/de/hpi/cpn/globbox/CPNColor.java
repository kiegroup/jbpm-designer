package de.hpi.cpn.globbox;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

import de.hpi.cpn.mapperhelper.XMLConvertable;

public class CPNColor extends XMLConvertable
{
	//	Example
	//	<color id="ID161365">
	//    <id>NameAlter</id>
	//    <product>
	//      <id>Name</id>
	//      <id>Alter</id>
	//    </product>
	//    <layout>colset NameAlter = product Name * Alter;</layout>
	//  </color>
	
	// Elements which are neither important for the Export nor the Import, but these elements
	// are necessary for XStream otherwise XStream raises an error
	private transient Object mltag, alias;
	
	private String idattri;
	private String idtag;

	// The product tag in the example is represented by the CPNProduct class. It depends on
	// the data type the user writes into the declaration field when modeling the CPNet
	private CPNString stringtag;
	private CPNProduct producttag;
	private CPNInteger integertag;
	private CPNBoolean booleantag;
	private CPNList listtag;
	private CPNUnit unittag;
	
	private String layout;
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
	   xstream.alias("color", CPNColor.class);
	   
	   xstream.aliasField("ml", CPNColor.class, "mltag");
	   xstream.aliasField("alias", CPNColor.class, "alias");
	   xstream.aliasField("id", CPNColor.class, "idattri");
	   xstream.aliasField("id", CPNColor.class, "idtag");
	   xstream.aliasField("bool", CPNColor.class, "booleantag");
	   xstream.aliasField("string", CPNColor.class, "stringtag");
	   xstream.aliasField("int", CPNColor.class, "integertag");
	   xstream.aliasField("product", CPNColor.class, "producttag");
	   xstream.aliasField("list", CPNColor.class, "listtag");
	   xstream.aliasField("unit", CPNColor.class, "unittag");
	   
	   xstream.useAttributeFor(CPNColor.class, "idattri");	   
	   
	   CPNString.registerMapping(xstream);
	   CPNProduct.registerMapping(xstream);
	   CPNBoolean.registerMapping(xstream);
	   CPNInteger.registerMapping(xstream);
	   CPNList.registerMapping(xstream);
	   CPNUnit.registerMapping(xstream);
	}
	
	// ------------------------------------------ JSON Reader ------------------------------------------
	
	public void readJSONname(JSONObject modelElement) throws JSONException 
	{
		String name = modelElement.getString("name");
		
		setIdtag(name);		
	}
	
	public void readJSONid(JSONObject modelElement) throws JSONException 
	{
		String id = modelElement.getString("id");
		
		setIdattri(id);		
	}
	
	public void readJSONtype(JSONObject modelElement) throws JSONException
	{
		String declarationDataType = modelElement.getString("type");
		
		// Choosing the type the user typed into the declaration field
		if (declarationDataType.indexOf(" * ") != -1)
			addProduct(modelElement);
		
		if (declarationDataType.equalsIgnoreCase("string"))
			addString(modelElement);
			
		if (declarationDataType.equalsIgnoreCase("integer") || declarationDataType.equalsIgnoreCase("int"))
			addInteger(modelElement);
		
		if (declarationDataType.equalsIgnoreCase("boolean") || declarationDataType.equalsIgnoreCase("bool"))
			addBoolean(modelElement);
		
		if (declarationDataType.startsWith("list") || declarationDataType.startsWith("List"))
			addList(modelElement);
		
		if (declarationDataType.equalsIgnoreCase("unit"))
			addUnit(modelElement);
	}
	
	
	private void addProduct(JSONObject modelElement) throws JSONException
	{
		CPNProduct tempProduct = new CPNProduct();
		
		// An entry like the following is expected:
		// Name * Alter (Name and Alter are previously defined Colorsets)
		String[] declarationDataTypeSegments = modelElement.getString("type").split(" ");
		
		// In order to get all entries and don't loose any exported information
		// even if the user type in something like this:
		// * Name Alter
		for (int i = 0; i < declarationDataTypeSegments.length; i++)
			if (!declarationDataTypeSegments[i].equals("*"))
				tempProduct.addId(declarationDataTypeSegments[i]);
		
		setProducttag(tempProduct);
		
		String layoutText = tempProduct.getLayoutText(modelElement);
		setLayout(layoutText);
	}
	
	private void addString(JSONObject modelElement) throws JSONException
	{
		CPNString tempString = new CPNString();
		
		setStringtag(tempString);
		
		String layoutText = tempString.getLayoutText(modelElement);
		setLayout(layoutText);
	}
	
	private void addInteger(JSONObject modelElement) throws JSONException
	{
		CPNInteger tempInteger = new CPNInteger();
		
		setIntegertag(tempInteger);
		
		String layoutText = tempInteger.getLayoutText(modelElement);
		setLayout(layoutText);
	}
	
	private void addBoolean(JSONObject modelElement) throws JSONException
	{
		CPNBoolean tempBoolean = new CPNBoolean();
		
		setBooleantag(tempBoolean);
		
		String layoutText = tempBoolean.getLayoutText(modelElement);
		setLayout(layoutText);
	}
	
	private void addList(JSONObject modelElement) throws JSONException
	{
		// An entry like the following is expected:
		// list Name (Name is previously defined Colorsets)
		String[] declarationDataTypeSegments = modelElement.getString("type").split(" ");
		
		// When this method is called, we know that the first word is list.
		// But if there is only element in declarationDataTypeSegments, then it must be the word list.
		if (declarationDataTypeSegments.length < 2) 
			return;
		
		CPNList tempList = new CPNList();				
		
		String listType = declarationDataTypeSegments[1];
		tempList.setId(listType);
		
		setListtag(tempList);
		
		String layoutText = tempList.getLayoutText(modelElement);
		setLayout(layoutText);
	}
	
	private void addUnit(JSONObject modelElement) throws JSONException
	{
		CPNUnit tempUnit = new CPNUnit();
		
		setUnittag(tempUnit);
		
		String layoutText = tempUnit.getLayoutText(modelElement);
		setLayout(layoutText);
	}
	
	// ---------------------------------------- Accessory ----------------------------------------

	public void setIdattri(String idattri) 
	{
		this.idattri = idattri;
	}
	public String getIdattri() 
	{
		return idattri;
	}

	public void setIdtag(String idtag) 
	{
		this.idtag = idtag;
	}
	public String getIdtag() 
	{
		return idtag;
	}

	public void setLayout(String layout) 
	{
		this.layout = layout;
	}
	public String getLayout()
	{
		return layout;
	}

	public void setStringtag(CPNString stringtag) 
	{
		this.stringtag = stringtag;
	}
	public CPNString getStringtag()
	{
		return stringtag;
	}

	public void setProducttag(CPNProduct producttag) 
	{
		this.producttag = producttag;
	}
	public CPNProduct getProducttag() 
	{
		return producttag;
	}

	public void setIntegertag(CPNInteger integertag)
	{
		this.integertag = integertag;
	}
	public CPNInteger getIntegertag() 
	{
		return integertag;
	}

	public void setBooleantag(CPNBoolean booleantag) 
	{
		this.booleantag = booleantag;
	}
	public CPNBoolean getBooleantag()
	{
		return booleantag;
	}

	public void setListtag(CPNList listtag)
	{
		this.listtag = listtag;
	}
	public CPNList getListtag()
	{
		return listtag;
	}

	public void setUnittag(CPNUnit unittag) 
	{
		this.unittag = unittag;
	}
	public CPNUnit getUnittag() 
	{
		return unittag;
	}
}
