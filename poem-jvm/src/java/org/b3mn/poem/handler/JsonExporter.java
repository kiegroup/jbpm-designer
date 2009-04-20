/***************************************
 * Copyright (c) 2009
 * Jan-Felix Schwarz
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

import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.util.ExportHandler;

@ExportHandler(uri="/json", formatName="JSON", iconUrl="/backend/images/silk/page_white_code.png")
public class JsonExporter extends HandlerBase {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object)  {
		
		res.setContentType("application/json");
  		res.setStatus(200);	
  		
  		try {
  			URL serverUrl = new URL( req.getScheme(),
                        req.getServerName(),
                        req.getServerPort(),
                        "" );
  			
 			String prepend = req.getParameter("jsonp");
  			
  			PrintWriter out = res.getWriter();
  			
  			String jsonRepresentation = object.read().getJson(serverUrl.toString());
  			
  			if(prepend==null)
  				out.write(jsonRepresentation);
  			else
  				out.write(prepend + "(" + jsonRepresentation + ");");
  			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
