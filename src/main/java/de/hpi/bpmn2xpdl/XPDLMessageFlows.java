package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("MessageFlows")
public class XPDLMessageFlows extends XMLConvertible {

	@Element("MessageFlow")
	protected ArrayList<XPDLMessageFlow> messageFlows;

	public void add(XPDLMessageFlow newFlow) {
		initializeMessageFlows();
		
		getMessageFlows().add(newFlow);
	}
	
	public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
		if (getMessageFlows() != null) {
			for (XPDLThing thing: getMessageFlows()) {
				thing.setResourceIdToObject(mapping);
				mapping.put(thing.getId(), thing);
			}
		}
	}
	
	public ArrayList<XPDLMessageFlow> getMessageFlows() {
		return messageFlows;
	}
	
	public void readJSONmessageflowsunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "messageflowsunknowns");
	}

	public void setMessageFlows(ArrayList<XPDLMessageFlow> flow) {
		this.messageFlows = flow;
	}
	
	public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
		ArrayList<XPDLMessageFlow> flows = getMessageFlows();
		if (flows != null) {
			initializeChildShapes(modelElement);
			
			JSONArray childShapes = modelElement.getJSONArray("childShapes");
			for (int i = 0; i < flows.size(); i++) {
				JSONObject newFlow = new JSONObject();
				flows.get(i).write(newFlow);
				
				childShapes.put(newFlow);
			}
		}
	}
	
	public void writeJSONmessageflowsunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "messageflowsunknowns");
	}
	
	protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
		if (modelElement.optJSONArray("childShapes") == null) {
			modelElement.put("childShapes", new JSONArray());
		}
	}
	
	protected void initializeMessageFlows() {
		if (getMessageFlows() == null) {
			setMessageFlows(new ArrayList<XPDLMessageFlow>());
		}
	}
}
