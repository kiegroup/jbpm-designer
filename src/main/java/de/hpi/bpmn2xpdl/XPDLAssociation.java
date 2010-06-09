package de.hpi.bpmn2xpdl;

import java.util.Arrays;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Association")
public class XPDLAssociation extends XPDLThingConnectorGraphics {

	@Attribute("Direction")
	protected String direction;
	@Attribute("Source")
	protected String source;
	@Attribute("Target")
	protected String target;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
			"Association_Undirected",
			"Association_Unidirectional",
			"Association_Bidirectional"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public String getDirection() {
		return direction;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void readJSONdirection(JSONObject modelElement) {
		setDirection(modelElement.optString("direction"));
	}
	
	public void readJSONresourceId(JSONObject modelElement) throws JSONException {
		super.readJSONresourceId(modelElement);
		findSourceId(getResourceId());
	}
	
	public void readJSONsource(JSONObject modelElement) throws JSONException {
	}
	
	public void readJSONtarget(JSONObject modelElement) throws JSONException {
		JSONObject target = modelElement.getJSONObject("target");
		setTarget(target.optString("resourceId"));
	}
	
	public void setDirection(String directionValue) {
		direction = directionValue;
	}
	
	public void setSource(String sourceValue) {
		source = sourceValue;
	}
	
	public void setTarget(String targetValue) {
		target = targetValue;
	}
	public void writeJSONgraphicsinfos(JSONObject modelElement) throws JSONException {
		super.writeJSONgraphicsinfos(modelElement);
		
		convertFirstAndLastDockerToRelative(getTarget(), getSource(), modelElement);
	}
	public void writeJSONsource(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "target", "");
	}
	
	@Override
	public void writeJSONoutgoing(JSONObject modelElement) throws JSONException {
		super.writeJSONoutgoing(modelElement);
		JSONArray outgoing = modelElement.optJSONArray("outgoing");
		outgoing.put(resourceIdToJSONObject(getTarget()));
		
	}
	public void writeJSONstencil(JSONObject modelElement) throws JSONException {
		
		String directionValue = getDirection();
		if (directionValue.equals("To")) {
			putProperty(modelElement, "direction", directionValue);
			writeStencil(modelElement, "Association_Unidirectional");
		} else if (directionValue.equals("Both")) {
			putProperty(modelElement, "direction", directionValue);
			writeStencil(modelElement, "Association_Bidirectional");
		} else {
			putProperty(modelElement, "direction", "None");
			writeStencil(modelElement, "Association_Undirected");
		}	
	}
	
	public void writeJSONtarget(JSONObject modelElement) throws JSONException {
		JSONObject target = new JSONObject();
		target.put("resourceId", getTarget());
		
		modelElement.put("target", target);
	}
	
	private void findSourceId(String resourceId) throws JSONException{
		for(Entry<String, JSONObject> entry: getResourceIdToShape().entrySet()){
			JSONArray outgoings=entry.getValue().optJSONArray("outgoing");
			if(outgoings!=null){
				for(int i=0; i<outgoings.length();i++){
					String shapeId = outgoings.getJSONObject(i).optString("resourceId");
					if(resourceId.equals(shapeId)) {
						setSource(entry.getKey());
					}
				}
			}
		}
	}
}
