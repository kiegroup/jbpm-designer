package org.b3mn.poem.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class RdfJsonTransformation {
	
	private final static String[] reservedNodeNames = 
		{ "rdf:type", "type", "mode", "stencilset", "render", "bounds", "dockers", "outgoing", "target", "parent" };
	
	private static JSONObject canvas;
	private static Map<String,JSONObject> objects; // resourceId -> JSONObject
	private static Map<String,String> parents; // resourceId -> parent's resourceId
	
	private static String hostUrl;

	public static JSONObject toJson(Document rdfDoc, String requestUrl) {
		
		canvas = new JSONObject();
		objects = new HashMap<String,JSONObject>();
		parents = new HashMap<String,String>();
		
		hostUrl = requestUrl;
		
		Node root = getRootNode(rdfDoc);
		if(root==null) return canvas;
		
		if(root.hasChildNodes()) {
			for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {

				if (node instanceof Text)
					continue;

				String type = getType(node);
				if (type == null)
					continue;
			
				Node rdfTypeNode = getChild(node, "rdf:type");
				if(rdfTypeNode!=null) {
					String rdfType = getAttributeValue(rdfTypeNode, "rdf:resource");
					if( (rdfType!=null) && rdfType.equals("http://oryx-editor.org/canvas") ) {
						handleCanvas(node);
						continue;
					}
				}
				
				handleShape(node);
				
			}
		}
		
		setupParentRelationships();
		
		return canvas;
	}
	
	private static void handleCanvas(Node n) {
		try {
			canvas.put("resourceId", getResourceId(n));
			canvas.put("childShapes", new JSONArray());
			handleProperties(n, canvas);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private static void handleShape(Node n) {
		try {
			String resourceId = getResourceId(n);
			if(resourceId.length()==0)
				return;
			JSONObject shape = new JSONObject();
			shape.put("resourceId", resourceId);
			objects.put(resourceId, shape);
			parents.put(resourceId, getParentResourceId(n));
			shape.put("outgoing", new JSONArray());
			shape.put("childShapes", new JSONArray());
			handleProperties(n, shape);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private static void setupParentRelationships() {
		for(String resourceId : parents.keySet()) {
			JSONObject child = objects.get(resourceId);
			JSONObject parent = objects.get(parents.get(resourceId));
			if(parent==null) parent = canvas;
			try {
				parent.getJSONArray("childShapes").put(child);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void handleProperties(Node n, JSONObject object) throws JSONException {
		JSONObject properties = new JSONObject();
		object.put("properties", properties);
		if(n.hasChildNodes()) {
			for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling()) {
				if( !isReservedNodeName(child.getNodeName()) )
					properties.put(child.getNodeName(), getContent(child));
				else
					handleReservedNodeName(child, object);
			}
		}
	}
	
	private static void handleReservedNodeName(Node n, JSONObject object) throws JSONException {
		
		if(n.getNodeName().equals("bounds")) {
			object.put("bounds", getBounds(n));
			
		} else if(n.getNodeName().equals("type")) {
			object.put("stencil", (new JSONObject()).put("id", getType(n)));
			
		} else if(n.getNodeName().equals("outgoing")) {
			JSONObject outgoingObject = new JSONObject();
			outgoingObject.put("resourceId", getResourceId(getAttributeValue(n, "rdf:resource")));
			object.getJSONArray("outgoing").put(outgoingObject);
			
		} else if(n.getNodeName().equals("target")) {
			JSONObject target = new JSONObject();
			target.put("resourceId", getResourceId(getAttributeValue(n, "rdf:resource")));
			object.put("target", target);
			
		} else if(n.getNodeName().equals("stencilset")) {
			JSONObject stencilset = new JSONObject();
			String stencilsetUrl = getAttributeValue(n, "rdf:resource");
			stencilset.put("url", hostUrl + stencilsetUrl);
			object.put("stencilset", stencilset);
			
		}
			
	}
	
	private static JSONObject getBounds(Node n) throws JSONException {
		String boundsString = getContent(n);
		if(boundsString==null) return null;
		String[] boundStringArr = boundsString.split(",");
		
		JSONObject upperLeft = new JSONObject();
		upperLeft.put("x", Double.parseDouble(boundStringArr[0]));
		upperLeft.put("y", Double.parseDouble(boundStringArr[1]));
		
		JSONObject lowerRight = new JSONObject();
		lowerRight.put("x", Double.parseDouble(boundStringArr[2]));
		lowerRight.put("y", Double.parseDouble(boundStringArr[3]));
		
		JSONObject bounds = new JSONObject();
		bounds.put("upperLeft", upperLeft);
		bounds.put("lowerRight", lowerRight);
		return bounds;
	}
	
	private static String getParentResourceId(Node n) {
		Node parentNode = getChild(n, "parent");
		if(parentNode==null)
			return null;
		String parentResourceId = getResourceId(getAttributeValue(parentNode, "rdf:resource"));
		if(parentResourceId!=null) return getResourceId(parentResourceId);
		else return null;
	}
	
	private static String getContent(Node node) {
		if (node != null && node.hasChildNodes())
			return node.getFirstChild().getNodeValue();
		return null;
	}

	private static String getAttributeValue(Node node, String attribute) {
		Node item = node.getAttributes().getNamedItem(attribute);
		if (item != null)
			return item.getNodeValue();
		else
			return null;
	}

	private static String getType(Node node) {
		String type = getContent(node);
		if (type != null)
			return type.substring(type.indexOf('#') + 1);
		else
			return null;
	}

	private static String getResourceId(Node node) {
		String attributeValue = getAttributeValue(node, "rdf:about");
		if (attributeValue != null)
			return getResourceId(attributeValue);
		else
			return null;
	}

	private static String getResourceId(String id) {
		if(id==null) return null;
		return id.substring(id.indexOf('#') + 1);
	}

	private static Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().equals(name)) 
				return node;
		return null;
	}

	private static Node getRootNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("rdf:RDF"))
			return null;
		return node;
	}
	
	private static boolean isReservedNodeName(String nodeName) {
		for(String n : reservedNodeNames) {
			if(n.equals(nodeName)) return true;
		}
		return false;
	}

}
