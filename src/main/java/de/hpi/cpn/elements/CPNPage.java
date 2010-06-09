/*
 * This comment deals with the different types of arc storages.
 * 
 * Oryx saves an arc like this. Each arc has an own resourceId. Furthermore it has
 * a key "target" which references the target of the arc. In the case of Colored Petri Nets
 * it can be a transition or a place. But where is the referenceId to the orign of the arc?
 * Somewhere in the JSON you will find a transition or place which has as an outgoingId that arc.
 * So then you know that this transition or place is the origin of the arc.
 * 
 * CPN Tools does it a little bit different. Each arc has also an id. But in difference
 * to Oryx CPN Tools saves the orign and the target of the arc in the same tag arc tag. The arc
 * tag has a referenceId to a transition and to a place. Furthermore it has an attribute
 * called "orientation" with the values "PtoT" (Place to Transition), "TtoP" (transition to Place).
 * 
 * What is the problem?
 * 
 * Using the XMLConvertable let you parse through the whole JSON structure and grap each element
 * one by one. So when I got to the point where I grab the arc element in the JSON I have the
 * target Id. But I don't know nothing about the orign or even the orign Id of the arc.
 * 
 * What is the solution?
 * 
 * The class CPNArcRelations is responsible for saving the orign and the target to each arc.
 * The class consits of two hashtables, a source table and a target table. Because every arc
 * has only one orgin and only one target, I decide that the arc resourceId is the key in both tables.
 * Source and target table are defined like this:
 * 		resourceId of the arc ---> resourceId of a transition or place.
 * 
 * But there is another problem. A resourceId in Oryx is in most cases something like
 * this, "oryx_FCDB338C-3D8D-450F-B27F-B5EA0610F140". But CPN Tools can't do anything with that id.
 * It only understands id like "ID171135".
 * 
 * So my algorithm for translating the arc relation into the CPN Tools format consists of three steps.
 * 1. Prepare CPNArcRelations:
 * Before this class is called the algorithm iterates over the JSON structure and
 * grabs all necessary ids to fill the two hashtables. After that is done, every arc is registered in
 * that table.  
 * 2. Changing the ids in CPNArcRelations:
 * Like I said before CPN Tools can't understand any resourceId Oryx generates. So the algorithm
 * has to create an id for CPN Tools by it's own. But now every mapped element has another id than
 * this one saved in CPNArcRelations. So CPNArcRelations is useless. Therefore the algorithm changes
 * the resourceId of all places and transitions in CPNArcRelations into ids that CPN Tools can
 * understand.  
 * 3. Doing the mapping:
 * When the steps before are done then the mapping of the arcs can begin. Notice that CPNArcRelations
 * is only necessary for the arc mapping. That's the reason why the creation of the arcs starts
 * after every place and transition is mapped.
 *  
 * */

package de.hpi.cpn.elements;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

import de.hpi.cpn.attributes.CPNLittleProperty;
import de.hpi.cpn.attributes.CPNModellingThing;
import de.hpi.cpn.attributes.CPNTextConverter;
import de.hpi.cpn.mapperhelper.CPNArcRelations;
import de.hpi.cpn.mapperhelper.CPNNodePositions;
import de.hpi.cpn.mapperhelper.XMLConvertable;


public class CPNPage extends XMLConvertable
{	
	//	Example
	//	<page id="ID6">
	//    <pageattr name="Tutorial"/>
	//	  <place id="ID23568">
	//			...
	//	  </place>
	//			...
	//	</page>
		
	// Elements which are neither important for the Export nor the Import, but these elements
	// are necessary for XStream otherwise XStream raises an error
	private transient Object auxtag, grouptag, vguidelines, hguidelines;
	
	// These variables are not needed directly for serialization of the XML
	// They are only variables that helps to export / import the files
	private CPNArcRelations arcRelation = new CPNArcRelations();
	private CPNNodePositions nodePositions = new CPNNodePositions();
	
	private String idattri;
	private CPNPageattr pageattr = new CPNPageattr();
	private ArrayList<CPNPlace> places = new ArrayList<CPNPlace>();
	private ArrayList<CPNTransition> transitions = new ArrayList<CPNTransition>();
	private ArrayList<CPNArc> arcs = new ArrayList<CPNArc>();

	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		// In the XML the class is represented as a page - tag
		xstream.alias("page", CPNPage.class);
				
