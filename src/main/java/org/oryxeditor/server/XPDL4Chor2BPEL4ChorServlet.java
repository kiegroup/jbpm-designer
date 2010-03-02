
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


import de.hpi.bpel4chor.transformation.Transformation;
import de.hpi.bpel4chor.transformation.TransformationResult;
import de.hpi.bpel4chor.transformation.TransformationResult.Type;
import de.hpi.bpel4chor.util.Output;
import de.hpi.bpel4chor.util.OutputElement;


/**
 * Copyright (c) 2008 Oliver Kopp
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
	
	private static final String VERSION = "1.0";
	
//	private static Configuration config = null;
	
	/**
	 * Public for other transformation plugins
	 * TODO refactor out to own class
	 */
	public static String escapeJSON(String json) {
		// escape (some) JSON special characters
		// sorry, this is code and fix. 
		// TODO a JSON-library should be used here...
		String res = json.replace("\"", "\\\"");
		res = res.replace("\n","\\n");
		res = res.replace("\r","\\r");
		res = res.replace("\t","\\t");
		return res;
	}
	
	/**
	 * Serializes a DOM document to String.
	 * 
	 * Public for other transformation plugins
	 * TODO refactor out to own class
	 * 
	 * @param document The document to serialize.
	 * @param output   The Output to print errors to.
	 * 
	 * @return The serialized document as string.
	 */
	public static String domToString(Document document) {
		Source source = new DOMSource(document);
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys. INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");
			
			StringWriter sw=new StringWriter();
            StreamResult resultStream = new StreamResult(sw);
            transformer.transform(source, resultStream);
            return sw.toString();
		} catch (TransformerException e) {
			return e.toString();
		}
	}
	
    /**
     * The POST request.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
		res.setContentType("application/json");

		try {
			String xpdl = req.getParameter("data");

	    	List<TransformationResult> result = new Transformation().transform(xpdl, false);

	    	// start result JSON
			res.getWriter().print("{\"version\": \"" + VERSION + "\",");
			
			res.getWriter().print("\"res\":[");
	    	
	    	Iterator<TransformationResult> it = result.iterator();
	    	
	    	while (it.hasNext()) {
	    		TransformationResult tr = it.next();
	    		
	    		res.getWriter().print("{\"type\": \"" + tr.getType() + "\",");
	    		if (tr.getType() == Type.PROCESS) {
	    			res.getWriter().print("\"name\": \"" + tr.getProcessname() + "\",");
	    		}
	    		
	    		if (tr.isSuccess()) {
		    		res.getWriter().print("\"success\": true,");
		    		res.getWriter().print("\"document\": \"");
		    		res.getWriter().print(escapeJSON(domToString(tr.getDocument())));
		    		res.getWriter().print("\"");
	    		} else {
		    		res.getWriter().print("\"success\": false,");
		    		res.getWriter().print("\"errors\": [");

		    		Output output = tr.getOutput();
		    		Iterator<OutputElement> itOutput = output.iterator();
		    		while (itOutput.hasNext()) {
		    			OutputElement el = itOutput.next();

		    			// open object
		    			res.getWriter().print("{");
		    			
		    			res.getWriter().print("\"message\": \"");
		    			res.getWriter().print(el.getMsg());
		    			res.getWriter().print("\"");
		    			
		    			if (el.hasElementInfo()) {
		    				res.getWriter().print(",");
		    				res.getWriter().print("\"id\": \"");
		    				res.getWriter().print(el.getElementId());
			    			res.getWriter().print("\"");
		    			}
		    			
		    			if (el.hasNodeInfo()) {
		    				res.getWriter().print(",");
		    				res.getWriter().print("\"node\": \"");
		    				res.getWriter().print(escapeJSON(el.getNode().toString()));
			    			res.getWriter().print("\"");
		    			}
		    			
		    			// close object
		    			if (itOutput.hasNext()) {
							res.getWriter().print("},");
						} else {
							res.getWriter().print("}");
						}	    		
		    		}
		    		
		    		// close content of "errors"
		    		res.getWriter().print("]");
	    		}
	    		
	    		// close object with success and document/errors
	    		if (it.hasNext()) {
	    			res.getWriter().print("},");
	    		} else {
	    			res.getWriter().print("}");
	    		}	    		
	    	}
	    	
	    	// close res[ array and the whole result
	    	res.getWriter().print("]}");
		} catch (Exception e) {
			try {
				res.getWriter().print("{"+
						"\"version\": \"" + VERSION + "\","+
						"\"res\": [{\"type\": \"" + Type.DIAGRAM + "\", \"success\": false, \"errors\": [{\"message\": \"");
				res.getWriter().print(escapeJSON(e.toString()));
				res.getWriter().print("\\r\\n");
				StackTraceElement[] trace = e.getStackTrace();
				for (int i = 0; i < trace.length; i++) {
					res.getWriter().print(escapeJSON(trace[i].toString()));
					res.getWriter().print("\\r\\n");
				}
				res.getWriter().print("\"}]}]}");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
    }    
}
