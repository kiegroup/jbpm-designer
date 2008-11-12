
package org.oryxeditor.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


import de.hpi.bpel4chor.transformation.TransformationResult;
import de.hpi.bpel4chor.transformation.XPDL4Chor2BPEL4Chor;


/**
 * Copyright (c) 2008 
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
public class XPDL4Chor2BPEL4ChorServlet extends HttpServlet {

	private static final long serialVersionUID = 316274845723034029L;
	
//	private static Configuration config = null;
	
	private static String escapeJSON(String json) {
		// escape (some) JSON special characters
		// sorry, this is code and fix. 
		// TODO a JSON-library should be used here...
		String res = json.replaceAll("\"", "\\\"");
		res = res.replaceAll("\n","\\\\n");
		res = res.replaceAll("\r","\\\\r");
		res = res.replaceAll("\t","\\\\t");
		return res;
	}
	
	
    /**
     * The POST request.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {

		try {
			res.setContentType("application/json");

			String xpdl = req.getParameter("data");

	    	List<TransformationResult> result = new XPDL4Chor2BPEL4Chor().transform(xpdl, false);

	    	// generate result JSON
	    	res.getWriter().print("{\"res\":[");
	    	
	    	Iterator<TransformationResult> it = result.iterator();
	    	
	    	while (it.hasNext()) {
	    		TransformationResult tr = it.next();

	    		String trres = escapeJSON(tr.result);
	    		
	    		res.getWriter().print("{\"successs\": ");
	    		res.getWriter().print(tr.success);
	    		res.getWriter().print(",");
	    		res.getWriter().print("\"content\": \"");
	    		res.getWriter().print(trres);
	    		
	    		if (it.hasNext()) {
	    			res.getWriter().print("\"},");
	    		} else {
	    			res.getWriter().print("\"}");
	    		}	    		
	    	}
	    	res.getWriter().print("]}");
		} catch (Exception e) {
			try {
				res.getWriter().print("{\"res\":[{\"success\":false,\"content\":\"");
				res.getWriter().print(escapeJSON(e.toString()));
				res.getWriter().print("\\r\\n");
				StackTraceElement[] trace = e.getStackTrace();
				for (int i = 0; i < trace.length; i++) {
					res.getWriter().print(escapeJSON(trace[i].toString()));
					res.getWriter().print("\\r\\n");
				}
				res.getWriter().print("\"}]}");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
    }    
}
