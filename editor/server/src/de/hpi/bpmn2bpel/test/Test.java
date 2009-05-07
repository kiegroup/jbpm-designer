package de.hpi.bpmn2bpel.test;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.sun.org.apache.xml.internal.serialize.DOMSerializer;
import com.sun.org.apache.xml.internal.serialize.Method;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.analysis.BPMNSESENormalizer;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn2bpel.BPMN2BPELTransformer;

public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		String path = "C:\\Dokumente und Einstellungen\\Sven\\workspace\\oryx2\\editor\\server\\src\\de\\hpi\\bpmn2bpel\\test\\";
//		File file = new File(path + "AndGatewayTest-Oryx.xml");
//		File file = new File(path + "bpel_trivial1.xml");
		File file = new File(path + "bpel_trivial2.xml");
		
		
		try {
			System.out.println("Start Test:");
			
			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			
			BPMNDiagram diagram = null;
			diagram = new BPMN11RDFImporter(document).loadBPMN();
			
			BPMNSESENormalizer normalizer = new BPMNSESENormalizer(diagram);
			normalizer.normalize();
			
			BPMN2BPELTransformer transformer = new BPMN2BPELTransformer();
			Document doc = transformer.transform(diagram);


			OutputFormat format = new OutputFormat(doc);
			format.setIndenting(true);
			format.setPreserveSpace(true);
			format.setLineSeparator(System.getProperty("line.separator"));
			format.setMethod(Method.XHTML);
			
			
			StringWriter sw = new StringWriter();
			XMLSerializer serial = new XMLSerializer(sw, format);
			DOMSerializer domserial = serial.asDOMSerializer();
			domserial.serialize(doc);
			System.out.println(sw.getBuffer().toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
