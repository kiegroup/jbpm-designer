package de.hpi.petrinet.stepthrough;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import de.hpi.PTnet.PTNet;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn2pn.converter.Preprocessor;
import de.hpi.bpmn2pn.converter.STConverter;
import de.hpi.petrinet.PetriNet;

public class StepThroughServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			res.setContentType("text");
			
			// Load rdf and convert it to a BPMNDiagram
			String rdf = req.getParameter("rdf");
			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes()));
			BPMNRDFImporter importer = new BPMNRDFImporter(document);
			BPMNDiagram diagram = (BPMNDiagram)importer.loadBPMN();
			new Preprocessor(diagram, new BPMNFactory()).process();
			// Produce a PetriNet and create a StepThroughMapper with it
			PetriNet net = new STConverter(diagram).convert();
			STMapper stm = new STMapper((PTNet)net);
			
			// Set whether the client wants to have the state of all resources or just of the last changes
			boolean onlyChangedObjects = false;
			if(req.getParameter("onlyChangedObjects").equals("true")) {
				onlyChangedObjects = true;
			}
			
			String objectsToFireString = req.getParameter("fire");

			// Simulate step by step
			String[] objectsToFire = objectsToFireString.split(";");
			for(int i = 0; i < objectsToFire.length; i++) {
				// If necessary, delete all uninteresting changed objects
				if(onlyChangedObjects) stm.clearChangedObjs();
				// Set AutoSwitchLevel
				String[] objAndLevel = objectsToFire[i].split(",");
				if(objAndLevel.length != 2) continue; // malformed string!
				stm.setAutoSwitchLevel(AutoSwitchLevel.fromInt(Integer.valueOf(objAndLevel[1]).intValue()));
				// and fire
				stm.fireObject(objAndLevel[0]);
			}
			
			// Submit the changed objects
			res.getWriter().print(stm.getChangedObjsAsString());
		}	catch (Exception e) {
			e.printStackTrace();
		}
	}
}
