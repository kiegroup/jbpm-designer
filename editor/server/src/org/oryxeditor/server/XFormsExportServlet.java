package org.oryxeditor.server;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;

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

import de.hpi.xforms.XForm;
import de.hpi.xforms.rdf.XFormsRDFImporter;
import de.hpi.xforms.serialization.XFormsXHTMLExporter;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class XFormsExportServlet extends HttpServlet {
	
	private static final long serialVersionUID = 6084194342174761093L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("text/xhtml");
		
		String rdf = req.getParameter("data");
		String cssUrl = req.getParameter("css");
		
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes()));
			
			XFormsRDFImporter importer = new XFormsRDFImporter(document);
			XForm form = importer.loadXForm();
			
			XFormsXHTMLExporter exporter = new XFormsXHTMLExporter(form);
			Document xhtmlDoc = exporter.getXHTMLDocument(cssUrl);
			
			OutputFormat format = new OutputFormat(xhtmlDoc);
			format.setIndenting(true);
			format.setPreserveSpace(true);
			format.setLineSeparator(System.getProperty("line.separator"));
			format.setMethod("text/xhtml");
			
			// TODO: newlines in serialize output (weird it doesn't work)
			
			XMLSerializer serial = new XMLSerializer(new BufferedWriter(res.getWriter()), format);
			serial.asDOMSerializer();
			serial.serialize(xhtmlDoc);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
	}
}
