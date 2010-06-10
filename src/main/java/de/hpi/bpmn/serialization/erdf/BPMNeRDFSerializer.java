package de.hpi.bpmn.serialization.erdf;

import java.util.Collection;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.serialization.BPMNSerializer;


public class BPMNeRDFSerializer implements BPMNSerializer {
	
	public String serializeBPMNDiagram(BPMNDiagram bpmnDiagram) {
		
		StringBuilder eRDF = new StringBuilder();
		
		BPMNeRDFSerialization serialization = new BPMNeRDFSerialization(bpmnDiagram);
		
		eRDF.append(serialization.getSerializationHeader());

		for(Edge edge : bpmnDiagram.getEdges()) {
			eRDF.append(edge.getSerialization(serialization));
		}
		
		this.addSerializationOfChildNodes(eRDF, serialization, bpmnDiagram.getChildNodes());

		eRDF.append(serialization.getSerializationFooter());

		return eRDF.toString();
		
	}
	
	private void addSerializationOfChildNodes(StringBuilder eRDF, BPMNeRDFSerialization serialization, Collection<Node> nodes) {
		for(Node node : nodes) {
			eRDF.append(node.getSerialization(serialization));
			if (node instanceof SubProcess) {
				addSerializationOfChildNodes(eRDF, serialization, ((SubProcess)node).getChildNodes());
			}
		}
	}

}
