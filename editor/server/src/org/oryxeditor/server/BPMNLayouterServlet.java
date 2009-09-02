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
package org.oryxeditor.server;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.layouting.grid.Grid;
import de.hpi.layouting.model.LayoutingBounds;
import de.hpi.layouting.model.LayoutingBoundsImpl;
import de.hpi.layouting.model.LayoutingElement;
import de.hpi.layouting.model.LayoutingDockers.Point;
import de.unihannover.se.infocup2008.bpmn.JsonErdfTransformation;
import de.unihannover.se.infocup2008.bpmn.dao.ERDFDiagramDao;
import de.unihannover.se.infocup2008.bpmn.dao.JSONDiagramDao;
import de.unihannover.se.infocup2008.bpmn.layouter.CatchingIntermediateEventLayouter;
import de.unihannover.se.infocup2008.bpmn.layouter.EdgeLayouter;
import de.unihannover.se.infocup2008.bpmn.layouter.LeftToRightGridLayouter;
import de.unihannover.se.infocup2008.bpmn.layouter.topologicalsort.TopologicalSorterBPMN;
import de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram;
import de.unihannover.se.infocup2008.bpmn.model.BPMNDiagramERDF;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElement;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElementERDF;
import de.unihannover.se.infocup2008.bpmn.model.BPMNType;

public class BPMNLayouterServlet extends HttpServlet {

	protected BPMNDiagram diagram;

	protected ERDFDiagramDao dao;

	private Map<BPMNElement, Grid<BPMNElement>> grids;

	private List<BPMNElement> subprocessOrder;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		grids = new HashMap<BPMNElement, Grid<BPMNElement>>();

		request.setCharacterEncoding("UTF-8");
		// String eRDF = request.getParameter("data");

		String jsonmodel = request.getParameter("data");
		// String eRDF = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<div
		// class=\"processdata\">"
		// + jsonToErdf(jsonmodel) + "\n</div>";
		//
		// // readInput
		// this.dao = new ERDFDiagramDao();
		// this.diagram = dao.getBPMNDiagramFromString(eRDF);

		JSONObject jsonModel;
		try {
			jsonModel = new JSONObject(jsonmodel);
			this.diagram = new JSONDiagramDao().getDiagramFromJSON(jsonModel);
		} catch (JSONException e1) {
			//throw new ServletException(e1);
			response.setStatus(500);
			response.getWriter().print("import of json failed:");
			e1.printStackTrace(response.getWriter());
			return;
		}

		if (this.diagram == null) {
			response.setStatus(500);
			response.getWriter().print("import failed");
			return;
		}

		try {
			doLayoutAlgorithm();
		} catch (Exception e) {
			response.setStatus(500);
			response.getWriter().print("layout failed:");
			e.printStackTrace(response.getWriter());
			return;
		}

		response.setStatus(200);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xhtml");