		xstream.useAttributeFor(CPNPage.class, "idattri");
		xstream.aliasAttribute(CPNPage.class, "idattri", "id");
		
		// In order not to display the places / transitions / arcs tag, but simply show
		// the page tags one after another.
		xstream.addImplicitCollection(CPNPage.class, "places", CPNPlace.class);
		xstream.addImplicitCollection(CPNPage.class, "transitions", CPNTransition.class);
		xstream.addImplicitCollection(CPNPage.class, "arcs", CPNArc.class);
		
		// Sometimes XStream cannot translate elements into a XML structure.
		// Therefore exists the possibility to create and register a converter for that element.
		xstream.registerConverter(new CPNTextConverter());
		
		// These instance variables are not needed for the mapping, that's why they are excluded
		xstream.omitField(CPNPage.class, "arcRelation");
		xstream.omitField(CPNPage.class, "nodePositions");		
		
		// Giving all fields a concrete name for the XML
		xstream.aliasField("Aux", CPNPage.class, "auxtag");
		xstream.aliasField("group", CPNPage.class, "grouptag");
		xstream.aliasField("vguidelines", CPNPage.class, "vguidelines");
		xstream.aliasField("hguidelines", CPNPage.class, "hguidelines");
		
		CPNPageattr.registerMapping(xstream);
		CPNModellingThing.registerMapping(xstream);
		CPNPlace.registerMapping(xstream);
		CPNTransition.registerMapping(xstream);
		CPNArc.registerMapping(xstream);
		CPNLittleProperty.registerMapping(xstream);
	}
	
	// ----------------------------------- JSON Reader  ----------------------------------------
	
	public void readJSONpageId(JSONObject modelElement) throws JSONException
	{
		String Id = modelElement.getString("pageId");		
		setId(Id);
	}
	
	public void readJSONproperties(JSONObject modelElement) throws JSONException
	{
		JSONObject properties = new JSONObject(modelElement.getString("properties"));
		this.readJSONtitle(properties);
	}
	
	public void readJSONtitle(JSONObject modelElement) throws JSONException
	{
		String title = modelElement.getString("title");
		
		// In order to give the diagram a title
		if (title.isEmpty())
			title = "Exported CPN";
		
		getPageattr().setName(title);
	}
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException 
	{
		// See the comment in the first line
		
		// Creating a queue 
		JSONArray arcs = new JSONArray();
		
		// Get all childshapes
		JSONArray childShapes = modelElement.optJSONArray("childShapes");		
		
		if (childShapes != null)
		{	
			int i;
			for ( i = 0; i < childShapes.length(); i++) 
			{
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				// Looking whether it is a transition, place or an arc
				if (CPNTransition.handlesStencil(stencil))
					createTransition(childShape,i);
				
				else if (CPNPlace.handlesStencil(stencil))
					createPlace(childShape, i);
				
				// Putting every arc in the queue in order to wait that every
				// place and transition is mapped.
				else if (CPNArc.handlesStencil(stencil))
					arcs.put(childShape);
			}
			
			// After every place and transition is mapped,
			// we can begin to map the arcs in the queue.
			for (i = 0; i < arcs.length(); i++)
				createArc(arcs.getJSONObject(i), i);
				
		}		
	}
	
	// -------------------------------- Helper --------------------------------------------
	
	private void createPlace(JSONObject modelElement, int index) throws JSONException
	{
		// Generating the id for the place.
		// In order to have more space (difference of the ids) between each place element I
		// multiply the index with 10. So the difference of the ids is always 10. 
		// The space is needed because each place has childnodes which have also id. 
		String resourceId = modelElement.getString("resourceId");
		String placeId = "ID" + (3000 + index * 10);
		
		// Correct the Hashtable - Entry for that place 
		getArcRelation().changePlaceId(resourceId, placeId);
		// Load the position of the place into the NodePositions class
		getNodePositions().newNodePosition(placeId, modelElement);
		
		CPNPlace place = new CPNPlace();	
		place.setId(placeId);		
		place.parse(modelElement);
		
		getPlaces().add(place);		
	}

	private void createTransition(JSONObject modelElement, int index) throws JSONException
	{
		// See comments above in createPlace(...)
		String resourceId = modelElement.getString("resourceId");
		String transId = "ID" + (4000 + index * 10);
		
		getArcRelation().changeTransitionId(resourceId, transId);
		getNodePositions().newNodePosition(transId, modelElement);
		
		CPNTransition transition = new CPNTransition();		
		transition.setId(transId);		
		transition.parse(modelElement);
		
		getTransitions().add(transition);		
	}
	
	private void createArc(JSONObject modelElement, int index) throws JSONException
	{
		String resourceId = modelElement.getString("resourceId");
		// Getting source and target of the arc
		String target = getArcRelation().getTargetValue(resourceId);
		String source = getArcRelation().getSourceValue(resourceId);
		String arcId = "ID" + (5000 + index * 10);
		
		String transend, placeend, orientation;
		
		// Setting the orientation of the arc
		if (isTransition(target))
		{
			transend = target;
			placeend = source;
			orientation = "PtoT";
		}
		else
		{
			transend = source;
			placeend = target;
			orientation = "TtoP";
		}
		
		modelElement.put("transend", transend);
		modelElement.put("placeend", placeend);
		modelElement.put("orientation", orientation);		
		
		CPNArc arc = new CPNArc();
		
		arc.setId(arcId);
		arc.parse(modelElement);
		
		arc.positionAnnotation(getNodePositions());
		arc.organizeBendpoints();
		
		getArcs().add(arc);		
	}
	
	public void prepareArcRelations(JSONObject modelElement) throws JSONException
	{
		getArcRelation().fill(modelElement);		
	}
	
	private boolean isTransition(String Id)
	{
		// I defined that every transition starts with the id number 4...
		char letterafterId = Id.charAt(2);
		
		return letterafterId == '4';
	}
	
	// ------------------------------ Accessor ------------------------------------------
    public String getId()
    {
       return this.idattri;
    } 
    public void setId(String _id)
    {
       this.idattri = _id;
    }
    
    public CPNPageattr getPageattr()
    {
       return this.pageattr;
    }
    public void setPageattr(CPNPageattr _pageattr)
    {
       this.pageattr = _pageattr;
    }    
    
    public ArrayList<CPNPlace> getPlaces()
    {
       return this.places;
    }
    public void setPlaces(ArrayList<CPNPlace> _places)
    {
       this.places = _places;
    }
    public void addPlace(CPNPlace _place)
    {
       this.places.add(_place);
    }
    public void removePlace(CPNPlace _place)
    {
       this.places.remove(_place);
    }
    public CPNPlace getPlace( int i)
    {
       return (CPNPlace) this.places.get(i);
    }
    
 
    public ArrayList<CPNArc> getArcs()
    {
       return this.arcs;
    }
    public void setArcs(ArrayList<CPNArc> _arcs)
    {
       this.arcs = _arcs;
    }
    public void addArc(CPNArc _arc)
    {
       this.arcs.add(_arc);
    }
    public void removeArc(CPNArc _arc)
    {
       this.arcs.remove(_arc);
    }
    public CPNArc getArc( int i)
    {
       return (CPNArc)this.arcs.get(i);
    }
    
    public ArrayList<CPNTransition> getTransitions()
    {
       return this.transitions;
    }
    public void setTransitions(ArrayList<CPNTransition> _transs)
    {
       this.transitions = _transs;
    }
    public void addTransition(CPNTransition _trans)
    {
       this.transitions.add(_trans);
    }
    public void removeTransition(CPNTransition _trans)
    {
       this.transitions.remove(_trans);
    }
    public CPNTransition getTrans( int i)
    {
       return (CPNTransition) this.transitions.get(i);
    }

	public void setArcRelation(CPNArcRelations arcRelation) 
	{
		this.arcRelation = arcRelation;
	}
	public CPNArcRelations getArcRelation() 
	{
		return arcRelation;
	}

	public void setNodePositions(CPNNodePositions nodePositions) 
	{
		this.nodePositions = nodePositions;
	}
	public CPNNodePositions getNodePositions() 
	{
		return nodePositions;
	}
}
