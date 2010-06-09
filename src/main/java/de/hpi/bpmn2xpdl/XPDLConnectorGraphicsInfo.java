package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("ConnectorGraphicsInfo")
public class XPDLConnectorGraphicsInfo extends XPDLGraphicsInfo {

	@Attribute("FillColor")
	protected String fillColor = "#0,0,0";
	
	protected String source;
	
	public void readJSONbounds(JSONObject modelElement) {
//		initializeCoordinates();
//		JSONObject bounds = modelElement.optJSONObject("bounds");
//		
//		XPDLCoordinates firstAnchor = createCoordinates(bounds.optJSONObject("upperLeft"));
//		XPDLCoordinates secondAnchor = createCoordinates(bounds.optJSONObject("lowerRight"));
//		
//		getCoordinates().add(0,firstAnchor);
//		getCoordinates().add(secondAnchor);
	}
	
	public void readJSONdockers(JSONObject modelElement) throws JSONException {
		JSONArray dockers = modelElement.optJSONArray("dockers");
		String resourceId= modelElement.optString("resourceId");
		
		String target = null;
		if (modelElement.optJSONObject("target") != null) {
			target = modelElement.optJSONObject("target").optString("resourceId");
		}
		if(target!=null){
			JSONObject doc=dockers.optJSONObject(dockers.length() - 1);
			adjustDockerToMidOfShape(doc,getResourceIdToShape().get(target));	
		}
		
		JSONObject source=getSourceForId(resourceId);
		if(source!=null){
			JSONObject doc=dockers.optJSONObject(0);
			adjustDockerToMidOfShape(doc, source);
		}
		
		if (dockers != null) {
			initializeCoordinates();
			
			for (int i = 0; i < dockers.length(); i++) {
				getCoordinates().add(createCoordinates(dockers.optJSONObject(i)));
			}
		}
	}

	public void readJSONgraphicsinfounknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "graphicsinfounknowns");
	}
	
	public void readJSONresourceId(JSONObject modelElement) {
	}
	
	public void readJSONtarget(JSONObject modelElement) {
	}
	
	public void writeJSONbounds(JSONObject modelElement) throws JSONException {
		ArrayList<XPDLCoordinates> coordinatesList = getCoordinates();
		if (coordinatesList != null) {
			if (coordinatesList.size() > 0) {
				XPDLCoordinates firstCoordinates = coordinatesList.get(0);
				
				double minX = firstCoordinates.getXCoordinate();
				double maxX = minX;
				double minY = firstCoordinates.getYCoordinate();
				double maxY = minY;
				
				for(XPDLCoordinates coordinate: coordinatesList) {
					minX = Math.min(minX, coordinate.getXCoordinate());
					maxX = Math.max(maxX, coordinate.getXCoordinate());
					minY = Math.min(minY, coordinate.getYCoordinate());
					maxY = Math.max(maxY, coordinate.getYCoordinate());
				}
				
				JSONObject upperLeft = new JSONObject();
				upperLeft.put("x", minX);
				upperLeft.put("y", minY);
				
				JSONObject lowerRight = new JSONObject();
				lowerRight.put("x", maxX);
				lowerRight.put("y", maxY);
				
				JSONObject bounds = new JSONObject();
				bounds.put("upperLeft", upperLeft);
				bounds.put("lowerRight", lowerRight);
				
				modelElement.put("bounds", bounds);				
			} else {
				writeEmptyBounds(modelElement);
			}
		} else {
			writeEmptyBounds(modelElement);
		}
	}
	
	public void writeJSONdockers(JSONObject modelElement) throws JSONException {
		JSONArray dockers = new JSONArray();
		ArrayList<XPDLCoordinates> coordinatesList = getCoordinates();
		if (coordinatesList != null) {
			for (int i = 0; i < coordinatesList.size(); i++) {
				XPDLCoordinates coordinate = coordinatesList.get(i);
				
				JSONObject docker = new JSONObject();
				docker.put("x", coordinate.getXCoordinate());
				docker.put("y", coordinate.getYCoordinate());
				
				dockers.put(docker);
			}
		}
		modelElement.put("dockers", dockers);		
	}
	
	public void writeJSONgraphicsinfounknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "graphicsinfounknowns");
	}
	
	private void adjustDockerToMidOfShape(JSONObject doc, JSONObject shape) throws JSONException{
		if(doc!=null){
			JSONObject targetShapeBounds=shape.optJSONObject("bounds");
			JSONObject upperLeft= targetShapeBounds.optJSONObject("upperLeft");

			doc.put("x", upperLeft.optDouble("x") + doc.optDouble("x"));
			doc.put("y", upperLeft.optDouble("y") + doc.optDouble("y"));
		}
	}
	
	private JSONObject getSourceForId(String resourceId) throws JSONException{
		for(Entry<String, JSONObject> entry: getResourceIdToShape().entrySet()){
			JSONArray outgoings=entry.getValue().optJSONArray("outgoing");
			if(outgoings!=null){
				for(int i=0; i<outgoings.length();i++){
					if(resourceId.equals(outgoings.getJSONObject(i).optString("resourceId"))) {
						return entry.getValue();
					};
				}
			}
		}
		return null;
	}
}