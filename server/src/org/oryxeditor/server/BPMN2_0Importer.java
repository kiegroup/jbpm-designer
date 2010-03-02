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

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.JSONBuilder;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;

/**
 * Servlet to generate JSON from BPMN 2.0 XML
 * 
 * @author Sven Wagner-Boysen
 * 
 */
public class BPMN2_0Importer extends HttpServlet {

	private static final long serialVersionUID = -8687832449710203280L;

	/**
	 * The post request
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		String bpmn20Xml = req.getParameter("data");

		/* Transform and return from DI */
		try {
			StringWriter output = this.getJsonFromBpmn20Xml(bpmn20Xml);
			res.setContentType("application/json");
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

	private StringWriter getJsonFromBpmn20Xml(String bpmn20Xml) throws JAXBException, JSONException {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		
		StringReader reader = new StringReader(bpmn20Xml);

		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Definitions def = (Definitions) unmarshaller.unmarshal(reader);
		
		BPMN2DiagramConverter converter = new BPMN2DiagramConverter("/" + this.getServletContext().getServletContextName() + "/");
		List<Diagram> dia = converter.getDiagramFromBpmn20(def);
		
		out.print(JSONBuilder.parseModeltoString(dia.get(0)));

		return writer;
	}

}
