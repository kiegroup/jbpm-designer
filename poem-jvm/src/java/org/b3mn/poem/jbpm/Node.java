package org.b3mn.poem.jbpm;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Node {
	protected String uuid;
	protected String name;
	protected Bounds bounds;
	protected List<Transition> outgoings;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Transition> getOutgoings() {
		return outgoings;
	}
	public void setOutgoings(List<Transition> outgoings) {
		this.outgoings = outgoings;
	}
	public Bounds getBounds() {
		return bounds;
	}
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public String toJpdl() throws InvalidModelException {
		return "";
	}
	
	public JSONObject toJson() throws JSONException {
		return new JSONObject();
	}
}
