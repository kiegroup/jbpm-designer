package de.hpi.petrinet.pnml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLFileLoaderSaver {

    public Document loadDocumentFromFile(String fileName) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(fileName));

		return document;
	}
    
    public Document createNewDocument() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
    	return document;
    }
	
	public void saveDocumentToFile(Document doc, String fileName) throws TransformerFactoryConfigurationError, FileNotFoundException, TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource        source = new DOMSource( doc );
		FileOutputStream os     = new FileOutputStream( new File(fileName) );
		StreamResult     result = new StreamResult( os );
		transformer.transform( source, result );
	}
	
}
