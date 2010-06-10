package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("ActivitySets")
public class XPDLActivitySets extends XMLConvertible {

	@Element("ActivitySet")
	protected ArrayList<XPDLActivitySet> actvitySets;

	public void add(XPDLActivitySet set) {
		initializeActivitySets();
		
		actvitySets.add(set);
	}
	
	public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
		if (getActvitySets() != null) {
			for (XPDLActivitySet thing : getActvitySets()) {
				thing.setResourceIdToObject(mapping);
				mapping.put(thing.getId(), thing);
				thing.createAndDistributeMapping(mapping);
			}
		}
	}
	
	public ArrayList<XPDLActivitySet> getActvitySets() {
		return actvitySets;
	}
	
	public void readJSONactivitysetsunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "activitysetsunknowns");
	}
	
	public void setActvitySets(ArrayList<XPDLActivitySet> actvitySets) {
		this.actvitySets = actvitySets;
	}
	
	public void write(JSONObject modelElement, ArrayList<XPDLActivity> activities) throws JSONException {
		ArrayList<XPDLActivitySet> unmapped = getActvitySets();
		if (getActvitySets() != null) {
			for (int i = 0; i < getActvitySets().size(); i++) {
				for (int j = 0; j < activities.size(); j++) {
					XPDLActivity searchActivity = activities.get(j);
					if (searchActivity.getBlockActivity() != null) {
						XPDLBlockActivity block = searchActivity.getBlockActivity();
						if (block.getActivitySetId().equals(getActvitySets().get(i).getId())) {
							block.setActivitySet(getActvitySets().get(i));
							unmapped.remove(getActvitySets().get(i));
						}
					}
				}
			}
		}
		writeUnmappedActivitySets(modelElement, unmapped);
	}
	
	public void writeJSONactivitysetsunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "activitysetsunknowns");
	}
	
	public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
		ArrayList<XPDLActivitySet> activitySetsList = getActvitySets();
		if (activitySetsList != null) {
			initializeChildShapes(modelElement);
			
			JSONArray childShapes = modelElement.getJSONArray("childShapes");
			for (int i = 0; i < activitySetsList.size(); i++) {
				JSONObject newActivitySet = new JSONObject();
				activitySetsList.get(i).write(newActivitySet);
				childShapes.put(newActivitySet);
			}
		}
	}
	
	protected void initializeActivitySets() {
		if (getActvitySets() == null) {
			setActvitySets(new ArrayList<XPDLActivitySet>());
		}
	}
		
	protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
		if (modelElement.optJSONArray("childShapes") == null) {
			modelElement.put("childShapes", new JSONArray());
		}
	}
	
	protected void writeUnmappedActivitySets(JSONObject modelElement, ArrayList<XPDLActivitySet> sets) throws JSONException {
		if (sets != null) {
			for (int i = 0; i < sets.size(); i++) {
				sets.get(i).writeUnmapped(modelElement);
			}
		}
	}
}
