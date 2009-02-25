package org.oryxeditor.server;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

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
	
	/*private static List<String> portTypes;
	private static Map<String, String> operations; // operation name -> port type
	private static Map<String, String> forms; // form url -> operation*/
	
	private static Map<String, Map<String, String>> forms; // port type -> ( operation name -> form url )

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("text/plain");
		
		Writer resWriter = res.getWriter();
		
		String wsdlUrl = req.getParameter("wsdlUrl");
		
		forms = new HashMap<String, Map<String, String>>();

		try {
			
			// get WSDL document
			URL url = new URL(wsdlUrl);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document wsdlDoc = builder.parse(url.openStream());
			
			// transform to XForms documents
			String wsdlId = generateWsdlId(wsdlUrl);
			List<Document> xformsDocs = WSDL2XFormsTransformation.transform(
						getServletContext(), wsdlDoc.getDocumentElement(), wsdlId);
			
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
				String modelName = wsdlId + " " + i;
				
				String modelUrl = repo.saveNewModel(
						erdfWriter.toString(), 
						modelName, 
						modelName, 
						"http://b3mn.org/stencilset/xforms#", 
						"/stencilsets/xforms/xforms.json");
				
				addResponseParams(xformsDoc.getDocumentElement(), Repository.getOryxUrl(req) + modelUrl);
				
				i++;
				
			}
			
			resWriter.write("svc_0=" + wsdlUrl);
			int ptId=0;
			for(String portType : forms.keySet()) {
				resWriter.write("&svc0_pt" + ptId + "=" + portType);
				int opId=0;
				for(String operationName : forms.get(portType).keySet()) {
					resWriter.write("&svc0_pt" + ptId + "_op" + opId + "=" + operationName);
					resWriter.write("&svc0_pt" + ptId + "_op" + opId + "_ui0=" + forms.get(portType).get(operationName));
					opId++;
				}
				ptId++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static String generateWsdlId(String url) {
		UUID uuid = UUID.nameUUIDFromBytes(url.getBytes());
		return uuid.toString();
	}
	
	private static void addResponseParams(Node formNode, String formUrl) {
		Node instanceNode = getChild(getChild(getChild(formNode, "xhtml:head"), "xforms:model"), "xforms:instance");
		if(instanceNode!=null) {
			String[] splitted = getAttributeValue(instanceNode, "id").split("\\.");
			Map<String, String> operations = new HashMap<String, String>();
			if(!forms.containsKey(splitted[1]))
				forms.put(splitted[1], operations);
			else 
				operations = forms.get(splitted[1]);
			operations.put(splitted[2], formUrl);
		}
	}
	
	private static Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().equals(name)) 
				return node;
		return null;
	}
	
	private static String getAttributeValue(Node node, String attribute) {
		Node item = node.getAttributes().getNamedItem(attribute);
		if (item != null)
			return item.getNodeValue();
		else
			return null;
	}
	
}
