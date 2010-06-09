
package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;


/**
 * Copyright (c) 2008-2009 
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
public class BPELImporter extends HttpServlet {

	private static final long serialVersionUID = 316274845723034029L;
	
	private BPELImportPreprocessor preprocessor = new BPELImportPreprocessor();
	
	private Logger log = Logger.getLogger("org.oryxeditor.server.BPELImporter");
	
    /**
     * The POST request.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
    	// No isMultipartContent => Error
    	final boolean isMultipartContent = ServletFileUpload.isMultipartContent(req);
    	if (!isMultipartContent){
    		printError(res, "No Multipart Content transmitted.");
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
    			printError(res, "Not exactly one File.");
    			return ;
    		}
    	} catch (FileUploadException e) {
    		handleException(res, e); 
	   		return;
    	} 
    	
    	// === prepare the bpel source ===
		// Get filename and content
    	final FileItem fileItem = (FileItem)items.get(0);
    	final String fileName = fileItem.getName();

    	if (!fileName.endsWith(".bpel")){
    		printError(res, "No file with .bepl extension uploaded.");
    		return ;
    	}
  	
    	final String fileContent = fileItem.getString();

    	// do a pre-processing on this bpel source
    	// in this preprocessor the following works will be done:
    	//  	1. mark all node stencil sets with the attribute "isNodeStencilSet"
    	//         mark all edge stencil sets with the attribute "isEdgeStencilSet"
    	//         in order to avoid the prefix problem
    	//  	2. calculate the bounds of each shape
    	//      3. generate for each shape a ID
    	//  	4. move the <link> elements from <links> element to
    	//         top of the root <process> element, and record linkID
    	//         as the value of element <outgoing> under the corresponding
    	//         activity, so they could be easier to handle in BPEL2eRDF.xslt
    	//      5. integrate the first <condition> and <activity> element
    	//         under a If-block into a <elseIF> element, so they
    	//         they could be easier to transform in BPEL2eRDF.xslt
    	//      6. transform the value of attribute "opaque" from "yes" to "true"
    	final String newContent = preprocessSource (res, fileContent);
    	
    	log.fine("newContent:");
    	log.fine(newContent);
    	
    	// Get the input stream	
    	final InputStream inputStream = new ByteArrayInputStream(newContent.getBytes());
	 
    	// Get the bpel source
    	final Source bpelSource = new StreamSource(inputStream);

    	
    	// === prepare the xslt source ===
    	// BPEL2eRDF XSLT source
    	final String xsltFilename = getServletContext().getRealPath("/xslt/BPEL2eRDF.xslt");
//    	final String xsltFilename = System.getProperty("catalina.home") + "/webapps/oryx/xslt/BPEL2eRDF.xslt";
    	final File bpel2eRDFxsltFile = new File(xsltFilename);
    	final Source bpel2eRDFxsltSource = new StreamSource(bpel2eRDFxsltFile);	
    	
    	// Transformer Factory
    	final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    	// === Get the eRDF result ===
    	String resultString = null;
    	try {
    		Transformer transformer = transformerFactory.newTransformer(bpel2eRDFxsltSource);
    		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    		StringWriter writer = new StringWriter();
    		transformer.transform(bpelSource, new StreamResult(writer));
    		resultString = writer.toString();
    	} catch (Exception e){
    		handleException(res, e); 
    		return;
    	}
    	
    	log.fine("reslut-XML");
    	log.fine(resultString);

    	if (resultString == null) {
    		printResponse(res, "transformation error");
    	}
    	
		Repository rep = new Repository("");
		resultString = rep.erdfToJson(resultString, getServletContext());

    	log.fine("reslut-json");
    	log.fine(resultString);
    	
    	printResponse(res, resultString);
    }
    
   private String preprocessSource (HttpServletResponse res, String oldString){
	   
	   StringWriter stringOut = new StringWriter();
	   try {
			// transform string to document
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream oldResultInputStream = new ByteArrayInputStream(oldString.getBytes());
			Document oldDocument = builder.parse(oldResultInputStream);
			
			// rearrange document
			Document newDocument = preprocessor.preprocessDocument (oldDocument);
			
			// transform document to string
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(newDocument);
			StreamResult result = new StreamResult(stringOut);
			transformer.transform(source, result);
			stringOut.flush();
	 
		} catch (Exception e){
		    handleException(res, e); 
		}
		
		return stringOut.toString();

   }

private void printResponse(HttpServletResponse res, String text){
    	if (res != null){
 
        	res.setContentType("text/html"); // as required by http://www.extjs.com/deploy/dev/docs/output/Ext.form.BasicForm.html
        	res.setStatus(HttpServletResponse.SC_OK);
        	
        	// Get the PrintWriter
        	PrintWriter out = null;
        	try {
        	    out = res.getWriter();
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
        	
    		out.print("{success:true, content:'"+text+"'}");
    	}
    }
    
    
    private void printError(HttpServletResponse res, String err){
    	if (res != null){
 
        	// Get the PrintWriter
        	res.setContentType("text/html"); // as required by http://www.extjs.com/deploy/dev/docs/output/Ext.form.BasicForm.html
        	res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	
        	PrintWriter out = null;
        	try {
        	    out = res.getWriter();
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
        	
        	out.print("{success:false, content:'"+err+"'}");
    	}
    }
    
	private void handleException(HttpServletResponse res, Exception e) {
		e.printStackTrace();
		printError(res, e.getLocalizedMessage());
	}
    
}
