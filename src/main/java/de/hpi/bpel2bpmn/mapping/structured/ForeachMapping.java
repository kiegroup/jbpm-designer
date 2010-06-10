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
import de.hpi.bpmn.Activity.MIFlowCondition;
import de.hpi.bpmn.Activity.MIOrdering;

public class ForeachMapping extends StructuredActivityMapping {

	private static ElementMapping instance = null;
	
	public static ElementMapping getInstance() {
      if(null == instance) {
          instance = new ForeachMapping();
       }
       return instance;
	}

	public void mapElement(Node node, MappingContext mappingContext) {
		Node activityNode  = BPEL2BPMNMappingUtil.getActivityChildNode(node);
		String name = BPEL2BPMNMappingUtil.getRealNameOfNode(node);
		
		SubProcess subProcess = mappingContext.getFactory().createSubProcess();
		subProcess.setLabel(name);
		subProcess.setLoopType(LoopType.Multiinstance);
		subProcess.setMiFlowCondition(MIFlowCondition.All);
		
		if (node.getAttributes().getNamedItem("parallel") != null) {
			if (node.getAttributes().getNamedItem("parallel").getTextContent().equalsIgnoreCase("yes")) {
				subProcess.setMiOrdering(MIOrdering.Parallel);
			}
			else {
				subProcess.setMiOrdering(MIOrdering.Sequential);
			}
		}
		
		/*
		 * let's set the mi condition, we just insert the expressions that
		 * determine the 'start counter value' and the 'final counter value'
		 */
		Node startCounterNode = BPEL2BPMNMappingUtil.getSpecificChildNode(node,"startcountervalue");
		Node finalCounterNode = BPEL2BPMNMappingUtil.getSpecificChildNode(node,"finalcountervalue");
		if ((startCounterNode != null) && (finalCounterNode != null)) {
			subProcess.setMiCondition(startCounterNode.getTextContent() 
					+ " - " + finalCounterNode.getTextContent());
		}
	
		/*
		 * let's create an annotation for the missing mapping of
		 * the completion condition
		 */ 
		Node conditionNode = BPEL2BPMNMappingUtil.getSpecificChildNode(node,"completionCondition");
		if (conditionNode != null) {
			String annotationText = "Please note that the BPEL 'for each' activity" +
					" specified a completion condition that cannot be mapped to BPMN.";
			createAnnotationAndAssociation(annotationText,subProcess,mappingContext);
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
