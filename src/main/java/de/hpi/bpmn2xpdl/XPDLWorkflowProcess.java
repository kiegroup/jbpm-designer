package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("WorkflowProcess")
public class XPDLWorkflowProcess extends XPDLThing {
	
	public static String implicitPool = "{" + 
		"\"resourceId\": \"oryx_00000000-0000-0000-0000-000000000000\"," +
		"\"properties\": {" +
			"\"poolid\": \"MainPool\"," +
			"\"name\": \"Main Pool\"," +
			"\"poolcategories\": \"\"," +
			"\"pooldocumentation\": \"\"," +
			"\"participantref\": \"\"," +
			"\"lanes\": \"\"," +
			"\"boundaryvisible\": false," +
			"\"mainpool\": true," +
			"\"processref\": \"\"," +
			"\"processname\": \"MainProcess\"," +
			"\"processtype\": \"None\"," +
			"\"status\": \"None\"," +
			"\"adhoc\": \"\"," +
			"\"adhocordering\": \"Sequential\"," +
			"\"adhoccompletioncondition\": \"\"," +
			"\"suppressjoinfailure\": true," +
			"\"enableinstancecompensation\": \"\"," +
			"\"processcategories\": \"\"," +
			"\"processdocumentation\": \"\"," +
			"\"bgcolor\": \"#ffffff\"," +
		"}," +
		"\"stencil\": {" +
			"\"id\": \"Pool\"" +
		"}," +
		"\"childShapes\": []," +
		"\"outgoing\": []," +
		"\"bounds\": {" +
			"\"lowerRight\": {" +
				"\"x\": 0," +
				"\"y\": 0," +
			"}," +
			"\"upperLeft\": {" +
				"\"x\": 0," +
				"\"y\": 0," +
			"}" +
		"}," +
		"\"dockers\": []" +
	"}";
	protected static String ID_SUFFIX = "-process";
	
	@Attribute("Adhoc")
	protected String adhoc;
	@Attribute("AdhocOrdering")
	protected String adhocOrdering;
	@Attribute("AdhocCompletionCondition")
	protected String adhocCompletionCondition;
	@Attribute("EnableInstanceCompensation")
	protected String enableInstanceCompensation;
	@Attribute("ProcessType")
	protected String processType;
	@Attribute("Status")
	protected String status;
	@Attribute("SuppressJoinFailure")
	protected String suppressJoinFailure;
	
	@Element("ActivitySets")
	protected XPDLActivitySets activitySets;
	@Element("Activities")
	protected XPDLActivities  activities;
	@Element("Transitions")
	protected XPDLTransitions transitions;
	
