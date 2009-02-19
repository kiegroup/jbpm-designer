package org.oryxeditor.server;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import de.hpi.xforms.XForm;
import de.hpi.xforms.generation.WSDL2XFormsTransformation;
import de.hpi.xforms.rdf.XFormsERDFExporter;
import de.hpi.xforms.serialization.XFormsXHTMLImporter;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class WSDL2XFormsServlet extends HttpServlet {
	
	private static final long serialVersionUID = 6084194342174761234L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("text/html");
		
		String wsdlUrl = req.getParameter("wsdlUrl");
		String redirectUrl = req.getParameter("redirectUrl");

		try {
			
			// get WSDL document
			URL url = new URL(wsdlUrl);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document wsdlDoc = builder.parse(url.openStream());
			
			// transform to XForms documents
			List<Document> xformsDocs = WSDL2XFormsTransformation.transform(
						getServletContext(), wsdlDoc.getDocumentElement(), generateWsdlId(wsdlUrl));
			
			String modelUrls = "";
			
			int i=0;
			for(Document xformsDoc : xformsDocs) {
				
				XFormsXHTMLImporter importer = new XFormsXHTMLImporter(xformsDoc);
				XForm form = importer.getXForm();
				
				// import XForms document for Oryx
				XFormsERDFExporter exporter = new XFormsERDFExporter(form);
				StringWriter erdfWriter = new StringWriter();
				exporter.exportERDF(erdfWriter);
				
				// save to backend
				Repository repo = new Repository(Repository.getBaseUrl(req));
				String modelName = generateWsdlId(wsdlUrl) + " " + i;
				
				String modelUrl = repo.saveNewModel(
						erdfWriter.toString(), 
						modelName, 
						modelName, 
						"http://b3mn.org/stencilset/xforms#", 
						"/stencilsets/xforms/xforms.json");
				
				modelUrls += "," + Repository.getOryxUrl(req) + modelUrl;
				
				i++;
				
			}
			
			System.out.println(redirectUrl);
			
			// redirect to specified URL
			if(redirectUrl!=null && redirectUrl.length()>0) {
				res.sendRedirect(redirectUrl + URLEncoder.encode(modelUrls, "UTF-8"));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static String generateWsdlId(String url) {
		UUID uuid = UUID.nameUUIDFromBytes(url.getBytes());
		return uuid.toString();
	}
}
