package org.b3mn.poem.jbpm;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class JpdlToJson {
	private static Process process;
	
	public static String transform(Document doc) {
		// trigger for transformation
		Node root = getRootNode(doc);
			
		process = new Process(root);
		process.createTransitions();
		
		try {
			return process.toJson();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";

	}

	public static JSONObject createJsonObject(String uuid, JSONObject stencil,
			JSONArray outgoing, JSONObject properties, JSONArray childShapes,
			JSONObject bounds) throws JSONException {
		// create Oryx compliant JSONObject for Node
		JSONObject node = new JSONObject();

		node.put("bounds", bounds);
		node.put("resourceId", uuid);
		node.put("stencil", stencil);
		node.put("outgoing", outgoing);
		node.put("properties", properties);
		node.put("childShapes", childShapes);
		return node;
	}

	private static Node getRootNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("process"))
			return null;
		return node;
	}
	
	public static Bounds getBounds(Node node) {
		String bounds = node.getNodeValue();
		return new Bounds(bounds.split(","));
	}
	
	public static String getAttribute(NamedNodeMap attributes, String name) {
		if(attributes.getNamedItem(name) != null)
			return attributes.getNamedItem(name).getNodeValue();
		return null;
	}
	
	public static Process getProcess() {
		return process;
	}
	
	public static JSONArray getTransitions(List<Transition> outgoings) throws JSONException {
		JSONArray outgoing = new JSONArray();

		for(Transition t : outgoings) {
			JSONObject tt = new JSONObject();
			tt.put("resourceId", t.getUuid());
			outgoing.put(tt);
		}
		return outgoing;
	}
}
