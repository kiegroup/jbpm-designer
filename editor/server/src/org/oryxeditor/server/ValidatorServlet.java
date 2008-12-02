package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn.validation.BPMNValidator;
import de.hpi.bpt.process.epc.EPCFactory;
import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.util.OryxParser;
import de.hpi.epc.Marking;
import de.hpi.epc.validation.EPCSoundnessChecker;

/**
 * Copyright (c) 2008 Kai Schlichting
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
public class ValidatorServlet extends HttpServlet {
	private static final long serialVersionUID = 730839148324562928L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			res.setContentType("text/json");

			String rdf = req.getParameter("data");

			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes()));
			
			processDocument(document, res.getWriter());
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	protected void processDocument(Document document, PrintWriter writer) {
		String type = new StencilSetUtil().getStencilSet(document);
		if (type.equals("bpmn.json") || type.equals("bpmneec.json")){
			processBPMN(new BPMNRDFImporter(document).loadBPMN(), writer);
		} else if (type.equals("bpmn1.1.json")) {
			processBPMN(new BPMN11RDFImporter(document).loadBPMN(), writer);
		} else if (type.equals("epc.json")) {
			try{
				processEPC(new OryxParser(new EPCFactory()).parse(document), writer);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	protected void processBPMN(BPMNDiagram diagram, PrintWriter writer){
		BPMNValidator validator = new BPMNValidator(diagram);
		validator.validate();
		
		// { leadToEnd : true/false,  conflicting : [{ id : <id>}, { id : <id>}, ...] } 
		
		try{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("leadsToEnd", validator.leadsToEnd);
			
			JSONArray conflictingNodes = new JSONArray();
			for(DiagramObject node: validator.getConflictingBPMNNodes()){
				JSONObject nodeObject = new JSONObject();
				nodeObject.put("id", node.getId());
				conflictingNodes.put(nodeObject);
			}
			
			jsonObject.put("conflictingNodes", conflictingNodes);
			
			writer.print(jsonObject.toString());
		} catch (JSONException exception) {
			exception.printStackTrace();
		}
	}
	
	protected void processEPC(List<IEPC> epcs, PrintWriter writer){
		IEPC epc = epcs.get(0);
		EPCSoundnessChecker soundChecker = new EPCSoundnessChecker(epc);
		soundChecker.calculate();
		
		try{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("isSound", soundChecker.isSound());
			
			JSONArray badStartArcs = new JSONArray();
			for(IControlFlow node: soundChecker.badStartArcs){
				JSONObject nodeObject = new JSONObject();
				nodeObject.put("id", node.getId());
				badStartArcs.put(nodeObject);
			}
			jsonObject.put("badStartArcs", badStartArcs);
			
			JSONArray badEndArcs = new JSONArray();
			for(IControlFlow node: soundChecker.badEndArcs){
				JSONObject nodeObject = new JSONObject();
				nodeObject.put("id", node.getId());
				badEndArcs.put(nodeObject);
			}
			jsonObject.put("badEndArcs", badEndArcs);
			
			JSONArray goodInitialMarkings = new JSONArray();
			for(Marking marking: soundChecker.goodInitialMarkings){
				JSONArray goodInitialMarking = new JSONArray();
				for(IControlFlow cf : marking.filterByState(epc.getControlFlow(), Marking.State.POS_TOKEN)){
					JSONObject nodeObject = new JSONObject();
					nodeObject.put("id", cf.getId());
					goodInitialMarking.put(nodeObject);
				}
				goodInitialMarkings.put(goodInitialMarking);
			}
			jsonObject.put("goodInitialMarkings", goodInitialMarkings);
			
			JSONArray goodFinalMarkings = new JSONArray();
			for(Marking marking: soundChecker.goodFinalMarkings){
				JSONArray goodFinalMarking = new JSONArray();
				for(IControlFlow cf : marking.filterByState(epc.getControlFlow(), Marking.State.POS_TOKEN)){
					JSONObject nodeObject = new JSONObject();
					nodeObject.put("id", cf.getId());
					goodFinalMarking.put(nodeObject);
				}
				goodFinalMarkings.put(goodFinalMarking);
			}
			jsonObject.put("goodFinalMarkings", goodFinalMarkings);
			
			writer.print(jsonObject.toString());
		} catch (JSONException exception) {
			exception.printStackTrace();
		}
	}
}
