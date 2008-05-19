package org.b3mn.poem.servlets;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;

public class SvgRenderer {

    private static final long serialVersionUID = 8526319871562210085L;

    public void doGet(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object, String hostname) {
	  	try {
	  		res.setContentType("image/svg+xml");
	  		res.setStatus(200);
	    	PrintWriter out = res.getWriter();
	    	out.write(object.read().getSvg());
	    } catch (Exception e) {
	      	e.printStackTrace();
	    }
    }

}
