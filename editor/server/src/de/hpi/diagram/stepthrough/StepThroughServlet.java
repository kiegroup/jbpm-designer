package de.hpi.diagram.stepthrough;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.oryxeditor.server.StencilSetUtil;
import org.w3c.dom.Document;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn2pn.converter.Preprocessor;
import de.hpi.bpmn2pn.converter.STConverter;
import de.hpi.bpt.process.epc.EPCFactory;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.util.OryxParser;
import de.hpi.epc.stepthrough.EPCStepThroughInterpreter;
import de.hpi.petrinet.stepthrough.AutoSwitchLevel;
import de.hpi.petrinet.stepthrough.STMapper;
import de.hpi.petrinet.stepthrough.STSyntaxChecker;

public class StepThroughServlet extends HttpServlet {
	// The servlet is responsible for getting the Ajax request,
	// checking for errors,
	// creating all necessary objects,
	// translating the executionTrace into commands for the STMapper
	// and writing a string into the answer of the request.
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			res.setContentType("text");
			
			// Load rdf and convert it to a BPMNDiagram
			String rdf = req.getParameter("rdf");
			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes("UTF-8")));
			
			processDocument(document, req, res);
		}	catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void processDocument(Document document, HttpServletRequest req, HttpServletResponse res) {
		String type = new StencilSetUtil().getStencilSet(document);
		
		String initialMarking = req.getParameter("initialMarking");
		
		IStepThroughInterpreter stm = null;
		
		if (type.equals("bpmn.json") || type.equals("bpmn1.1.json")){
			try{
				// Check the syntax?
				if (req.getParameter("checkSyntax").equals("true")) {
					BPMNDiagram diagram = loadBPMN(document);
					// Check Diagram in Syntax and Compatibility
					STSyntaxChecker checker = new STSyntaxChecker(diagram);
					checker.checkSyntax(true);
					if (checker.getErrors().size() > 0) {
						PrintWriter writer = res.getWriter();
						// Announce errors
						writer.print("!errors!");
						// Write errors into the output, if any exist
						for (Entry<String, String> error : checker.getErrors()
								.entrySet()) {
							res.getWriter().print(
									error.getKey() + ":" + error.getValue() + ";");
						}
						
						// Stop further execution
						return;
					}
				}
				
				// Produce a BPMN2PN converter and create a StepThroughMapper with it
				stm = new STMapper(loadConverter(document));
			} catch (IOException e){
				e.printStackTrace();
			}
		} else if (type.equals("epc.json")){
			try{
				IEPC epcDiagram = new OryxParser(new EPCFactory()).parse(document).get(0); 
				EPCStepThroughInterpreter epcStm = new EPCStepThroughInterpreter(epcDiagram);
				epcStm.setInitialMarking(Arrays.asList(initialMarking.split(";")));
				stm = epcStm;
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		try{
			stm.setAutoSwitchLevel(AutoSwitchLevel.SemiAuto);
			
			// Set whether the client wants to have the state of all resources or just of the last changes
			boolean onlyChangedObjects = false;
			if(req.getParameter("onlyChangedObjects").equals("true")) {
				onlyChangedObjects = true;
			}
			
			String objectsToFireString = req.getParameter("fire");
	
			// Simulate step by step
			String[] objectsToFire = objectsToFireString.split(";");
			for (int i = 0; i < objectsToFire.length; i++) {
				// If necessary, delete all uninteresting changed objects
				if(onlyChangedObjects) stm.clearChangedObjs();
				// Check for proper string: While initializing step through, there is 
				// an post with empty fire header. This seems to result in 
				// objectsToFire = [""] which cannot be fired.
				if(objectsToFire[i].trim().equals("")) continue;
				// and fire
				stm.fireObject(objectsToFire[i]);
			}
			
			// Submit the changed objects
			res.getWriter().print(stm.getChangedObjsAsString());
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	private STConverter loadConverter(Document document) {
		BPMNDiagram diagram = null;
		String type = new StencilSetUtil().getStencilSet(document);
		if (type.equals("bpmn.json")) {
			diagram = new BPMNRDFImporter(document).loadBPMN();
		} else if (type.equals("bpmn1.1.json")) {
			diagram = new BPMN11RDFImporter(document).loadBPMN();
		}
		
		new Preprocessor(diagram, new BPMNFactory()).process();
		return new STConverter(diagram);
	}
	
	private BPMNDiagram loadBPMN(Document document) {
		String type = new StencilSetUtil().getStencilSet(document);
		if (type.equals("bpmn.json"))
			return new BPMNRDFImporter(document).loadBPMN();
		else if (type.equals("bpmn1.1.json"))
			return new BPMN11RDFImporter(document).loadBPMN();
		else 
			return null;
	}
}
