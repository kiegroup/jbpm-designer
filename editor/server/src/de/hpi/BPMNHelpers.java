package de.hpi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn2pn.converter.HighConverter;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.serialization.PetriNetPNMLExporter;

public class BPMNHelpers {
	public static void printBPMN(BPMNDiagram diagram){
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document pnmlDoc = builder.newDocument();
			PetriNet net = new HighConverter(diagram).convert();
			PetriNetPNMLExporter exp = new PetriNetPNMLExporter();
			exp.savePetriNet(pnmlDoc, net);
			OutputFormat format = new OutputFormat(pnmlDoc);
			StringWriter stringOut = new StringWriter();
			XMLSerializer serial2 = new XMLSerializer(stringOut, format);
			serial2.asDOMSerializer();
			serial2.serialize(pnmlDoc.getDocumentElement());
			
			new File("C:\\Dokumente und Einstellungen\\Kai\\Eigene Dateien\\Downloads\\test.pnml").delete();
			new RandomAccessFile("C:\\Dokumente und Einstellungen\\Kai\\Eigene Dateien\\Downloads\\test.pnml", "rw").writeBytes(stringOut.toString());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static BPMNDiagram loadRDFDiagram(String fileName) {
		try{
			RandomAccessFile file = new RandomAccessFile("C:\\Dokumente und Einstellungen\\Kai\\Eigene Dateien\\Downloads\\"+fileName, "r");
			String rdf = "";
			String zeile;
			while ( (zeile = file.readLine()) != null){
				rdf += zeile;
			}
			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes()));
			BPMNRDFImporter importer = new BPMNRDFImporter(document);
			BPMNDiagram diagram = (BPMNDiagram) importer.loadBPMN();
			return diagram;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}
}
