package org.oryxeditor.server;

import java.net.ResponseCache;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.treeGraph.Diagram;
import de.hpi.treeGraph.Shape;

public class TreeGraphChecker extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
    	try {
    	Diagram diagram = new Diagram();
    	String eRdf = req.getParameter("data");
    	String valideRdf = eRdf.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><div>").concat("</div>");
    	  
    	if (!diagram.deserializeFromeRdf(valideRdf)) {
    		String json = "[";
    		for (Shape shape : diagram.getShapesWithErrors()){
    			json += "\"" + shape.getId() +"\","; 
    		}
    		json = json.substring(0, json.length() - 1).concat("]");
    		res.getWriter().print(json);
    	} else {
    		// res.getWriter().print("Errors");
    	} 
    	} catch (Exception e) {
    		try {
    			res.getWriter().print("Exception: "+e.getMessage());
    		} catch (Exception ee) {}
    	}
    }
}
