package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;

public class XPDLActivitySet extends XPDLThing {

	@Attribute("AdHoc")
	protected String adHoc;
	@Attribute("AdHocOrdering")
	protected String adHocOrdering;
	@Attribute("AdHocCompletionCondition")
	protected String adHocCompletionCondition;
	
	@Element("Activities")
	protected XPDLActivities activities;
	@Element("Transitions")
	protected XPDLTransitions transitions;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				"Subprocess",
				"CollapsedSubprocess"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
		if (getActivities() != null) {
			getActivities().createAndDistributeMapping(mapping);
		}
		if (getTransitions() != null) {
			getTransitions().createAndDistributeMapping(mapping);
		}
	}

	public String getAdHoc() {
		return adHoc;
	}

	public String getAdHocOrdering() {
		return adHocOrdering;
	}

	public String getAdHocCompletionCondition() {
		return adHocCompletionCondition;
	}

	public XPDLActivities getActivities() {
		return activities;
	}

	public XPDLTransitions getTransitions() {
		return transitions;
	}
	
	public void readJSONadhoccompletioncondition(JSONObject modelElement) {
		setAdHocCompletionCondition(modelElement.optString("adhoccompletioncondition"));
	}
	
	public void readJSONadhocordering(JSONObject modelElement) {
		setAdHocOrdering(modelElement.optString("adhocordering"));
	}
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		if (childShapes != null) {
			for (int i = 0; i<childShapes.length(); i++) {
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				if (XPDLTransition.handlesStencil(stencil)) {
					createTransition(childShape);
				} else if (XPDLActivity.handlesStencil(stencil)) {
					createActivity(childShape);
				}
			}
		}
	}
	
	public void readJSONentry(JSONObject modelElement) {
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(getProperId(modelElement) + "-activitySet");
	}
	
	public void readJSONinputmaps(JSONObject modelElement) {
	}
	
	public void readJSONisadhoc(JSONObject modelElement) {
		setAdHoc(modelElement.optString("isadhoc"));
	}

	public void setAdHoc(String adHoc) {
		this.adHoc = adHoc;
	}

	public void setAdHocOrdering(String adHocOrdering) {
		this.adHocOrdering = adHocOrdering;
	}

	public void setAdHocCompletionCondition(String adHocCompletionCondition) {
		this.adHocCompletionCondition = adHocCompletionCondition;
	}

	public void setActivities(XPDLActivities activities) {
		this.activities = activities;
	}

	public void setTransitions(XPDLTransitions transitions) {
		this.transitions = transitions;
	}
	
	public void writeJSONactivities(JSONObject modelElement) {
		XPDLActivities activitiesList = getActivities();
		if (activitiesList != null) {
			activitiesList.write(modelElement);
		}
	}
	
	public void writeJSONadhoc(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "isadhoc", Boolean.parseBoolean(getAdHoc()));
	}
	
	public void writeJSONadhoccompletioncondition(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "adhoccompletioncondition", getAdHocCompletionCondition());
	}
	
	public void writeJSONadhocordering(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "adhocordering", getAdHocOrdering());
	}
	
	public void writeJSONtransitions(JSONObject modelElement) {
		XPDLTransitions transitionsList = getTransitions();
		if (transitionsList != null) {
			transitionsList.write(modelElement);
		}
	}
	
	public void writeUnmapped(JSONObject modelElement) throws JSONException {
		writeActivities(modelElement);
		writeTransitions(modelElement);
	}	
	
	protected void createActivity(JSONObject modelElement) {
		initializeActivities();
		
		XPDLActivity nextActivity = new XPDLActivity();
		nextActivity.setResourceIdToShape(getResourceIdToShape());
		nextActivity.parse(modelElement);
		getActivities().add(nextActivity);
	}
	
	protected void createTransition(JSONObject modelElement) {
		initializeTransitions();
		
		XPDLTransition nextTranistion = new XPDLTransition();
		nextTranistion.setResourceIdToShape(getResourceIdToShape());
		nextTranistion.parse(modelElement);
		getTransitions().add(nextTranistion);
	}
	
	protected void initializeActivities() {
		if (getActivities() == null) {
			setActivities(new XPDLActivities());
		}
	}

	protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
		if (modelElement.optJSONArray("childShapes") == null) {
			modelElement.put("childShapes", new JSONArray());
		}
	}
	
	protected void initializeTransitions() {
		if (getTransitions() == null) {
			setTransitions(new XPDLTransitions());
		}
	}
	
	protected void writeActivities(JSONObject modelElement) throws JSONException {
		if (getActivities() != null) {
			ArrayList<XPDLActivity> activitiesList = getActivities().getActivities();
			if (activitiesList != null) {
				initializeChildShapes(modelElement);
				JSONArray childShapes = modelElement.getJSONArray("childShapes");
				
				for (int i = 0; i < activitiesList.size(); i++) {
					JSONObject newActivity = new JSONObject();
					XPDLActivity activity = activitiesList.get(i);
					activity.write(newActivity);
					childShapes.put(newActivity);
				}
			}
		}
	}
	
	protected void writeTransitions(JSONObject modelElement) throws JSONException {
		if (getTransitions() != null) {
			ArrayList<XPDLTransition> transitionsList = getTransitions().getTransitions();
			if (transitionsList != null) {
				initializeChildShapes(modelElement);
				JSONArray childShapes = modelElement.getJSONArray("childShapes");
				
				for (int i = 0; i < transitionsList.size(); i++) {
					JSONObject newTransition = new JSONObject();
					XPDLTransition transition = transitionsList.get(i);
					transition.write(newTransition);
					childShapes.put(newTransition);
				}
			}
		}
	}
}
