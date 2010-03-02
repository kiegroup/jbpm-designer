package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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
import de.hpi.bpmn.validation.BPMNSyntaxChecker;
import de.hpi.diagram.verification.SyntaxChecker;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.converter.IBPMNConverter;
import de.hpi.ibpmn.rdf.IBPMNRDFImporter;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.enforceability.EnforceabilityChecker;
import de.hpi.interactionnet.localmodelgeneration.DesynchronizabilityChecker;
import de.hpi.interactionnet.serialization.InteractionNetRDFImporter;
import de.hpi.petrinet.Transition;

/**
 * Copyright (c) 2008 Gero Decker, Philipp Berger
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
public class EnforceabilityServlet extends HttpServlet {
	private static final long serialVersionUID = -8374877061121257562L;
	
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
		List<Transition> conflictingTransitions = new ArrayList<Transition>();
		
		try {
			String type = new StencilSetUtil().getStencilSet(document);
			if (type.equals("ibpmn.json"))
				processIBPMN(document, conflictingTransitions);
			else if (type.equals("interactionpetrinets.json"))
				processIPN(document, conflictingTransitions);

			writer.print("{\"conflicttransitions\": [");
			boolean isFirst = true;
			for (Transition t: conflictingTransitions) {
				if (isFirst)
					isFirst = false;
				else
					writer.print(",");
				writer.print("\""+t.getId()+"\"");
			}
			writer.print("]}");

		} catch (SyntaxErrorException e) {
			writer.print("{\"syntaxerrors\": {");
			boolean isFirst = true;
			for (Entry<String,String> error: e.getErrors().entrySet()) {
				if (isFirst)
					isFirst = false;
				else
					writer.print(",");
				writer.print("\""+error.getKey()+"\": \""+error.getValue()+"\"");
			}
			writer.print("}}");
		}
	}
	
	protected void processIBPMN(Document document, List<Transition> conflictingTransitions) throws SyntaxErrorException {
		IBPMNRDFImporter importer = new IBPMNRDFImporter(document);
		BPMNDiagram diagram = (IBPMNDiagram) importer.loadIBPMN();
		BPMNSyntaxChecker checker = diagram.getSyntaxChecker();
		if (!checker.checkSyntax())
			throw new SyntaxErrorException(checker.getErrors());

		InteractionNet net = (InteractionNet)new IBPMNConverter(diagram).convert();

		new EnforceabilityChecker(net).checkEnforceability();
	}

	protected void processIPN(Document document, List<Transition> conflictingTransitions) throws SyntaxErrorException {
		InteractionNetRDFImporter importer = new InteractionNetRDFImporter(document);
		InteractionNet net = (InteractionNet) importer.loadInteractionNet();
		SyntaxChecker checker = net.getSyntaxChecker();
		if (!checker.checkSyntax())
			throw new SyntaxErrorException(checker.getErrors());
		
		new EnforceabilityChecker(net).checkEnforceability();
	}


	
}
