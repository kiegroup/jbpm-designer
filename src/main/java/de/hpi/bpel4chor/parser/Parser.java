package de.hpi.bpel4chor.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.hpi.bpel4chor.util.Output;

import de.hpi.bpel4chor.model.Diagram;

/**
 * Parses the given XPDL4Chor diagram to the internal diagram representation.
 * Sets up the parser configuration and starts the {@link DiagramParser}.
 */
public class Parser {
	
	/**
	 * Sets up the document builder for parsing the XPDL4Chor.
	 *  
	 * @param validate True, if the XPDL4Chor diagram should be validated 
	 * against the XPDL4Chor schema before the parsing.
	 * @param output The output to print erros to. 
	 * 
	 * @return The document builder
	 */
	private static DocumentBuilder setUpBuilder(boolean validate, final Output output) {
		DocumentBuilderFactory factory = 
			DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		factory.setValidating(validate);				
		
		factory.setAttribute(
			    "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
			    "http://www.w3.org/2001/XMLSchema");
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new ErrorHandler() {
				public void error(SAXParseException exception) {
					output.addError(exception);
				}
	
				public void fatalError(SAXParseException exception) {
					output.addError(exception);
				}
	
				public void warning(SAXParseException exception) {
					output.addError(exception);
				}
			});
			return builder;
		} catch (ParserConfigurationException e) {
			output.addError(e);
		}
		return null;
		
	}
	
	/**
	 * Parses the XPDL4Chor diagram serialized as string into the internal
	 * diagram representation. The document root of the XPDL4Chor diagram must be 
	 * a "Package" element.
	 * 
	 * @param content  XPDL4Chor diagram serialized as String.
	 * @param validate True, if the XPDL4Chor diagram should be validated 
	 *                 against the XPDL4Chor schema before the parsing.
	 * @param output   The output to print errors to.
	 * 
	 * @return	The diagram parsed from the XPDL4Chor diagram. The result is null,
	 * if the diagram could not be parsed.
	 */
	public static Diagram parse(String content, boolean validate, Output output) {
		Diagram diagram = null;
		
		InputStream input = 
			new ByteArrayInputStream(content.getBytes());
		
		try {
			DocumentBuilder builder = setUpBuilder(validate, output);
			Document doc = builder.parse(input);
			if (doc.getDocumentElement().getLocalName().equals("Package")) {
				DiagramParser parser = new DiagramParser(output);
				diagram = parser.parseDiagram(doc.getDocumentElement());
			} else {
				output.addParseError("The document root is not a package element.", doc.getDocumentElement());
			}
		} catch (SAXException e) {
			output.addError(e);
		} catch (IOException e) {
			output.addError(e);
		}
		return diagram;
	}
}
