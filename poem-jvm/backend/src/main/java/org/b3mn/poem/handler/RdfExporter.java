package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.xml.transform.TransformerException;

import org.b3mn.poem.Identity;

public class RdfExporter extends HandlerBase {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object)  {
		
		res.setContentType("text/xml");
  		res.setStatus(200);	
  		try {
  			PrintWriter out = res.getWriter();
  			String rdfRepresentation = object.read().getRdf(this.getServletContext());    		
			out.write(rdfRepresentation);
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
    }
}
