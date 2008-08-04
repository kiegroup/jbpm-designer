package de.hpi.petrinet.stepthrough;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.oryxeditor.server.StencilSetUtil;
import org.w3c.dom.Document;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn.rdf.BPMNRDFImporter;

public class StepThroughCheckerServlet extends HttpServlet {

	private static final long serialVersionUID = 3925194456829964422L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			res.setContentType("text");
			
			// Load rdf and convert it to a BPMNDiagram
			String rdf = req.getParameter("rdf");
			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes()));

			BPMNDiagram diagram = loadBPMN(document);
			
			// Check Diagram in Syntax and Compatibility
			STSyntaxChecker checker = new STSyntaxChecker(diagram);
			checker.checkSyntax(true);
			
			if (checker.getErrors().size() > 0) {
				// Write errors into the output, if any exist
				for (Entry<String, String> error : checker.getErrors().entrySet()) {
					res.getWriter().print(error.getKey() + ":" + error.getValue() + ";");
				}
			}
			else {
				res.getWriter().print("");
			}
		}	catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BPMNDiagram loadBPMN(Document document) {
		String type = new StencilSetUtil().getStencilSet(document);
		if (type.equals("bpmn.json"))
			return new BPMNRDFImporter(document).loadBPMN();
		else if (type.equals("bpmn1.1.json"))
			return new BPMN11RDFImporter(document).loadBPMN();
		else 
			return null;
	}
	
}
