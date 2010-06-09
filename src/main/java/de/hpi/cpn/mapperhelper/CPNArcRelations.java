package de.hpi.cpn.mapperhelper;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.cpn.elements.CPNArc;
import de.hpi.cpn.elements.CPNPage;
import de.hpi.cpn.elements.CPNPlace;
import de.hpi.cpn.elements.CPNTransition;

public class CPNArcRelations
{
	private Hashtable<String, String> sourceTable = new Hashtable<String, String>();
	private Hashtable<String, String> targetTable = new Hashtable<String, String>();
	
	// ----------------------------------------- API ------------------------------------
	public void fill(CPNPage tempPage)
	{
		ArrayList<CPNArc> arcs = tempPage.getArcs();
		
		for (int i = 0; i < arcs.size(); i++)
		{
			CPNArc tempArc = arcs.get(i);
			if (tempArc != null)
			{
				String source = null, target = null;
				String orientation = tempArc.getOrientation();
				
				// Does the arc go from a place to a transition?
				if (orientation.equals("PtoT"))
				{
					source = tempArc.getPlaceend().getIdref();
					target = tempArc.getTransend().getIdref();					
				}
				// Does the arc go from a transition to a place?
				else if (orientation.equals("TtoP"))
				{
					source = tempArc.getTransend().getIdref();
					target = tempArc.getPlaceend().getIdref();
				}
				// There is also the possibility that the orientation is in both directions
				// Then I create two new arcs and append them at the end of the array 
				else if (orientation.equals("BOTHDIR"))
				{
					// Copying the arc attributes
					CPNArc arcTtoP = CPNArc.newCPNArc(tempArc);
					arcTtoP.setOrientation("TtoP");
					
					CPNArc arcPtoT = CPNArc.newCPNArc(tempArc);
					// Adding i two times in order to increase the possibility that the id
					// is unique
					arcPtoT.setId(tempArc.getId() + i + i);
					arcPtoT.setOrientation("PtoT");
					
					// Adding the two new arcs
					arcs.add(arcPtoT);
					arcs.add(arcTtoP);

					// Removing the current arc, so the array gets shorter
					// That why i is decreased otherwise an element would be skipped
					arcs.remove(i);
					i--;
					
					continue;
				}
				
				if (source != null && target != null)
				{
					getSourceTable().put(tempArc.getId(), source);
					getTargetTable().put(tempArc.getId(), target);
				}
			}			
		}
		
		// Putting the new arcs array to the pages arc array
		tempPage.setArcs(arcs);
	}
	
	public ArrayList<String> getSourcesFor(String valueToSearchFor)
	{
		Enumeration<String> tempEnumeration = getSourceTable().keys();
		ArrayList<String> result = new ArrayList<String>();	
		
		while (tempEnumeration.hasMoreElements())
		{
			String key = tempEnumeration.nextElement();
			String value = (String) getSourceTable().get(key);
			
			if (value.equals(valueToSearchFor))
				result.add(key);			
		}
		
		return result;
	}
	
	public void fill(JSONObject modelElement) throws JSONException
	{
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		if (childShapes != null)
		{
			for (int i = 0; i < childShapes.length(); i++) 
			{
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				if (CPNTransition.handlesStencil(stencil))
					newSourceEntry(childShape);
				
				else if (CPNPlace.handlesStencil(stencil))
					newSourceEntry(childShape);
				
				else if (CPNArc.handlesStencil(stencil))
					newTargetEntry(childShape);
			}
		}
	}


	
	private void newSourceEntry(JSONObject childShape) throws JSONException
	{
		String childShapeResourceId = childShape.getString("resourceId");
		
		JSONArray outgoing = childShape.optJSONArray("outgoing");
		
		if (outgoing != null)
		{
			// Making a new entry for each outgoing Node
			for (int i = 0; i < outgoing.length(); i++) 
			{
				JSONObject outgoingNode = outgoing.getJSONObject(i);
				String outgoingNodeResourceId = outgoingNode.getString("resourceId");
				
				getSourceTable().put(outgoingNodeResourceId, childShapeResourceId);
			}
		}		
	}
	
	private void newTargetEntry(JSONObject childShape) throws JSONException
	{
		String arcResourceId = childShape.getString("resourceId");		
		String targetResourceId = childShape.getJSONObject("target").getString("resourceId");
		
		getTargetTable().put(arcResourceId, targetResourceId);
	}
	
	public void changePlaceId(String oldId, String newId)
	{
		changeIdvalue(getSourceTable(), oldId, newId);
		changeIdvalue(getTargetTable(), oldId, newId);
	}
	
	public void changeTransitionId(String oldId, String newId)
	{		
		changeIdvalue(getSourceTable(), oldId, newId);
		changeIdvalue(getTargetTable(), oldId, newId);
	}
	
	private void changeIdvalue(Hashtable<String, String> hashtable, String oldId, String newId)
	{
		Enumeration<String> tempEnumeration = hashtable.keys();
		
		// Looking in each key's value
		while (tempEnumeration.hasMoreElements())
		{
			String key = tempEnumeration.nextElement();
			String value = (String) hashtable.get(key);
			
			// If value and oldId are the same then the new should be put into the dictionary
			if (value.equals(oldId))
				hashtable.put(key, newId);
			
		}
	}
	
	public String getTargetValue(String resourceId)
	{
		return (String) getTargetTable().get(resourceId);
	}
	
	public String getSourceValue(String resourceId)
	{
		return (String) getSourceTable().get(resourceId);
	}

	
	// ---------------------------------------- Accessory -----------------------------------

	public void setSourceTable(Hashtable<String, String> sourceTable)
	{
		this.sourceTable = sourceTable;
	}
	public Hashtable<String, String> getSourceTable() 
	{
		return sourceTable;
	}

	public void setTargetTable(Hashtable<String, String> targetTable)
	{
		this.targetTable = targetTable;
	}
	public Hashtable<String, String> getTargetTable()
	{
		return targetTable;
	}
}
