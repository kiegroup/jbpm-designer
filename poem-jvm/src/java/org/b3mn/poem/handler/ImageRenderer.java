package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderException;
import org.b3mn.poem.Identity;

public class ImageRenderer extends HandlerBase {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object)  {
    	setResponseHeaders(res);
    	try {
    		String SvgRepresentation = object.read().getSvg();
    		if(SvgRepresentation == null) {
    			SvgRepresentation = "<svg xmlns=\"http://www.w3.org/2000/svg\" " +
    					"xmlns:oryx=\"http://oryx-editor.org\" id=\"oryx_1\" width=\"800\" " +
    					"height=\"400\" xlink=\"http://www.w3.org/1999/xlink\" " +
    					"svg=\"http://www.w3.org/2000/svg\"><text x=\"30\" y=\"30\" font-size=\"12px\">" +
    					"Sorry, there is no graphical representation available on the server.<tspan x=\"30\" y=\"50\">" +
    					"Please load the process with the Oryx Editor and push the Save button.</tspan></text></svg>";
    		}
    		transcode(SvgRepresentation, res.getOutputStream());
		} catch (TranscoderException e) {
			e.printStackTrace();
		} catch (Exception ie) {
			ie.printStackTrace();
		}
    }
    protected void setResponseHeaders(HttpServletResponse res) {
  		res.setContentType("image/svg+xml");
  		res.setStatus(200);
    }
    
    protected void transcode(String in_s, OutputStream out) throws TranscoderException, IOException {
    	out.write(in_s.getBytes());
    }

}
