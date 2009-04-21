package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.analysis.BPMNNormalizer;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn2bpel.BPMN2BPELTransformer;

public class BPMN2BPELServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3184239975651510296L;
	
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
		Document bpel = transformer.transform(diagram);
		
		writer.write(bpel.toString());
	}

	 

}
