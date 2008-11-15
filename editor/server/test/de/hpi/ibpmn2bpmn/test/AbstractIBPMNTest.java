package de.hpi.ibpmn2bpmn.test;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import de.hpi.bpmn.Container;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Pool;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.Interaction;
import de.hpi.ibpmn.rdf.IBPMNRDFImporter;

public class AbstractIBPMNTest extends TestCase {

	public static final String path = "server/test/samples_ibpmn_rdf/";
	
    protected IBPMNDiagram loadIBPMNDiagramFromRDF(String fileName) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(fileName));

		IBPMNRDFImporter importer = new IBPMNRDFImporter(document);
		return (IBPMNDiagram)importer.loadIBPMN();
	}
	
	protected Node addInteraction(Node n, String label, Pool senderRole, Pool receiverRole, IBPMNDiagram ibpmn) {
		Interaction i = (Interaction)addNode(n, label, ibpmn);
		i.setSenderRole(senderRole);
		i.setReceiverRole(receiverRole);
		return (Node)i;
	}

	protected Node addNode(Node n, String label, Container parent) {
		n.setLabel(label);
		n.setParent(parent);
		return n;
	}

	protected void addSequenceFlow(Edge edge, Node source, Node target, IBPMNDiagram ibpmn) {
		edge.setSource(source);
		edge.setTarget(target);
		ibpmn.getEdges().add(edge);
	}

}
