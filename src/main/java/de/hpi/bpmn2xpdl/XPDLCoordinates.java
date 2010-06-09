package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Coordinates")
public class XPDLCoordinates extends XMLConvertible {
	
	@Attribute("XCoordinate")
	protected double xCoordinate;
	@Attribute("YCoordinate")
	protected double yCoordinate;

	public double getXCoordinate() {
		return xCoordinate;
	}

	public double getYCoordinate() {
		return yCoordinate;
	}

	public void readJSONx(JSONObject modelElement) {
		setXCoordinate(modelElement.optDouble("x", 0.0));
	}

	public void readJSONy(JSONObject modelElement) {
		setYCoordinate(modelElement.optDouble("y", 0.0));
	}

	public void setXCoordinate(double xValue) {
		xCoordinate = xValue;
	}

	public void setYCoordinate(double yValue) {
		yCoordinate = yValue;
	}
}