		if(request.getParameter("output") != null && request.getParameter("output").equals("coordinatesonly")){
			JSONArray json = new JSONArray();
	
			try {
				for (String id : this.diagram.getElements().keySet()) {
					BPMNElement element = (BPMNElement) this.diagram.getElement(id);
					JSONObject obj = new JSONObject();
					obj.put("id", id);
	
					LayoutingBounds bounds = element.getGeometry();
					String boundsString = bounds.getX() + " " + bounds.getY() + " "
							+ bounds.getX2() + " " + bounds.getY2();
					obj.put("bounds", boundsString);
	
					if (BPMNType.isAConnectingElement(element.getType())
							|| BPMNType.isACatchingIntermediateEvent(element
									.getType())) {
						if (element.getDockers() != null) {
							obj.put("dockers", buildDockersArray(element));//buildDokersString(element));
						} else {
							obj.put("dockers", JSONObject.NULL);
						}
					}
	
					json.put(obj);
				}
				json.write(response.getWriter());
			} catch (JSONException e) {
				response.getWriter().print("exception");
			}
		}else{
			try {
				jsonModel.write(response.getWriter());
			} catch (JSONException e) {
				response.setStatus(500);
				response.getWriter().print("json export failed:");
				e.printStackTrace(response.getWriter());
				return;
			}
		}

	}
	
	private JSONArray buildDockersArray(BPMNElement element){
		JSONArray dockers = new JSONArray();
		for (Point p : element.getDockers().getPoints()) {
			JSONObject point = new JSONObject();
			try {
				point.put("x", p.x);
				point.put("y", p.y);
				dockers.put(point);
			} catch (JSONException e) {
			}
		}
		return dockers;
	}

	private String buildDokersString(BPMNElement element) {
		String dockersString = "";
		for (Point p : element.getDockers().getPoints()) {
			dockersString += p.x + " " + p.y + " ";
		}
		dockersString += " # ";
		return dockersString;
	}

	protected void doLayoutAlgorithm() {
		preprocessHeuristics();

		// Layouting subprocesses
		calcLayoutOrder();
		for (BPMNElement subProcess : subprocessOrder) {

			LeftToRightGridLayouter lToRGridLayouter = layoutProcess(subProcess);

			// set bounds
			double subprocessWidth = lToRGridLayouter.getWidthOfDiagramm();
			double subprocessHeight = lToRGridLayouter.getHeightOfDiagramm();
			subProcess.setGeometry(new LayoutingBoundsImpl(0, 0, subprocessWidth,
					subprocessHeight));
			grids.putAll(lToRGridLayouter.getGridParentMap());
		}

		// Layouting main process
		LeftToRightGridLayouter lToRGridLayouter = layoutProcess(null);
		grids.putAll(lToRGridLayouter.getGridParentMap());
		calcLayoutOrder();

		CatchingIntermediateEventLayouter
				.setCatchingIntermediateEvents(diagram);

		// Setting edges
		List<LayoutingElement> flows = diagram.getConnectingElements();
		for (LayoutingElement flow : flows) {
			new EdgeLayouter(this.grids, (BPMNElement)flow);
		}
	}

	private LeftToRightGridLayouter layoutProcess(BPMNElement parent) {
		// Sorting elements topologicaly
		Queue<LayoutingElement> sortedElements = new TopologicalSorterBPMN(diagram,
				parent).getSortedElements();

		// Sorted
		int count = 0;
		List<String> sortedIds = new LinkedList<String>();
		for (LayoutingElement element : sortedElements) {
			sortedIds.add(element.getId());
			count++;
		}

		// Layouting from left to right using grid
		LeftToRightGridLayouter lToRGridLayouter = new LeftToRightGridLayouter(
				sortedIds, parent);
		lToRGridLayouter.setDiagram(diagram);
		lToRGridLayouter.doLayout();

		return lToRGridLayouter;
	}

	/**
	 * calculates the nesting order of lanes and subprocesses
	 */
	private void calcLayoutOrder() {
		subprocessOrder = new LinkedList<BPMNElement>();
		processChilds(null);
		Collections.reverse(subprocessOrder);
	}

	/**
	 * @see TopologicalAlgorithm.calcLayoutOrder()
	 * @param parent
	 *            the element to process the childs from
	 */
	private void processChilds(BPMNElement parent) {
		for (LayoutingElement c : this.diagram.getChildElementsOf(parent)) {
			BPMNElement child = (BPMNElement) c;			
			String childType = child.getType();
			if (childType.equals(BPMNType.Subprocess)) {
				subprocessOrder.add(child);
				processChilds(child);
			}
		}
	}

	private void preprocessHeuristics() {
		// turn direction of associations to text annotations towards them
		// so that they are right of the elements
		for (LayoutingElement textAnnotation : this.diagram
				.getElementsOfType(BPMNType.TextAnnotation)) {
			for (BPMNElement edge : textAnnotation.getOutgoingLinks().toArray(
					new BPMNElement[0])) {
				BPMNElement target = (BPMNElement) edge.getOutgoingLinks().get(0);
				// remove old connection
				textAnnotation.removeOutgoingLink(edge);
				// edge.removeIncomingLink(textAnnotation);

				// edge.removeOutgoingLink(target);
				target.removeIncomingLink(edge);

				// reconnect properly
				target.addOutgoingLink(textAnnotation);
				// edge.addIncomingLink(target);

				// edge.addOutgoingLink(textAnnotation);
				textAnnotation.addIncomingLink(target);
			}
		}
	}

	

	protected static String jsonToErdf(String json) {
		JsonErdfTransformation trans = new JsonErdfTransformation(json);

		return trans.toString();
	}
}
