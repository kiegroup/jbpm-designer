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

import org.w3c.dom.Document;
/*import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;*/

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.org.apache.xml.internal.serialize.DOMSerializer;

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
			
			/*//System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMImplementationSourceImpl");
			
			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
			LSOutput out = impl.createLSOutput();
			out.setByteStream(res.getOutputStream());
			LSSerializer writer = impl.createLSSerializer();
			writer.write(xhtmlDoc, out);
			
			DOMImplementation implementation = DOMImplementationRegistry.newInstance().getDOMImplementation("XML 3.0");
			DOMImplementationLS feature = (DOMImplementationLS) implementation.getFeature("LS", "3.0");
			LSSerializer serializer = feature.createLSSerializer();
			LSOutput output = feature.createLSOutput();
			output.setByteStream(res.getOutputStream());
			serializer.write(xhtmlDoc, output);*/

			
			OutputFormat format = new OutputFormat(xhtmlDoc);
			format.setIndenting(true);
			format.setPreserveSpace(true);
			format.setLineSeparator(System.getProperty("line.separator"));
			format.setMethod("text/xhtml");
			
			// TODO: newlines in serialize output (weird it doesn't work)
			
			XMLSerializer serial = new XMLSerializer(new BufferedWriter(res.getWriter()), format);
			DOMSerializer domserial = serial.asDOMSerializer();
			domserial.serialize(xhtmlDoc); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
