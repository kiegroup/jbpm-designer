package org.oryxeditor.server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.epc.layouting.EPCEdgeLayouter;
import de.hpi.epc.layouting.EPCTopToBottomGridLayouter;
import de.hpi.epc.layouting.TopologicalSorterEPC;
import de.hpi.epc.layouting.model.EPCDiagram;
import de.hpi.epc.layouting.model.EPCElement;
import de.hpi.epc.layouting.model.EPCJSONParser;
import de.hpi.epc.layouting.model.EPCType;
import de.hpi.layouting.grid.Grid;
import de.hpi.layouting.model.LayoutingBounds;
import de.hpi.layouting.model.LayoutingElement;
import de.hpi.layouting.model.LayoutingDockers.Point;

public class EPCLayoutServlet extends HttpServlet {
	
	private static final long serialVersionUID = -5592867075605609828L;

	private Grid<LayoutingElement> grid;
	
	protected EPCDiagram diagram;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		

		request.setCharacterEncoding("UTF-8");
		String jsonmodel = request.getParameter("data");

		JSONObject jsonModel;
		try {
			jsonModel = new JSONObject(jsonmodel);
			this.diagram = getEPCDiagramFromJSON(jsonModel);
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

		JSONArray json = new JSONArray();

		try {
			for (String id : this.diagram.getElements().keySet()) {
				LayoutingElement element = this.diagram.getElement(id);
				JSONObject obj = new JSONObject();
				obj.put("id", id);

				LayoutingBounds bounds = element.getGeometry();
				String boundsString = bounds.getX() + " " + bounds.getY() + " "
						+ bounds.getX2() + " " + bounds.getY2();
				obj.put("bounds", boundsString);

				if (EPCType.isAConnectingElement(element.getType())) {
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
	}
		
	protected EPCDiagram getEPCDiagramFromJSON(JSONObject jsonModel) throws JSONException {
		EPCJSONParser parser = new EPCJSONParser();
		return  parser.loadEPCFromJSON(jsonModel);
	}

	private JSONArray buildDockersArray(LayoutingElement element){
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


	protected void doLayoutAlgorithm() {
		preprocessHeuristics();

		// Layouting main process
		EPCTopToBottomGridLayouter gridLayouter = layoutProcess();
		grid =  gridLayouter.getGrid();


		// Setting edges
		List<LayoutingElement> flows = diagram.getConnectingElements();
		for (LayoutingElement flow : flows) {
			new EPCEdgeLayouter(this.grid, flow);
		}
	}

	private EPCTopToBottomGridLayouter layoutProcess() {
		// Sorting elements topologicaly
		Queue<LayoutingElement> sortedElements = new TopologicalSorterEPC(diagram,
				null).getSortedElements();

		// Sorted
		int count = 0;
		List<String> sortedIds = new LinkedList<String>();
		for (LayoutingElement element : sortedElements) {
			sortedIds.add(element.getId());
			count++;
		}

		EPCTopToBottomGridLayouter gridLayouter = new EPCTopToBottomGridLayouter(sortedIds);
		gridLayouter.setDiagram(diagram);
		gridLayouter.doLayout();

		return gridLayouter;
	}

	private void preprocessHeuristics() {
		// turn direction of associations to text annotations towards them
		// so that they are right of the elements
		for (LayoutingElement textNote : this.diagram
				.getElementsOfType(EPCType.TextNote)) {
			for (EPCElement edge : textNote.getOutgoingLinks().toArray(
					new EPCElement[0])) {
				EPCElement target = (EPCElement) edge.getOutgoingLinks().get(0);
				// remove old connection
				textNote.removeOutgoingLink(edge);

				target.removeIncomingLink(edge);

				// reconnect properly
				target.addOutgoingLink(textNote);
				
				textNote.addIncomingLink(target);
			}
		}
	}

}
