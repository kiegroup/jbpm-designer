package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Artifacts")
public class XPDLArtifacts extends XMLConvertible {

	@Element("Artifact")
	protected ArrayList<XPDLArtifact> artifacts;

	public void add(XPDLArtifact newArtifact) {
		initializeArtifacts();
		
		getArtifacts().add(newArtifact);
	}
	
	public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
		if (getArtifacts() != null) {
			for (XPDLThing thing: getArtifacts()) {
				thing.setResourceIdToObject(mapping);
				mapping.put(thing.getId(), thing);
			}
		}
	}
	
	public ArrayList<XPDLArtifact> getArtifacts() {
		return artifacts;
	}

	public void readJSONartifactsunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "artifactsunknowns");
	}
	
	public void setArtifacts(ArrayList<XPDLArtifact> artifacts) {
		this.artifacts = artifacts;
	}
	
	public void writeJSONartifactsunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "artifactsunknowns");
	}
	
	public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
		ArrayList<XPDLArtifact> artifactList = getArtifacts();
		if (artifactList != null) {
			initializeChildShapes(modelElement);
			
			JSONArray childShapes = modelElement.getJSONArray("childShapes");
			for (int i = 0; i < artifactList.size(); i++) {
				JSONObject newArtifact = new JSONObject();
				artifactList.get(i).write(newArtifact);
				childShapes.put(newArtifact);
			}
		}
	}
	
	protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
		if (modelElement.optJSONArray("childShapes") == null) {
			modelElement.put("childShapes", new JSONArray());
		}
	}
	
	protected void initializeArtifacts() {
		if (getArtifacts() == null) {
			setArtifacts(new ArrayList<XPDLArtifact>());
		}
	}
}
