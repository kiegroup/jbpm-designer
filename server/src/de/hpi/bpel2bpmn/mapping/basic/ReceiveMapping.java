package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.StartMessageEvent;

public class ReceiveMapping extends BasicActivityMapping {
	
	static private ReceiveMapping instance = null;
	
	static public ReceiveMapping getInstance() {
		if(null == instance) {
			instance = new ReceiveMapping();
		}
		return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		
		String name = BPEL2BPMNMappingUtil.getRealNameOfNode(node);
		
		if (BPEL2BPMNMappingUtil.isCreateInstanceSet(node)) {
			StartMessageEvent event = mappingContext.getFactory().createStartMessageEvent();
			event.setParent(mappingContext.getDiagram());
			event.setLabel(name);
			setConnectionPointsWithControlLinks(node, null, event, null, mappingContext);
			mappingContext.addMappingElementToSet(node,event);
		}
		else {
			IntermediateMessageEvent event = mappingContext.getFactory().createIntermediateMessageEvent();
			event.setParent(mappingContext.getDiagram());
			event.setThrowing(false);
			event.setLabel(name);
			setConnectionPointsWithControlLinks(node, event, event, null, mappingContext);
			mappingContext.addMappingElementToSet(node,event);
		}
	}
}
