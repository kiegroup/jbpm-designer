package de.hpi.bpel2bpmn.mapping.structured;

import java.util.List;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpmn.DiagramObject;

public class SequenceMapping extends StructuredActivityMapping {
	
	private static ElementMapping instance = null;
	
	public static ElementMapping getInstance() {
      if(null == instance) {
          instance = new SequenceMapping();
       }
       return instance;
	}

	public void mapElement(Node node, MappingContext mappingContext) {
		if (node.hasChildNodes()) {
			List<Node> activityNodes = getActivityChildNodes(node);
			if (activityNodes.size() > 1) {
				Node start = activityNodes.get(0);
				for (int i = 1; i < activityNodes.size(); i++) {
					Node end = activityNodes.get(i);
					createSequenceFlowBetweenDiagramObjectsOfNodes(start, end, mappingContext);
					start = activityNodes.get(i);
				}
			}
			DiagramObject in  = mappingContext.getMappingConnectionIn().get(activityNodes.get(0));
			DiagramObject out = mappingContext.getMappingConnectionOut().get(activityNodes.get(activityNodes.size() - 1));
			String conditionExpression = mappingContext.getMappingConnectionOutExpression().get(activityNodes.get(activityNodes.size() - 1));
			setConnectionPointsWithControlLinks(node, in, out, conditionExpression, mappingContext);
		}
	}

}
