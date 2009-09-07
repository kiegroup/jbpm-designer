package org.oryxeditor.server.diagram;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Philipp
 *
 *
 */
public class JSONBuilder {
	/**
	 * @param json
	 * @return	Model with all shapes defined in JSON
	 * @throws JSONException
	 */
	public static String parseModeltoString(Diagram model) throws JSONException {
		return parseModel(model).toString();
	}

	/**
	 * @param json	hierarchical JSON object 
	 * @return	Model with all shapes defined in JSON
	 * @throws JSONException
	 */
	public static JSONObject parseModel(Diagram model) throws JSONException {
		JSONObject result= new JSONObject();
		parseShape(result, model);
		
		return result;	}
	
	/**
	 * @param modelJSON
	 * @param current
	 * @throws JSONException 
	 */
	private static void parseShape(JSONObject modelJSON, Shape current) throws JSONException{
		if(current.getResourceId()!=null)
			modelJSON.append("resourceId", current.getResourceId());
		if(current instanceof Diagram){
			parseStencilSet(modelJSON, (Diagram)current);
			parseSsextensions(modelJSON,(Diagram)current);}
		parseStencil(modelJSON, current);
		parseProperties(modelJSON, current);
		parseOutgoings(modelJSON, current);
		parseChildShapes(modelJSON, current);
		parseDockers(modelJSON, current);
		parseBounds(modelJSON, current);
		parseTarget(modelJSON, current);
	}
	/**
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	private static void parseStencil(JSONObject modelJSON, Shape current)
			throws JSONException {
		if(current.getStencil()!=null){
			JSONObject stencil=new JSONObject();
			stencil.append("id", current.getStencil().getId());
			modelJSON.append("stencil",stencil );
		}
	}

	/**
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	private static void parseStencilSet(JSONObject modelJSON, Diagram current)
			throws JSONException {
		if(current.getStencilset()!=null){
			JSONObject stencilSet= new JSONObject();
			if(current.getStencilset().getUrl()!=null)
				stencilSet.append("url", current.getStencilset().getUrl());
			if(current.getStencilset().getNamespace()!=null)
				stencilSet.append("namespace", current.getStencilset().getNamespace());
			modelJSON.append("stencilset", stencilSet);
		}
	}

	/**
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	private static void parseProperties(JSONObject modelJSON, Shape current)
			throws JSONException {
		if(current.getProperties()!=null){
			HashMap<String,String> props=current.getProperties();
			JSONObject propObject = new JSONObject();
			for(String key:props.keySet())
				propObject.append(key, props.get(key));
			modelJSON.append("properties", propObject);
			
		}
	}

	/**
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	private static void parseSsextensions(JSONObject modelJSON, Diagram current)
			throws JSONException {
		if(current.getSsextensions()!=null){
			JSONArray ssexts= new JSONArray();
			for(String sse:current.getSsextensions()){
				ssexts.put(sse);
			}
			modelJSON.append("ssextensions", ssexts);
		}
	}

	/**
	 * @param shapes
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	private static void parseOutgoings(JSONObject modelJSON,
			Shape current) throws JSONException {
		if (current.getOutgoings()!=null) {
			JSONArray outs=new JSONArray();
			for(Shape out:current.getOutgoings()){
				JSONObject outObject=new JSONObject();
				outObject.append("resourceId", out.getResourceId());
				outs.put(outObject);
			}
			modelJSON.append("outgoing", outs);
		}
	}

	/**
	 * @param shapes
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	private static void parseChildShapes(JSONObject modelJSON, Shape current) throws JSONException {
		if(current.getChildShapes()!=null){
			JSONArray childs=new JSONArray();
			for(Shape child: current.getChildShapes()){
				JSONObject childObject=new JSONObject();
				parseShape(childObject, child);
				childs.put(childObject);
			}
			modelJSON.append("childShapes",childs);
		}
	}

	/**
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	private static void parseDockers(JSONObject modelJSON, Shape current)
			throws JSONException {
		if(current.getDockers()!=null){
			JSONArray dockers=new JSONArray();
			for(Point docker:current.getDockers()){
				JSONObject pointObj= new JSONObject();
				pointObj.append("x", docker.getX());
				pointObj.append("y", docker.getY());
				dockers.put(pointObj);
			}
			modelJSON.append("dockers", dockers);
		}
	}

	/**
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	private static void parseBounds(JSONObject modelJSON, Shape current)
			throws JSONException {
		if(current.getBounds()!=null){
			JSONObject bounds=new JSONObject();
			JSONObject lowerPoint= new JSONObject();
			lowerPoint.append("x", current.getBounds().getLowerRight().getX());
			lowerPoint.append("y", current.getBounds().getLowerRight().getY());
			bounds.append("lowerRight", lowerPoint);
			JSONObject upperPoint= new JSONObject();
			upperPoint.append("x", current.getBounds().getUpperLeft().getX());
			upperPoint.append("y", current.getBounds().getUpperLeft().getY());
			bounds.append("upperLeft", upperPoint);
			
			modelJSON.append("bounds", bounds);
		}
	}

	/**
	 * @param modelJSON
	 * @param current
	 * @throws JSONException
	 */
	private static void parseTarget(JSONObject modelJSON,
			Shape current) throws JSONException {
		if(current.getTarget()!=null){
			JSONObject target=new JSONObject();
			target.append("resourceId", current.getTarget().getResourceId());
			modelJSON.append("target", target);
			
		}
	}

}

