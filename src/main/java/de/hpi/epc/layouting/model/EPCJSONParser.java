package de.hpi.epc.layouting.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.layouting.model.LayoutingBoundsImpl;


public class EPCJSONParser {
	
	protected EPCDiagram epc;
	
	public EPCJSONParser() {
		this.epc = new EPCDiagramImpl();
	}

	public EPCDiagram loadEPCFromJSON(JSONObject node) throws JSONException {
		walkChilds(node, null);
		return this.epc;
	}
	
	protected void walkChilds(JSONObject node, EPCElement parent)
			throws JSONException {
		JSONArray shapes = node.getJSONArray("childShapes");
		for (int i = 0; i < shapes.length(); i++) {
			walkShape(shapes.getJSONObject(i), parent);
		}
	}

	private void walkShape(JSONObject node, EPCElement parent)
			throws JSONException {
		EPCElementJSON elem = (EPCElementJSON) epc.getElement(node.getString("resourceId"));
		elem.setElementJSON(node);
		
		JSONObject stencil = node.getJSONObject("stencil");
		elem.setType(EPCType.PREFIX + stencil.getString("id"));
		elem.setParent(parent);

		JSONArray outLinks = node.getJSONArray("outgoing");
		for (int i = 0; i < outLinks.length(); i++) {
			JSONObject link = outLinks.getJSONObject(i);
			EPCElement target = (EPCElement) epc.getElement(link.getString("resourceId"));
			elem.addOutgoingLink(target);
			target.addIncomingLink(elem);
		}
		JSONObject bounds = node.getJSONObject("bounds");
		double x = bounds.getJSONObject("upperLeft").getDouble("x");
		double y = bounds.getJSONObject("upperLeft").getDouble("y");
		double x2 = bounds.getJSONObject("lowerRight").getDouble("x");
		double y2 = bounds.getJSONObject("lowerRight").getDouble("y");
		elem.setGeometry(new LayoutingBoundsImpl(x, y, x2 - x, y2 - y));
		elem.setBoundsJSON(bounds);
		
		JSONArray dockers = node.getJSONArray("dockers");
		elem.getDockers().getPoints().clear();
		for (int i = 0; i < dockers.length(); i++) {
			JSONObject point = dockers.getJSONObject(i);
			elem.getDockers().addPoint(point.getDouble("x"), point.getDouble("y"));
		}
		elem.setDockersJSON(dockers);
		
		walkChilds(node, elem);

	}

}
