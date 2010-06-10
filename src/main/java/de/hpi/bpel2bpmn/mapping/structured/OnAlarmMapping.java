package de.hpi.bpel2bpmn.mapping.structured;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.IntermediateTimerEvent;

public class OnAlarmMapping extends StructuredActivityMapping {
	
	private static ElementMapping instance = null;
	
	public static ElementMapping getInstance() {
      if(null == instance) {
          instance = new OnAlarmMapping();
       }
       return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		
		String name = BPEL2BPMNMappingUtil.getRealNameOfNode(node);
		
		IntermediateTimerEvent event = mappingContext.getFactory().createIntermediateTimerEvent();
		event.setLabel(name);
		
		Node timerNode = BPEL2BPMNMappingUtil.getSpecificChildNode(node, "for");
		if (timerNode != null) {
			event.setTimeDate(timerNode.getTextContent());
		}
		else {
			timerNode = BPEL2BPMNMappingUtil.getSpecificChildNode(node, "until");
			if (timerNode != null) {
				event.setTimeDate(timerNode.getTextContent());
			}
			else {
				timerNode = BPEL2BPMNMappingUtil.getSpecificChildNode(node, "every");
				if (timerNode != null) {
					event.setTimeCycle(timerNode.getTextContent());
				}
			}
		}
		
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
