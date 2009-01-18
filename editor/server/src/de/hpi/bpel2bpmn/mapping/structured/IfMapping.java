package de.hpi.bpel2bpmn.mapping.structured;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.XORDataBasedGateway;

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
		
		mappingContext.addMappingElementToSet(node,startGateway);
		mappingContext.addMappingElementToSet(node,endGateway);
	}

}
