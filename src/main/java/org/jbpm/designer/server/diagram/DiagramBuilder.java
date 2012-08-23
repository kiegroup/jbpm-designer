package org.jbpm.designer.server.diagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Philipp Helper class to build an abstract diagram structure out of
 *         json
 */
public class DiagramBuilder {

    private final static String jsonPattern = "glossary://(.*?)/([\\w\\W]*?)(;;)";

    public static Diagram parseJson(String json) throws JSONException {
        return parseJson(json, false);
    }

    /**
     * Parse the json string to the diagram model, assumes that the json is
     * hierarchical ordered
     * 
     * @param json
     * @return Model with all shapes defined in JSON
     * @throws JSONException
     */
    public static Diagram parseJson(String json, Boolean keepGlossaryLink) throws JSONException {
        JSONObject modelJSON = new JSONObject(json);
        return parseJson(modelJSON, keepGlossaryLink);
    }

    public static Diagram parseJson(JSONObject json) throws JSONException {
        return parseJson(json, false);
    }

    /**
     * do the parsing on an JSONObject, assumes that the json is hierarchical
     * ordered, so all shapes are reachable over child relations
     * 
     * @param json
     *            hierarchical JSON object
     * @return Model with all shapes defined in JSON
     * @throws JSONException
     */
    public static Diagram parseJson(JSONObject json, Boolean keepGlossaryLink) throws JSONException {
        ArrayList<Shape> shapes = new ArrayList<Shape>();
        HashMap<String, JSONObject> flatJSON = flatRessources(json);
        for (String resourceId : flatJSON.keySet()) {
            parseRessource(shapes, flatJSON, resourceId, keepGlossaryLink);

        }
        String id = "canvas";

        if (json.has("resourceId")) {
            id = json.getString("resourceId");
            shapes.remove(new Shape(id));

        }
        ;
        Diagram diagram = new Diagram(id);

        // remove Diagram
        // (Diagram)getShapeWithId(json.getString("resourceId"), shapes);
        parseStencilSet(json, diagram);
        parseSsextensions(json, diagram);
        parseStencil(json, diagram);
        parseProperties(json, diagram, keepGlossaryLink);
        parseChildShapes(shapes, json, diagram);
        parseBounds(json, diagram);
        diagram.setShapes(shapes);
        return diagram;
    }

    /**
     * Parse one resource to a shape object and add it to the shapes array
     * 
     * @param shapes
     * @param flatJSON
     * @param resourceId
     * @throws JSONException
     */
    private static void parseRessource(ArrayList<Shape> shapes, HashMap<String, JSONObject> flatJSON, String resourceId, Boolean keepGlossaryLink)
            throws JSONException {
        JSONObject modelJSON = flatJSON.get(resourceId);
        Shape current = getShapeWithId(modelJSON.getString("resourceId"), shapes);

        parseStencil(modelJSON, current);

        parseProperties(modelJSON, current, keepGlossaryLink);
        parseOutgoings(shapes, modelJSON, current);
        parseChildShapes(shapes, modelJSON, current);
        parseDockers(modelJSON, current);
        parseBounds(modelJSON, current);
        parseTarget(shapes, modelJSON, current);
    }

    /**
     * parse the stencil out of a JSONObject and set it to the current shape
     * 
     * @param modelJSON
     * @param current
     * @throws JSONException
     */
    private static void parseStencil(JSONObject modelJSON, Shape current) throws JSONException {
        // get stencil type
        if (modelJSON.has("stencil")) {
            JSONObject stencil = modelJSON.getJSONObject("stencil");
            // TODO other attributes of stencil
            String stencilString = "";
            if (stencil.has("id")) {
                stencilString = stencil.getString("id");
            }
            current.setStencil(new StencilType(stencilString));
        }
    }

    /**
     * crates a StencilSet object and add it to the current diagram
     * 
     * @param modelJSON
     * @param current
     * @throws JSONException
     */
    private static void parseStencilSet(JSONObject modelJSON, Diagram current) throws JSONException {
        // get stencil type
        if (modelJSON.has("stencilset")) {
            JSONObject object = modelJSON.getJSONObject("stencilset");
            String url = null;
            String namespace = null;

            if (object.has("url"))
                url = object.getString("url");

            if (object.has("namespace"))
                namespace = object.getString("namespace");

            current.setStencilset(new StencilSet(url, namespace));

        }
    }

    /**
     * create a HashMap form the json properties and add it to the shape
     * 
     * @param modelJSON
     * @param current
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    private static void parseProperties(JSONObject modelJSON, Shape current, Boolean keepGlossaryLink) throws JSONException {
        if (modelJSON.has("properties")) {
            JSONObject propsObject = modelJSON.getJSONObject("properties");
            Iterator<String> keys = propsObject.keys();
            Pattern pattern = Pattern.compile(jsonPattern);

            while (keys.hasNext()) {
                StringBuilder result = new StringBuilder();
                int lastIndex = 0;
                String key = keys.next();
                String value = propsObject.getString(key);

                if (!keepGlossaryLink) {
                    Matcher matcher = pattern.matcher(value);
                    while (matcher.find()) {
                        String id = matcher.group(1);
                        current.addGlossaryIds(id);
                        String text = matcher.group(2);
                        result.append(text);
                        lastIndex = matcher.end();
                    }
                    result.append(value.substring(lastIndex));
                    value = result.toString();
                }

                current.putProperty(key, value);
            }
        }
    }

    /**
     * adds all json extension to an diagram
     * 
     * @param modelJSON
     * @param current
     * @throws JSONException
     */
    private static void parseSsextensions(JSONObject modelJSON, Diagram current) throws JSONException {
        if (modelJSON.has("ssextensions")) {
            JSONArray array = modelJSON.getJSONArray("ssextensions");
            for (int i = 0; i < array.length(); i++) {
                current.addSsextension(array.getString(i));
            }
        }
    }

