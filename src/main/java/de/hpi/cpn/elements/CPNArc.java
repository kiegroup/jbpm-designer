/* In Oryx JSON the docker node looks like this:
 * "dockers":[{"x":20,"y":20},{"x":259,"y":217},{"x":76,"y":253},{"x":16,"y":16}]
 * The first and the last entry of the JSON Array are not important because they are the dockers
 * attached to the source or target element. The real interesting dockers are these array
 * elements between the first and the last one. These dockers are the bendpoints of the arc.
 *  
 * In CPN Tools every bendpoint has an attribute serial. The serial value is a number which defines
 * the order of the bendpoint. 1 means first bendpoint, 2 means second bandpoint, ...
 * Well that is easy but more interesting and important to know is the fact that the order begins from 
 * the transition element.
 * Example:
 * When you have an arc going from an transition to place and having 3 bendpoints, then the
 * first bendpoint you reach from the transition has the serial 1. The second has the serial 2 and
 * son on. Now imagine the same arc but in the reversed order (going now from a place to a
 * transition). The bendpoints keep at the same position but the serial number does not change. Now
 * the first bendpoint you reach from the place has the serial 3. The second has the serial 2, ... .
 *  
 * */ 

package de.hpi.cpn.elements;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

import de.hpi.cpn.attributes.CPNBendpoint;
import de.hpi.cpn.attributes.CPNLittleProperty;
import de.hpi.cpn.attributes.CPNModellingThing;
import de.hpi.cpn.attributes.CPNProperty;
import de.hpi.cpn.mapperhelper.CPNNodePositions;


public class CPNArc extends CPNModellingThing
{
	
	private String orientation;
	private String order = "1";
	private CPNLittleProperty arrowattr = CPNLittleProperty.arrowattr();
	private CPNLittleProperty transend;
    private CPNLittleProperty placeend;
	private CPNProperty annot = new CPNProperty();
	private ArrayList<CPNBendpoint> bendpoints;	
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("arc", CPNArc.class);
		
		xstream.aliasField("bendpoint", CPNArc.class, "bendpoint");
		xstream.addImplicitCollection(CPNArc.class, "bendpoints", CPNBendpoint.class);
	
		xstream.useAttributeFor(CPNArc.class, "orientation");
		xstream.useAttributeFor(CPNArc.class, "order");
		
