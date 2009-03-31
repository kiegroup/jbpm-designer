package org.b3mn.poem.util;

import java.io.StringWriter;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonJpdlTransformation {
	
	private static StringWriter jpdlRepresentation;
	private static HashMap<String, JSONObject> processNodes;
	
	public static String toJPDL(JSONObject jsonDoc) {
		
		// Transform process properties
		JSONObject processProperties;
		try {
			processProperties = jsonDoc.getJSONObject("properties");
		
			jpdlRepresentation = new StringWriter();
			jpdlRepresentation.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			jpdlRepresentation.write("<process name=\"");
			jpdlRepresentation.write(processProperties.getString("name")); 
			jpdlRepresentation.write("\" xmlns=\"http://jbpm.org/4/jpdl\">\n");
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
					jpdlRepresentation.write(transformStartEvent(currentElement));
				else if(currentElementID.equals("wait")) 
					jpdlRepresentation.write(transformState(currentElement));
				else if(currentElementID.equals("Exclusive_Databased_Gateway"))
					jpdlRepresentation.write(transformExclusive(currentElement));
				else if(currentElementID.equals("AND_Gateway")) 
					jpdlRepresentation.write(transformParallel(currentElement));
				else if(currentElementID.equals("EndEvent")) 
					jpdlRepresentation.write(transformEndEvent(currentElement));
				else if(currentElementID.equals("EndErrorEvent")) 
					jpdlRepresentation.write(transformEndErrorEvent(currentElement));
				else if(currentElementID.equals("EndCancelEvent")) 
					jpdlRepresentation.write(transformEndCancelEvent(currentElement));
				else if(currentElementID.equals("java"))
					jpdlRepresentation.write(transformJava(currentElement));
				else if(currentElementID.equals("Task"))
					jpdlRepresentation.write(transformTask(currentElement));
				else if(currentElementID.equals("script"))
					jpdlRepresentation.write(transformScript(currentElement));
				else if(currentElementID.equals("esb")) 
					jpdlRepresentation.write(transformEsb(currentElement));
				else if(currentElementID.equals("hql")) 
					jpdlRepresentation.write(transformHql(currentElement));
				else if(currentElementID.equals("sql")) 
					jpdlRepresentation.write(transformSql(currentElement));
			}
		
		} catch (JSONException e) {
			// Do nothing
			e.printStackTrace();
		}
		
		jpdlRepresentation.write("</process>");
		return jpdlRepresentation.toString();
	}
	
	private static String transformBoundsForNode(JSONObject node) {
		// target format g="ulx,uly,width,height"
		StringWriter g = new StringWriter();
			g.write(" g=\"");
		try {
			JSONObject bounds = node.getJSONObject("bounds");
			JSONObject upperLeft = bounds.getJSONObject("upperLeft");
			JSONObject lowerRight = bounds.getJSONObject("lowerRight");
			int ulx = upperLeft.getInt("x");
			int uly = upperLeft.getInt("y");
			int width = lowerRight.getInt("x") - ulx;
			int height = lowerRight.getInt("y") - uly;
			g.write(ulx + ",");
			g.write(uly + ",");
			g.write(width + ",");
			g.write(String.valueOf(height));
			
		} catch (JSONException e) {
			// throw error, stencil without bounds(upperLeft(x,y),lowerRight(x,y)) is invalid
		}
		g.write("\" ");
		return g.toString();
	}
	
	private static String addEdges(JSONArray outgoings) {
		StringWriter edges = new StringWriter();
		try {
			for(int i=0; i < outgoings.length(); i++) {
				
				// set target
				edges.write("    <transition to=\"");
				JSONObject edge = processNodes.get(outgoings.getJSONObject(i).getString("resourceId"));
				JSONObject target = processNodes.get(edge.getJSONObject("target").getString("resourceId"));
				edges.write(target.getJSONObject("properties").getString("name"));
				edges.write("\" ");
				
				edges.write(addAttribute(edge, "name", "name"));
				
				// perhaps add condition
				try {
					String expr = edge.getJSONObject("properties").getString("conditionexpression");
					edges.write(">\n");
					edges.write("      <condition expr=\"" + expr + "\" />\n");
					edges.write("    </transition>\n");
				} catch (JSONException e) {
					edges.write("/>\n");
				}
			}	

		} catch (JSONException e) {
			// do nothing
		}
		return edges.toString();
	}
	
	private static String addAttribute(JSONObject node, String old_name, String new_name) {
		StringWriter transformedAttribute = new StringWriter();
		try {
			String attribute = node.getJSONObject("properties").getString(old_name);
			transformedAttribute.write(" ");
			transformedAttribute.write(new_name);
			transformedAttribute.write("=\"");
			transformedAttribute.write(attribute);
			transformedAttribute.write("\"");
		} catch (JSONException e) {
			// do nothing
		}
		return transformedAttribute.toString();
		
	}
	
	private static String transformStartEvent(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <start");
		transformedNode.write(addAttribute(node, "name", "name"));
		transformedNode.write(transformBoundsForNode(node));
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			if(outgoings.length() == 0) transformedNode.write(" />\n");
			else {
				transformedNode.write(">\n");
				transformedNode.write(addEdges(outgoings));
				transformedNode.write("  </start>\n");
			}
		} catch (JSONException e) {
			transformedNode.write(" />\n");
		}
		return transformedNode.toString();
	}
	
	private static String transformState(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <state");
		transformedNode.write(addAttribute(node, "name", "name"));
		transformedNode.write(transformBoundsForNode(node));

		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			if(outgoings.length() == 0) transformedNode.write(" />\n");
			else {
				transformedNode.write(">\n");
				transformedNode.write(addEdges(outgoings));
				transformedNode.write("  </state>\n");
			}
		} catch (JSONException e) {
			transformedNode.write(" />\n");
		}
		
		return transformedNode.toString();
	}
	
	private static String transformExclusive(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <exclusive");
		
		transformedNode.write(addAttribute(node, "name", "name"));
		transformedNode.write(addAttribute(node, "expr", "expr"));
		transformedNode.write(transformBoundsForNode(node));
		transformedNode.write(" >\n");
		
		// perhaps add handler
		try {
			String handler = node.getJSONObject("properties").getString("handler");
			transformedNode.write("<handler class=\"" + handler + "\" />\n");
		} catch (JSONException e) {
			// Do nothing
		}
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			transformedNode.write(addEdges(outgoings));
			transformedNode.write("  </exclusive>\n");
		} catch (JSONException e) {
			// Do nothing
		}
		
		return transformedNode.toString();
	}
	
	private static String transformParallel(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <fork");
		
		transformedNode.write(addAttribute(node, "name", "name"));
		transformedNode.write(transformBoundsForNode(node));
		transformedNode.write(" >\n");
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			transformedNode.write(addEdges(outgoings));
			transformedNode.write("  </fork>\n");
		} catch (JSONException e) {
			// Do nothing
		}
		
		return transformedNode.toString();
		
	}
	
	private static String transformEndEvent(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <end");
		
		transformedNode.write(addEndEventAttributes(node));
		
		return transformedNode.toString();
		
	}
	
	private static String transformEndErrorEvent(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <end-error");
		
		transformedNode.write(addEndEventAttributes(node));
		
		return transformedNode.toString();
		
	}
	
	private static String transformEndCancelEvent(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <end-cancel");
		
		transformedNode.write(addEndEventAttributes(node));
		
		return transformedNode.toString();
		
	}
	
	private static String addEndEventAttributes(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		
		transformedNode.write(addAttribute(node, "name", "name"));
		transformedNode.write(addAttribute(node, "state", "state"));
		transformedNode.write(addAttribute(node, "ends", "ends"));
		transformedNode.write(transformBoundsForNode(node));
		transformedNode.write(" />\n");
		
		return transformedNode.toString();
	}

	private static String transformJava(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <java");
		transformedNode.write(addAttribute(node, "name", "name"));
		transformedNode.write(addAttribute(node, "class", "class"));
		transformedNode.write(addAttribute(node, "method", "method"));
		transformedNode.write(addAttribute(node, "var", "var"));
		transformedNode.write(transformBoundsForNode(node));
		
		// TODO add fields, args
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			if(outgoings.length() == 0) transformedNode.write(" />\n");
			else {
				transformedNode.write(">\n");
				transformedNode.write(addEdges(outgoings));
				transformedNode.write("  </java>\n");
			}
		} catch (JSONException e) {
			transformedNode.write(" />\n");
		}
		
		return transformedNode.toString();
	}
	
	private static String transformTask(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <task");
		transformedNode.write(addAttribute(node, "name", "name"));
		transformedNode.write(addAttribute(node, "assignee", "assignee"));
		transformedNode.write(transformBoundsForNode(node));
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			if(outgoings.length() == 0) transformedNode.write(" />\n");
			else {
				transformedNode.write(">\n");
				transformedNode.write(addEdges(outgoings));
				transformedNode.write("  </task>\n");
			}
		} catch (JSONException e) {
			transformedNode.write(" />\n");
		}
		
		return transformedNode.toString();
	}

	private static String transformScript(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <script");
		transformedNode.write(addAttribute(node, "name", "name"));
		transformedNode.write(addAttribute(node, "expr", "expr"));
		transformedNode.write(addAttribute(node, "lang", "lang"));
		transformedNode.write(addAttribute(node, "var", "var"));
		transformedNode.write(transformBoundsForNode(node));
		transformedNode.write(">\n");
		
		// perhaps add text
		try {
			String text = node.getJSONObject("properties").getString("text");
			transformedNode.write("    <text>\n      ");
			transformedNode.write(text);
			transformedNode.write("\n    </text>\n");
		} catch (JSONException e) {
			// Do nothing
		}
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			transformedNode.write(addEdges(outgoings));
		} catch (JSONException e) {
			// Do nothing
		}
		transformedNode.write("  </script>\n");
		return transformedNode.toString();
	}

	private static String transformEsb(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <esb");
		transformedNode.write(addAttribute(node, "name", "name"));
		transformedNode.write(addAttribute(node, "category", "category"));
		transformedNode.write(addAttribute(node, "service", "service"));
		transformedNode.write(transformBoundsForNode(node));
		
		// TODO add parts
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			if(outgoings.length() == 0) transformedNode.write(" />\n");
			else {
				transformedNode.write(">\n");
				transformedNode.write(addEdges(outgoings));
				transformedNode.write("  </esb>\n");
			}
		} catch (JSONException e) {
			transformedNode.write(" />\n");
		}
		
		return transformedNode.toString();
	}

	private static String transformHql(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <hql");
		transformedNode.write(addAttribute(node, "name", "name"));
		transformedNode.write(addAttribute(node, "var", "var"));
		transformedNode.write(addAttribute(node, "unique", "unique"));
		transformedNode.write(transformBoundsForNode(node));
		transformedNode.write(">\n");

		// add query
		try {
			String query = node.getJSONObject("properties").getString("query");
			transformedNode.write("    <query>\n      ");
			transformedNode.write(query);
			transformedNode.write("\n    </query>\n");
		} catch (JSONException e) {
			// TODO throw Error - Query is required
		}
		
		// TODO add parameters
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			transformedNode.write(addEdges(outgoings));
		} catch (JSONException e) {
			// Do nothing
		}
		transformedNode.write("  </hql>\n");
		return transformedNode.toString();
	}

	private static String transformSql(JSONObject node) {
		StringWriter transformedNode = new StringWriter();
		transformedNode.write("  <sql");
		transformedNode.write(addAttribute(node, "name", "name"));
		transformedNode.write(addAttribute(node, "var", "var"));
		transformedNode.write(addAttribute(node, "unique", "unique"));
		transformedNode.write(transformBoundsForNode(node));
		transformedNode.write(">\n");

		// add query
		try {
			String query = node.getJSONObject("properties").getString("query");
			transformedNode.write("    <query>\n      ");
			transformedNode.write(query);
			transformedNode.write("\n    </query>\n");
		} catch (JSONException e) {
			// TODO throw Error - Query is required
		}
		
		// TODO add parameters
		
		// add outgoing edge
		try {
			JSONArray outgoings = node.getJSONArray("outgoing");
			transformedNode.write(addEdges(outgoings));
		} catch (JSONException e) {
			// Do nothing
		}
		transformedNode.write("  </sql>\n");
		return transformedNode.toString();
	}

}