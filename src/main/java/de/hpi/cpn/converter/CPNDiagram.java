/*This comment deals with the mapping of position attributes.
 * 
 * In Oryx every position is relative to the upperLeft corner of the canvas. CPN Tools represents
 * position information in a usual coordinate system. For the export I choose to export net
 * into the 4. quadrant (where x - values are positive and y - values are negative). The reason
 * for that is the fact that you don't have to calculate much. You only have to multiply -1 to
 * the y value, in order to make it negative. It's implemented in the method
 * getXCoordinateWith(...) in CPNModellingElement.
 * 
 * In order to do now the other way around we must find out at the biggest bounds of the
 * coordinate system relative to its upperLeft corner. You have to this because otherwise some
 * elements would be positioned on the wrong place where you cannot see the elements because they
 * are covered by some Oryx Editor elements (like Toolbar, StencilSet palette, ...).
 */

package de.hpi.cpn.converter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.oryxeditor.server.diagram.*;

import de.hpi.cpn.attributes.CPNBendpoint;
import de.hpi.cpn.attributes.CPNModellingThing;
import de.hpi.cpn.elements.CPNArc;
import de.hpi.cpn.elements.CPNPage;
import de.hpi.cpn.elements.CPNPlace;
import de.hpi.cpn.elements.CPNTransition;

public class CPNDiagram 
{
	public static int[] getMaxBounds(CPNPage tempPage)
	{
		// Take a look at the big comment above in order to know why maxBounds are needed   
		
		int[] maxBounds = {0,0};
		
		ArrayList<CPNPlace> places = tempPage.getPlaces();
		ArrayList<CPNTransition> transitions = tempPage.getTransitions();
		ArrayList<CPNArc> arcs = tempPage.getArcs();
		
		// Getting the biggest extension
		maxBounds = getMaxBoundsof(places, maxBounds);
		
		maxBounds = getMaxBoundsof(transitions, maxBounds);		
		
		for (int i = 0; i < arcs.size(); i++)
		{
			CPNArc tempArc = arcs.get(i);
			
			if (tempArc != null)
			{
				ArrayList<CPNBendpoint> bendpoints = tempArc.getBendpoints();
				
				maxBounds = getMaxBoundsof(bendpoints, maxBounds);
			}
		}
		
		
		return maxBounds;
	}
	
	private static <T> int[] getMaxBoundsof(ArrayList<T> elements, int[] currentMaxBounds)
	{
		// Loooking in all 
		for (int i = 0; i < elements.size(); i++)
		{
			T tempElement = elements.get(i);
			
			if (tempElement != null)
			{
				int X = (int) Double.parseDouble(((CPNModellingThing) tempElement).getPosattr().getX());
				int Y = (int) Double.parseDouble(((CPNModellingThing) tempElement).getPosattr().getY());
				
				if (X < currentMaxBounds[0])
					currentMaxBounds[0] = X;
				if (Y > currentMaxBounds[1])
					currentMaxBounds[1] = Y;
			}
		}
		
		return currentMaxBounds;
	}
	
	public static void setDiagramBounds(Diagram diagram, int[] boundsArray)
	{
		// 1485 and 1050 are default extent values
		Point UpperLeft = new Point(0.0, 0.0);
		Point LowerRight = new Point(1485.0 + boundsArray[0], 1050.0 + boundsArray[1]); 
		
		diagram.setBounds(new Bounds(LowerRight, UpperLeft));				
	}
	
	public static Diagram newColoredPetriNetDiagram()
	{
		String resourceId = "oryx-canvas123";		
		StencilType type = new StencilType("Diagram");		
		String stencilSetNs = "http://b3mn.org/stencilset/coloredpetrinet#";		
		// Take care of the root "/oryx/"; it might be changed when the root changes
		String url ="/oryx/stencilsets/coloredpetrinets/coloredpetrinet.json";
		
		StencilSet stencilSet = new StencilSet(url, stencilSetNs);		
		Diagram diagram = new Diagram(resourceId, type, stencilSet);
		
		diagram.setProperties(getDiagramProperties());
		
		return diagram;
	}
	
	private static HashMap<String, String> getDiagramProperties()
	{
		// Setting all properties specific for a CPN diagram
		HashMap<String, String> propertyMap = new HashMap<String, String>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date today = new Date();
		
		propertyMap.put("title","");
		propertyMap.put("engine","false");
		propertyMap.put("version","");
		propertyMap.put("author","");
		propertyMap.put("language","English");
		propertyMap.put("creationdate",sdf.format(today).toString());
		propertyMap.put("modificationdate",sdf.format(today).toString());
		propertyMap.put("documentation","");
		propertyMap.put("declarations","");
		
		return propertyMap;
	}
	
	public static JSONObject getDeclarationJSONObject(JSONArray declarations) throws JSONException
	{
		// It should look like this: 
		// {"totalCount":4,"items":[{"name":"Name","type":"String","declarationtype":"Colorset"},
		// {"name":"Alter","type":"Integer","declarationtype":"Colorset"}]}
		JSONObject declarationJSONObject = new JSONObject();
		
		declarationJSONObject.put("totalCount", declarations.length());
		declarationJSONObject.put("items", declarations);
		
		return declarationJSONObject;
	}
		
	public static JSONObject getOneDeclaration(String name, String type, String declarationtype) throws JSONException
	{
		// Creating a declaration entry
		JSONObject declaration = new JSONObject();
		
		declaration.put("name", name);
		declaration.put("type", type);
		declaration.put("declarationtype", declarationtype);
		
		return declaration;
	}
	
