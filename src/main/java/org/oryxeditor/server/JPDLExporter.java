
package org.oryxeditor.server;

import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.jbpm.InvalidModelException;
import de.hpi.jbpm.JsonToJpdl;
import org.json.JSONObject;

/**
 * Copyright (c) 2010
 * 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class JPDLExporter extends HttpServlet {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5914861838543319038L;
	
    /**
     * The POST request.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
		res.setContentType("application/xml");
  		res.setStatus(200);	
  		
  		try {
  			
  			PrintWriter out = res.getWriter();
  			
  			out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
  			JsonToJpdl transformation = JsonToJpdl.createInstance(new JSONObject(req.getParameter("data")));
  			String result = "";
  			try {
  				result = transformation.transform();
  	
  			} catch (InvalidModelException e) {
  				result = "<error>" + e.getMessage() + "</error>";
  			}
  			out.write(result);
  			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
    
}
