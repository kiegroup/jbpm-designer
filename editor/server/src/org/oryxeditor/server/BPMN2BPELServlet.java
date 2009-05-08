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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.DOMSerializer;
import com.sun.org.apache.xml.internal.serialize.Method;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.analysis.BPMNNormalizer;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn2bpel.BPMN2BPELTransformer;
import de.hpi.bpmn2bpel.TransformationResult;

public class BPMN2BPELServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3184239975651510296L;
	
	StringBuilder response = new StringBuilder();
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) {
		try {
			res.setContentType("application/xhtml");

			String rdf = req.getParameter("data");

			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes("UTF-8")));
			
			processDocument(document, res.getWriter());
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void processDocument(Document document, PrintWriter writer) {
		String type = new StencilSetUtil().getStencilSet(document);
		BPMNDiagram diagram = null;
		if (type.equals("bpmn.json"))
			diagram = new BPMNRDFImporter(document).loadBPMN();
		else if (type.equals("bpmn1.1.json"))
			diagram = new BPMN11RDFImporter(document).loadBPMN();

		/* Normalize diagram */
 		BPMNNormalizer normalizer = new BPMNNormalizer(diagram);
		normalizer.normalize();
		
		/* Transform to BPEL */
		BPMN2BPELTransformer transformer = new BPMN2BPELTransformer();
		List<TransformationResult> results = transformer.transform(diagram);
		
		for(TransformationResult result : results) {
			if(result.getType().equals(TransformationResult.Type.PROCESS)) {
				appendResult("process", result.getDocument());
			}
			if(result.getType().equals(TransformationResult.Type.DEPLOYMENT_DESCRIPTOR)) {
				appendResult("deploy", result.getDocument());
			}
		}
	}
	
	private void appendResult(String param, Document result) {
		OutputFormat format = new OutputFormat(result);
		format.setIndenting(true);
		format.setPreserveSpace(true);
		format.setLineSeparator(System.getProperty("line.separator"));
		format.setMethod(Method.XHTML);
		
		
		StringWriter sw = new StringWriter();
		XMLSerializer serial = new XMLSerializer(sw, format);
		try {
			DOMSerializer domserial = serial.asDOMSerializer();
			domserial.serialize(result);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(response.length() == 0) {
			response.append(param);
			response.append("=");
			response.append(sw.getBuffer().toString());
		} else {
			response.append("&");
			response.append(param);
			response.append("=");
			response.append(sw.getBuffer().toString());
		}
	}

	 

}