    /**
     * parse the outgoings form an json object and add all shape references to
     * the current shapes, add new shapes to the shape array
     * 
     * @param shapes
     * @param modelJSON
     * @param current
     * @throws JSONException
     */
    private static void parseOutgoings(ArrayList<Shape> shapes, JSONObject modelJSON, Shape current) throws JSONException {
        if (modelJSON.has("outgoing")) {
            ArrayList<Shape> outgoings = new ArrayList<Shape>();
            JSONArray outgoingObject = modelJSON.getJSONArray("outgoing");
            for (int i = 0; i < outgoingObject.length(); i++) {
                Shape out = getShapeWithId(outgoingObject.getJSONObject(i).getString("resourceId"), shapes);
                outgoings.add(out);
                out.addIncoming(current);
            }
            if (outgoings.size() > 0)
                current.setOutgoings(outgoings);
        }
    }

    /**
     * creates a shape list containing all child shapes and set it to the
     * current shape new shape get added to the shape array
     * 
     * @param shapes
     * @param modelJSON
     * @param current
     * @throws JSONException
     */
    private static void parseChildShapes(ArrayList<Shape> shapes, JSONObject modelJSON, Shape current) throws JSONException {
        if (modelJSON.has("childShapes")) {
            ArrayList<Shape> childShapes = new ArrayList<Shape>();

            JSONArray childShapeObject = modelJSON.getJSONArray("childShapes");
            for (int i = 0; i < childShapeObject.length(); i++) {
                childShapes.add(getShapeWithId(childShapeObject.getJSONObject(i).getString("resourceId"), shapes));
            }
            if (childShapes.size() > 0) {
                for (Shape each : childShapes)
                    each.setParent(current);
                current.setChildShapes(childShapes);
            }
            ;

        }
    }

    /**
     * creates a point array of all dockers and add it to the current shape
     * 
     * @param modelJSON
     * @param current
     * @throws JSONException
     */
    private static void parseDockers(JSONObject modelJSON, Shape current) throws JSONException {
        if (modelJSON.has("dockers")) {
            ArrayList<Point> dockers = new ArrayList<Point>();

            JSONArray dockersObject = modelJSON.getJSONArray("dockers");
            for (int i = 0; i < dockersObject.length(); i++) {
                Double x = dockersObject.getJSONObject(i).getDouble("x");
                Double y = dockersObject.getJSONObject(i).getDouble("y");
                dockers.add(new Point(x, y));

            }
            if (dockers.size() > 0)
                current.setDockers(dockers);

        }
    }

    /**
     * creates a bounds object with both point parsed from the json and set it
     * to the current shape
     * 
     * @param modelJSON
     * @param current
     * @throws JSONException
     */
    private static void parseBounds(JSONObject modelJSON, Shape current) throws JSONException {
        if (modelJSON.has("bounds")) {
            JSONObject boundsObject = modelJSON.getJSONObject("bounds");
            current.setBounds(new Bounds(new Point(boundsObject.getJSONObject("lowerRight").getDouble("x"), boundsObject.getJSONObject("lowerRight").getDouble(
                    "y")), new Point(boundsObject.getJSONObject("upperLeft").getDouble("x"), boundsObject.getJSONObject("upperLeft").getDouble("y"))));
        }
    }

    /**
     * parse the target resource and add it to the current shape
     * 
     * @param shapes
     * @param modelJSON
     * @param current
     * @throws JSONException
     */
    private static void parseTarget(ArrayList<Shape> shapes, JSONObject modelJSON, Shape current) throws JSONException {
        if (modelJSON.has("target")) {
            JSONObject targetObject = modelJSON.getJSONObject("target");
            if (targetObject.has("resourceId")) {
                current.setTarget(getShapeWithId(targetObject.getString("resourceId"), shapes));
            }
        }
    }

    /**
     * Gives a new or already instantiated Shape (out of the shapes Array)
     * 
     * @param id
     * @param shapes
     * @return
     */
    private static Shape getShapeWithId(String id, ArrayList<Shape> shapes) {
        Shape result = new Shape(id);
        if (shapes.contains(result)) {
            return shapes.get(shapes.indexOf(result));
        }

        shapes.add(result);
        return result;
    }

    /**
     * Prepare a model JSON for analyze, resolves the hierarchical structure
     * creates a HashMap which contains all resourceIds as keys and for each key
     * the JSONObject, all id are keys of this map
     * 
     * @param object
     * @return a HashMap keys: all ressourceIds values: all child JSONObjects
     * @throws JSONException
     */
    public static HashMap<String, JSONObject> flatRessources(JSONObject object) throws JSONException {
        HashMap<String, JSONObject> result = new HashMap<String, JSONObject>();

        // no cycle in hierarchies!!
        if (object.has("resourceId") && object.has("childShapes")) {
            result.put(object.getString("resourceId"), object);
            JSONArray childShapes = object.getJSONArray("childShapes");
            for (int i = 0; i < childShapes.length(); i++) {
                result.putAll(flatRessources(childShapes.getJSONObject(i)));
            }

        }
        ;

        return result;
    }
}
