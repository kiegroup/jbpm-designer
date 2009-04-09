package org.b3mn.poem.jbpm;

import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class Bounds {
	private int ulx = 0;
	private int uly = 0;
	private int height = 80;
	private int width = 100;
	
	public Bounds(JSONObject bounds) {
		try {
			JSONObject upperLeft = bounds.getJSONObject("upperLeft");
			JSONObject lowerRight = bounds.getJSONObject("lowerRight");
			ulx = upperLeft.getInt("x");
			uly = upperLeft.getInt("y");
			width = lowerRight.getInt("x") - ulx;
			height = lowerRight.getInt("y") - uly;
		} catch (JSONException e) {}
	}
	
	public String toJpdl() {
		StringWriter jpdl = new StringWriter();
		jpdl.write(" g=\"");
		jpdl.write(ulx + ",");
		jpdl.write(uly + ",");
		jpdl.write(width + ",");
		jpdl.write(height + "\"");
		return jpdl.toString();
	}
	
	public JSONObject toJson() throws JSONException {

		JSONObject lowerRight = new JSONObject();
		lowerRight.put("x", ulx + width);
		lowerRight.put("y", uly + height);
		
		JSONObject upperLeft = new JSONObject();
		upperLeft.put("x", ulx);
		upperLeft.put("y", uly);
		
		JSONObject bounds = new JSONObject();
		bounds.put("lowerRight", lowerRight);
		bounds.put("upperLeft", upperLeft);
		
		return bounds;
	}
	
}