		CPNBendpoint.registerMapping(xstream);
	}
	
	
	// ------------------------------------------- JSON Reader --------------------------------
	
	public void readJSONproperties(JSONObject modelElement) throws JSONException
	{
		JSONObject properties = new JSONObject(modelElement.getString("properties"));
		this.parse(properties);
	}
	
	public void readJSONlabel(JSONObject modelElement) throws JSONException
	{
		String annot = modelElement.getString("label");
		
		JSONObject tempJSON = new JSONObject();
		tempJSON.put("label", annot);
		tempJSON.put("id", getId() + "1");
		
		getAnnot().parse(tempJSON);
	}
	
	public void readJSONtransend(JSONObject modelElement) throws JSONException
	{
		String transendIdref = modelElement.getString("transend");
		
		setTransend(CPNLittleProperty.transend(transendIdref));
	}
	
	public void readJSONplaceend(JSONObject modelElement) throws JSONException
	{
		String placeendIdref = modelElement.getString("placeend");		
		setPlaceend(CPNLittleProperty.placeend(placeendIdref));
	}
	
	public void readJSONorientation(JSONObject modelElement) throws JSONException
	{
		String orientation = modelElement.getString("orientation");		
		setOrientation(orientation);
	}
	
	public void readJSONdockers(JSONObject modelElement) throws JSONException
	{
		// Please see the comment in the first line. 
		JSONArray dockers = modelElement.getJSONArray("dockers");
	
		// The first and the last element of the JSONArray are not important for the mapping.
		// So we only concentrate on elements surrounded by them. 
		for (int i = 1; i < dockers.length() - 1; i++)
		{
			// For each docker a new bendpoint is created 
			JSONObject docker = dockers.getJSONObject(i);
			
			// Add import attributes for the bendpoint. For example the number for
			// the attribute serial. Please take a look at the comment in the first line
			// in order to know why serial is so important.
			docker.put("serial", "" + i);			
			docker.put("id", getId() + "2" + i);	
			
			CPNBendpoint tempBendpoint = new CPNBendpoint();			
			tempBendpoint.parse(docker);
			
			getBendpoints().add(tempBendpoint);
		}
	}
	
	public void organizeBendpoints()
	{
		// See the comment in the first line concerning the serial then this might be clearer.
		// As a result of that comment you might understand that if the arc starts with a
		// place then you have to change (reverse) the order of the bendpoint changing their serial.
		int bendPointsCount = getBendpoints().size();
		
		// If there is only one bendpoint then there is nothing to do.
		if (startsAtPlace() && bendPointsCount > 1)
		{			
			for (int i = 0; i < bendPointsCount; i++)
			{
				// Inverting the order
				CPNBendpoint tempBendPoint = getBendpoints().get(i);				
				tempBendPoint.setSerial( "" + (bendPointsCount - i));
			}
		}
	}
	
	private boolean startsAtPlace()
	{
		if (getOrientation().equals("PtoT"))
			return true;
		
		return false;
	}
	
	
	public void positionAnnotation(CPNNodePositions nodePositionTable)
	{
		// The positioning of the annotation has two possibilities.
		// If an arc has no bendpoints then the annotation is placed exactly in the middle
		// between the source and the target. If the are bendpoints then the you get
		// first one and places the annotation right above this bendpoint. 
		if (getBendpoints().isEmpty())
		{
			// Getting the position of the source and the target
			int[] transPosition = nodePositionTable.getPositionOfNode(getTransend().getIdref());
			int[] placePosition = nodePositionTable.getPositionOfNode(getPlaceend().getIdref());
			
			positionAnnotationBetween(transPosition, placePosition);
			
			return;
		}
		else
		{
			// Getting the first bendpoint
			CPNBendpoint tempBendpoint = getBendpoints().get(0);
			int[] bendPointPosition = new int[2];
			
			bendPointPosition[0] = (int) Double.parseDouble(tempBendpoint.getPosattr().getX());
			bendPointPosition[1] = (int) Double.parseDouble(tempBendpoint.getPosattr().getY());
			
			positionAnnotationAt(bendPointPosition); 
		}		
	}
	
	private void positionAnnotationAt(int[] bendPosition)
	{
		// In order to make a little distance between the arc and the annotation
		int defaultAnnotationPaddingFromMiddlePosition = 10;
		
		int[] annotationPosition = bendPosition;
		// Adding the padding
		annotationPosition[1] = annotationPosition[1] + defaultAnnotationPaddingFromMiddlePosition;
		
		positionAnnotation(annotationPosition);
	}
	
	private void positionAnnotationBetween(int[] transPosition, int[] placePosition)
	{
		// In order to make a little distance between the arc and the annotation
		int defaultAnnotationPaddingFromMiddlePosition = 10;
		
		int[] annotationPosition = middlePositionBetweenTransAndPlace(transPosition, placePosition);
		
		// Adding the padding
		annotationPosition[1] = annotationPosition[1] + defaultAnnotationPaddingFromMiddlePosition;
		
		positionAnnotation(annotationPosition);
	}
	
	private void positionAnnotation(int[] annotationPosition)
	{
		getAnnot().getPosattr().setX("" + annotationPosition[0] + ".000000");
		getAnnot().getPosattr().setY("" + annotationPosition[1] + ".000000");		
	}
	
	private int[] middlePositionBetweenTransAndPlace(int[] transPosition, int[] placePosition)
	{		
		int[] middlePosition = new int[2];
		middlePosition[0] = (int) ((transPosition[0] + placePosition[0]) / 2);
		middlePosition[1] = (int) ((transPosition[1] + placePosition[1]) / 2);
		
		return middlePosition;
	}
	
	public static boolean handlesStencil(String stencil)
	{		
		return stencil.equals("Arc");
	}
	
	// Create a copy of the CPNArc
	public static CPNArc newCPNArc(CPNArc arc)
	{
		CPNArc tempArc = new CPNArc();
		
		tempArc.setAnnot(arc.getAnnot());
		tempArc.setArrowattr(arc.getArrowattr());
		tempArc.setFillattr(arc.getFillattr());
		tempArc.setId(arc.getId());
		tempArc.setLineattr(arc.getLineattr());
		tempArc.setOrder(arc.getOrder());
		tempArc.setOrientation(arc.getOrientation());
		tempArc.setPlaceend(arc.getPlaceend());
		tempArc.setPosattr(arc.getPosattr());
		tempArc.setTextattr(arc.getTextattr());
		tempArc.setTransend(arc.getTransend());
		tempArc.setBendpoints(arc.getBendpoints());
		
		return tempArc;
	}
	
	// ---------------------------------------- Accessory ------------------------------
	
	public void setOrientation(String orientation) 
	{
		this.orientation = orientation;
	}
	public String getOrientation() 
	{
		return orientation;
	}

	public void setOrder(String order)
	{
		this.order = order;
	}
	public String getOrder()
	{
		return order;
	}

	public void setArrowattr(CPNLittleProperty arrowattr) 
	{
		this.arrowattr = arrowattr;
	}
	public CPNLittleProperty getArrowattr() 
	{
		return arrowattr;
	}

	public void setTransend(CPNLittleProperty transend) 
	{
		this.transend = transend;
	}
	public CPNLittleProperty getTransend()
	{
		return transend;
	}

	public void setPlaceend(CPNLittleProperty placeend) 
	{
		this.placeend = placeend;
	}
	public CPNLittleProperty getPlaceend() 
	{
		return placeend;
	}

	public void setAnnot(CPNProperty annot) 
	{
		this.annot = annot;
	}
	public CPNProperty getAnnot() 
	{
		return annot;
	}

	public void setBendpoints(ArrayList<CPNBendpoint> bendpoint)
	{
		this.bendpoints = bendpoint;
	}
	public ArrayList<CPNBendpoint> getBendpoints()
	{
		if (this.bendpoints == null)
			this.bendpoints = new ArrayList<CPNBendpoint>();
		return bendpoints;
	}
}