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

import org.apache.commons.configuration.Configuration;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn2pn.converter.StandardConverter;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.pnml.PetriNetPNMLExporter;

/**
 * Copyright (c) 2008 Lutz Gericke
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
public class SimplePNMLExporter extends HttpServlet {
	private static final long serialVersionUID = -8374877061121257562L;
	private static Configuration config = null;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			res.setContentType("text/pnml+xml");

			String rdf = req.getParameter("data");

			DocumentBuilder builder;
			BPMNDiagram diagram;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes()));
			BPMNRDFImporter importer = new BPMNRDFImporter(document);
			diagram = (BPMNDiagram) importer.loadBPMN();

			// URL only for testing purposes...
			PetriNet net = new StandardConverter(diagram).convert();
			Document pnmlDoc = builder.newDocument();

			PetriNetPNMLExporter exp = new PetriNetPNMLExporter();
			exp.savePetriNet(pnmlDoc, net);
			OutputFormat format = new OutputFormat(pnmlDoc);

			StringWriter stringOut = new StringWriter();
			XMLSerializer serial2 = new XMLSerializer(stringOut, format);
			serial2.asDOMSerializer();

			serial2.serialize(pnmlDoc.getDocumentElement());

			res.getWriter().print(stringOut.toString());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
