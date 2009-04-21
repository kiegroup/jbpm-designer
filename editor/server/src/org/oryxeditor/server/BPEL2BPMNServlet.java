package org.oryxeditor.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;

import de.hpi.bpel2bpmn.BPEL2BPMNTransformer;
import de.hpi.bpel2bpmn.util.BPELParser;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.serialization.erdf.BPMNeRDFSerializer;

/**
 * Copyright (c) 2008 Matthias Weidlich
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
public class BPEL2BPMNServlet extends HttpServlet {

	private static final long serialVersionUID = 128489343034029L;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    	// Get the PrintWriter
    	res.setContentType("text/plain");
    	PrintWriter out = null;
    	try {
    	    out = res.getWriter();
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    	
    	// No isMultipartContent => Error
    	final boolean isMultipartContent = ServletFileUpload.isMultipartContent(req);
    	if (!isMultipartContent){
    		printError(out, "No Multipart Content transmitted.");
			return ;
    	}
    	
    	// Get the uploaded file
    	final FileItemFactory factory = new DiskFileItemFactory();
    	final ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
    	servletFileUpload.setSizeMax(-1);
    	final List<?> items;
    	try {
    		items = servletFileUpload.parseRequest(req);
    		if (items.size() != 1){
    			printError(out, "Not exactly one File.");
    			return ;
    		}
    	} catch (FileUploadException e) {
    		handleException(out, e); 
	   		return;
    	} 
    	final FileItem fileItem = (FileItem)items.get(0);	
    	    	
    	// try to parse the BPEL file
		BPELParser parser = new BPELParser();
		Document doc = parser.parseBPELFile(fileItem.getInputStream());
		
		if (doc == null) {
			printError(out, "The file could not be parsed.");
			return;
		}
		
		// try to map the BPEL process to BPMN
		BPMNDiagram diagram = null;
		try {
	    	BPEL2BPMNTransformer transformer = new BPEL2BPMNTransformer(doc);
	    	diagram = transformer.mapBPEL2BPMN();
		} catch (Exception e) {
			printError(out, "BPEL could not be mapped to BPMN.");
			return;
		}
		
		if (diagram == null) {
			printError(out, "BPMN diagram could not be created.");
			return;
		}

		// serialize the resulting BPMN
		try {
	    	BPMNeRDFSerializer serializer = new BPMNeRDFSerializer();
	    	String eRDF = serializer.serializeBPMNDiagram(diagram).replaceAll("\"", "'");

    		out.print("\"success\":\"true\", ");

			// check for XML schema validation errors
			if (parser.isSuccessfulValidation()) {
	    		out.print("\"successValidation\":\"true\", \"validationError\":\"\", ");
			}
			else {
	    		out.print("\"successValidation\":\"false\", \"validationError\":\""+parser.getValidationException()+"\", ");
			}
			
    		out.print("\"content\":\""+eRDF+"\"");

		} catch (Exception e) {
			printError(out, "Resulting BPMN diagram could not be serialized.");
			return;
		}
	}
	
    private void printError(PrintWriter out, String err){
    	if (out != null){
    		out.print("\"success\":\"false\", \"content\":\""+err+"\"");
    	}
    }
    
	private void handleException(PrintWriter out, Exception e) {
		e.printStackTrace();
		printError(out, e.getLocalizedMessage());
	}

}
