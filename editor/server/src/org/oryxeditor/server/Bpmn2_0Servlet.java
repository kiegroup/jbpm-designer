package org.oryxeditor.server;

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

import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.json.JSONObject;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.hpi.bpmn2_0.ExportValidationEventCollector;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;

/**
 * This servlet provides the access point to the interchange format of BPMN 2.0
 * 
 * @author Sven Wagner-Boysen
 *
 */
public class Bpmn2_0Servlet extends HttpServlet {

	private static final long serialVersionUID = -4308758083419724953L;
	
	/**
	 * The post request
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
		String json = req.getParameter("data");
		boolean asXML = req.getParameter("xml") != null;
		
		/* Transform and return from DI */
		try {
			StringWriter output = this.performTransformationToDi(json, asXML);
			res.setContentType("application/xml");
			res.setStatus(200);
			res.getWriter().print(output.toString());
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
	 * Triggers the transformation from Diagram to BPMN model and writes the 
	 * resulting BPMN XML on success.
	 * 
	 * @param json
	 * 		The diagram in JSON format
	 * @param writer
	 * 		The HTTP-response writer
	 * @throws Exception
	 * 		Exception occurred while processing
	 */
	protected StringWriter performTransformationToDi(String json, boolean asXML) throws Exception {
		StringWriter writer = new StringWriter();
		JSONObject result = new JSONObject();
		
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
		
		/* Set Schema validation properties */
		SchemaFactory sf = SchemaFactory
				.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		String xsdPath = this.getServletContext().getRealPath("/WEB-INF/lib/bpmn20/BPMN20.xsd");
		
		Schema schema = sf.newSchema(new File(xsdPath));
		marshaller.setSchema(schema);
		
		ExportValidationEventCollector vec = new ExportValidationEventCollector();
		marshaller.setEventHandler(vec);
		
		/* Marshal BPMN 2.0 XML */
		marshaller.marshal(bpmnDefinitions, writer);
		
		if(asXML) {
			return writer;
		}
		
		result.put("xml", writer.toString());
		
		/* Append XML Schema validation results */
		if(vec.hasEvents()) {
			ValidationEvent[] events = vec.getEvents();
			StringBuilder builder = new StringBuilder();
			builder.append("Validation Errors: <br /><br />");
			
			for(ValidationEvent event : Arrays.asList(events)) {
				
				builder.append("Line: ");
				builder.append(event.getLocator().getLineNumber());
				builder.append(" Column: ");
				builder.append(event.getLocator().getColumnNumber());
				
				builder.append("<br />Error: ");
				builder.append(event.getMessage());
				builder.append("<br /><br />");
			}
			result.put("validationEvents", builder.toString());
		}
		
		/* Prepare output */
		writer = new StringWriter();
		writer.write(result.toString());
		
		return writer;		
	}

}
