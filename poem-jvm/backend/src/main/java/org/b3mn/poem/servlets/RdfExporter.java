package org.b3mn.poem.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.b3mn.poem.Identity;

public class RdfExporter {

	public void doGet(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object, String hostname) throws IOException {
		
		res.setContentType("text/xml");
  		res.setStatus(200);	
  		try {
  			String rdfRepresentation = object.read().getRdf();
    		PrintWriter out = res.getWriter();
			out.write(rdfRepresentation);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object, String hostname) throws IOException {
  		res.setStatus(403);
	   	PrintWriter out = res.getWriter();
	   	out.write("Forbidden!");
	}
    public void doPut(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object, String hostname) throws IOException {
  		res.setStatus(403);
	   	PrintWriter out = res.getWriter();
	   	out.write("Forbidden!");
	}
    public void doDelete(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object, String hostname) throws IOException {
  		res.setStatus(403);
	   	PrintWriter out = res.getWriter();
	   	out.write("Forbidden!");
	}

}
