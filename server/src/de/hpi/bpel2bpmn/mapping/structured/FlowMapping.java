package de.hpi.bpel2bpmn.mapping.structured;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.ANDGateway;

public class FlowMapping extends StructuredActivityMapping {
	
	private static ElementMapping instance = null;
	
	public static ElementMapping getInstance() {
      if(null == instance) {
          instance = new FlowMapping();
       }
       return instance;
	}

	public void mapElement(Node node, MappingContext mappingContext) {
		
		// one parallel gateway is always needed to sync all branches inside the flow
		ANDGateway endANDGateway = mappingContext.getFactory().createANDGateway();
		mappingContext.addMappingElementToSet(node,endANDGateway);

		/*
		 * Check whether the flow contains start activities
		 */
		if (BPEL2BPMNMappingUtil.hasActivityChildNodeWithCreateInstanceSet(node)) {
			setConnectionPointsWithControlLinks(node, null, endANDGateway, null, mappingContext);
		}
		else {
			ANDGateway startANDGateway = mappingContext.getFactory().createANDGateway();
			mappingContext.addMappingElementToSet(node,startANDGateway);
			
			setConnectionPointsWithControlLinks(node, startANDGateway, endANDGateway, null, mappingContext);
			
			// connections to the activity mappings
			for (Node child : BPEL2BPMNMappingUtil.getAllActivityChildNodes(node)) {
				createSequenceFlowBetweenDiagramObjects(startANDGateway, mappingContext.getMappingConnectionIn().get(child), 
						null,
						mappingContext);
			}
		}
		
		// connections to the synchronizing parallel gateway
		for (Node child : BPEL2BPMNMappingUtil.getAllActivityChildNodes(node)) {
			createSequenceFlowBetweenDiagramObjects(mappingContext.getMappingConnectionOut().get(child), endANDGateway,
					mappingContext.getMappingConnectionOutExpression().get(child),
					mappingContext);
		}
		
		/*
		 * Still to do: mapping of the actual control links
		 */
		
	}

}
