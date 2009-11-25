package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import de.hpi.bpmn2pn.BPMN2PNSyntaxChecker;
import de.hpi.diagram.verification.SyntaxChecker;
import de.hpi.epc.rdf.EPCDiagramRDFImporter;
import de.hpi.epc.validation.EPCSyntaxChecker;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.rdf.IBPMNRDFImporter;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.serialization.InteractionNetRDFImporter;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.serialization.erdf.PetriNeteRDFParser;
import de.hpi.petrinet.verification.PetriNetSyntaxChecker;

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
	
	/** This can be used to set which syntax for a given stencil set should be used
	 * E.g., BPMN diagrams could have a special syntax checker for step through and
	 * bpmn2pn conversion
	*/ 
	private String context;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			res.setContentType("text/json");
			
			String isJson = req.getParameter("isJson");
			
			if(isJson.equals("true")) {
				String json = req.getParameter("data");
				
				processDocument(json, res.getWriter());
			} else {
				String rdf = req.getParameter("data");
				
				context = req.getParameter("context");
				
				DocumentBuilder builder;
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				builder = factory.newDocumentBuilder();
				Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes("UTF-8")));
				
				processDocument(document, res.getWriter());			
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BpmnConverterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void processDocument(Document document, PrintWriter writer) {
		String type = new StencilSetUtil().getStencilSet(document);
		SyntaxChecker checker = null;
		if(type != null){
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
		}
		
		if(checker == null) {//try eRDF
			try {
				NamedNodeMap map = XPathAPI.selectSingleNode(document, "//a[@rel='oryx-stencilset']").getAttributes();
				type = map.getNamedItem("href").getNodeValue();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			if(type != null && type.endsWith("petrinet.json")){
				checker = getCheckerPetriNet(document);
			}
		}

		if (checker == null) {
			writer.print("{}");
		} else {
			checker.checkSyntax();
			writer.print(checker.getErrorsAsJson().toString());
		}
	}
	
	protected void processDocument(String jsonDocument, PrintWriter writer) throws JSONException, BpmnConverterException {
		Diagram diagram = DiagramBuilder.parseJson(jsonDocument);
		
		String type = diagram.getStencilset().getNamespace();
		SyntaxChecker checker = null;
		
		if(type != null && (type.equals("http://b3mn.org/stencilset/bpmn2.0#") ||
				type.equals("http://b3mn.org/stencilset/bpmn2.0choreography#") ||
				type.equals("http://b3mn.org/stencilset/bpmn2.0conversation#"))) {
			checker = getCheckerBPMN2(diagram);
		}
		
		if (checker == null) {
			writer.print("{}");
		} else {
			checker.checkSyntax();
			writer.print(checker.getErrorsAsJson().toString());
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
		if(context != null && context.equals("bpmn2pn")){
			return new BPMN2PNSyntaxChecker(diagram);
		} else {
			return diagram.getSyntaxChecker();
		}
	}
	
	protected SyntaxChecker getCheckerBPMN2(Diagram diagram) throws BpmnConverterException {
		Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram);
		
		Definitions defs = converter.getDefinitionsFromDiagram();
		return defs.getSyntaxChecker();
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
		return new EPCSyntaxChecker(importer.loadEPCDiagram());
	}
	
	protected SyntaxChecker getCheckerPetriNet(Document document) {
		PetriNeteRDFParser parser = new PetriNeteRDFParser(document);
		PetriNet petrinet = parser.parse();
		return new PetriNetSyntaxChecker(petrinet);
	}
}
