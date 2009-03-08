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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.serialization.erdf.PetriNeteRDFParser;
import de.hpi.petrinet.verification.PetriNetSoundnessChecker;

public class PetriNetSoundnessCheckerServlet extends HttpServlet {
	private static final long serialVersionUID = -3215102566003538575L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			//TODO correct mime type??
			res.setContentType("application/xhtml");

			String rdf = req.getParameter("data");

			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes("UTF-8")));
			
			processDocument(document, res.getWriter());
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	protected void processDocument(Document document, PrintWriter writer) {
		String type = new StencilSetUtil().getStencilSet(document);
		PetriNet net = new PetriNeteRDFParser(document).parse();

		PetriNetSoundnessChecker checker = new PetriNetSoundnessChecker(net);
		checker.calculateRG();
				
		JSONObject object = new JSONObject();
		
		try {
			object.put("isSound", checker.isSound());
			object.put("isWeakSound", checker.isWeakSound());
			object.put("isRelaxedSound", checker.isRelaxedSound());
			
			object.put("deadLocks", checker.getDeadLocksAsJson());
			
			object.put("deadTransitions", checker.getDeadTransitionsAsJson());
			
			object.put("improperTerminatings", checker.getImproperTerminatingsAsJson());
			
			object.put("notParticipatingTransitions", checker.getNotParticipatingTransitionsAsJson());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		writer.write(object.toString());
	}
}
