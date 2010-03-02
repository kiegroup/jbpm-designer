package de.hpi.petrinet.serialization.erdf;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.verification.PetriNetGraphAlgorithms;

public class PetriNeteRDFParserTest {

	@Before
	public void setUp() throws Exception {
	}
	
	@Test public void testParser(){
		PetriNeteRDFParser parser = new PetriNeteRDFParser(readFile("petrinet.example"));
		
		PetriNet net = parser.parse();
		
		assertNotNull(net);
		assertEquals(3, net.getPlaces().size());
		assertEquals(3, net.getTransitions().size());
		assertEquals(6, net.getFlowRelationships().size());
		
		assertEquals(1, PetriNetGraphAlgorithms.getPlaceById(net, "start_place").getOutgoingFlowRelationships().size());
		assertEquals(1, PetriNetGraphAlgorithms.getPlaceById(net, "start_place").getIncomingFlowRelationships().size());
		assertEquals(2, PetriNetGraphAlgorithms.getPlaceById(net, "middle_place").getOutgoingFlowRelationships().size());
	}

	@After
	public void tearDown() throws Exception {
	}

	private Document readFile(String name){
		try {
			File file = new File("editor/test/server/de/hpi/petrinet/serialization/erdf/" + name);
			InputStream input = new FileInputStream(file);
			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			return builder.parse(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
