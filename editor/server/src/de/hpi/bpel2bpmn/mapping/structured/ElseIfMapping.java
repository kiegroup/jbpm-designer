package de.hpi.bpel2bpmn.mapping.structured;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.SequenceFlow.ConditionType;

public class ElseIfMapping extends StructuredActivityMapping {

	private static ElementMapping instance = null;
	
	public static ElementMapping getInstance() {
      if(null == instance) {
          instance = new ElseIfMapping();
       }
       return instance;
	}

	public void mapElement(Node node, MappingContext mappingContext) {

		Node ifNode = node.getParentNode();
		DiagramObject start = mappingContext.getMappingConnectionIn().get(ifNode);
		DiagramObject end = mappingContext.getMappingConnectionOut().get(ifNode);
			
		Node activityNode = BPEL2BPMNMappingUtil.getActivityChildNode(node);
		

		/*
		 * Are we in a 'else' or 'else if' block?
		 */
		if (node.getNodeName().equalsIgnoreCase("elseif")) {
			// get the condition
			String elseIfCondition = "";
			Node conditionNode = BPEL2BPMNMappingUtil.getSpecificChildNode(node,"condition");
			if (conditionNode != null) {
				elseIfCondition = conditionNode.getTextContent();
			}
			createSequenceFlowBetweenDiagramObjects(start, mappingContext.getMappingConnectionIn().get(activityNode), 
					elseIfCondition,
					mappingContext);
		}
		else {
			// create the default flow
			SequenceFlow sequenceFlow = mappingContext.getFactory().createSequenceFlow();
			sequenceFlow.setConditionType(ConditionType.DEFAULT);
			sequenceFlow.setSource(start);
			sequenceFlow.setTarget(mappingContext.getMappingConnectionIn().get(activityNode));
			mappingContext.getDiagram().getEdges().add(sequenceFlow);
		}
		
		/*
		 * Connect to the gateway indicating the end of the 'if' mapping block
		 */
		createSequenceFlowBetweenDiagramObjects(mappingContext.getMappingConnectionOut().get(activityNode), end,
					mappingContext.getMappingConnectionOutExpression().get(activityNode),
					mappingContext);
	}
}
