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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.hpi.xforms.XForm;
import de.hpi.xforms.generation.WSDL2XFormsTransformation;
import de.hpi.xforms.rdf.XFormsERDFExporter;
import de.hpi.xforms.serialization.XFormsXHTMLImporter;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 * @author ole.eckermann@student.hpi.uni-potsdam.de
 */
public class WSDL2XFormsServlet extends HttpServlet {

	private static final long serialVersionUID = 6084194342174761234L;
	private static String wsdlUrl = "";

	// port type -> ( operation name -> form url )
	private static Map<String, Map<String, String>> forms;

	// GET is totally wrong in this case!!! ONLY for testing!
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		doPost(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		wsdlUrl = req.getParameter("wsdlUrl");
		forms = new HashMap<String, Map<String, String>>();
		
		HashMap<String, Document> distinctXFormsDocs = getXFormDocuments(
				getWSDL(), generateWsdlId(wsdlUrl));
		saveXFormsInOryxRepository(distinctXFormsDocs, Repository
				.getBaseUrl(req));
		writeResponse(req, res);
	}

	private Document getWSDL() {
		try {
			URL url = new URL(wsdlUrl);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(url.openStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private HashMap<String, Document> getXFormDocuments(Document wsdl,
			String wsdlId) {
		// transform to XForms documents
		List<Document> xformsDocs = WSDL2XFormsTransformation.transform(
				getServletContext(), wsdl.getDocumentElement(), wsdlId);

		// filter duplicates
		HashMap<String, Document> distinctXFormsDocs = new HashMap<String, Document>();
		for (Document xformsDoc : xformsDocs) {
			String name = getSuitableFormName(xformsDoc);
			if (!distinctXFormsDocs.containsKey(name))
				distinctXFormsDocs.put(name, xformsDoc);
		}
		return distinctXFormsDocs;
	}

	private void saveXFormsInOryxRepository(
			HashMap<String, Document> distinctXFormsDocs, String baseUrl) {

		for (String xformsDocName : distinctXFormsDocs.keySet()) {
			Document xformsDoc = distinctXFormsDocs.get(xformsDocName);
			XFormsXHTMLImporter importer = new XFormsXHTMLImporter(xformsDoc);
			XForm form = importer.getXForm();

			// convert XForm to erdf
			XFormsERDFExporter exporter = new XFormsERDFExporter(form,
					getServletContext().getRealPath(
							"/stencilsets/xforms/xforms.json"));
			StringWriter erdfWriter = new StringWriter();
			exporter.exportERDF(erdfWriter);

			// save XForm to repository
			Repository repo = new Repository(baseUrl);

			String modelUrl = baseUrl
					+ repo.saveNewModelErdf(erdfWriter.toString(), xformsDocName,
							xformsDocName,
							"http://b3mn.org/stencilset/xforms#",
							"/stencilsets/xforms/xforms.json",
							getServletContext());
			addResponseParams(xformsDoc.getDocumentElement(), modelUrl
					.substring(modelUrl.lastIndexOf("http://")));
		}
	}

	private static String generateWsdlId(String url) {
		UUID uuid = UUID.nameUUIDFromBytes(url.getBytes());
		return uuid.toString();
	}

	private static String getSuitableFormName(Document formNode) {
		Node instanceNode = getChild(getChild(getChild(formNode
				.getDocumentElement(), "xhtml:head"), "xforms:model"),
				"xforms:instance");
		if (instanceNode != null) {
			String[] splitted = getAttributeValue(instanceNode, "id").split(
					"\\.");
			return splitted[1] + ":" + splitted[2];
		}
		return null;
	}

	private static void addResponseParams(Node formNode, String formUrl) {
		Node instanceNode = getChild(getChild(getChild(formNode, "xhtml:head"),
				"xforms:model"), "xforms:instance");
		if (instanceNode != null) {
			String[] splitted = getAttributeValue(instanceNode, "id").split(
					"\\.");
			Map<String, String> operations = new HashMap<String, String>();
			if (!forms.containsKey(splitted[1]))
				forms.put(splitted[1], operations);
			else
				operations = forms.get(splitted[1]);
			operations.put(splitted[2], formUrl);
		}
	}

	private static Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node = n.getFirstChild(); node != null; node = node
				.getNextSibling())
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

	private static void writeResponse(HttpServletRequest req,
			HttpServletResponse res) {

		String representation = req.getParameter("representation");
		try {
			Writer resWriter = res.getWriter();
			if (representation != null && representation.equals("xhtml")) {
				// TODO: examination of the HTTP Accept header (see
				// http://www.w3.org/TR/xhtml-media-types/#media-types)
				res.setContentType("application/xhtml+xml");
				resWriter
						.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
								+ "<body style=\"font-size: 75%; font-family: sans-serif;\">"
								+ "<h1>Generated User Interfaces for Service: "
								+ wsdlUrl
								+ "</h1>"
								+ "<a href=\""
								+ wsdlUrl
								+ "\">View WSDL Definition of the Service</a>"
								+ "<p>To execute the forms below, you will need an XForms-capable browser, e.g., "
								+ "<a href=\"http://www.x-smiles.org/\">X-Smiles</a>, "
								+ "<br />"
								+ "or a suitable browser plugin, e.g., "
								+ "<a href=\"https://addons.mozilla.org/en-US/firefox/addon/824\">the XForms extension for Firefox 2.x and 3.x</a> "
								+ "or <a href=\"http://www.formsplayer.com/\">formsPlayer for Internet Explorer</a>."
								+ "<br />"
								+ "See also <a href=\"http://www.xml.com/pub/a/2003/09/10/xforms.html\">Ten Favorite XForms Engines</a> "
								+ "and <a href=\"http://en.wikipedia.org/wiki/Xforms#Software_support\">XForms Software Support</a>."
								+ "</p>");

				String contextPath = req.getContextPath();

				for (String portType : forms.keySet()) {
					resWriter.write("<h2>PortType: " + portType + "</h2>");
					for (String operationName : forms.get(portType).keySet()) {
						resWriter.write("<h3>Operation: " + operationName
								+ "</h3>");
						resWriter
								.write("<a href=\""
										+ forms
												.get(portType)
												.get(operationName)
												.replace(
														"/backend",
														contextPath
																+ "/xformsexport?path=/backend")
										+ "\">Run in XForms-capable Client</a> | ");
						resWriter
								.write("<a href=\""
										+ forms
												.get(portType)
												.get(operationName)
												.replace(
														"/backend",
														contextPath
																+ "/xformsexport-orbeon?path=/backend")
										+ "\">Run on Server</a> | ");
						resWriter.write("<a href=\""
								+ forms.get(portType).get(operationName)
								+ "\">Open in Editor</a>");
					}
				}
				resWriter.write("</body></html>");
			} else if (representation != null && representation.equals("json")) {
				res.setContentType("application/json");
				JSONObject response = new JSONObject();
				try {
					response.put("wsdlUrl", wsdlUrl);
					JSONArray portTypes = new JSONArray();
					for (String portTypeName : forms.keySet()) {
						JSONObject portType = new JSONObject();
						JSONArray operations = new JSONArray();
						for (String operationName : forms.get(portTypeName).keySet()) {
							JSONObject operation = new JSONObject();
							operation.put("name", operationName);
							operation.put("url", forms.get(portTypeName).get(operationName));
							operations.put(operation);
						}
						portType.put("operations", operations);
						portType.put("name", portTypeName);
						portTypes.put(portType);
					}
					response.put("portTypes", portTypes);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				resWriter.write(response.toString());
			} else {
				res.setContentType("text/plain");
				resWriter.write("svc0=" + wsdlUrl);
				int ptId = 0;
				for (String portType : forms.keySet()) {
					resWriter.write("&svc0_pt" + ptId + "=" + portType);
					int opId = 0;
					for (String operationName : forms.get(portType).keySet()) {
						resWriter.write("&svc0_pt" + ptId + "_op" + opId + "="
								+ operationName);
						resWriter.write("&svc0_pt" + ptId + "_op" + opId
								+ "_ui0="
								+ forms.get(portType).get(operationName));
						opId++;
					}
					ptId++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
