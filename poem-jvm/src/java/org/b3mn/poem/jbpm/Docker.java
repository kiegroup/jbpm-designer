package org.b3mn.poem.jbpm;

import org.json.JSONException;
import org.json.JSONObject;

public class Docker {
	private int x;
	private int y;
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public Docker(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public JSONObject toJson() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("x", x);
		o.put("y", y);
		return o;
	}
	
	public String toJpdl() {
		return x + "," + y;
	}
	
}
