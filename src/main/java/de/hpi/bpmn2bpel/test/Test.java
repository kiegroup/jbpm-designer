package de.hpi.bpmn2bpel.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.analysis.BPMNSESENormalizer;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn2bpel.BPMN2BPELTransformer;
import de.hpi.bpmn2bpel.TransformationResult;

public class Test {
	
	static StringBuilder response;

	static String path = "src/main/webapp/repository/";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) {
//		String path = "C:\\Users\\sven.wagner-boysen\\workspace\\oryx\\editor\\server\\src\\de\\hpi\\bpmn2bpel\\test\\";
//		File file = new File(path + "AndGatewayTest-Oryx.xml");
//		File file = new File(path + "bpel_trivial1.xml");
//		File file = new File(path + "bpel_trivial2.xml");
//		File file = new File(path + "bpel_sequence_test.xml");
//		File file = new File(path + "bpel_dataobject.xml");
//		File file = new File(path + "bpel_calculator_test.xml");
//		File file = new File(path + "bpel_calculator2_test.xml");
//		File file = new File(path + "bpel_calculator_with_dataobject_test.xml");
//		File file = new File(path + "bpel_uplink_signal_test.xml");
		File file = new File(path + "a.rdf");
//		file = new File("src/main/java/de/hpi/bpmn2bpel/test", "odeTestBPEL.xml");
		
		
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
//			List<TransformationResult> results = transformer.transform(diagram);
			List<TransformationResult> results = transformer.transformAndDeployProcessOnOde(diagram, "http://localhost:8080/ode");
			
			response = new StringBuilder();
			
			File processDir = null;
			String processName = null;
			for(TransformationResult result : results) {
				if(result.getType().equals(TransformationResult.Type.PROCESS)) {
					processName =((org.w3c.dom.Element) result.getDocument().getElementsByTagName("process").item(0)).getAttribute("id");
					processDir = new File(path, processName + File.separator + processName + ".ode");
					processDir.mkdirs();
					appendResult("process", result.getDocument(),new File(processDir, processName + ".bpel"));
				}
				if(result.getType().equals(TransformationResult.Type.DEPLOYMENT_DESCRIPTOR)) {
					appendResult("deploy", result.getDocument(), new File(processDir, "deploy.xml"));
				}
				if(result.getType().equals(TransformationResult.Type.PROCESS_WSDL)) {
					appendResult("process_wsdl", result.getDocument(), new File(processDir, "Invoke" + processName.substring(0,1).toUpperCase() + processName.substring(1) + ".wsdl"));
				}
			}
			
			System.out.println(response.toString());

//			OutputFormat format = new OutputFormat(doc);
//			format.setIndenting(true);
//			format.setPreserveSpace(true);
//			format.setLineSeparator(System.getProperty("line.separator"));
//			format.setMethod(Method.XHTML);
//			
//			
//			StringWriter sw = new StringWriter();
//			XMLSerializer serial = new XMLSerializer(sw, format);
//			DOMSerializer domserial = serial.asDOMSerializer();
//			domserial.serialize(doc);
//			System.out.println(sw.getBuffer().toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void appendResult(String param, Document doc, File  file) {
//		String xmlString = null;
//		try {
//			Transformer transformer =TransformerFactory.newInstance().newTransformer();
//			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//			//initialize StreamResult with File object to save to file
//			StreamResult result = new StreamResult(new StringWriter());
//			DOMSource source = new DOMSource(doc);
//			transformer.transform(source, result);
//			xmlString = result.getWriter().toString();
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		System.out.println(xmlString);
		
		DOMBuilder builder = new DOMBuilder();
		org.jdom.Document jdomDoc = builder.build(doc);
		
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		try {
			out.output(jdomDoc, new FileOutputStream(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		OutputFormat format = new OutputFormat(result);
//		format.setIndenting(true);
//		format.setIndent(4);
//		format.setPreserveSpace(true);
//		format.setLineSeparator(System.getProperty("line.separator"));
//		format.setMethod(Method.XHTML);
//		
//		
//		XMLSerializer serial = new XMLSerializer(sw, format);
//		try {
//			DOMSerializer domserial = serial.asDOMSerializer();
//			domserial.serialize(result);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		
//		if(response.length() == 0) {
//			response.append(param);
//			response.append("=");
////			response.append(sw.getBuffer().toString());
//			response.append(xmlString);
//		} else {
//			response.append("&");
//			response.append(param);
//			response.append("=");
//			response.append(xmlString);
//		}
	}

}
