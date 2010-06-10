package de.unihannover.se.infocup2008.bpmn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.layouting.model.LayoutingDockers.Point;

public class BPMNElementJSON extends BPMNAbstractElement implements BPMNElement {

	private JSONObject elementJSON;
	private JSONObject boundsJSON;
	private JSONArray dockersJSON;
	
	public void updateDataModel() {
		try {
			//bounds
			boundsJSON.getJSONObject("upperLeft").put("x", this.getGeometry().getX());
			boundsJSON.getJSONObject("upperLeft").put("y", this.getGeometry().getY());
			boundsJSON.getJSONObject("lowerRight").put("x", this.getGeometry().getX2());
			boundsJSON.getJSONObject("lowerRight").put("y", this.getGeometry().getY2());
			//dockers
			JSONArray dockers = new JSONArray();
			for(Point p : this.getDockers().getPoints()){
				JSONObject point = new JSONObject();
				point.put("x", p.x);
				point.put("y", p.y);
				dockers.put(point);
			}
			elementJSON.put("dockers", dockers);
		} catch (JSONException e) {
			
		}
	}

	public void setBoundsJSON(JSONObject boundsJSON) {
		this.boundsJSON = boundsJSON;
	}

	public JSONObject getBoundsJSON() {
		return boundsJSON;
	}

	public void setDockersJSON(JSONArray dockers) {
		this.dockersJSON = dockers;
	}

	public JSONArray getDockersJSON() {
		return dockersJSON;
	}
	
	public void setElementJSON(JSONObject elementJSON) {
		this.elementJSON = elementJSON;
	}
	
	public JSONObject getElementJSON() {
		return elementJSON;
	}

}