	public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
		if (getActivities() != null) {
			getActivities().createAndDistributeMapping(mapping);
		}
		if (getTransitions() != null) {
			getTransitions().createAndDistributeMapping(mapping);
		}
		if (getActivitySets() != null) {
			getActivitySets().createAndDistributeMapping(mapping);
		}
	}
	
	public XPDLActivities getActivities() {
		return activities;
	}
	
	public XPDLActivitySets getActivitySets() {
		return activitySets;
	}
	
	public String getAdhoc() {
		return adhoc;
	}
	
	public String getAdhocCompletionCondition() {
		return adhocCompletionCondition;
	}
	
	public String getAdhocOrdering() {
		return adhocOrdering;
	}
	
	public String getAdhocOrderingCondition() {
		return adhocCompletionCondition;
	}
	
	public String getEnableInstanceCompensation() {
		return enableInstanceCompensation;
	}

	public String getProcessType() {
		return processType;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getSuppressJoinFailure() {
		return suppressJoinFailure;
	}
	
	public XPDLTransitions getTransitions() {
		return transitions;
	}
	
	public void readJSONactivitiesunknowns(JSONObject modelElement) throws JSONException {
		initializeActivities();
		
		JSONObject passObject = new JSONObject();
		passObject.put("activitiesunknowns", modelElement.optString("activitiesunknowns"));
		getActivities().parse(passObject);
	}
	
	public void readJSONactivitysetsunknowns(JSONObject modelElement) throws JSONException {
		initializeActivitySets();
		
		JSONObject passObject = new JSONObject();
		passObject.put("activitysetsunknowns", modelElement.optString("activitysetsunknowns"));
		getActivitySets().parse(passObject);
	}
	
	public void readJSONadhoc(JSONObject modelElement) {
		setAdhoc(modelElement.optString("adhoc"));
	}
	
	public void readJSONadhocordering(JSONObject modelElement) {
		setAdhocOrdering(modelElement.optString("adhocordering"));
	}
	
	public void readJSONadhoccompletioncondition(JSONObject modelElement) {
		setAdhocCompletionCondition(modelElement.optString("adhocCompletionCondition"));
	}
	
	public void readJSONbounds(JSONObject modelElement) {
	}
	
	public void readJSONboundaryvisible(JSONObject modelElement) {
	}
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		if (childShapes != null) {
			for (int i = 0; i<childShapes.length(); i++) {
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				if (XPDLActivitySet.handlesStencil(stencil)) {
					createActivitySet(childShape);
				} else if (XPDLTransition.handlesStencil(stencil)) {
					createTransition(childShape);
				} else if (XPDLActivity.handlesStencil(stencil)) {
					createActivity(childShape);
				}
			}
		}
	}
	
	public void readJSONenableinstancecompensation(JSONObject modelElemet) {
		setEnableInstanceCompensation(modelElemet.optString("enableinstancecompensation"));
	}
	
	public void readJSONgraphicsinfounknowns(JSONObject modelElement) {
	}
	
	public void readJSONgraphicsinfosunknowns(JSONObject modelElement) {
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(getProperId(modelElement) + XPDLWorkflowProcess.ID_SUFFIX);
	}
	
	public void readJSONlanesunknowns(JSONObject modelElement) {
	}
	
	public void readJSONmainpool(JSONObject modelElement) {
	}
	
	public void readJSONname(JSONObject modelElement) {
	}
	
	public void readJSONparticipantref(JSONObject modelElement) {
		createExtendedAttribute("participantref", modelElement.optString("participantref"));
	}
	
	public void readJSONpoolcategories(JSONObject modelElement) {
	}
	
	public void readJSONpooldocumentation(JSONObject modelElement) {
	}
	
	public void readJSONpoolid(JSONObject modelElement) {
	}
	
	public void readJSONprocesscategories(JSONObject modelElement) throws JSONException {
		JSONObject categorieObject = new JSONObject();
		categorieObject.put("categories", modelElement.optString("processcategories"));
		categorieObject.put("id", getProperId(modelElement));
		
		parse(categorieObject);
	}
	
	public void readJSONprocessdocumentation(JSONObject modelElement) throws JSONException {
		JSONObject documentationObject = new JSONObject();
		documentationObject.put("documentation", modelElement.optString("processdocumentation"));
		documentationObject.put("id", getProperId(modelElement));
		
		parse(documentationObject);
	}
	
	public void readJSONprocessname(JSONObject modelElement) {
		setName(modelElement.optString("processname"));
	}
	
	public void readJSONprocessref(JSONObject modelElement) {
		createExtendedAttribute("processref", modelElement.optString("processref"));
	}
	
	public void readJSONprocesstype(JSONObject modelElement) {
		setProcessType(modelElement.optString("processtype"));
	}
	
	public void readJSONprocessunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "processunknowns");
	}
	
	public void readJSONresourceId(JSONObject modelElement) {
		setResourceId(modelElement.optString("resourceId"));
		
		setId(getProperId(modelElement) + XPDLWorkflowProcess.ID_SUFFIX);
	}
	
	public void readJSONstatus(JSONObject modelElement) {
		setStatus(modelElement.optString("status"));
	}
	
	public void readJSONsuppressjoinfailure(JSONObject modelElement) {
		setSuppressJoinFailure(modelElement.optString("suppressjoinfailure"));
	}
	
	public void readJSONtransitionsunknowns(JSONObject modelElement) throws JSONException {
		initializeTransitions();
		JSONObject passObject = new JSONObject();
		passObject.put("transitionsunknowns", modelElement.optString("transitionsunknowns"));
		getTransitions().parse(passObject);
	}
	
	public void readJSONunknowns(JSONObject modelElement) {
	}
	
	public void setActivities(XPDLActivities activitiesList) {
		activities = activitiesList;
	}
	
	public void setActivitySets(XPDLActivitySets sets) {
		activitySets = sets;
	}
	
	public void setAdhoc(String adhocValue) {
		adhoc = adhocValue;
	}
	
	public void setAdhocCompletionCondition(String condition) {
		adhocCompletionCondition = condition;
	}
	
	public void setAdhocOrdering(String orderingValue) {
		adhocOrdering = orderingValue;
	}
	
	public void setAdhocOrderingCondition(String conditionValue) {
		adhocCompletionCondition = conditionValue;
	}
	
	public void setEnableInstanceCompensation(String compensation) {
		enableInstanceCompensation = compensation;
	}
	
	public void setProcessType(String typeValue) {
		processType = typeValue;
	}
	
	public void setStatus(String statusValue) {
		status = statusValue;
	}
	
	public void setSuppressJoinFailure(String joinFailure) {
		suppressJoinFailure = joinFailure;
	}
	
	public void setTransitions(XPDLTransitions transitionsValue) {
		transitions = transitionsValue;
	}
	
	public void writeActivitySets(JSONObject modelElement) throws JSONException {
		XPDLActivitySets sets = getActivitySets();
		if (sets != null) {
			if (getActivities() != null) {
				if (getActivities().getActivities() != null) {
					sets.write(modelElement, getActivities().getActivities());
				} else {
					sets.write(modelElement, new ArrayList<XPDLActivity>());
				}
			} else {
				sets.write(modelElement, new ArrayList<XPDLActivity>());
			}
		}
	}
	
	public void writeActivities(JSONObject modelElement) throws JSONException {
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
	
	public void writeChildrenOnly(JSONObject modelElement) throws JSONException {
		writeActivitySets(modelElement);
		writeActivities(modelElement);
		writeTransitions(modelElement);
	}
	
	public void writeTransitions(JSONObject modelElement) throws JSONException {
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
	
	public void writeJSONactivities(JSONObject modelElement) {
		XPDLActivities activitiesList = getActivities();
		if (activitiesList != null) {
			activitiesList.write(modelElement);
		}
	}
	
	public void writeJSONactivitysets(JSONObject modelElement) throws JSONException {
		writeActivitySets(modelElement);
	}
	
	public void writeJSONactivitysetsunknowns(JSONObject modelElement) {
		XPDLActivitySets activitySetsList = getActivitySets();
		if (activitySetsList != null) {
			activitySetsList.write(modelElement);
		}
	}
	
	public void writeJSONadhoc(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "adhoc", getAdhoc());
	}
	
	public void writeJSONadhocordering(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "adhocordering", getAdhocOrdering());
	}
	
	public void writeJSONadhoccompletioncondition(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "adhoccompletioncondition", getAdhocCompletionCondition());
	}
	
	public void writeJSONenableinstancecompensation(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "enableinstancecompensation", getEnableInstanceCompensation());
	}
	
	public void writeJSONprocesstype(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "processtype", getProcessType());
	}
	
	public void writeJSONprocessunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "processunknowns");
	}
	
	public void writeJSONstatus(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "status", getStatus());
	}
	
	public void writeJSONsuppressjoinfailure(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "suppressjoinfailure", getSuppressJoinFailure());
	}
	
	public void writeJSONtransitions(JSONObject modelElement) {
		XPDLTransitions transitionsList = getTransitions();
		if (transitionsList != null) {
			transitionsList.write(modelElement);
		}
	}
	
	public void writeJSONunknowns(JSONObject modelElement) {
	}
	
	protected void createActivity(JSONObject modelElement) {
		initializeActivities();
		
		XPDLActivity nextActivity = new XPDLActivity();
		nextActivity.setResourceIdToShape(getResourceIdToShape());
		nextActivity.parse(modelElement);
		getActivities().add(nextActivity);
	}
	
	protected void createActivitySet(JSONObject modelElement) throws JSONException {
		initializeActivitySets();
		XPDLActivitySet nextSet = new XPDLActivitySet();
		nextSet.setResourceIdToShape(getResourceIdToShape());
		
		JSONObject properties = modelElement.optJSONObject("properties");
		properties.put("resourceId", modelElement.optString("resourceId"));
		
		JSONObject passObject = new JSONObject();
		passObject.put("childShapes", modelElement.optJSONArray("childShapes"));
		passObject.put("adhoccompletioncondition", properties.optString("adhoccompletioncondition"));
		passObject.put("adhocordering", properties.optString("adhocordering"));
		passObject.put("isadhoc", properties.optString("isadhoc"));
		passObject.put("id", getProperId(properties));
		
		nextSet.parse(passObject);
		getActivitySets().add(nextSet);
		
		createActivity(modelElement);
	}
	
	protected void createTransition(JSONObject modelElement) {
		initializeTransitions();
		
		XPDLTransition nextTranistion = new XPDLTransition();
		nextTranistion.setResourceIdToShape(getResourceIdToShape());
		nextTranistion.parse(modelElement);
		getTransitions().add(nextTranistion);
	}
	
	protected String getProperId(JSONObject modelElement) {
		String idValue = modelElement.optString("poolid");
		if (!idValue.equals("")) {
			return idValue;
		}
		return modelElement.optString("resourceId");
	}
	
	protected void initializeActivities() {
		if (getActivities() == null) {
			setActivities(new XPDLActivities());
		}
	}
	
	protected void initializeActivitySets() {
		if (getActivitySets() == null) {
			setActivitySets(new XPDLActivitySets());
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
}
