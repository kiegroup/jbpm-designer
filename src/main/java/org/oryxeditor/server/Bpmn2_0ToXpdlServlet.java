/**
 * Copyright (c) 2009 
 * 
 * Sven Wagner-Boysen
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

package org.oryxeditor.server;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;

/**
 * This servlet provides an XPDL2.1 Export of BPMN 2.0 using the XSLT Style
 * Sheet provided by the BPMN 2.0 user group.
 * 
 * @author Sven Wagner-Boysen
 * 
 */
public class Bpmn2_0ToXpdlServlet extends HttpServlet {

	private static final long serialVersionUID = 5410535711130039207L;

	/**
	 * The post request
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		String json = req.getParameter("data");

		/* Transform and return from DI */
		try {
			StringWriter output = this.performTransformationToDi(json);

			StringReader reader = new StringReader(output.toString());

			Document doc = new SAXBuilder().build(reader);
			Source xmlFile = new JDOMSource(doc);
			JDOMResult xpdlResult = new JDOMResult();

			InputStream xsltStream = this.getServletContext()
					.getResourceAsStream("/WEB-INF/lib/bpmn2xpdl.xsl");

			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer(
							new StreamSource(xsltStream));
			transformer.transform(xmlFile, xpdlResult);

			XMLOutputter xmlOutputter = new XMLOutputter();

			res.setContentType("application/xml");
			res.setStatus(200);
			xmlOutputter.output(xpdlResult.getDocument(), res.getWriter());
		} catch (Exception e) {
			try {
				e.printStackTrace();
				res.setStatus(500);
				res.setContentType("text/plain");
				res.getWriter().write(e.getCause().getMessage());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * Performs the generation of BPMN 2.0 XML and triggers the XSLT
	 * transformation.
	 * 
	 * @param json
	 *            The diagram in JSON format
	 * @param writer
	 *            The HTTP-response writer
	 * @throws Exception
	 *             Exception occurred while processing
	 */
	protected StringWriter performTransformationToDi(String json)
			throws Exception {
		StringWriter writer = new StringWriter();

		/* Retrieve diagram model from JSON */

		Diagram diagram = DiagramBuilder.parseJson(json);

		/* Build up BPMN 2.0 model */
		Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram);
		Definitions bpmnDefinitions = converter.getDefinitionsFromDiagram();

		/* Perform XML creation */
		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		NamespacePrefixMapper nsp = new BPMNPrefixMapper();
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", nsp);

		marshaller.marshal(bpmnDefinitions, writer);

		return writer;
	}
}
