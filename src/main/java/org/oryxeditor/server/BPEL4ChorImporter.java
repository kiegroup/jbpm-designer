
package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


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
public class BPEL4ChorImporter extends HttpServlet {

	private static final long serialVersionUID = 316274845723034029L;
	
//	private static Configuration config = null;
	
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
    	final String fileContent = fileItem.getString();

    	// because the namespace of bpel process is't unique, and it's difficult 
    	// to handle a unknown namespace in XSLT 1.0 (it doesn't support the xPath 
    	// e.g."*:process"), we remove the attribute "xmlns" before we transform
    	// this file. 
    	final String contentWithoutNamespace = removeAttributeXMLNS(fileContent);
    	
    	// Get the input stream	
    	final InputStream inputStream = new ByteArrayInputStream(contentWithoutNamespace.getBytes());
	   	
    	// Get the bpel source
    	final Source bpelSource;

    	if (fileName.endsWith(".bpel")){
    		bpelSource = new StreamSource(inputStream);
    	} else {
    		printError(res, "No file with .bepl extension uploaded.");
    		return ;
    	}
    	
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
    		StringWriter writer = new StringWriter();
    		transformer.transform(bpelSource, new StreamResult(writer));
    		resultString = writer.toString();
    	} catch (Exception e){
    		handleException(res, e); 
    		return;
    	}

    	if (resultString != null){
    		try {
    		       printResponse (res, resultString);
    		       return;
    		} catch (Exception e){
    		       handleException(res, e); 
    		}
    	}
    }
    
    
    
   private String removeAttributeXMLNS(String fileContent) {
	int beginIndex = fileContent.indexOf("xmlns=");
	
	if (beginIndex == -1) return fileContent;
	
	int endIndex = fileContent.indexOf(" ", beginIndex);
	
	// locate the attribute "xmlns"
	String attributeXMLNS = fileContent.substring(beginIndex, endIndex + 1); 
	
	return fileContent.replace(attributeXMLNS, "");
}



private void printResponse(HttpServletResponse res, String text){
    	if (res != null){
 
        	// Get the PrintWriter
        	res.setContentType("text/plain");
        	
        	PrintWriter out = null;
        	try {
        	    out = res.getWriter();
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
        	
    		out.print(text);
    	}
    }
    
    
    private void printError(HttpServletResponse res, String err){
    	if (res != null){
 
        	// Get the PrintWriter
        	res.setContentType("text/html");
        	
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