	public static Shape getanArc(String resourceId)
	{	
		// "properties":{"id":"","label":"","transformation":""}
		StencilType stencil = new StencilType("Arc");
		
		Shape arc = new Shape(resourceId, stencil);
		
		arc.getProperties().put("id", "");
		arc.getProperties().put("label", "");
		arc.getProperties().put("transformation", "");		
		
		return arc;
	}
	
	public static void setArcBounds(Shape arc)
	{
		// Oryx doesn't need in order to position the arc correctly
		Point UpperLeft = new Point(0.0, 0.0);
		Point LowerRight = new Point(0.0, 0.0); 
		
		arc.setBounds(new Bounds(LowerRight, UpperLeft));	
	}
	
	public static Point getDockerBendpoint(CPNBendpoint bendPoint, int[] boundsArray)
	{		
		int xPos = stringToInteger(bendPoint.getPosattr().getX()); 
		int yPos = stringToInteger(bendPoint.getPosattr().getY());
		
		// Translate the position in the cpnFile to a bounds position in Oryx
		// I multiply -1 in order to make the y value positive
		return new Point(0.0 + xPos + boundsArray[0], (0.0 + yPos + boundsArray[1]) * -1);
	}
	
	public static Shape getaTransition(String resourceId)
	{		
		StencilType stencil = new StencilType("Transition");
		
		Shape transition = new Shape(resourceId, stencil);
		
		transition.getProperties().put("id", "");
		transition.getProperties().put("title", "");
		transition.getProperties().put("firetype", "Automatic");
		transition.getProperties().put("href", "");
		transition.getProperties().put("omodel", "");
		transition.getProperties().put("oform", "");
		transition.getProperties().put("guard", "");		
		
		return transition;
	}
	
	public static void setTransitionBounds(Shape transition, int[] boundsArray, CPNTransition tempTransition)
	{
		// Default extent
		int w = 40, h = 40;
		
		// Adapting the extent of the transition
		int titleLen = transition.getProperties().get("title").length();
		
		w = w + titleLen * 3;
		h = h + titleLen;
		
		int xPos = stringToInteger(tempTransition.getPosattr().getX()); 
		int yPos = stringToInteger(tempTransition.getPosattr().getY()); 
		
		// Translate the position in the cpnFile to a bounds position in Oryx
		// I multiply -1 in order to make the y value positive
		Point UpperLeft = new Point(0.0 + xPos + boundsArray[0], (0.0 + yPos + boundsArray[1]) * -1);
		Point LowerRight = new Point(0.0 + xPos + boundsArray[0] + w, (0.0 + yPos + boundsArray[1]) * -1 + h); 
		
		transition.setBounds(new Bounds(LowerRight, UpperLeft));	
	}
	
	public static Shape getaPlace(String resourceId)
	{
		StencilType stencil = new StencilType("Place");
		
		Shape place = new Shape(resourceId, stencil);
		
		place.getProperties().put("id", "");
		place.getProperties().put("title", "");
		place.getProperties().put("external", "false");
		place.getProperties().put("exttype", "Push");
		place.getProperties().put("href", "");
		place.getProperties().put("locatornames", "");
		place.getProperties().put("locatortypes", "");
		place.getProperties().put("locatorexpr", "");
		place.getProperties().put("colordefinition", "");		
		
		return place;
	}
	
	public static void setPlaceBounds(Shape place, int[] boundsArray, CPNPlace tempPlace)
	{
		// Default extent
		int w = 64, h = 64;
		
		int xPos = stringToInteger(tempPlace.getPosattr().getX()); 
		int yPos = stringToInteger(tempPlace.getPosattr().getY()); 
		
		// Translate the position in the cpnFile to a bounds position in Oryx
		// I multiply -1 in order to make the y value positive 
		Point UpperLeft = new Point(0.0 + xPos + boundsArray[0], (0.0 + yPos + boundsArray[1]) * -1);
		Point LowerRight = new Point(0.0 + xPos + boundsArray[0] + w, (0.0 + yPos + boundsArray[1]) * -1 + h); 
		
		place.setBounds(new Bounds(LowerRight, UpperLeft));		
	}
	
	public static Shape getaToken(String resourceId)
	{
		// "properties":{"initialmarking":"\"Gerardo\"","quantity":"1","color":""}
		
		StencilType stencil = new StencilType("Token");
		
		Shape token = new Shape(resourceId, stencil);
		
		token.getProperties().put("initialmarking", "");
		token.getProperties().put("quantity", "1");
		token.getProperties().put("color", "#ffffff");
		token.getProperties().put("exttype", "Push");		
		
		return token;
	}
	
	public static void setTokenBounds(Shape token, int i)
	{
		// Default extent
		int w = 12, h = 12;
		
		// Token bounds are relative to the bounds of the place it contains. So the center 
		// of the place is (32;32) because it has a default extent of 64 and 64. But the token
		// shape has also the upperLeft and lowerRight position. So the the upperLeft position 
		// of the token when the token is positioned in the center of the place is not (32;32).
		// Do not forget about the extent of the token itself. So after all the upperLeft position
		// of the token mentioned is (26;26). 26 = 64 / 2 - 12 / 2 = 32 - 6 
		int xMid = 26, yMid = 26; 
		
		// In order to position the token in a circle in the place
		double c = 10.0;
		double yRel = Math.sin((Math.PI / 6) * i) * c;
		double xRel = Math.cos((Math.PI / 6) * i) * c;
		
		Point UpperLeft = new Point(xMid + xRel, yMid + yRel);
		Point LowerRight = new Point(xMid + xRel + w, yMid + yRel + h); 
		
		token.setBounds(new Bounds(LowerRight, UpperLeft));		
	}
	
	private static int stringToInteger(String number)
	{
		return (int) Double.parseDouble(number);
	}
}
