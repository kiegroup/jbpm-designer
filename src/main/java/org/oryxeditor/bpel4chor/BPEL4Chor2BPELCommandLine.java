package org.oryxeditor.bpel4chor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BPEL4Chor2BPELCommandLine {

	//private static Logger log = Logger.getLogger("BPEL4Chor2BPEL");

	/**
	 * Main method
	 * can be used to call the transformation from the command line instead of calling it via Oryx
	 * 
	 * @param argv
	 */
	public static void main(String argv[]) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		
		
		File file = new File(".");
		File[] topology = file.listFiles(new FilenameFilter() {public boolean accept(File dir, String name) {return name.endsWith("topology.xml");}});
		if (topology.length != 1) {
			throw new Exception("topology error");
		}
		Document docTopo = docBuilder.parse(topology[0]);

		File[] grounding = file.listFiles(new FilenameFilter() {public boolean accept(File dir, String name) {return name.endsWith("grounding.bpel");}});
		if (grounding.length != 1) {
			throw new Exception("grounding error");
		}
		Document docGround = docBuilder.parse(grounding[0]);
		
		File[] pbds = file.listFiles(new FilenameFilter() {public boolean accept(File dir, String name) {
			if (!name.equals("grounding.bpel")){ return name.endsWith(".bpel");}
			else return false ;
		}});
		if (pbds.length == 0) {
			throw new Exception("No PBDs found");
		}
		ArrayList<Document> pbdDocs = new ArrayList<Document>(pbds.length);
		for (File f: pbds) {
			Document res = docBuilder.parse(f);
			pbdDocs.add(res);
		}
		
		

		// assumption: working directory is the directory where the files are included
		// This is ensured by the Eclipse Debug configuration -..-> Arguments -> Working directory
		//Document docGround = docBuilder.parse("groundingSA.bpel");
		//Document docTopo = docBuilder.parse("topologySA.xml");
		//Document docPBD = docBuilder.parse("processSA.bpel");

		//ArrayList<Document> pbdDocs = new ArrayList<Document>();
		//pbdDocs.add(docPBD);
		
		BPEL4Chor2BPEL t = new BPEL4Chor2BPEL();
		List<Document> res = t.convert((Element) docGround.getFirstChild(), (Element) docTopo.getFirstChild(), pbdDocs);

		// setup reusable transformer
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
        // Setup indenting to "pretty print"
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        /**************************output of the converted PBD******************************/
//		for (Document currentPBD: res) {
//			Source sourceBPEL = new DOMSource(currentPBD);
			
			//File bpelFile = new File("/home/eysler/work/DiplomArbeit/oryx-editor/editor/server/src/org/oryxeditor/bpel4chor/testFiles/PBDConvertion.bpel");
			for(int i = 0; i<res.size()/2; i++){
				File bpelFile = new File("process(" + (i+1) + ")-converted.bpel");
				Result resultBPEL = new StreamResult(bpelFile);
				
				Source sourceBPEL = new DOMSource(res.get(i));
				
				// Write the converted docPBD to the file
				xformer.transform(sourceBPEL, resultBPEL);
			}
			
			/*************************output of the created wsdl****************************/
			for(int i = res.size()/2; i < res.size(); i++){
				File wsdlFile = new File("process(" + (i+1-res.size()/2) + ")-converted.wsdl");
				Result resultWSDL = new StreamResult(wsdlFile);
				
				Source sourceWSDL = new DOMSource(res.get(i));
				
				// Write the converted docPBD to the file
				xformer.transform(sourceWSDL, resultWSDL);
			}
//		}
	}
	

	
}
