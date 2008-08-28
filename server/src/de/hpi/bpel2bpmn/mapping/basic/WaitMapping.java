package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.IntermediateTimerEvent;

public class WaitMapping extends BasicActivityMapping {
	
	static private WaitMapping instance = null;
	
	static public WaitMapping getInstance() {
		if(null == instance) {
			instance = new WaitMapping();
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
		}
		
		setConnectionPointsWithControlLinks(node, event, event, null, mappingContext);
		mappingContext.addMappingElementToSet(node,event);
	}
}
