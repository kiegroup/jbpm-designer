package de.hpi.petrinet;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.petrinet.serialization.erdf.PetriNeteRDFParser;



public abstract class AbstractPetriNetTest {

	protected FlowRelationship createFlowRelationship(PetriNet net, Node source, Node target){
		FlowRelationship rel = new FlowRelationship();
		rel.setSource(source);
		rel.setTarget(target);
		net.getFlowRelationships().add(rel);
		return rel;
	}

	protected Transition createTransition(PetriNet net){
		return createTransition(net, null);
	}
	
	
	protected Transition createTransition(PetriNet net, String id){
		Transition trans = new LabeledTransitionImpl();
		trans.setId(id);
		net.getTransitions().add(trans);
		return trans;
	}
	
	protected Place createPlace(PetriNet net, String id){
		Place place = new PlaceImpl();
		place.setId(id);
		net.getPlaces().add(place);
		return place;
	}
	
	static protected PetriNet openPetriNetFromFile(String fileName){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File("editor/test/server/de/hpi/petrinet/"+fileName));

			return (new PetriNeteRDFParser(document).parse());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
