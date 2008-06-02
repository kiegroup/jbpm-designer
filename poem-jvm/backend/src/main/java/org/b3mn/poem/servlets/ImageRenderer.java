package org.b3mn.poem.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderException;

import org.b3mn.poem.Identity;

public class ImageRenderer {
	
    public void doGet(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object, String hostname) throws IOException {
    	setResponseHeaders(res);
    	try {
			transcode(object.read().getSvg(), res.getOutputStream());
		} catch (TranscoderException e) {
			e.printStackTrace();
		}
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object, String hostname) throws IOException {
  		res.setStatus(200);
	   	PrintWriter out = res.getWriter();
	   	out.write("Forbidden!");
	}
    public void doPut(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object, String hostname) throws IOException {
  		res.setStatus(200);
	   	PrintWriter out = res.getWriter();
	   	out.write("Forbidden!");
	}
    public void doDelete(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object, String hostname) throws IOException {
  		res.setStatus(200);
	   	PrintWriter out = res.getWriter();
	   	out.write("Forbidden!");
	}

    protected void setResponseHeaders(HttpServletResponse res) {
  		res.setContentType("image/svg+xml");
  		res.setStatus(200);
    }
    
    protected void transcode(String in_s, OutputStream out) throws TranscoderException, IOException {
    	out.write(in_s.getBytes());
    }

}
