package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.IntermediateMessageEvent;

public class InvokeMapping extends BasicActivityMapping {

	static private InvokeMapping instance = null;
	
	static public InvokeMapping getInstance() {
      if(null == instance) {
         instance = new InvokeMapping();
      }
      return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		
		String name = BPEL2BPMNMappingUtil.getRealNameOfNode(node);
		
		IntermediateMessageEvent event_throw = mappingContext.getFactory().createIntermediateMessageEvent();
		event_throw.setParent(mappingContext.getDiagram());
		event_throw.setLabel(name);
		event_throw.setThrowing(true);
		
		if (BPEL2BPMNMappingUtil.isSynchronousInvoke(node)) {
			IntermediateMessageEvent event_catch = mappingContext.getFactory().createIntermediateMessageEvent();
			event_catch.setParent(mappingContext.getDiagram());
			event_catch.setLabel(name);
			event_catch.setThrowing(false);
			
			createSequenceFlowBetweenDiagramObjects(event_throw, event_catch, null, mappingContext);

			setConnectionPointsWithControlLinks(node, event_throw, event_catch, null, mappingContext);
		}
		else {
			setConnectionPointsWithControlLinks(node, event_throw, event_throw, null, mappingContext);
		}
		
	}
	
}
