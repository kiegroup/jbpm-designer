package de.hpi.bpel2bpmn.mapping;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.TextAnnotation;
import de.hpi.bpmn.UndirectedAssociation;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.SequenceFlow.ConditionType;

public abstract class ElementMappingImpl implements ElementMapping {

	protected void setConnectionPointsWithControlLinks(
			Node node, 
			DiagramObject in, 
			DiagramObject out, 
			String conditionExpression, 
			MappingContext mappingContext) {
	
		DiagramObject inWithLinks = in;
		DiagramObject outWithLinks = out;
		
		// outgoing links lead to the creation of an inclusive OR gateway
		// behind the actual activity mapping
		if (BPEL2BPMNMappingUtil.nodeHasOutgoingControlLinks(node)) {
			ORGateway orGateway = mappingContext.getFactory().createORGateway();
			orGateway.setParent(mappingContext.getDiagram());
			
			// connect the actual mapping of the node with the or gateway
			createSequenceFlowBetweenDiagramObjects(out, orGateway, conditionExpression, mappingContext);
			// the out condition expression of the actual mapping is no longer required
			// as we move the exit point of the mapping
			// please note that the exit point might be moved again
			// due to incoming links (see below)
			conditionExpression = null;
			outWithLinks = orGateway;
			
			// set the control link connection points
			for (String link : BPEL2BPMNMappingUtil.getAllOutgoingControlLinkNames(node)) {
				mappingContext.getControlLinkSource().put(link, orGateway);
			}
			
			// extract transition conditions for the outgoing links
			mappingContext.getControlLinkSourceTransitionConditions().putAll(
					BPEL2BPMNMappingUtil.getTransitionConditionsOfNode(node));
		}

		// incoming links lead to the creation of a complex gateway
		// in front, and a an exclusive OR gateway behind the actual
		// activity mapping (DPE case) or an error end event
		if (BPEL2BPMNMappingUtil.nodeHasIncomingControlLinks(node)) {
			
			/*
			 * At first do everything that does not depend on suppressJoinFailure
			 */
			ComplexGateway complexGateway = mappingContext.getFactory().createComplexGateway();
			complexGateway.setParent(mappingContext.getDiagram());
			
			String joinCondition = BPEL2BPMNMappingUtil.getJoinConditionOfNode(node);
			String negJoinCondition = "NOT ( " + joinCondition + " )";

			// connect the complex gateway with the mapping of the node
			createSequenceFlowBetweenDiagramObjects(complexGateway, inWithLinks, joinCondition, mappingContext);

			// the complex gateway is now the entry point for this mapping
			inWithLinks = complexGateway;
			
			// set the control link connection points
			for (String link : BPEL2BPMNMappingUtil.getAllIncomingControlLinkNames(node)) {
				mappingContext.getControlLinkTarget().put(link, complexGateway);
			}
			
			/*
			 * Let's consider suppressJoinFailure
			 */
			if (BPEL2BPMNMappingUtil.isSuppressJoinFailure(node)) {
				// DPE case
				XORDataBasedGateway xorGateway = mappingContext.getFactory().createXORDataBasedGateway();
				xorGateway.setParent(mappingContext.getDiagram());
				
				// connect the complex gateway with the xor gateway
				createSequenceFlowBetweenDiagramObjects(complexGateway, xorGateway, negJoinCondition, mappingContext);
				// connect the mapping of the node (or the inclusive OR gateway from above) with the xor gateway
				createSequenceFlowBetweenDiagramObjects(outWithLinks, xorGateway, conditionExpression, mappingContext);
				
				// the xor gateway is now the exit point for this mapping
				outWithLinks = xorGateway;
				
				// the out condition expression of the actual mapping is no longer required
				conditionExpression = null;
			}
			else {
				// Error case
				EndErrorEvent endErrorEvent = mappingContext.getFactory().createEndErrorEvent();
				endErrorEvent.setParent(mappingContext.getDiagram());
				endErrorEvent.setErrorCode("joinFailure");
				
				// connect the complex gateway with the end error event
				createSequenceFlowBetweenDiagramObjects(complexGateway, endErrorEvent, negJoinCondition, mappingContext);
				
			}
		}
			
		setConnectionPoints(node, inWithLinks, outWithLinks, conditionExpression, mappingContext);
	}

	protected void setConnectionPoints(Node node, DiagramObject in, DiagramObject out, String conditionExpression, MappingContext mappingContext) {
		mappingContext.getMappingConnectionIn().put(node, in);
		mappingContext.getMappingConnectionOut().put(node, out);
		if ((conditionExpression != null ) && !(conditionExpression.equals(""))) {
			mappingContext.getMappingConnectionOutExpression().put(node, conditionExpression);
		}
	}
	
	protected void createSequenceFlowBetweenDiagramObjectsOfNodes(Node start, 
			Node end, MappingContext mappingContext) {
		
		if (mappingContext.getMappingConnectionOut().containsKey(start) && 
			mappingContext.getMappingConnectionIn().containsKey(end)) {
			
			DiagramObject startObject = mappingContext.getMappingConnectionOut().get(start);
			DiagramObject endObject   = mappingContext.getMappingConnectionIn().get(end);
			
			String condition = null;
			
			// do we have to consider an expression?
			if (mappingContext.getMappingConnectionOutExpression().containsKey(start)) {
				condition = mappingContext.getMappingConnectionOutExpression().get(start);

			}
			
			createSequenceFlowBetweenDiagramObjects(startObject,endObject,condition,mappingContext);
			
		}
	}
	
	protected void createSequenceFlowBetweenDiagramObjects(DiagramObject startObject, 
			DiagramObject endObject, String condition, MappingContext mappingContext) {
		
		SequenceFlow sequenceFlow = mappingContext.getFactory().createSequenceFlow();
		
		// do we have to consider an expression?
		if (condition != null) {
			sequenceFlow.setConditionType(ConditionType.EXPRESSION);
			sequenceFlow.setConditionExpression(condition);
		}

		sequenceFlow.setSource(startObject);
		sequenceFlow.setTarget(endObject);
		mappingContext.getDiagram().getEdges().add(sequenceFlow);
		
	}
	
	/**
	 * Creates a text annotation and connects it with a certain diagram object.
	 * 
	 * @param text
	 * @param annotatedObject
	 * @param mappingContext
	 */
	protected void createAnnotationAndAssociation(String text, DiagramObject annotatedObject,
			MappingContext mappingContext) {
		
		TextAnnotation annotation = mappingContext.getFactory().createTextAnnotation();
		annotation.setParent(mappingContext.getDiagram());
		annotation.setText(text);
		
		UndirectedAssociation association = mappingContext.getFactory().createUndirectedAssociation();
		association.setSource(annotatedObject);
		association.setTarget(annotation);
		
		mappingContext.getDiagram().getEdges().add(association);
	}
	
}
