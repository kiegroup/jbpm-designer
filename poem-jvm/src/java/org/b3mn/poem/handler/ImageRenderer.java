/***************************************
 * Copyright (c) 2008
 * Bjoern Wagner
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
****************************************/

package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderException;
import org.b3mn.poem.Identity;
import org.b3mn.poem.Representation;
import org.b3mn.poem.util.ExportHandler;

@ExportHandler(uri="/svg", formatName="SVG", iconUrl="/backend/images/silk/page_white_vector.png")
public class ImageRenderer extends HandlerBase {
	

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object)  {
    	setResponseHeaders(res);
    	try {
    		
    		Representation representation = object.read();
			String SvgRepresentation = representation.getSvg();
    		if ((SvgRepresentation == null) || (SvgRepresentation.length() == 0)){
    			SvgRepresentation = "<svg xmlns=\"http://www.w3.org/2000/svg\" " +
    					"xmlns:oryx=\"http://oryx-editor.org\" id=\"oryx_1\" width=\"800\" " +
    					"height=\"400\" xlink=\"http://www.w3.org/1999/xlink\" " +
    					"svg=\"http://www.w3.org/2000/svg\"><text x=\"30\" y=\"30\" font-size=\"12px\">" +
    					"Sorry, there is no graphical representation available on the server.<tspan x=\"30\" y=\"50\">" +
    					"Please load the process in Oryx Editor and press the Save button.</tspan></text></svg>";
    		}
    		transcode(SvgRepresentation, res.getOutputStream(), representation);
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
    
    protected void transcode(String in_s, OutputStream out, Representation representation) throws TranscoderException, IOException {
    	out.write(in_s.getBytes("UTF-8"));
    }

}
