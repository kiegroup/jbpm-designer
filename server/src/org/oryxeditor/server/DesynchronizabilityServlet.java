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

import org.apache.commons.configuration.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.converter.IBPMNConverter;
import de.hpi.ibpmn.rdf.IBPMNRDFImporter;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.localmodelgeneration.DesynchronizabilityChecker;
import de.hpi.interactionnet.rdf.InteractionNetRDFImporter;
import de.hpi.petrinet.SyntaxChecker;
import de.hpi.petrinet.Transition;

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
public class DesynchronizabilityServlet extends HttpServlet {
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
			String type = getStencilSet(document);
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

		new DesynchronizabilityChecker().check(net, conflictingTransitions);
	}

	protected void processIPN(Document document, List<Transition> conflictingTransitions) throws SyntaxErrorException {
		InteractionNetRDFImporter importer = new InteractionNetRDFImporter(document);
		InteractionNet net = (InteractionNet) importer.loadInteractionNet();
		SyntaxChecker checker = net.getSyntaxChecker();
		if (!checker.checkSyntax())
			throw new SyntaxErrorException(checker.getErrors());
		
		new DesynchronizabilityChecker().check(net, conflictingTransitions);
	}


	
	protected String getStencilSet(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("rdf:RDF"))
			return null;
		
		node = node.getFirstChild();
		while (node != null) {
			 String about = getAttributeValue(node, "rdf:about");
			 if (about != null && about.contains("oryxcanvas")) break;
			 node = node.getNextSibling();
		}
		String type = getAttributeValue(getChild(node, "stencilset"), "rdf:resource");
		if (type != null)
			return type.substring(type.lastIndexOf('/')+1);
		
		return null;
	}

//	protected String getContent(Node node) {
//		if (node != null && node.hasChildNodes())
//			return node.getFirstChild().getNodeValue();
//		return null;
//	}
	
	private String getAttributeValue(Node node, String attribute) {
		Node item = node.getAttributes().getNamedItem(attribute);
		if (item != null)
			return item.getNodeValue();
		else
			return null;
	}

	private Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().indexOf(name) >= 0) 
				return node;
		return null;
	}

}
