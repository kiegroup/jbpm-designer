package de.hpi.bpel2bpmn.mapping.structured;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.IntermediateMessageEvent;

public class OnMessageMapping extends StructuredActivityMapping {
	
	private static ElementMapping instance = null;
	
	public static ElementMapping getInstance() {
      if(null == instance) {
          instance = new OnMessageMapping();
       }
       return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		
		String name = BPEL2BPMNMappingUtil.getRealNameOfNode(node);
		
		IntermediateMessageEvent event = mappingContext.getFactory().createIntermediateMessageEvent();
		event.setLabel(name);
		
		if (BPEL2BPMNMappingUtil.hasActivityChildNode(node)) {
			Node childNode = BPEL2BPMNMappingUtil.getActivityChildNode(node);

			DiagramObject out = mappingContext.getMappingConnectionOut().get(childNode);
			String conditionExpression = mappingContext.getMappingConnectionOutExpression().get(childNode);
			setConnectionPoints(node, event, out, conditionExpression, mappingContext);
		
			DiagramObject in = mappingContext.getMappingConnectionIn().get(childNode);
			createSequenceFlowBetweenDiagramObjects(event, in, null, mappingContext);
		}
		else {
			setConnectionPoints(node, event, event, null, mappingContext);
		}
		
		mappingContext.addMappingElementToSet(node,event);

	}

}
