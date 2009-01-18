package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.IntermediateCompensationEvent;

public class CompensateScopeMapping extends BasicActivityMapping {
	
	static private CompensateScopeMapping instance = null;
	
	static public CompensateScopeMapping getInstance() {
		if(null == instance) {
			instance = new CompensateScopeMapping();
		}
		return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		String name = BPEL2BPMNMappingUtil.getRealNameOfNode(node);
		
		IntermediateCompensationEvent event = mappingContext.getFactory().createIntermediateCompensationEvent();
		event.setLabel(name);
		event.setThrowing(true);

		setConnectionPointsWithControlLinks(node, event, event, null, mappingContext);
		mappingContext.addMappingElementToSet(node,event);
		
		String annotationText = "Please note that there are several conceptual mismatches," +
				" when mapping a BPEL 'compensate scope' activity to a compensation calling event in BPMN." +
				" In contrast to BPEL, the event is non-blocking, thus execution continues immediately. In addition," +
				" the compensation target has to be adapted, as compensation triggers are not propagated in BPMN (as with default handlers in BPEL).";
		createAnnotationAndAssociation(annotationText,event,mappingContext);

	}
	
}
