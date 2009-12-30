/*
 * ATTENTION: Please synch these files with each other!
 * org.oryxeditor.server.RdfJsonTransformation
 * org.b3mn.poem.util.RdfJsonTransformation
 */

package org.b3mn.poem.util;

import java.net.URLDecoder;
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
	{ "rdf:type", "type", "mode", "stencilset", "render", "bounds", "dockers", "outgoing", "target", "parent", "ssextension", "ssnamespace" };
	
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

				// Does this make any sense??? it just don't parse xml docs with no new lines!!!
				//String type = getType(node);
				//if (type == null)
				//	continue;
			
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
			if(!isValidShapeNode(n))
				return;
			
			String resourceId = getResourceId(n);
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
	
	/**
	 * Evaluates if given node can be a shape. E.g., it looks for an resource id, if there are any
	 * children and if it have a node named generatorAgent (which provides only meta info about the rdf) 
	 * @param n Node
	 * @return true if given node seems to be a shape
	 */
	private static boolean isValidShapeNode(Node n){
		if(getResourceId(n).length()==0) //if there isn't a resource id
			return false;
		if(n.hasChildNodes()){
			//if there is the generatorAgent node
			if(n.getFirstChild().getLocalName() != null && n.getFirstChild().getLocalName().equals("generatorAgent"))
				return false;
		} else {
			//if there isn't any child node
			return false;
		}
		return true;
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
				if( !isReservedNodeName(child.getNodeName()) ) {
					String content = getContent(child);
					if(content==null) {
						properties.put(child.getNodeName(), content);
					} else {
						try {
							// try to parse property value to JSON object (for complex properties)
							JSONObject jsonObj = new JSONObject(content);
							properties.put(child.getNodeName(), jsonObj);
						} catch (JSONException e) {
							// non-JSON content
							try {
								properties.put(child.getNodeName(), URLDecoder.decode(content));
							} catch (Exception z) {
								properties.put(child.getNodeName(), content);
							} 
							
						}
					}
				} else {
					handleReservedNodeName(child, object);
				}
			}
		}
	}
	
	private static void handleReservedNodeName(Node n, JSONObject object) throws JSONException {
		
		if(n.getNodeName().equals("bounds")) {
			object.put("bounds", getBounds(n));

		} else if(n.getNodeName().equals("dockers")) {
			object.put("dockers", getDockers(n));
		} else if(n.getNodeName().equals("ssextension")) {
			// There can be several extensions so put them all in an array
			if(!object.has("ssextensions")){
				object.put("ssextensions", new JSONArray());
			}
			JSONArray extensions = (JSONArray)object.get("ssextensions");
			extensions.put(getContent(n));
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
			JSONObject stencilset;
			if(object.has("stencilset")) {
				stencilset = object.getJSONObject("stencilset");
			} else {
				stencilset = new JSONObject();
				object.put("stencilset", stencilset);
			}
			
			String stencilsetUrl = getAttributeValue(n, "rdf:resource");
			if(!stencilsetUrl.startsWith(hostUrl))
				stencilsetUrl = hostUrl + stencilsetUrl;
			
			//hack for reverse proxies:
			stencilsetUrl = stencilsetUrl.substring(stencilsetUrl.lastIndexOf("http://"));
			
			stencilset.put("url", stencilsetUrl);

		} else if(n.getNodeName().equals("ssnamespace")) {
			JSONObject stencilset;
			if(object.has("stencilset")) {
				stencilset = object.getJSONObject("stencilset");
			} else {
				stencilset = new JSONObject();
				object.put("stencilset", stencilset);
			}
			
			String namespace = getAttributeValue(n, "rdf:resource");

			stencilset.put("namespace", namespace);
			
			
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
	
	/**
	 * Transforms a docker representation like {@code '50 60 20 60 #' ]} to {@code [{x: 50, y: 60}, {x: 20, y: 60}]}
	 * @param n The node which contains a docker
	 * @return A json array of dockers
	 * @throws JSONException
	 */
	private static JSONArray getDockers(Node n) throws JSONException {
		String dockersString = getContent(n);
		if(dockersString==null) return null;
		
		String[] dockerPoints = dockersString.replaceAll("#|\\s+"," ").trim().split(" ");
		JSONArray dockers = new JSONArray();
		
		JSONObject currentDocker = null;
		for(int i = 0; i < dockerPoints.length; i++){
			Double point = Double.parseDouble(dockerPoints[i]);
			
			if((i % 2) == 0){ // if it is a x coordinate
				currentDocker = new JSONObject();
				currentDocker.put("x", point);
			} else { // else it is a y coordinate
				currentDocker.put("y", point);
				dockers.put(currentDocker);
			}
		}
		
		return dockers;
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
