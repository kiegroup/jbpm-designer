package de.hpi.epc;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.bpt.process.epc.EPCFactory;
import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.IFlowObject;
import de.hpi.bpt.process.epc.util.OryxParser;

public class AbstractEPCTest {
	protected IEPC epc;

	protected IControlFlow connect(IFlowObject source, IFlowObject target){
		return epc.addControlFlow(source, target);
	}
	
	protected IFlowObject add(IFlowObject fo){
		epc.addFlowObject(fo);
		return fo;
	}
	
	static protected IEPC openEpcFromFile(String fileName){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File("editor/test/server/de/hpi/epc/"+fileName));

			return (new OryxParser(new EPCFactory())).parse(document).get(0);
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