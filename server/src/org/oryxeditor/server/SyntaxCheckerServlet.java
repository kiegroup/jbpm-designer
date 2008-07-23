package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.diagram.Diagram;
import de.hpi.epc.rdf.EPCDiagramRDFImporter;
import de.hpi.epc.validation.EPCSyntaxChecker;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.rdf.IBPMNRDFImporter;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.serialization.InteractionNetRDFImporter;
import de.hpi.petrinet.verification.SyntaxChecker;

/**
 * Copyright (c) 2008 Gero Decker
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class SyntaxCheckerServlet extends HttpServlet {
	private static final long serialVersionUID = 929153463101368351L;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			res.setContentType("text/json");

			String rdf = req.getParameter("data");

			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes()));
			
			processDocument(document, res.getWriter());
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	protected void processDocument(Document document, PrintWriter writer) {
		String type = new StencilSetUtil().getStencilSet(document);
		SyntaxChecker checker = null;
		if (type.equals("bpmn.json") || type.equals("bpmneec.json"))
			checker = getCheckerBPMN(document);
		else if (type.equals("bpmn1.1.json"))
			checker = getCheckerBPMN11(document);
		else if (type.equals("ibpmn.json"))
			checker = getCheckerIBPMN(document);
		else if (type.equals("interactionpetrinets.json"))
			checker = getCheckerIPN(document);
		else if (type.equals("epc.json"))
			checker = getCheckerEPC(document);

		if (checker == null || checker.checkSyntax()) {
			writer.print("{}");
		} else {
			writer.print("{");
			boolean isFirst = true;
			for (Entry<String,String> error: checker.getErrors().entrySet()) {
				if (isFirst)
					isFirst = false;
				else
					writer.print(",");
				writer.print("\""+error.getKey()+"\": \""+error.getValue()+"\"");
			}
			writer.print("}");
		}
	}
	
	protected SyntaxChecker getCheckerBPMN(Document document) {
		BPMNRDFImporter importer = new BPMNRDFImporter(document);
		BPMNDiagram diagram = importer.loadBPMN();
		return diagram.getSyntaxChecker();
	}

	protected SyntaxChecker getCheckerBPMN11(Document document) {
		BPMN11RDFImporter importer = new BPMN11RDFImporter(document);
		BPMNDiagram diagram = importer.loadBPMN();
		return diagram.getSyntaxChecker();
	}

	protected SyntaxChecker getCheckerIBPMN(Document document) {
		IBPMNRDFImporter importer = new IBPMNRDFImporter(document);
		BPMNDiagram diagram = (IBPMNDiagram) importer.loadIBPMN();
		return diagram.getSyntaxChecker();
	}

	protected SyntaxChecker getCheckerIPN(Document document) {
		InteractionNetRDFImporter importer = new InteractionNetRDFImporter(document);
		InteractionNet net = (InteractionNet) importer.loadInteractionNet();
		return net.getSyntaxChecker();
	}
	
	protected SyntaxChecker getCheckerEPC(Document document) {
		EPCDiagramRDFImporter importer = new EPCDiagramRDFImporter(document);
		Diagram diagram = importer.loadEPCDiagram();
		return new EPCSyntaxChecker(diagram);
	}

}
