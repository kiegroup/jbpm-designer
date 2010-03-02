package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import de.hpi.bpt.epc.EPC;
import de.hpi.bpt.epc.aml.util.AMLParser;
import de.hpi.bpt.epc.aml.util.OryxSerializer;

/**
 * Copyright (c) 2008 Willi Tscheschner
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
public class AMLSupport extends HttpServlet {

	private static final long serialVersionUID = 316274845723034029L;

	/**
	 * The POST request.EPCUpload.java
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		
		PrintWriter out = null;
		
		FileItem fileItem = null;
		
		try {
			String oryxBaseUrl = req.getScheme() + "://" + req.getServerName()
					+ ":" + req.getServerPort() + req.getContextPath() + "/";

			// Get the PrintWriter
			res.setContentType("text/plain");
			res.setCharacterEncoding("utf-8");

			out = res.getWriter();

			// No isMultipartContent => Error
			final boolean isMultipartContent = ServletFileUpload
					.isMultipartContent(req);
			if (!isMultipartContent) {
				printError(out, "No Multipart Content transmitted.");
				return;
			}

			// Get the uploaded file
			final FileItemFactory factory = new DiskFileItemFactory();
			final ServletFileUpload servletFileUpload = new ServletFileUpload(
					factory);
			servletFileUpload.setSizeMax(-1);
			final List<?> items;

			items = servletFileUpload.parseRequest(req);
			if (items.size() != 1) {
				printError(out, "Not exactly one File.");
				return;
			}

			fileItem = (FileItem) items.get(0);

			// replace dtd reference by existing reference /oryx/lib/ARIS-Export.dtd
			String amlStr = fileItem.getString("UTF-8");
			
			amlStr = amlStr.replaceFirst("\"ARIS-Export.dtd\"", "\"" + oryxBaseUrl + "lib/ARIS-Export.dtd\"");
			
			FileOutputStream fileout = null;
			OutputStreamWriter outwriter = null;
			
			try {
				fileout = new FileOutputStream(((DiskFileItem)fileItem).getStoreLocation());
				outwriter = new OutputStreamWriter(fileout, "UTF-8");
				outwriter.write(amlStr);
				outwriter.flush();
				
			} finally {
				if(outwriter != null)
					outwriter.close();
				if(fileout != null)
					fileout.close();
			}
			
			//parse AML file
			AMLParser parser = new AMLParser(((DiskFileItem) fileItem).getStoreLocation().getAbsolutePath());
			parser.parse();
			Collection<EPC> epcs = new HashSet<EPC>();
			Iterator<String> ids = parser.getModelIds().iterator();
			while (ids.hasNext()) {
				String modelId = ids.next();
				epcs.add(parser.getEPC(modelId));
			}

			// serialize epcs to eRDF oryx format
			OryxSerializer oryxSerializer = new OryxSerializer(epcs,
					oryxBaseUrl);
			oryxSerializer.parse();

			Document outputDocument = oryxSerializer.getDocument();

			// get document as string
			String docAsString = "";

			Source source = new DOMSource(outputDocument);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer transformer = tfactory.newTransformer();
			transformer.transform(source, result);
			docAsString = stringWriter.getBuffer().toString();

			// write response
			out.print("" + docAsString + "");

		} catch (Exception e) {
			handleException(out, e);
		} finally {
			if(fileItem != null) {
				fileItem.delete();
			}
		}
	}

	private void printError(PrintWriter out, String err) {
		if (out != null) {
			out.print("" + err + "");

		}
	}

	private void handleException(PrintWriter out, Exception e) {
		e.printStackTrace();
		printError(out, e.getLocalizedMessage());
	}

}
