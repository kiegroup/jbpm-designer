/*This comment deals with the mapping of position attributes.
 * 
 * In Oryx every position is relative to the upperLeft corner of the canvas. CPN Tools represents
 * position information in a usual coordinate system. For the export I choose to export net
 * into the 4. quadrant (where x - values are positive and y - values are negative). The reason
 * for that is the fact that you don't have to calculate much. You only have to multiply -1 to
 * the y value, in order to make it negative. It's implemented in the method
 * getXCoordinateWith(...) in CPNModellingElement.
 * 
 * */

package de.hpi.cpn.elements;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

import de.hpi.cpn.attributes.CPNLittleProperty;
import de.hpi.cpn.attributes.CPNModellingThing;
import de.hpi.cpn.attributes.CPNProperty;


public class CPNPlace extends CPNModellingThing
{
	// Elements which are neither important for the Export nor the Import, but these elements
	// are necessary for XStream otherwise XStream raises an error
	private transient Object ports, tokens, fusioninfos;
	
	private String text;
	private CPNLittleProperty ellipse = CPNLittleProperty.ellipse();
	private CPNLittleProperty token = CPNLittleProperty.token();	
	private CPNLittleProperty marking = CPNLittleProperty.marking();
	private CPNProperty type = new CPNProperty();
	private CPNProperty initmark = new CPNProperty();
	
	
	public CPNPlace()
	{
		super();		
	}
	
// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("place", CPNPlace.class);
	}
	
	// ----------------------------------------- JSON Reader ------------------------------------
	
	public void readJSONproperties(JSONObject modelElement) throws JSONException
	{
		JSONObject properties = new JSONObject(modelElement.getString("properties"));
		this.parse(properties);
	}
	
	public void readJSONtitle(JSONObject modelElement) throws JSONException
	{
		String text = modelElement.getString("title");		
		setText(text);
	}
	
	public void readJSONcolorsettype(JSONObject modelElement) throws JSONException
	{
		String colorsettype = modelElement.getString("colorsettype");
		
		JSONObject tempJSON = new JSONObject();
		tempJSON.put("colordefinition", colorsettype);
		tempJSON.put("id", getId() + "1");
		
		getType().parse(tempJSON);		
	}	
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException
	{
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		JSONObject tokenProperties = new JSONObject();
		tokenProperties.put("id", getId() + 2);
		
		if (childShapes != null)
		{
			// Iterating over all tokens 
			for (int i = 0; i < childShapes.length(); i++) 
			{
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				if (stencil.equals("Token"))
				{
					tokenProperties = new JSONObject(childShape.getString("properties"));
					getInitmark().parse(tokenProperties);					
				}
			}
		}	
	}
	
	public void readJSONbounds(JSONObject modelElement) throws JSONException
	{
		// There is no need to calculate the center, so I choose the upperLeft
		JSONObject boundsJSON = modelElement.getJSONObject("bounds").getJSONObject("upperLeft");
		
		setPositionAttributes(boundsJSON);
		
		settypePositionAttributes(boundsJSON);
		setinitmarkPositionAttributes(boundsJSON);
	}
	
	// ------------------------------------------ Helper ----------------------------------------
	
	public static boolean handlesStencil(String stencil)
	{		
		return stencil.equals("Place");
	}
	
	public void setPositionAttributes(JSONObject modelElement) throws JSONException
	{	
		// See the comment in the first line
		// DefaultShift is zero, because exact point of the place
		getPosattr().setX(getXCoordinateWith(0, modelElement));
		getPosattr().setY(getYCoordinateWith(0, modelElement));
	}
	
	public void setinitmarkPositionAttributes(JSONObject modelElement) throws JSONException
	{
		int defaultShiftX = 57;
		int defaultShiftY = -23;
		
		JSONObject initmarkingPositionJSON = new JSONObject();
		
		initmarkingPositionJSON.put("initpostattrX", getXCoordinateWith(defaultShiftX, modelElement));
		initmarkingPositionJSON.put("initpostattrY", getYCoordinateWith(defaultShiftY, modelElement));
		
		getInitmark().parse(initmarkingPositionJSON);
	}
	
	public void settypePositionAttributes(JSONObject modelElement) throws JSONException
	{
		int defaultShiftX = 43;
		int defaultShiftY = 23;
		
		JSONObject typePositionJSON = new JSONObject();
		
		typePositionJSON.put("typepostattrX", getXCoordinateWith(defaultShiftX, modelElement));
		typePositionJSON.put("typepostattrY", getYCoordinateWith(defaultShiftY, modelElement));
		
		getType().parse(typePositionJSON);
	}
	
	
	// ---------------------------------------- Accessory ----------------------------------------
   
    public CPNLittleProperty getEllipse()
    {
       return this.ellipse;
    }
    public void setEllipse(CPNLittleProperty _ellipse)
    {
       this.ellipse = _ellipse;
    }  
    
    public String getText()
    {
  	   return text;
    }
    public void setText(String _text)
    {
	    this.text = _text;
    }

    public void setToken(CPNLittleProperty token) 
    {
	    this.token = token;
    }   
    public CPNLittleProperty getToken() 
    {
 	    return token;
    }
   
    public void setMarking(CPNLittleProperty marking) 
    {
 	   this.marking = marking;
    }
    public CPNLittleProperty getMarking()
    {
	  return marking;
    } 

    public void setInitmark(CPNProperty initmarking)
    {
 	   this.initmark = initmarking;
    }   
    public CPNProperty getInitmark()
    {
 	   return initmark;
    }
	
    public void setType(CPNProperty type) 
    {
	    this.type = type;
    }	
    public CPNProperty getType() 
    {
	    return type;
    }
}