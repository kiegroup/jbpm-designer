package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.iaas.bpel.BPELFactory;
import de.iaas.bpel.models.BPELDiagram;
import de.iaas.bpel.models.DiagramObject;
import de.iaas.bpel.models.Process;
import de.iaas.bpel.rdf.BPELRDFImporter;


/**
 * Copyright (c) 2008 
 * 
 * Zhen Peng
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
public class BPELExporter extends HttpServlet {
	private static final long serialVersionUID = -8374877061121257562L;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		try {
			res.setContentType("text/bpel+xml");
			
			String rdf = req.getParameter("data");

			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document sourceDocument = builder.parse(new ByteArrayInputStream(rdf.getBytes()));
			Document bpelDoc = builder.newDocument();
			System.out.println("**********************");
			System.out.print(sourceDocument);
		//	processBPEL(sourceDocument, bpelDoc);
			
		//	OutputFormat format = new OutputFormat(bpelDoc);

		//	StringWriter stringOut = new StringWriter();
		//	XMLSerializer serial2 = new XMLSerializer(stringOut, format);
		//	serial2.asDOMSerializer();

		//	serial2.serialize(bpelDoc.getDocumentElement());
		//	res.getWriter().print(stringOut.toString());
			res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + sourceDocument.toString());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	protected void processBPEL(Document sourceDocument, Document bpelDoc) {
		BPELRDFImporter importer = new BPELRDFImporter(sourceDocument);
		BPELDiagram diagram = (BPELDiagram) importer.loadBPEL();
		diagram.identifyProcesses();
		// we have already limited that, each diagram contains just one bpel process
		Process process = diagram.getProcess();		
		Element root = (Element) bpelDoc.appendChild(bpelDoc.createElement("process"));
		parsingElement(root,(DiagramObject)process);
	}
	

	protected void parsingElement(Element xmlElement, DiagramObject element){
		parsingProperties (xmlElement, element);
	}

	protected void parsingProperties (Element xmlElement, DiagramObject element){
		
	}
	
	protected String getStencilSet(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("rdf:RDF"))
			return null;
		
		node = node.getFirstChild();
		while (node != null) {
			 String about = getAttributeValue(node, "rdf:about");
			 if (about != null && about.contains("canvas")) break;
			 node = node.getNextSibling();
		}
		String type = getAttributeValue(getChild(node, "stencilset"), "rdf:resource");
		if (type != null)
			return type.substring(type.lastIndexOf('/')+1);
		
		return null;
	}

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
