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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.serialization.erdf.PetriNeteRDFParser;
import de.hpi.petrinet.verification.MaxNumOfStatesReachedException;
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
			
			try {
				processDocument(document, res.getWriter());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	protected void processDocument(Document document, PrintWriter writer) throws JSONException {
		String type = new StencilSetUtil().getStencilSet(document);
		PetriNet net = new PetriNeteRDFParser(document).parse();

		JSONObject object = new JSONObject();
		JSONArray errors = new JSONArray();
		
		try {			
			PetriNetSoundnessChecker checker = new PetriNetSoundnessChecker(net);
			checker.calculateRG();
			
			object.put("isSound", checker.isSound());
			object.put("isWeakSound", checker.isWeakSound());
			object.put("isRelaxedSound", checker.isRelaxedSound());
			
			object.put("deadLocks", checker.getDeadLocksAsJson());
			
			object.put("deadTransitions", checker.getDeadTransitionsAsJson());
			
			object.put("improperTerminatings", checker.getImproperTerminatingsAsJson());
			
			object.put("notParticipatingTransitions", checker.getNotParticipatingTransitionsAsJson());
		} catch (MaxNumOfStatesReachedException e){
			errors.put("ORYX.I18N.PNSoundnessChecker.errors.maxNumOfStatesReached");
		}
		
		object.put("errors", errors);
		
		writer.write(object.toString());
	}
}
