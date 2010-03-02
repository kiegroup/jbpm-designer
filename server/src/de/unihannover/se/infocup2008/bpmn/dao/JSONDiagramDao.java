/**
 * Copyright (c) 2009
 * Ingo Kitzmann, Christoph Koenig
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package de.unihannover.se.infocup2008.bpmn.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.layouting.model.LayoutingBoundsImpl;
import de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram;
import de.unihannover.se.infocup2008.bpmn.model.BPMNDiagramJSON;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElement;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElementJSON;
import de.unihannover.se.infocup2008.bpmn.model.BPMNType;

public class JSONDiagramDao {
	public BPMNDiagram getDiagramFromJSON(JSONObject node) throws JSONException {
		BPMNDiagramJSON dia = new BPMNDiagramJSON();
		walkChilds(node, dia, null);
		return dia;
	}

	/**
	 * @param node
	 * @param dia
	 * @throws JSONException
	 */
	private void walkChilds(JSONObject node, BPMNDiagramJSON dia, BPMNElement parent)
			throws JSONException {
		JSONArray shapes = node.getJSONArray("childShapes");
		for (int i = 0; i < shapes.length(); i++) {
			walkShape(shapes.getJSONObject(i), dia, parent);
		}
	}

	private void walkShape(JSONObject node, BPMNDiagramJSON dia, BPMNElement parent)
			throws JSONException {
		BPMNElementJSON elem = (BPMNElementJSON) dia.getElement(node.getString("resourceId"));
		elem.setElementJSON(node);
		JSONObject stencil = node.getJSONObject("stencil");
		elem.setType(BPMNType.PREFIX + stencil.getString("id"));
		elem.setParent(parent);

		JSONArray outLinks = node.getJSONArray("outgoing");
		for (int i = 0; i < outLinks.length(); i++) {
			JSONObject link = outLinks.getJSONObject(i);
			BPMNElementJSON target = (BPMNElementJSON) dia.getElement(link.getString("resourceId"));
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
		
		walkChilds(node, dia, elem);

	}
}
