package org.b3mn.poem.util;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonJpdlTransformation {
	
	private static String jpdlRepresentation;
	private static HashMap<String, JSONObject> processNodes;
	
	public static String toJPDL(JSONObject jsonDoc) {
		
		// Transform process properties
		JSONObject processProperties;
		try {
			processProperties = jsonDoc.getJSONObject("properties");
		
			jpdlRepresentation = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			jpdlRepresentation += "<process name=\""+ processProperties.getString("name") 
			+ "\" xmlns=\"http://jbpm.org/4/jpdl\">\n";
		} catch (JSONException e) {
			// Do nothing
		}
		
		
		JSONArray processElements;
		processNodes = new HashMap<String, JSONObject>();
		try {
			processElements = jsonDoc.getJSONArray("childShapes");
			
			// Collect all elements for fast access
			for(int i = 0; i < processElements.length(); i++) {
				JSONObject currentElement = processElements.getJSONObject(i); 
				processNodes.put(currentElement.getString("resourceId"), currentElement);
			}
			
			// Transform all process elements
			for(int i = 0; i < processElements.length(); i++) {
				JSONObject currentElement = processElements.getJSONObject(i);
				String currentElementID = currentElement.getJSONObject("stencil").getString("id");
				if(currentElementID.equals("StartEvent")) 
					jpdlRepresentation += transformStartEvent(currentElement);
				else if(currentElementID.equals("wait")) 
					jpdlRepresentation += transformState(currentElement);
				else if(currentElementID.equals("Exclusive_Databased_Gateway"))
					jpdlRepresentation += transformExclusive(currentElement);
				else if(currentElementID.equals("AND_Gateway")) 
					jpdlRepresentation += transformParallel(currentElement);
				else if(currentElementID.equals("EndEvent")) 
					jpdlRepresentation += transformEndEvent(currentElement);
				else if(currentElementID.equals("EndErrorEvent")) 
					jpdlRepresentation += transformEndErrorEvent(currentElement);
				else if(currentElementID.equals("EndCancelEvent")) 
					jpdlRepresentation += transformEndCancelEvent(currentElement);
				else if(currentElementID.equals("java"))
					jpdlRepresentation += transformJava(currentElement);
				else if(currentElementID.equals("Task"))
					jpdlRepresentation += transformTask(currentElement);
				else if(currentElementID.equals("script"))
					jpdlRepresentation += transformScript(currentElement);
				else if(currentElementID.equals("esb")) 
					jpdlRepresentation += transformEsb(currentElement);
				else if(currentElementID.equals("hql")) 
					jpdlRepresentation += transformHql(currentElement);
				else if(currentElementID.equals("sql")) 
					jpdlRepresentation += transformSql(currentElement);
			}
		
		} catch (JSONException e) {
			// Do nothing
			e.printStackTrace();
		}
		
		jpdlRepresentation += "</process>";
		return jpdlRepresentation;
	}
	
	private static String addEdges(JSONArray outgoings) {
		String edges = "";
		try {
			for(int i=0; i < outgoings.length(); i++) {
				
				// set target
				edges += "    <transition to=\"";
				JSONObject edge = processNodes.get(outgoings.getJSONObject(i).getString("resourceId"));
				JSONObject target = processNodes.get(edge.getJSONObject("target").getString("resourceId"));
				edges += target.getJSONObject("properties").getString("name");
				edges += "\" ";
				
				edges += addAttribute(edge, "name", "name");
				
				// perhaps add condition
				try {
					String expr = edge.getJSONObject("properties").getString("conditionexpression");
					edges += ">\n";
					edges += "      <condition expr=\"" + expr + "\" />\n";
					edges += "    </transition>\n";
				} catch (JSONException e) {
					edges += "/>\n";
				}
			}	

		} catch (JSONException e) {
			// do nothing
		}
		return edges;
	}
	
	private static String addAttribute(JSONObject node, String old_name, String new_name) {
		String transformedAttribute = "";
		try {
			String attribute = node.getJSONObject("properties").getString(old_name);
			transformedAttribute += " " + new_name + "=\"" + attribute + "\"";
		} catch (JSONException e) {
			// do nothing
		}
		return transformedAttribute;
		
	}
	
	private static String transformStartEvent(JSONObject node) {
		String transformedNode = "  <start";
		transformedNode += addAttribute(node, "name", "name");

		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			if(outgoings.length() == 0) transformedNode += " />\n";
			else {
				transformedNode += ">\n";
				transformedNode += addEdges(outgoings);
				transformedNode += "  </start>\n";
			}
		} catch (JSONException e) {
			transformedNode += " />\n";
		}
		return transformedNode;
	}
	
	private static String transformState(JSONObject node) {
		String transformedNode = "  <state";
		transformedNode += addAttribute(node, "name", "name");

		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			if(outgoings.length() == 0) transformedNode += " />\n";
			else {
				transformedNode += ">\n";
				transformedNode += addEdges(outgoings);
				transformedNode += "  </state>\n";
			}
		} catch (JSONException e) {
			transformedNode += " />\n";
		}
		
		return transformedNode;
	}
	
	private static String transformExclusive(JSONObject node) {
		String transformedNode = "  <exclusive";
		
		transformedNode += addAttribute(node, "name", "name");
		transformedNode += addAttribute(node, "expr", "expr");
		transformedNode += " >\n";
		
		// perhaps add handler
		try {
			String handler = node.getJSONObject("properties").getString("handler");
			transformedNode += "<handler class=\"" + handler + "\" />\n";
		} catch (JSONException e) {
			// Do nothing
		}
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			transformedNode += addEdges(outgoings);
			transformedNode += "  </exclusive>\n";
		} catch (JSONException e) {
			// Do nothing
		}
		
		return transformedNode;
	}
	
	private static String transformParallel(JSONObject node) {
		String transformedNode = "  <fork";
		
		transformedNode += addAttribute(node, "name", "name");
		transformedNode += " >\n";
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			transformedNode += addEdges(outgoings);
			transformedNode += "  </fork>\n";
		} catch (JSONException e) {
			// Do nothing
		}
		
		return transformedNode;
		
	}
	
	private static String transformEndEvent(JSONObject node) {
		String transformedNode = "  <end";
		
		transformedNode += addEndEventAttributes(node);
		
		return transformedNode;
		
	}
	
	private static String transformEndErrorEvent(JSONObject node) {
		String transformedNode = "  <end-error";
		
		transformedNode += addEndEventAttributes(node);
		
		return transformedNode;
		
	}
	
	private static String transformEndCancelEvent(JSONObject node) {
		String transformedNode = "  <end-cancel";
		
		transformedNode += addEndEventAttributes(node);
		
		return transformedNode;
		
	}
	
	private static String addEndEventAttributes(JSONObject node) {
		String transformedNode = "";
		
		transformedNode += addAttribute(node, "name", "name");
		transformedNode += addAttribute(node, "state", "state");
		transformedNode += addAttribute(node, "ends", "ends");
		transformedNode += " />\n";
		
		return transformedNode;
	}

	private static String transformJava(JSONObject node) {
		String transformedNode = "  <java";
		transformedNode += addAttribute(node, "name", "name");
		transformedNode += addAttribute(node, "class", "class");
		transformedNode += addAttribute(node, "method", "method");
		transformedNode += addAttribute(node, "var", "var");
		
		// TODO add fields, args
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			if(outgoings.length() == 0) transformedNode += " />\n";
			else {
				transformedNode += ">\n";
				transformedNode += addEdges(outgoings);
				transformedNode += "  </java>\n";
			}
		} catch (JSONException e) {
			transformedNode += " />\n";
		}
		
		return transformedNode;
	}
	
	private static String transformTask(JSONObject node) {
		String transformedNode = "  <task";
		transformedNode += addAttribute(node, "name", "name");
		transformedNode += addAttribute(node, "assignee", "assignee");
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			if(outgoings.length() == 0) transformedNode += " />\n";
			else {
				transformedNode += ">\n";
				transformedNode += addEdges(outgoings);
				transformedNode += "  </task>\n";
			}
		} catch (JSONException e) {
			transformedNode += " />\n";
		}
		
		return transformedNode;
	}

	private static String transformScript(JSONObject node) {
		String transformedNode = "  <script";
		transformedNode += addAttribute(node, "name", "name");
		transformedNode += addAttribute(node, "expr", "expr");
		transformedNode += addAttribute(node, "lang", "lang");
		transformedNode += addAttribute(node, "var", "var");
		transformedNode += ">\n";
		
		// perhaps add text
		try {
			String text = node.getJSONObject("properties").getString("text");
			transformedNode += "    <text>\n      ";
			transformedNode += text;
			transformedNode += "\n    </text>\n";
		} catch (JSONException e) {
			// Do nothing
		}
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			transformedNode += addEdges(outgoings);
		} catch (JSONException e) {
			// Do nothing
		}
		transformedNode += "  </script>\n";
		return transformedNode;
	}

	private static String transformEsb(JSONObject node) {
		String transformedNode = "  <esb";
		transformedNode += addAttribute(node, "name", "name");
		transformedNode += addAttribute(node, "category", "category");
		transformedNode += addAttribute(node, "service", "service");
		
		// TODO add parts
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			if(outgoings.length() == 0) transformedNode += " />\n";
			else {
				transformedNode += ">\n";
				transformedNode += addEdges(outgoings);
				transformedNode += "  </esb>\n";
			}
		} catch (JSONException e) {
			transformedNode += " />\n";
		}
		
		return transformedNode;
	}

	private static String transformHql(JSONObject node) {
		String transformedNode = "  <hql";
		transformedNode += addAttribute(node, "name", "name");
		transformedNode += addAttribute(node, "var", "var");
		transformedNode += addAttribute(node, "unique", "unique");
		transformedNode += ">\n";

		// add query
		try {
			String query = node.getJSONObject("properties").getString("query");
			transformedNode += "    <query>\n      ";
			transformedNode += query;
			transformedNode += "\n    </query>\n";
		} catch (JSONException e) {
			// TODO throw Error - Query is required
		}
		
		// TODO add parameters
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			transformedNode += addEdges(outgoings);
		} catch (JSONException e) {
			// Do nothing
		}
		transformedNode += "  </hql>\n";
		return transformedNode;
	}

	private static String transformSql(JSONObject node) {
		String transformedNode = "  <sql";
		transformedNode += addAttribute(node, "name", "name");
		transformedNode += addAttribute(node, "var", "var");
		transformedNode += addAttribute(node, "unique", "unique");
		transformedNode += ">\n";

		// add query
		try {
			String query = node.getJSONObject("properties").getString("query");
			transformedNode += "    <query>\n      ";
			transformedNode += query;
			transformedNode += "\n    </query>\n";
		} catch (JSONException e) {
			// TODO throw Error - Query is required
		}
		
		// TODO add parameters
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			transformedNode += addEdges(outgoings);
		} catch (JSONException e) {
			// Do nothing
		}
		transformedNode += "  </sql>\n";
		return transformedNode;
	}



}
