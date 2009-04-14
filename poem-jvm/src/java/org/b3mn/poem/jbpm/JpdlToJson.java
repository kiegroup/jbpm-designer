package org.b3mn.poem.jbpm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JpdlToJson {
	public static JSONObject createJsonObject(String uuid, JSONObject stencil, JSONArray outgoing,
			JSONObject properties, JSONArray childShapes, JSONObject bounds)
			throws JSONException {
		JSONObject node = new JSONObject();

		node.put("bounds", bounds);
		node.put("resourceId", "oryx_" + uuid);
		node.put("stencil", stencil);
		node.put("outgoing", outgoing);
		node.put("properties", properties);
		node.put("childShapes", childShapes);
		return node;
	}
}
