package org.oryxeditor.server;

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

import de.hpi.xforms.XForm;
import de.hpi.xforms.rdf.XFormsERDFExporter;
import de.hpi.xforms.serialization.XFormsXHTMLImporter;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class XFormsImportServlet extends HttpServlet {
	
	private static final long serialVersionUID = -1644973493026627250L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("text/xhtml");
		
		String xhtml = req.getParameter("data");
		
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(xhtml.getBytes()));
			
			XFormsXHTMLImporter importer = new XFormsXHTMLImporter(document);
			XForm form = importer.getXForm();
			
			XFormsERDFExporter exporter = new XFormsERDFExporter(form);
			exporter.exportERDF(res.getWriter());
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
	}
}
