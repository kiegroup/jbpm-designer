package de.hpi.bpel2bpmn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

/**
 * Parses the BPEL file and does the XML validation. In case validation is not
 * successful the private member validationException is set.
 * 
 * Main method: parseBPELFile(String bpelFile)
 * 
 * @author matthias.weidlich
 *
 */
public class BPELParser {
	
	protected static final String BPEL_ABSTRACT_NS = "http://docs.oasis-open.org/wsbpel/2.0/process/abstract";
	
	private static final String PATH_TO_LIB_DIR = File.separator + "webapps" + File.separator + "oryx" + File.separator + "WEB-INF" + File.separator + "lib" + File.separator;

	protected static final String FILENAME_BPEL_EXECUTABLE_SCHEMA = System.getProperty("catalina.home") + PATH_TO_LIB_DIR + "ws-bpel_executable.xsd";
	protected static final String FILENAME_BPEL_ABSTRACT_SCHEMA = System.getProperty("catalina.home") + PATH_TO_LIB_DIR + "ws-bpel_abstract.xsd";
	protected static final String FILENAME_XML_DEFINITION_SCHEMA = System.getProperty("catalina.home") + PATH_TO_LIB_DIR + "xml.xsd";
		
	protected boolean successfulValidation = false;

	protected String validationException = "";
	
	/**
	 * We do not want to fetch the XML definition schema
	 * every time we check a BPEL file (it is imported by the BPEL Schema).
	 * Therefore, we need the resolver to use a local copy of the
	 * XML definition schema.
	 *
	 * @author matthias.weidlich
	 *
	 */
	private class BPELResolver implements LSResourceResolver {
		
	     private DOMImplementationLS domImplementationLS;

	     public BPELResolver(DOMImplementationLS domImplementationLS) {
	          this.domImplementationLS = domImplementationLS;
	     }
	     
		public LSInput resolveResource(String arg0, String arg1, String arg2, String arg3, String arg4) {

			if (arg0.equals("http://www.w3.org/2001/XMLSchema") &&
					arg1.equals("http://www.w3.org/XML/1998/namespace")) {
				LSInput ret = domImplementationLS.createLSInput();
				try {
					// try to get the local copy of the schema
					ret.setByteStream(new FileInputStream(FILENAME_XML_DEFINITION_SCHEMA));
				    return ret;
				} catch (Exception e) {
					return null;
				}
			}
				
			return null;
		}
		
	}
	
	public Document parseBPELFile(String bpelFile) {
		InputStream bpelStream = null;
		try {
			bpelStream = new FileInputStream(bpelFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (bpelStream != null) {
			return parseBPELFile(bpelStream);
		}
		else {
			return null;
		}
	}
	
	
		
	public Document parseBPELFile(InputStream bpelStream) {
	
       // System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

       // Get Document Builder Factory
       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
              
       // turn off validation, we use the new validation package
       factory.setValidating(false);
       // ignore comments
       factory.setIgnoringComments(true);
       // turn on namespaces
       factory.setNamespaceAware(true);
       
       Document doc = null;
       
        try {
        	
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(bpelStream);
            
            if (doc != null) {
            	
            	//System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema","org.apache.xerces.jaxp.validation.XMLSchemaFactory");
	            
            	// Validate
	            SchemaFactory constraintFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
                DOMImplementationLS domImplementationLS = (DOMImplementationLS)registry.getDOMImplementation("LS");
                if (domImplementationLS != null) {
		            // set our own resolver implementation
	                constraintFactory.setResourceResolver(new BPELResolver(domImplementationLS));
                }
	            // should we validate against "executable" or "abstract" BPEL ?
	            // default is "executable"
                Source bpelConstraints = new StreamSource(new File(FILENAME_BPEL_EXECUTABLE_SCHEMA));
	            if (doc.getDocumentElement().getNamespaceURI() != null) {
		            if (doc.getDocumentElement().getNamespaceURI().equals(BPEL_ABSTRACT_NS)) {
			            bpelConstraints = new StreamSource(new File(FILENAME_BPEL_ABSTRACT_SCHEMA));
		            }
	            }
	            
	            Schema schema = constraintFactory.newSchema(bpelConstraints);
	            Validator validator = schema.newValidator();
	            try {
		            validator.validate(new DOMSource(doc));
					this.successfulValidation = true;
				} catch (SAXException e) {
					// schema validation failed!
					// we continue with the transformation, however, the user is notified
					this.successfulValidation = false;
					this.validationException = e.getMessage();
				}
            }
        } catch (Exception e) {
        	// a lot can go wrong, no chance to recover
			e.printStackTrace();        	
        }
        
		return doc;
	}

	public String getValidationException() {
		return validationException.replaceAll("\"", "").replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\n", "");
	}

	public boolean isSuccessfulValidation() {
		return successfulValidation;
	}
	
}
