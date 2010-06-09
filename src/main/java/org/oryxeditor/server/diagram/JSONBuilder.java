package org.oryxeditor.server.diagram;

/**
 * Copyright (c) 2009
 * Philipp Giese
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Philipp Giese
 *
 */

public class JSONBuilder {
	
	public static String parseModeltoString(Diagram diagram) throws JSONException {
		return parseModel(diagram).toString();
	}
	
	public static JSONObject parseModel(Diagram diagram) throws JSONException {
			
		JSONObject json = new JSONObject();
				
		json.put("resourceId", 		diagram.getResourceId().toString());
		json.put("properties", 		parseProperties(diagram.getProperties()));
		json.put("stencil", 		parseStencil(diagram.getStencilId()));
		json.put("childShapes", 	parseChildShapesRecursive(diagram.getChildShapes()));
		json.put("bounds", 			parseBounds(diagram.getBounds()));
		json.put("stencilset", 		parseStencilSet(diagram.getStencilset()));
		json.put("ssextensions", 	parseStencilSetExtensions(diagram.getSsextensions()));
		
		return json;
	}
	
	/**
	 * Delivers the correct JSON Object for the stencilId
	 * 
	 * @param stencilId
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject parseStencil(String stencilId) throws JSONException {
		JSONObject stencilObject = new JSONObject();
		
		stencilObject.put("id", stencilId.toString());
		
		return stencilObject;
	}
	
	/**
	 * Parses all child Shapes recursively and adds them to the correct
	 * JSON Object
	 * 
	 * @param childShapes
	 * @return
	 * @throws JSONException
	 */
	private static  JSONArray parseChildShapesRecursive(ArrayList<Shape> childShapes) throws JSONException {
		if(childShapes != null) {
			JSONArray childShapesArray = new JSONArray();
			
			for(Shape childShape : childShapes) {
				JSONObject childShapeObject = new JSONObject();				
				
				childShapeObject.put("resourceId", 	childShape.getResourceId().toString());
				childShapeObject.put("properties", 	parseProperties(childShape.getProperties()));
				childShapeObject.put("stencil", 	parseStencil(childShape.getStencilId()));
				childShapeObject.put("childShapes", parseChildShapesRecursive(childShape.getChildShapes()));
				childShapeObject.put("outgoing", 	parseOutgoings(childShape.getOutgoings()));
				childShapeObject.put("bounds", 		parseBounds(childShape.getBounds()));
				childShapeObject.put("dockers", 	parseDockers(childShape.getDockers()));
				
				if(childShape.getTarget() != null) 
					childShapeObject.put("target", parseTarget(childShape.getTarget()));
				
				childShapesArray.put(childShapeObject);
			}
			
			return childShapesArray;
		}
		
		return new JSONArray();
	}
	
	/**
	 * Delivers the correct JSON Object for the target
	 * 
	 * @param target
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject parseTarget(Shape target) throws JSONException {
		JSONObject targetObject = new JSONObject();
		
		targetObject.put("resourceId", target.getResourceId().toString());
		
		return targetObject;
	}
	
	/**
	 * Delivers the correct JSON Object for the dockers
	 * 
	 * @param dockers
	 * @return
	 * @throws JSONException
	 */
	private static JSONArray parseDockers(ArrayList<Point> dockers) throws JSONException {
		if(dockers != null) {
			JSONArray dockersArray = new JSONArray();
			
			for(Point docker : dockers) {
				JSONObject dockerObject = new JSONObject();
				
				dockerObject.put("x", docker.getX().doubleValue());
				dockerObject.put("y", docker.getY().doubleValue());
				
				dockersArray.put(dockerObject);
			}
			
			return dockersArray;
		}
		
		return new JSONArray();
	}
	
	/**
	 * Delivers the correct JSON Object for outgoings
	 * 
	 * @param outgoings
	 * @return
	 * @throws JSONException
	 */
	private static JSONArray parseOutgoings(ArrayList<Shape> outgoings) throws JSONException {
		if(outgoings != null) {
			JSONArray outgoingsArray = new JSONArray();
			
			for(Shape outgoing : outgoings) {
				JSONObject outgoingObject = new JSONObject();
				
				outgoingObject.put("resourceId", outgoing.getResourceId().toString());
				outgoingsArray.put(outgoingObject);
			}
				
			return outgoingsArray;
		}
		
		return new JSONArray();
	}
	
	/**
	 * Delivers the correct JSON Object for properties
	 * 
	 * @param properties
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject parseProperties(HashMap<String, String> properties) throws JSONException {
		if(properties != null) {
			JSONObject propertiesObject = new JSONObject();
			
			for(String key : properties.keySet()) {
				String propertyValue = properties.get(key);
				
				/*if(propertyValue.matches("true|false")) {
				
					propertiesObject.put(key, propertyValue.equals("true"));

				} else if(propertyValue.matches("[0-9]+")) {
					
					Integer value = Integer.parseInt(propertyValue);
					propertiesObject.put(key, value);
				
				} else */ if (propertyValue.startsWith("{") && propertyValue.endsWith("}"))
				{
					propertiesObject.put(key, new JSONObject(propertyValue));
					
				} else {
					propertiesObject.put(key, propertyValue.toString());
				}
			}
			
			return propertiesObject;
		}
		
		return new JSONObject();
	}
	
	/**
	 * Delivers the correct JSON Object for the Stencilset Extensions
	 * 
	 * @param extensions
	 * @return
	 */
	private static JSONArray parseStencilSetExtensions(ArrayList<String> extensions) {
		if(extensions != null) {
			JSONArray extensionsArray = new JSONArray();
			
			for(String extension : extensions) 
				extensionsArray.put(extension.toString());
			
			return extensionsArray;
		}
		
		return new JSONArray();
	}
	
	/**
	 * Delivers the correct JSON Object for the Stencilset
	 * 
	 * @param stencilSet
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject parseStencilSet(StencilSet stencilSet) throws JSONException {
		if(stencilSet != null) {
			JSONObject stencilSetObject = new JSONObject();

			stencilSetObject.put("url", stencilSet.getUrl().toString());
			stencilSetObject.put("namespace", stencilSet.getNamespace().toString());
			
			return stencilSetObject;
		}
		
		return new JSONObject();
	}
	
	/**
	 * Delivers the correct JSON Object for the Bounds
	 * 
	 * @param bounds
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject parseBounds(Bounds bounds) throws JSONException {
		if(bounds != null) {		
			JSONObject boundsObject	= new JSONObject();
			JSONObject lowerRight 	= new JSONObject();
			JSONObject upperLeft  	= new JSONObject();
			
			lowerRight.put("x", bounds.getLowerRight().getX().doubleValue());
			lowerRight.put("y", bounds.getLowerRight().getY().doubleValue());
			
			upperLeft.put("x", bounds.getUpperLeft().getX().doubleValue());
			upperLeft.put("y", bounds.getUpperLeft().getY().doubleValue());
			
			boundsObject.put("lowerRight", lowerRight);
			boundsObject.put("upperLeft", upperLeft);
			
			return boundsObject;
		}
		
		return new JSONObject();
	}
}