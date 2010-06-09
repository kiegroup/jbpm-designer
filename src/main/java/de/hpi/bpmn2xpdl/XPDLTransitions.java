package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Transitions")
public class XPDLTransitions extends XMLConvertible {

	@Element("Transition")
	protected ArrayList<XPDLTransition> transitions;

	public void add(XPDLTransition newTransition) {
		initializeTransitions();
		
		getTransitions().add(newTransition);
	}
	
	public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
		if (getTransitions() != null) {
			for (XPDLThing thing: getTransitions()) {
				thing.setResourceIdToObject(mapping);
				mapping.put(thing.getId(), thing);
			}
		}
	}
	
	public ArrayList<XPDLTransition> getTransitions() {
		return transitions;
	}
	
	public void readJSONtransitionsunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "transitionsunknowns");
	}

	public void setTransitions(ArrayList<XPDLTransition> transitions) {
		this.transitions = transitions;
	}
	
	public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
		ArrayList<XPDLTransition> transitionsList = getTransitions();
		if (transitionsList != null) {
			initializeChildShapes(modelElement);
			
			JSONArray childShapes = modelElement.getJSONArray("childShapes");
			for (int i = 0; i < transitionsList.size(); i++) {
				JSONObject newTransition = new JSONObject();
				transitionsList.get(i).write(newTransition);
				childShapes.put(newTransition);
			}
		}
	}
	
	public void writeJSONtransitionsunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "transitionsunknowns");
	}

	protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
		if (modelElement.optJSONArray("childShapes") == null) {
			modelElement.put("childShapes", new JSONArray());
		}
	}
	
	protected void initializeTransitions() {
		if (getTransitions() == null) {
			setTransitions(new ArrayList<XPDLTransition>());
		}
	}
}
