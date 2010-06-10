package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
import de.hpi.bpmn2yawl.BPMN2YAWLConverter;
import de.hpi.bpmn2yawl.BPMN2YAWLNormalizer;
import de.hpi.bpmn2yawl.BPMN2YAWLResourceMapper;
import de.hpi.bpmn2yawl.BPMN2YAWLSyntaxChecker;

/**
 * Copyright (c) 2009
 * 
 * @author Armin Zamani
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
 * s
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class BPMN2YAWLServlet extends HttpServlet {
	private static final long serialVersionUID = -4589304713944851993L;

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			//TODO correct mime type??
			res.setContentType("application/zip");

			String rdf = req.getParameter("data");

			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes()));

			byte[] array = processDocument(document, res);
			if(array ==null )
				return;
			ServletOutputStream out = res.getOutputStream();
			res.setContentType("application/zip"); 
			res.setHeader("Content-Disposition","attachment; filename=" + "yawl.zip");
			res.setHeader("Vary", "Accept-Encoding");
			out.write(array);
			out.flush();

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * process the document to map it to YAWL
	 * @param document
	 * @param res
	 */
	// Create a buffer for reading the files
	byte[] buf = new byte[1024];
	protected byte[] processDocument(Document document, HttpServletResponse res) throws IOException {
		String yawlXML, yawlOrgDataXML;
		String type = new StencilSetUtil().getStencilSet(document);

		BPMNDiagram diagram = getDiagram(document, type);

		//STEP 1: Syntax-Checking
		BPMN2YAWLSyntaxChecker checker = new BPMN2YAWLSyntaxChecker(diagram);
		boolean isOK = checker.checkSyntax();
		if(!isOK){
			res.getWriter().print(checker.getErrorsAsJson().toString());
			return null;
		}

		//STEP 2: Normalization
		//normalize the given diagram for easier mapping
		BPMN2YAWLNormalizer normalizerForBPMN = new BPMN2YAWLNormalizer(diagram);
		normalizerForBPMN.normalizeForYAWL();

		boolean noEmptyTasks = checker.checkForNonEmptyTasks(diagram);
		if(!noEmptyTasks){
			res.getWriter().print(checker.getErrorsAsJson().toString());
			return null;
		}

		//STEP 3: resource mapping
		BPMN2YAWLResourceMapper resourcing = new BPMN2YAWLResourceMapper();
		yawlOrgDataXML = resourcing.translate(diagram);
		
//		ZipOutputStream out = new ZipOutputStream(res.getOutputStream());
		ByteArrayOutputStream bout=new ByteArrayOutputStream(); 
		ZipOutputStream out = new ZipOutputStream(bout);
		putNewZipEntry("OryxBPMNToYAWL.ybkp", yawlOrgDataXML, out);


		//YawlXmlValidator validator = new YawlXmlValidator();

		//STEP 4: control and data flow mapping
		int numberOfPools = diagram.getProcesses().size();
		for(int i = 0; i < numberOfPools; i++){
			BPMN2YAWLConverter converter = new BPMN2YAWLConverter();
			yawlXML = converter.translate(diagram, i, resourcing.getNodeMap());
			putNewZipEntry("OryxBPMNToYAWL_" + i + ".yawl", yawlXML, out);
		}
	
		out.flush();
		bout.flush();
		out.close();
		bout.close();
		return bout.toByteArray();
	}

	/**
	 * @param name of the entry
	 * @param content content to write
	 * @param zipstream stream to add an entry
	 * @throws IOException
	 */
	private void putNewZipEntry(String name, String content, ZipOutputStream zipstream)
			throws IOException {
		zipstream.putNextEntry(new ZipEntry(name));
		ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes());
		// Transfer bytes from the String to the ZIP file
		int len;
		while ((len = stream.read(buf)) > 0) {
			zipstream.write(buf, 0, len);
		}
		// Complete the entry
		zipstream.closeEntry();
		stream.close();
	}

	/**
	 * calls the appropiate RDF Importer
	 * @param document
	 * @param type
	 * @return the BPMN Diagram
	 */
	private BPMNDiagram getDiagram(Document document, String type) {
		if (type.equals("bpmn.json"))
			return new BPMNRDFImporter(document).loadBPMN();
		else if (type.equals("bpmn1.1.json"))
			return new BPMN11RDFImporter(document).loadBPMN();
		else
			return null;
	}
}