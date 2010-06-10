package org.oryxeditor.server;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Copyright (c) 2008-2009 Falko Menge
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

/**
 * transforms the RDF representation of a model using BPMN 1.1, the BPMN
 * Extension for XForms User Interfaces, and the BPMN Subset for Service
 * Composition into an XHTML page with an aggregated XForm
 */
public class BPMN2XFormsServlet extends HttpServlet {

	private static final long serialVersionUID = 7473520605927335684L;

	private static final String UI_FOLDER      = "generated-uis/";

	private static final String UI_SAVE_PATH   = Repository.getOryxPath() + UI_FOLDER;

	private static final String UI_FILE_PREFIX = "aggregated_ui";

	private Repository repository;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) {
		this.repository = new Repository(req);

		res.setContentType("application/xhtml");
	
		final String rdf = req.getParameter("data");
    	// transform RDF String to XHTML + XForms string
		String result = applyXSLT(rdf, getServletContext().getRealPath("/xslt/BPMN2XForms.xslt"));
		
		// save XForm on server if requested
		final String save = req.getParameter("save");
		if (save != null) {
			result = saveXFormOnServer(result); 
		}	
			
		try {
			res.getWriter().println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private String saveXFormOnServer(String result) {
		Date creationDate = new Date(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS");
		String filename = UI_FILE_PREFIX + "_" + dateFormat.format(creationDate) + ".xhtml";
		
		File file = new File(UI_SAVE_PATH + filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
				BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
				fileWriter.write(result);
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return repository.getOryxUrl() + UI_FOLDER + filename;
	}

	public static String applyXSLT(String input, String xsltPath) {
    	String transformationResult = null;

		try {
			// prepare XML input
			final Source xmlSource = new StreamSource(new ByteArrayInputStream(input.getBytes("UTF-8")));
			
			// prepare XSL transformation
	    	final String xsltFilename = xsltPath;
	    	final File xsltFile = new File(xsltFilename);
	    	final Source xsltSource = new StreamSource(xsltFile);
	    	
	    	// perform transformation
	    	final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    		Transformer transformer = transformerFactory.newTransformer(xsltSource);
    		final StringWriter writer = new StringWriter();
    		transformer.transform(xmlSource, new StreamResult(writer));
    		transformationResult = writer.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return transformationResult;
	}
}
