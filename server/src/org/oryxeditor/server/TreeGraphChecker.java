package org.oryxeditor.server;

import java.net.ResponseCache;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.treeGraph.Diagram;

public class TreeGraphChecker extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
    	try {
    	Diagram diagram = new Diagram();
    	String eRdf = req.getParameter("data");
    	eRdf.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><div>");
    	eRdf.concat("</div>");
    	diagram.deserializeFromeRdf(eRdf);
    	if (diagram.checkSyntax()) {
    		res.getWriter().print("No Errors");
    	} else {
    		res.getWriter().print("Errors");
    	}
    	} catch (Exception e) {
    		try {
    			res.getWriter().print("Exception: "+e.getMessage());
    		} catch (Exception ee) {}
    	}
    }
}
