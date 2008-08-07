package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.IntermediateMessageEvent;

public class ReplyMapping extends BasicActivityMapping {
	
	static private ReplyMapping instance = null;
	
	static public ReplyMapping getInstance() {
		if(null == instance) {
			instance = new ReplyMapping();
		}
		return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		
		String name = BPEL2BPMNMappingUtil.getRealNameOfNode(node);

		IntermediateMessageEvent event_throw = mappingContext.getFactory().createIntermediateMessageEvent();
		event_throw.setParent(mappingContext.getDiagram());
		event_throw.setLabel(name);
		event_throw.setThrowing(true);
		
		setConnectionPointsWithControlLinks(node, event_throw, event_throw, null, mappingContext);
		
	}
}
