package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Parameters {
	private List<WireObjectGroup> parameters;

	public Parameters(org.w3c.dom.Node parameters) {

	}

	public Parameters(JSONObject parameters) {
		this.parameters = new ArrayList<WireObjectGroup>();
		try {
			JSONArray items = parameters.getJSONArray("items");
			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i);
				WireObjectGroup newItem = null;
				try {
					if (item.getString("type").toLowerCase().equals("string")) {
						String sName = item.getString("name");
						String sValue = item.getString("value");
						newItem = new WireString(sName, sValue);
					}
					if (item.getString("type").toLowerCase().equals("object")) {
						String oName = item.getString("name");
						newItem = new WireObjectType(oName);
					}
				} catch (JSONException e) {
				}
				if (item != null)
					this.parameters.add(newItem);
			}
		} catch (JSONException e) {
		}
	}

	public List<WireObjectGroup> getParameters() {
		return parameters;
	}

	public void setParameters(List<WireObjectGroup> parameters) {
		this.parameters = parameters;
	}

	public String toJpdl() {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<parameters>\n");

		for (WireObjectGroup o : parameters) {
			jpdl.write(o.toJpdl());
		}

		jpdl.write("</parameters>\n");
		return jpdl.toString();
	}

	public JSONObject toJson() throws JSONException {
		JSONObject parameters = new JSONObject();
		parameters.put("totalCount", 1);
		JSONArray items = new JSONArray();
		JSONObject a = new JSONObject();
		a.put("name", "a");
		a.put("value", "b");
		a.put("type", "string");
		items.put(a);
		parameters.put("items", items);

		return parameters;
	}

}
