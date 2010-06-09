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

package de.hpi.cpn.attributes;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

import de.hpi.cpn.mapperhelper.XMLConvertable;


public class CPNModellingThing extends XMLConvertable
{
	// There are a lot of elements in the CPN Tools XML structure that have these variables 
	// Inheritance is the best way to summarize them.
	private String id;
	private CPNPosattr posattr = new CPNPosattr();
	private CPNFillattr fillattr = new CPNFillattr();
	private CPNLineattr lineattr = new CPNLineattr();
	private CPNTextattr textattr = new CPNTextattr();
	
	
	public CPNModellingThing()
	{
		getFillattr().setdefaultCPNFillattr();
		getLineattr().setdefaultCPNLineattr();
		getTextattr().setdefaultCPNTextattr();
	}
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("ModellingThing", CPNModellingThing.class);
		
		xstream.useAttributeFor(CPNModellingThing.class, "id");
		
		CPNPosattr.registerMapping(xstream);
		CPNFillattr.registerMapping(xstream);
		CPNTextattr.registerMapping(xstream);
		CPNLineattr.registerMapping(xstream);	
	}
	
	// ---------------------------------------- Helper ----------------------------------------
	
	public static String getXCoordinateWith(int defaultShiftX, JSONObject modelElement) throws JSONException
	{
		int x = (int) Double.parseDouble(modelElement.getString("x"));
		x += defaultShiftX;
		
		return "" + x + ".000000";		
	}
	
	public static String getYCoordinateWith(int defaultShiftY, JSONObject modelElement) throws JSONException
	{		
		int y = (int) Double.parseDouble(modelElement.getString("y"));
		y += defaultShiftY;
		
		return "" + (-1 * y) + ".000000";
	}
	
	// ---------------------------------------- Accessor ----------------------------------------
	
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	
	public void setPosattr(CPNPosattr _posattr)
	{
		this.posattr = _posattr;
	}
	public CPNPosattr getPosattr() {
		return posattr;
	}
	
	public void setFillattr(CPNFillattr _fillattr) {
		this.fillattr = _fillattr;
	}
	public CPNFillattr getFillattr() {
		return fillattr;
	}
	
	public void setLineattr(CPNLineattr _lineattr) {
		this.lineattr = _lineattr;
	}
	public CPNLineattr getLineattr() {
		return lineattr;
	}
	
	public void setTextattr(CPNTextattr _textattr) {
		this.textattr = _textattr;
	}
	public CPNTextattr getTextattr() {
		return textattr;
	}		
}
