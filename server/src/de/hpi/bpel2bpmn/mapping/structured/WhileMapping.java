package de.hpi.bpel2bpmn.mapping.structured;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.Activity;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Activity.LoopType;

public class WhileMapping extends StructuredActivityMapping {
	
	private static ElementMapping instance = null;
	
	public static ElementMapping getInstance() {
	      if(null == instance) {
	          instance = new WhileMapping();
	       }
	       return instance;
		}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		
		Node activityNode  = BPEL2BPMNMappingUtil.getActivityChildNode(node);
		String name = BPEL2BPMNMappingUtil.getRealNameOfNode(node);

		Node conditionNode = BPEL2BPMNMappingUtil.getSpecificChildNode(node,"condition");
		
//		if (BPEL2BPMNMappingUtil.nodeIsMappedToSingleActivity(activityNode)) {
//			// simple case: just set the loop attribute of the activity
//			Activity activity = (Activity) mappingContext.getMappingConnectionIn().get(activityNode);
//			activity.setLoopType(LoopType.Standard);
//			if (conditionNode != null) {
//				activity.setLoopCondition(conditionNode.getTextContent());
//			}
//			
//			this.setConnectionPointsWithControlLinks(node, activity, activity, 
//					mappingContext.getMappingConnectionOutExpression().get(activityNode), 
//					mappingContext);
//		}

		SubProcess subProcess = mappingContext.getFactory().createSubProcess();
		subProcess.setLabel(name);
		subProcess.setLoopType(LoopType.Standard);
		
		if (conditionNode != null) {
			subProcess.setLoopCondition(conditionNode.getTextContent());
		}
		
		// we need plain events
		StartPlainEvent startEvent = mappingContext.getFactory().createStartPlainEvent();			
		EndPlainEvent endEvent = mappingContext.getFactory().createEndPlainEvent();
		startEvent.setLabel(name);
		endEvent.setLabel(name);
		startEvent.setParent(subProcess);
		endEvent.setParent(subProcess);
		
		// connect the start and end event with the mapping of the contained activity
		DiagramObject in  = mappingContext.getMappingConnectionIn().get(activityNode);
		DiagramObject out = mappingContext.getMappingConnectionIn().get(activityNode);
		String conditionExpression = mappingContext.getMappingConnectionOutExpression().get(activityNode);
		
		createSequenceFlowBetweenDiagramObjects(startEvent, in, null, mappingContext);
		createSequenceFlowBetweenDiagramObjects(out, endEvent, conditionExpression, mappingContext);
			
		setConnectionPointsWithControlLinks(node, subProcess, subProcess, null, mappingContext);
		
		mappingContext.addMappingElementToSet(node,subProcess);

	}
}
