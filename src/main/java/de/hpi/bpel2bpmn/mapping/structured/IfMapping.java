package de.hpi.bpel2bpmn.mapping.structured;

import java.util.Collection;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.SequenceFlow.ConditionType;

public class IfMapping extends StructuredActivityMapping {

	private static ElementMapping instance = null;
	
	public static ElementMapping getInstance() {
      if(null == instance) {
          instance = new IfMapping();
       }
       return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {

		XORDataBasedGateway startGateway = mappingContext.getFactory().createXORDataBasedGateway();
		XORDataBasedGateway endGateway = mappingContext.getFactory().createXORDataBasedGateway();
		
		setConnectionPointsWithControlLinks(node, startGateway, endGateway, null, mappingContext);

		// let's get the condition
		String ifCondition = "";
		Node conditionNode = BPEL2BPMNMappingUtil.getSpecificChildNode(node,"condition");
		if (conditionNode != null) {
			ifCondition = conditionNode.getTextContent();
		}
		
		/*
		 * Connect the activity in the 'if' block with start and end gateways
		 */
		Node activityNode = BPEL2BPMNMappingUtil.getActivityChildNode(node);
		createSequenceFlowBetweenDiagramObjects(startGateway, mappingContext.getMappingConnectionIn().get(activityNode), 
					ifCondition,
					mappingContext);
		createSequenceFlowBetweenDiagramObjects(mappingContext.getMappingConnectionOut().get(activityNode), endGateway,
					mappingContext.getMappingConnectionOutExpression().get(activityNode),
					mappingContext);
		
		/*
		 * Look for contained elseif or else blocks
		 */
		Collection<Node> elseifNodes = BPEL2BPMNMappingUtil.getAllSpecificChildNodes(node, "elseif");
		for (Node elseifNode : elseifNodes) {
			mapElseIfPart(elseifNode, startGateway, endGateway, mappingContext);
		}
		Collection<Node> elseNodes = BPEL2BPMNMappingUtil.getAllSpecificChildNodes(node, "else");
		for (Node elseNode : elseNodes) {
			mapElsePart(elseNode, startGateway, endGateway, mappingContext);
		}
				
		mappingContext.addMappingElementToSet(node,startGateway);
		mappingContext.addMappingElementToSet(node,endGateway);
	}
	
	private void mapElseIfPart(Node node, DiagramObject start, DiagramObject end, MappingContext mappingContext) {
		Node activityNode = BPEL2BPMNMappingUtil.getActivityChildNode(node);

		// get the condition
		String elseIfCondition = "";
		Node conditionNode = BPEL2BPMNMappingUtil.getSpecificChildNode(node,"condition");
		if (conditionNode != null) {
			elseIfCondition = conditionNode.getTextContent();
		}
		createSequenceFlowBetweenDiagramObjects(start, mappingContext.getMappingConnectionIn().get(activityNode), 
				elseIfCondition,
				mappingContext);
		
		/*
		 * Connect to the gateway indicating the end of the 'if' mapping block
		 */
		createSequenceFlowBetweenDiagramObjects(mappingContext.getMappingConnectionOut().get(activityNode), end,
					mappingContext.getMappingConnectionOutExpression().get(activityNode),
					mappingContext);
	}

	private void mapElsePart(Node node, DiagramObject start, DiagramObject end, MappingContext mappingContext) {
		Node activityNode = BPEL2BPMNMappingUtil.getActivityChildNode(node);

		// create the default flow
		SequenceFlow sequenceFlow = mappingContext.getFactory().createSequenceFlow();
		sequenceFlow.setConditionType(ConditionType.DEFAULT);
		sequenceFlow.setSource(start);
		sequenceFlow.setTarget(mappingContext.getMappingConnectionIn().get(activityNode));
		mappingContext.getDiagram().getEdges().add(sequenceFlow);
		
		/*
		 * Connect to the gateway indicating the end of the 'if' mapping block
		 */
		createSequenceFlowBetweenDiagramObjects(mappingContext.getMappingConnectionOut().get(activityNode), end,
					mappingContext.getMappingConnectionOutExpression().get(activityNode),
					mappingContext);
	}
}
