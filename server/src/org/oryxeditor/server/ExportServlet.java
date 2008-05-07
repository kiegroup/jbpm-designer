package org.oryxeditor.server;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn2execpn.converter.ExecConverter;
import de.hpi.execpn.ExecPetriNet;
import de.hpi.execpn.pnml.ExecPNPNMLExporter;
import de.hpi.petrinet.PetriNet;

/**
 * Copyright (c) 2007 Alexander Koglin Copyright (c) 2008 Lutz Gericke
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
public class ExportServlet extends HttpServlet {
	private static Configuration config = null;
	private static final long serialVersionUID = 2381703508756797343L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("text/html");
		try {
			if (config == null) {
				config = new PropertiesConfiguration("pnengine.properties");
			}
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String postVariable = config.getString("pnengine.post_variable");
		String engineURL = config.getString("pnengine.url") + "/petrinets";
		String modelURL = config.getString("pnengine.default_model_url");
		String formURL = null;
		String bindingsURL = null;
		
		String rdf = req.getParameter("data");
		String diagramTitle = req.getParameter("title");

		DocumentBuilder builder;
		BPMNDiagram diagram;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes()));
			BPMNRDFImporter importer = new BPMNRDFImporter(document);
			diagram = (BPMNDiagram) importer.loadBPMN();

			// URL only for testing purposes...
			PetriNet net = new ExecConverter(diagram, modelURL).convert();			
			ExecPetriNet execnet = (ExecPetriNet)net;
			Document pnmlDoc = builder.newDocument();

			ExecPNPNMLExporter exp = new ExecPNPNMLExporter();
			execnet.setName(diagramTitle);
			exp.savePetriNet(pnmlDoc, execnet);

			String basefilename = String.valueOf(System.currentTimeMillis());
			String tmpPNMLFile = this.getServletContext().getRealPath("/") + "tmp" + File.separator + basefilename + ".pnml";
			BufferedWriter out1 = new BufferedWriter(new FileWriter(tmpPNMLFile));

			OutputFormat format = new OutputFormat(pnmlDoc);
			XMLSerializer serial = new XMLSerializer(out1, format);
			serial.asDOMSerializer();
			serial.serialize(pnmlDoc.getDocumentElement());
			out1.close();

			StringWriter stringOut = new StringWriter();
			XMLSerializer serial2 = new XMLSerializer(stringOut, format);
			serial2.asDOMSerializer();

			serial2.serialize(pnmlDoc.getDocumentElement());

			URL url_engine = new URL(engineURL);
			HttpURLConnection connection_engine = (HttpURLConnection) url_engine.openConnection();
			connection_engine.setRequestMethod("POST");

			String encoding = new sun.misc.BASE64Encoder().encode("testuser:".getBytes());
			connection_engine.setRequestProperty("Authorization", "Basic " + encoding);

			connection_engine.setUseCaches(false);
			connection_engine.setDoInput(true);
			connection_engine.setDoOutput(true);

			String escaped_content = postVariable + "=" + URLEncoder.encode(stringOut.toString(), "UTF-8");

			connection_engine.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection_engine.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");

			// connection_engine.setRequestProperty("Content-Length",
			// ""+escaped_content.getBytes().length);
			String errorMessage = null;
			try {
				connection_engine.getOutputStream().write(escaped_content.getBytes());
				connection_engine.connect();
			} catch (ConnectException e) {
				errorMessage = e.getMessage();
			}

			// output link address
			res.getWriter().print("tmp/" + basefilename + ".pnml" + "\">View PNML</a><br/><br/>");
			if (errorMessage == null && connection_engine.getResponseCode() == 200) {
				res.getWriter().println(
						"Deployment to Engine <a href=\"" + engineURL + "\" target=\"_blank\">" + engineURL
								+ "</a><br/><b>successful</b>!");
			} else {
				res.getWriter().println("Deployment to Engine <a href=\"" 
						+ engineURL + "\" target=\"_blank\">" 
						+ engineURL + "</a><br/><b>failed</b> with message: \n" 
						+ errorMessage + "!");
			}

		} catch (ParserConfigurationException e1) {
			res.getWriter().println(e1.getMessage());
		} catch (SAXException e1) {
			res.getWriter().println(e1.getMessage());
		}

	}
}
