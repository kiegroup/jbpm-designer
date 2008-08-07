package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpmn.EndTerminateEvent;
import de.hpi.bpmn.XORDataBasedGateway;

public class ExitMapping extends BasicActivityMapping {
	
	static private ExitMapping instance = null;
	
	static public ExitMapping getInstance() {
		if(null == instance) {
			instance = new ExitMapping();
		}
		return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		
		XORDataBasedGateway gateway = mappingContext.getFactory().createXORDataBasedGateway();
		gateway.setParent(mappingContext.getDiagram());
				
		EndTerminateEvent event = mappingContext.getFactory().createEndTerminateEvent();
		event.setParent(mappingContext.getDiagram());
		
		createSequenceFlowBetweenDiagramObjects(gateway, event, "true", mappingContext);
		
		setConnectionPointsWithControlLinks(node, gateway, gateway, "false", mappingContext);

	}

}
