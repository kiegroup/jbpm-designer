package de.hpi.bpel2bpmn.mapping.structured;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Activity.LoopType;
import de.hpi.bpmn.Activity.TestTime;

public class WhileRepeatUntilMapping extends StructuredActivityMapping {
	
	private static ElementMapping instance = null;
	
	public static ElementMapping getInstance() {
	      if(null == instance) {
	          instance = new WhileRepeatUntilMapping();
	       }
	       return instance;
		}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		
		Node activityNode  = BPEL2BPMNMappingUtil.getActivityChildNode(node);
		String name = BPEL2BPMNMappingUtil.getRealNameOfNode(node);
		
		SubProcess subProcess = mappingContext.getFactory().createSubProcess();
		subProcess.setLabel(name);
		subProcess.setLoopType(LoopType.Standard);
	
		// let's set the condition
		Node conditionNode = BPEL2BPMNMappingUtil.getSpecificChildNode(node,"condition");
		if (conditionNode != null) {
			subProcess.setLoopCondition(conditionNode.getTextContent());
		}

		// are we in a 'while' or 'repeat until' loop?
		if (node.getNodeName().equalsIgnoreCase("while")) {
			subProcess.setTestTime(TestTime.Before);
		}
		else {
			subProcess.setTestTime(TestTime.After);
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
		DiagramObject out = mappingContext.getMappingConnectionOut().get(activityNode);
		String conditionExpression = mappingContext.getMappingConnectionOutExpression().get(activityNode);
		
		createSequenceFlowBetweenDiagramObjects(startEvent, in, null, mappingContext);
		createSequenceFlowBetweenDiagramObjects(out, endEvent, conditionExpression, mappingContext);
			
		setConnectionPointsWithControlLinks(node, subProcess, subProcess, null, mappingContext);
		
		mappingContext.addMappingElementToSet(node,subProcess);

	}
}
