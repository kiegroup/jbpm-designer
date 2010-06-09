package de.hpi.cpn;

import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

import de.hpi.cpn.elements.CPNPage;
import de.hpi.cpn.globbox.CPNGlobbox;
import de.hpi.cpn.mapperhelper.XMLConvertable;

public class CPNCpnet extends XMLConvertable 
{
	// Elements which are neither important for the Export nor the Import, but these elements
	// are necessary for XStream otherwise XStream raises an error 
	private transient Object instancestag, optionstag, binderstag, monitorblocktag, indexnodetag;
	
	// for colorsets and variables
	private CPNGlobbox globbox = new CPNGlobbox();
	
	// Pages for all the nets
	private ArrayList<CPNPage> pages = new ArrayList<CPNPage>();	
	
	
	// ------------------------------------ Mapping ------------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		// In the XML the class is represented as a cpnet tag
		xstream.alias("cpnet", CPNCpnet.class);
		
		// In order not to display the pages tag, but simply show the page tags one after another
		xstream.addImplicitCollection(CPNCpnet.class, "pages", CPNPage.class);
		
		// Giving all fields a concrete name for the XML
		xstream.aliasField("instances", CPNCpnet.class, "instancestag");
		xstream.aliasField("options", CPNCpnet.class, "optionstag");
		xstream.aliasField("binders", CPNCpnet.class, "binderstag");
		xstream.aliasField("monitorblock", CPNCpnet.class, "monitorblocktag");
		xstream.aliasField("IndexNode", CPNCpnet.class, "indexnodetag");		
		
		CPNGlobbox.registerMapping(xstream);
		CPNPage.registerMapping(xstream);
	}
	
    
	// ---------------------------------------- JSON Reader ------------------------------------
	
	public void readJSONresourceId(JSONObject modelElement) throws JSONException
	{
		createPage(modelElement);
		createGlobbox(modelElement);
	}
	 
	private void createPage(JSONObject modelElement) throws JSONException
	{
		CPNPage page = new CPNPage();
		
		// Giving the page that is exported a concrete id
		page.setId("ID123456789");
		
		// Preparing the Arc Relations, so that every arc knows which id is its source or target
		page.prepareArcRelations(modelElement);		
		page.parse(modelElement);
		
		getPages().add(page);
	}
	 
	private void createGlobbox(JSONObject modelElement)
	{
		getGlobbox().parse(modelElement);
	}  

    // ------------------------------------ Accessory ----------------------------------------
    
	public CPNGlobbox getGlobbox()
    {
       return this.globbox;
    }
    public void setGlobbox(CPNGlobbox _globbox)
    {
       this.globbox = _globbox;
    }
    
    public ArrayList<CPNPage> getPages()
    {
       return this.pages;
    }
    public void setPages(ArrayList<CPNPage> _pages)
    {
       this.pages = _pages;
    }
    public void addPage(CPNPage _page)
    {
       this.pages.add(_page);
    }
    public void removePage(CPNPage _page)
    {
       this.pages.remove(_page);
    }
    public CPNPage getPage( int i)
    {
       return (CPNPage) this.pages.get(i);
    }
} 

