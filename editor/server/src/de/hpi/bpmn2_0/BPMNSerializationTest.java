package de.hpi.bpmn2_0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;
import org.oryxeditor.server.diagram.JSONBuilder;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;
import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;

public class BPMNSerializationTest {

	final static String path = "C:\\Users\\Sven Wagner-Boysen\\workspace\\oryx3\\editor\\server\\src\\de\\hpi\\bpmn2_0\\";
	final static String schemaFilePath = "C:\\Users\\Sven Wagner-Boysen\\workspace\\oryx3\\editor\\lib\\xsd\\bpmn20\\Bpmn20.xsd";
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		toBpmn2_0();
		// fromXml();

	}

	public static void toBpmn2_0() throws Exception {
		File json = new File(path + "trivial_bpmn2.0_process.json");
		BufferedReader br = new BufferedReader(new FileReader(json));
		String bpmnJson = "";
		String line;
		while ((line = br.readLine()) != null) {
			bpmnJson += line;
		}
		Diagram diagram = DiagramBuilder.parseJson(bpmnJson);

		Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram);
		Definitions def = converter.getDefinitionsFromDiagram();

		// def.getOtherAttributes().put(new QName("xmlns:local"), "nurlokal");

		// final XMLStreamWriter xmlStreamWriter = XMLOutputFactory
		// .newInstance().createXMLStreamWriter(System.out);
		//		
		// xmlStreamWriter.setPrefix("bpmndi","http://bpmndi.org");

		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Marshaller m = context.createMarshaller();

		/* Schema validation */
		SchemaFactory sf = SchemaFactory
				.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(new File(schemaFilePath));
		m.setSchema(schema);
		
		ExportValidationEventCollector vec = new ExportValidationEventCollector();
		m.setEventHandler(vec);
		

		// m.setListener(new ListenerTest());
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		NamespacePrefixMapper nsp = new BPMNPrefixMapper();
		m.setProperty("com.sun.xml.bind.namespacePrefixMapper", nsp);
		m.marshal(def, System.out);
		ValidationEvent[] events = vec.getEvents();
		
		StringBuilder builder = new StringBuilder();
		builder.append("Validation Errors: \n\n");
		
		for(ValidationEvent event : Arrays.asList(events)) {
			
			builder.append("Line: ");
			builder.append(event.getLocator().getLineNumber());
			builder.append(" Column: ");
			builder.append(event.getLocator().getColumnNumber());
			
			builder.append("\nError: ");
			builder.append(event.getMessage());
			builder.append("\n\n\n");
		}
		
		System.out.println(builder.toString());

		// SyntaxChecker checker = def.getSyntaxChecker();
		// checker.checkSyntax();
		//		
		// System.out.println(checker.getErrorsAsJson().toString());
	}
	

	public static void fromXml() throws JAXBException, JSONException {
		File xml = new File(path + "bpmntest.xml");
		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Definitions def = (Definitions) unmarshaller.unmarshal(xml);

		BPMN2DiagramConverter converter = new BPMN2DiagramConverter("/oryx/");
		List<Diagram> dia = converter.getDiagramFromBpmn20(def);

		System.out.println(JSONBuilder.parseModeltoString(dia.get(0)));
	}
}