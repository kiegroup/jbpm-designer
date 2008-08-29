package de.hpi.bpel2bpmn.mapping.structured;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.Activity;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.IntermediateErrorEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.XORDataBasedGateway;

public class ScopeMapping extends StructuredActivityMapping {

	private static ElementMapping instance = null;
	
	public static ElementMapping getInstance() {
      if(null == instance) {
          instance = new ScopeMapping();
       }
       return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		String name = BPEL2BPMNMappingUtil.getRealNameOfNode(node);
		
		SubProcess subProcess = mappingContext.getFactory().createSubProcess();
		subProcess.setLabel(name);
		subProcess.setParent(mappingContext.getDiagram());

		Node child = BPEL2BPMNMappingUtil.getActivityChildNode(node);

		/*
		 * Check whether the scope contains start activities
		 */
		if (!(BPEL2BPMNMappingUtil.hasActivityChildNodeWithCreateInstanceSet(node))) {
			StartPlainEvent startEvent = mappingContext.getFactory().createStartPlainEvent();
			startEvent.setParent(mappingContext.getDiagram());
			
			createSequenceFlowBetweenDiagramObjects(startEvent, 
					mappingContext.getMappingConnectionIn().get(child), 
					null,
					mappingContext);
		}
		
		EndPlainEvent endEvent = mappingContext.getFactory().createEndPlainEvent();
		endEvent.setParent(mappingContext.getDiagram());
		
		createSequenceFlowBetweenDiagramObjects(mappingContext.getMappingConnectionOut().get(child),
				endEvent,
				mappingContext.getMappingConnectionOutExpression().get(child),
				mappingContext);
		
	

		Node faultHandlersNode = BPEL2BPMNMappingUtil.getSpecificChildNode(node, "faulthandlers");
		
		if (faultHandlersNode == null) {
			setConnectionPointsWithControlLinks(node, subProcess, subProcess, null, mappingContext);
		}
		else {
			XORDataBasedGateway gateway = mappingContext.getFactory().createXORDataBasedGateway();
			gateway.setParent(mappingContext.getDiagram());
			
			for(Node catchNode : BPEL2BPMNMappingUtil.getAllSpecificChildNodes(faultHandlersNode, "catch")) {
				mapExceptionHandler(catchNode, mappingContext, subProcess, gateway);
			}
			for(Node catchNode : BPEL2BPMNMappingUtil.getAllSpecificChildNodes(faultHandlersNode, "catchall")) {
				mapExceptionHandler(catchNode, mappingContext, subProcess, gateway);
			}
			
			createSequenceFlowBetweenDiagramObjects(subProcess, gateway, null, mappingContext);			
			
			setConnectionPointsWithControlLinks(node, subProcess, gateway, null, mappingContext);
		}

	}
	
	private void mapExceptionHandler(Node catchNode, MappingContext mappingContext, Activity activity, DiagramObject out) {
		IntermediateErrorEvent errorEvent = mappingContext.getFactory().createIntermediateErrorEvent();
		errorEvent.setParent(mappingContext.getDiagram());
		errorEvent.setActivity(activity);
		
		// in case of a catchall we won't find any fault name
		Node errorNode = catchNode.getAttributes().getNamedItem("faultName");
		if (errorNode != null) {
			errorEvent.setErrorCode(errorNode.getTextContent());
		}
		
		
		Node handlerContent = BPEL2BPMNMappingUtil.getActivityChildNode(catchNode);
		createSequenceFlowBetweenDiagramObjects(errorEvent, 
				mappingContext.getMappingConnectionIn().get(handlerContent),
				null, mappingContext);			

		createSequenceFlowBetweenDiagramObjects(
				mappingContext.getMappingConnectionOut().get(handlerContent),
				out, 
				mappingContext.getMappingConnectionOutExpression().get(handlerContent), 
				mappingContext);			
	}

}
