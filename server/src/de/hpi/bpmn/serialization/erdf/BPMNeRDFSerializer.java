package de.hpi.bpmn.serialization.erdf;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.serialization.BPMNSerializer;


public class BPMNeRDFSerializer implements BPMNSerializer {
	
	public String serializeBPMNDiagram(BPMNDiagram bpmnDiagram) {
		
		StringBuilder eRDF = new StringBuilder();
		
		BPMNeRDFSerialization serialization = new BPMNeRDFSerialization(bpmnDiagram);
		
		eRDF.append(serialization.getSerializationHeader());

		for(Edge edge : bpmnDiagram.getEdges()) {
			eRDF.append(edge.getSerialization(serialization));
		}
		
		for(Node node : bpmnDiagram.getChildNodes()) {
			eRDF.append(node.getSerialization(serialization));
		}

		eRDF.append(serialization.getSerializationFooter());

		return eRDF.toString();
		
	}

}
