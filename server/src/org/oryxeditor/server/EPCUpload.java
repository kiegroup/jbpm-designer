package org.oryxeditor.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


/**
 * Copyright (c) 2008 Stefan Krumnow.
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
public class EPCUpload extends HttpServlet {

	private static final long serialVersionUID = 316274845723034029L;
	
	private static Configuration config = null;
	
    /**
     * The POST request.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
    	
    	// Get the PrintWriter
    	PrintWriter out = null;
    	try {
    	    out = res.getWriter();
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    	
    	// Get the resourceID - Needed for redirect.
    	final String resourceID = req.getParameter("resource");
    	
    	// No isMultipartContent => Error
    	final boolean isMultipartContent = ServletFileUpload.isMultipartContent(req);
    	if (!isMultipartContent){
    		printError(out, "No Multipart Content transmitted.", resourceID);
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
    			printError(out, "Not exactly one File.", resourceID);
    			return ;
    		}
    	} catch (FileUploadException e) {
    		handleException(out, resourceID, e); 
	   		return;
    	} 
    	final FileItem fileItem = (FileItem)items.get(0);
    		
    	// Get filename and content (needed to distinguish between EPML and AML)
    	final String fileName = fileItem.getName();
    	String content = fileItem.getString();


    	// Get the input stream	
    	final InputStream inputStream;
    	try {
    		inputStream = fileItem.getInputStream();
    	} catch (IOException e){ 
    		handleException(out, resourceID, e); 
    		return;
    	}
	   		
    	// epml2eRDF XSLT source
    	final File epml2eRDFxsltFile = new File("webapps/oryx/xslt/EPML2eRDF.xslt");
    	final Source epml2eRDFxsltSource = new StreamSource(epml2eRDFxsltFile);	

    	// Transformer Factory
    	final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    	// Get the epml source
    	final Source epmlSource;

    	if (fileName.endsWith(".epml") || content.contains("http://www.epml.de")){
    		epmlSource = new StreamSource(inputStream);
    	} else if (fileName.endsWith(".xml")){
    		try {
	    		final File aml2epmlXsltFile = new File("webapps/oryx/xslt/AML2EPML_2.xslt");
	        	final Source aml2epmlXsltSource = new StreamSource(aml2epmlXsltFile);
	    		Transformer transformer = transformerFactory.newTransformer(aml2epmlXsltSource);
	    		content = content.replace("ARIS-Export.dtd", "webapps/oryx/lib/ARIS-Export.dtd");
	    		StringWriter writer = new StringWriter();
	    		transformer.transform( new StreamSource(new StringReader(content)), new StreamResult(writer));
	    		String epmlString = writer.toString();
	    		epmlSource = new StreamSource(new StringReader(epmlString));
    		} catch (Exception e){
        		handleException(out, resourceID, e); 
        		return;
        	}
    	} else {
    		printError(out, "No EPML or AML file uploaded.", resourceID);
    		return ;
    	}
    		
    	// Get the result string
    	String resultString = null;
    	try {
    		Transformer transformer = transformerFactory.newTransformer(epml2eRDFxsltSource);
    		StringWriter writer = new StringWriter();
    		transformer.transform(epmlSource, new StreamResult(writer));
    		resultString = writer.toString();
    	} catch (Exception e){
    		handleException(out, resourceID, e); 
    		return;
    	}
    	
    	// Write result to database
    	if (resultString != null){
    		try {
    			if (resultString.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>")){
    				resultString = resultString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>", "");
    				resultString = resultString.replace("</root>", "");
    				writeToDatabase(resourceID, resultString);
    			} else {
    				printError(out, "Error during transformation.", resourceID);
    			}

    		} catch (Exception e){
    			handleException(out, resourceID, e); 
    			return;
    		}
    	}
    		
    	// Redirect to oryx
    	res.setHeader("Location", "./server?resource="+resourceID);
    	res.setStatus(301);	
    }
    
    
    
    private void printError(PrintWriter out, String err, String resourceID){
    	if (out != null){
    		out.println("<html><head><title>Error during upload</title></head><body>");
    		out.println("An error has occured: <br />");
    		out.println("  " + err);
    		out.println("<br /><br />");
    		out.println("<a href='./server?resource="+resourceID+"'>Back to Oryx</a>");
    		out.println("</body></html>");
    	}
    }
    
	private void handleException(PrintWriter out, final String resourceID, Exception e) {
		e.printStackTrace();
		printError(out, e.getLocalizedMessage(), resourceID);
	}
    
    private void writeToDatabase(String resourceID, String content) throws ConfigurationException, SQLException{
    	if (config == null){
    		config = new PropertiesConfiguration("database.properties");
    	}
    	//String connector = config.getString("db.connector");
    	String url = config.getString("db.url");
    	String username = config.getString("db.username");
    	String password = config.getString("db.password");
    	
    	Connection database = DriverManager.getConnection(url, username, password);
    	PreparedStatement stmt = database.prepareStatement("SELECT ID FROM sites WHERE Name = ?");
    	stmt.setString(1, resourceID);

		if (stmt.executeQuery().next()) {
		    PreparedStatement store = database.prepareStatement("UPDATE sites SET Site = ? WHERE Name = ?");
		    store.setString(1, content);
		    store.setString(2, resourceID);
		    store.execute();
	    }
    }
}
