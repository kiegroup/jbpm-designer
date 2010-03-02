package org.oryxeditor.server;

import java.net.ResponseCache;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.treeGraph.Diagram;
import de.hpi.treeGraph.Shape;

public class TreeGraphChecker extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
    	try {

    	String eRdf = req.getParameter("data");
    	String valideRdf = eRdf.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><div>").concat("</div>");
    	Diagram diagram = new Diagram(valideRdf);  
    	
    	Collection<String> rootNodeIds = diagram.getRootNodeIds();
    	
    	if (rootNodeIds.size() != 1) {
			if (rootNodeIds.size() > 1) {
				res.getWriter().println(rootNodeIds.toString());
			} else {

			}
		}
    	/*
    	if (!diagram.deserializeFromeRdf(valideRdf)) {
    		String json = "[";
    		for (Shape shape : diagram.getShapesWithErrors()){
    			json += "\"" + shape.getId() +"\","; 
    		}
    		json = json.substring(0, json.length() - 1).concat("]");
    		res.getWriter().print(json);
    	} else {
    		// res.getWriter().print("Errors");
    	} */
    	} catch (Exception e) {
    		throw new ServletException(e);
    	}
    }
}
