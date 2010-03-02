package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.DOMSerializer;
import com.sun.org.apache.xml.internal.serialize.Method;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.analysis.BPMNNormalizer;
import de.hpi.bpmn.analysis.BPMNSESENormalizer;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn2bpel.BPMN2BPELTransformer;
import de.hpi.bpmn2bpel.TransformationResult;
import de.hpi.diagram.OryxUUID;

public class BPMN2BPELServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3184239975651510296L;
	
	JSONObject response = null;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) {
		try {
			res.setContentType("application/xhtml");

			String rdf = req.getParameter("data");
			String optionsParam = req.getParameter("options");
			JSONObject options = new JSONObject(optionsParam);

			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes("UTF-8")));
			
			processDocument(document, options, res.getWriter());
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts the transformation BPMN to BPEL according to the options parameter.
	 * It also writes the HTTP-response
	 * 
	 * @param document
	 * 		The diagram from the Oryx-Editor in RDF-format.
	 * @param options
	 * 		The configuration of the transformation e.g. deployment or only transformation.
	 * @param writer
	 * 		The writer of the response.
	 */
	protected void processDocument(Document document, JSONObject options, PrintWriter writer) {
		response = new JSONObject();
		
		String type = new StencilSetUtil().getStencilSet(document);
		BPMNDiagram diagram = null;
		if (type.equals("bpmn.json"))
			diagram = new BPMNRDFImporter(document).loadBPMN();
		else if (type.equals("bpmn1.1.json"))
			diagram = new BPMN11RDFImporter(document).loadBPMN();

		/* Normalize diagram */
		if(diagram.getId() == null) 
			diagram.setId(OryxUUID.generate());
 		BPMNSESENormalizer normalizer = new BPMNSESENormalizer(diagram);
		normalizer.normalize();
		
		
		List<TransformationResult> results = null;
		try {
			BPMN2BPELTransformer transformer = new BPMN2BPELTransformer();
			
			/* Transform to BPEL */
			if (options.getString("action").equals("transform")) {
				results = transformer.transform(diagram);
			}
			
			/* Deployment on Apache ODE */
			else if (options.getString("action").equals("deploy")){
				results = transformer.transformAndDeployProcessOnOde(
						diagram,
						options.getString("apacheOdeUrl"));
			}
			
			for(TransformationResult result : results) {
				if(result.getType().equals(TransformationResult.Type.PROCESS)) {
					appendResult("process", result.getDocument());
				}
				if(result.getType().equals(TransformationResult.Type.SERVICE_NAME)) {
					response.put(
							"serviceName", 
							options.getString("apacheOdeUrl") +
							"/processes/" +
							(String) result.getObject() +
							"?wsdl");
				}
//			if(result.getType().equals(TransformationResult.Type.DEPLOYMENT_DESCRIPTOR)) {
//				appendResult("deploy", result.getDocument());
//			}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		writer.write(response.toString());
	}
	
	private void appendResult(String param, Document result) {
		
		StringWriter sw = new StringWriter();
		DOMBuilder builder = new DOMBuilder();
		org.jdom.Document jdomDoc = builder.build(result);
		
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		
		try {
//			out.output(jdomDoc, System.out);
			out.output(jdomDoc, sw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
//		OutputFormat format = new OutputFormat(result);
//		format.setIndenting(true);
//		format.setPreserveSpace(true);
//		format.setLineSeparator(System.getProperty("line.separator"));
//		format.setMethod(Method.XML);
		
		
//		XMLSerializer serial = new XMLSerializer(sw, format);
//		try {
//			DOMSerializer domserial = serial.asDOMSerializer();
//			domserial.serialize(result);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		
		try {
			response.put(param, sw.getBuffer().toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		if(response.length() == 0) {
//			response.append(param);
//			response.append("=");
//			response.append(sw.getBuffer().toString());
//		} else {
//			response.append("&");
//			response.append(param);
//			response.append("=");
//			response.append(sw.getBuffer().toString());
//		}
	}

	 

}
