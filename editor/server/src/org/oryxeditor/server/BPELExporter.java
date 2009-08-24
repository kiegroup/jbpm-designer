
package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

/**
 * Copyright (c) 2008-2009 
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
public class BPELExporter extends HttpServlet {

	private static final long serialVersionUID = 316274845723034029L;
	
	private BPELExportPostprocessor postprocessor = new BPELExportPostprocessor();

	private static String escapeJSON(String json) {
		// escape (some) JSON special characters
		String res = json.replace("\"", "\\\"");
		res = res.replace("\n","\\n");
		res = res.replace("\r","\\r");
		res = res.replace("\t","\\t");
		return res;
	}
	
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
    	
    	transformProcesses (rdfString, out);
    	
    	out.print("]}");
    }
   
   public void transformProcesses (String rdfString, PrintWriter out){
	   
	   // Get the rdf source
	   final Source rdfSource;
	   InputStream rdf = new ByteArrayInputStream(rdfString.getBytes());
	   rdfSource = new StreamSource(rdf);

	   	// RDF2BPEL XSLT source
	   String xsltFilename;
		try {
			ServletContext context = getServletContext();
			final String contextPath = context.getRealPath("");
			xsltFilename = contextPath + "/xslt/RDF2BPEL.xslt";
		} catch (Exception e) {
			handleException(out, e);
			return;
		}
	   
   		final File xsltFile = new File(xsltFilename);
   		final Source xsltSource = new StreamSource(xsltFile);	
   	
   		// Transformer Factory
   		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    	
   		// Get the result string
    	String resultString = null;
    	try {
    		Transformer transformer = transformerFactory.newTransformer(xsltSource);
    		StringWriter writer = new StringWriter();
    		transformer.transform(rdfSource, new StreamResult(writer));
    		resultString = writer.toString();
    	} catch (Exception e){
    		handleException(out, e); 
    		return;
    	}
    	
    	//System.out.println(resultString);
    	if (resultString != null){
    		try {
    	    	// do a post-processing on this result bpel document
    	    	// in this postprocessor the following works will be done:
    	    	//  	1. rearrange the position of nodes on the basis of their
    			//         bounding. (except child nodes under <flow>)
    	    	//  	2. rearrange the position of child nodes of <flow> on the
    			//         basis of the order of <link>
    	    	//      3. separate the first <elseIF> element under <if>-block
    			//         to <condition> and <activity> element
    			//      4. remove all useless attributes and elements, which contain
    			//         the necessary informations for the above works but useless
    			//         right now
    			resultString = postprocessResult (out, resultString);

    			
    			ArrayList<String> processList = separateProcesses(resultString);
    			
    			Iterator<String> processListIter = processList.iterator();
    			String process;
    			while (processListIter.hasNext()){
				   process = processListIter.next();
				   printResponse (out, process);
				   if (processListIter.hasNext()){
					   out.print(',');
				   }
    			};
    		    return;
    		} catch (Exception e){
    		    handleException(out, e); 
    		}
    	}
   }
   
   private String postprocessResult (PrintWriter out, String oldString){
	   
	   StringWriter stringOut = new StringWriter();
	   try {
			// transform string to document
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream oldResultInputStream = new ByteArrayInputStream(oldString.getBytes());
			Document oldDocument = builder.parse(oldResultInputStream);
			
			// rearrange document
			Document newDocument = postprocessor.postProcessDocument(oldDocument);
			
			// transform document to string
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(newDocument);
			StreamResult result = new StreamResult(stringOut);
			transformer.transform(source, result);
			stringOut.flush();
	 
		} catch (Exception e){
		    handleException(out, e); 
		}
		
		return stringOut.toString();

   }
   
   private ArrayList<String> separateProcesses (String resultString){
	   ArrayList<String> resultList = new ArrayList<String>();
	   int indexOfProcess = resultString.indexOf("<process");
	   int indexOfEndProcess = 0;
	   
	   while (indexOfProcess != -1){
		   indexOfEndProcess = resultString.indexOf("process>", indexOfProcess + 1);
		   if (indexOfEndProcess == -1){
			   indexOfEndProcess = resultString.indexOf("/>", indexOfProcess + 1) - 6;
		   }
		   String process = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" 
			   + resultString.substring(indexOfProcess, indexOfEndProcess + 8);
		   
		   resultList.add(process);
		   
		   indexOfProcess = resultString.indexOf("<process", indexOfEndProcess + 1);
	   }
	
	   return resultList;
   }
   
   
   private void printResponse(PrintWriter out, String text){
		out.print("{\"type\":\"process\",");
		out.print("\"success\":true,");
		out.print("\"content\":\"");
		out.print(escapeJSON(text));
		out.print("\"}");

    }
    
    
    private void printError(PrintWriter out, String err){
		out.print("{\"type\":\"process\",");
		out.print("\"success\":false,");
		out.print("\"content\":\"");
		out.print(escapeJSON(err));
		out.print("\"}");
    }
    
	private void handleException(PrintWriter out, Exception e) {
		e.printStackTrace();
		printError(out, e.getLocalizedMessage());
	}
    
}
