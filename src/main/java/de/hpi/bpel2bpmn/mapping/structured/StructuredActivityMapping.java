package de.hpi.bpel2bpmn.mapping.structured;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.hpi.bpel2bpmn.mapping.ElementMappingImpl;


public abstract class StructuredActivityMapping extends  ElementMappingImpl {

	
	protected List<Node> getActivityChildNodes(Node parent) {
		List<Node> activityNodes = new ArrayList<Node>();
		for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (!(child instanceof Text))
				activityNodes.add(child);
		}
		return activityNodes;
	}
	
	protected boolean isScopeNode(Node node) {
		return node.getNodeName().equalsIgnoreCase("scope");
	}
	
}
