
package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;



/**
 * Copyright (c) 2008 
 * 
 * Zhen Peng
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
public class BPEL4ChorExporter extends HttpServlet {

	
	private static final long serialVersionUID = 3551528829474652693L;
	
	private BPELExporter bpelExporter = new BPELExporter();
	

	/**
     * The POST request.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
    	
    	res.setContentType("application/json");
    	PrintWriter out = null;
    	try {
    	    out = res.getWriter();
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    	
    	out.print("{\"res\":[");
    	
    	String rdfString = req.getParameter("data");
    	    	
    	transformTopology (rdfString, out);
    	
    	out.print(',');

    	transformGrounding (rdfString, out);
    	
    	out.print(',');
    	
    	transformProcesses (rdfString, out);

    	out.print("]}");

    }
  
	private static String escapeJSON(String json) {
		// escape (some) JSON special characters
		String res = json.replace("\"", "\\\"");
		res = res.replace("\n","\\n");
		res = res.replace("\r","\\r");
		res = res.replace("\t","\\t");
		return res;
	}
	
    private void transformTopology (String rdfString, PrintWriter out){
  	   
	   	// XSLT source
	   	final String xsltFilename = System.getProperty("catalina.home") + "/webapps/oryx/xslt/RDF2BPEL4Chor_Topology.xslt";
	   	final File xsltFile = new File(xsltFilename);
	   	final Source xsltSource = new StreamSource(xsltFile);	
	   	
	   	// Transformer Factory
	   	final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	
	   	// Get the rdf source
	   	final Source rdfSource;
	   	InputStream rdf = new ByteArrayInputStream(rdfString.getBytes());
	   	rdfSource = new StreamSource(rdf);
	 
	   	// Get the result string
	   	String resultString = null;
	   	try {
	   		Transformer transformer = transformerFactory.newTransformer(xsltSource);
	   		StringWriter writer = new StringWriter();
	   		transformer.transform(rdfSource, new StreamResult(writer));
	   		resultString = writer.toString();
	   		printResponse (out, "topology", resultString);
	   	} catch (Exception e){
	   		handleException(out, "topology", e); 
	   	}

   }
    
    private void transformGrounding (String rdfString, PrintWriter out){
  	   
	   	// XSLT source
	   	final String xsltFilename = System.getProperty("catalina.home") + "/webapps/oryx/xslt/RDF2BPEL4Chor_Grounding.xslt";
	   	final File xsltFile = new File(xsltFilename);
	   	final Source xsltSource = new StreamSource(xsltFile);	
	   	
	   	// Transformer Factory
	   	final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	
	   	// Get the rdf source
	   	final Source rdfSource;
	   	InputStream rdf = new ByteArrayInputStream(rdfString.getBytes());
	   	rdfSource = new StreamSource(rdf);
	 
	   	// Get the result string
	   	String resultString = null;
	   	try {
	   		Transformer transformer = transformerFactory.newTransformer(xsltSource);
	   		StringWriter writer = new StringWriter();
	   		transformer.transform(rdfSource, new StreamResult(writer));
	   		resultString = writer.toString();
	   		printResponse (out, "grounding", resultString);
	   	} catch (Exception e){
	   		handleException(out, "grounding", e);
	   	}

   }
    

    private void transformProcesses (String rdfString, PrintWriter out){
	   
    	bpelExporter.transformProcesses (rdfString, out);
   }
    
   private void printResponse(PrintWriter out, String type, String text){
		out.print("{\"type\":\"" + type + "\",");
		out.print("\"success\":true,");
		out.print("\"content\":\"");
		out.print(escapeJSON(text));
		out.print("\"}");
    }
    
    
    private void printError(PrintWriter out, String type, String err){
		out.print("{\"type\":\"" + type+ "\",");
		out.print("\"success\":false,");
		out.print("\"content\":\"");
		out.print(escapeJSON(err));
		out.print("\"}");
    }
    
	private void handleException(PrintWriter out, String type, Exception e) {
		e.printStackTrace();
		printError(out, type, e.getLocalizedMessage());
	}
    
}
