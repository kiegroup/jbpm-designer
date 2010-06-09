package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("TaskReceive")
public class XPDLTaskReceive extends XMLConvertible {

	@Attribute("Implementation")
	protected String implementation;
	@Attribute("Instantiate")
	protected String instantiate;
	
	public String getImplementation() {
		return implementation;
	}
	
	public String getInstantiate() {
		return instantiate;
	}
	
	public void readJSONimplementation(JSONObject modelElement) {
		setImplementation(modelElement.optString("implementation"));
	}
	
	public void readJSONinstantiate(JSONObject modelElement) {
		setInstantiate(modelElement.optString("instantiate"));
	}
	
	public void readJSONtaskref(JSONObject modelElement) {
	}
	
	public void readJSONtasktypeunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "tasktypeunknowns");
	}
	
	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	
	public void setInstantiate(String instantiate) {
		this.instantiate = instantiate;
	}
	
	public void writeJSONimplementation(JSONObject modelElement) throws JSONException {
		modelElement.put("implementation", getImplementation());
	}
	
	public void writeJSONinstantiate(JSONObject modelElement) throws JSONException {
		modelElement.put("instantiate", getInstantiate());
	}
	
	public void writeJSONtasktype(JSONObject modelElement) throws JSONException {
		modelElement.put("tasktype", "Receive");
	}
	
	public void writeJSONtasktypeunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "tasktypeunknowns");
	}
}
