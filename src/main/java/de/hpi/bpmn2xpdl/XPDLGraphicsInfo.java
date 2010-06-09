package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("GraphicsInfo")
public abstract class XPDLGraphicsInfo extends XMLConvertible {
	
	@Attribute("BorderColor")
	protected String borderColor = "#0,0,0";
	@Element("Coordinates")
	protected ArrayList<XPDLCoordinates> coordinates;
	@Attribute("FillColor")
	protected String fillColor;
	@Attribute("ToolId")
	protected String toolId = "Oryx";
	
	public XPDLGraphicsInfo() {
		setCoordinates(new ArrayList<XPDLCoordinates>());
	}
	
	public String getBorderColor() {
		return borderColor;
	}
	
	public ArrayList<XPDLCoordinates> getCoordinates() {
		return coordinates;
	}
	
	public String getFillColor() {
		return fillColor;
	}
	
	public String getToolId() {
		return toolId;
	}
	
	public void readJSONbgcolor(JSONObject modelElement) {
		setFillColor(modelElement.optString("bgcolor"));
	}
	
	public void readJSONbounds(JSONObject modelElement) {		
	}
	
	public void setBorderColor(String color) {
		borderColor = color;
	}
	
	public void setCoordinates(ArrayList<XPDLCoordinates> coordinatesList) {
		coordinates = coordinatesList;
	}
	
	public void setFillColor(String color) {
		fillColor = color;
	}
	
	public void setToolId(String tool) {
		toolId = tool;
	}
	
	public void writeJSONbgcolor(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "bgcolor", getFillColor());
	}
	
	protected XPDLCoordinates createCoordinates(JSONObject modelElement) {
		XPDLCoordinates createdCoordinates = new XPDLCoordinates();
		createdCoordinates.parse(modelElement);
		return createdCoordinates;
	}
	
	protected JSONObject getProperties(JSONObject modelElement) {
		return modelElement.optJSONObject("properties");
	}
	
	protected void initializeCoordinates() {
		if (getCoordinates() == null) {
			setCoordinates(new ArrayList<XPDLCoordinates>());
		}
	}
	
	protected void initializeProperties(JSONObject modelElement) throws JSONException {
		JSONObject properties = modelElement.optJSONObject("properties");
		if (properties == null) {
			JSONObject newProperties = new JSONObject();
			modelElement.put("properties", newProperties);
			properties = newProperties;
		}
	}
	
	protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
		initializeProperties(modelElement);
		
		getProperties(modelElement).put(key, value);
	}
	
	protected void writeEmptyBounds(JSONObject modelElement) throws JSONException {
		JSONObject upperLeft = new JSONObject();
		upperLeft.put("x", 0);
		upperLeft.put("y", 0);
		
		JSONObject lowerRight = new JSONObject();
		lowerRight.put("x", 0);
		lowerRight.put("y", 0);
		
		JSONObject bounds = new JSONObject();
		bounds.put("upperLeft", upperLeft);
		bounds.put("lowerRight", lowerRight);
		
		modelElement.put("bounds", bounds);
	}
}
