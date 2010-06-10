package de.hpi.bpel2bpmn.mapping.structured;

import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.hpi.bpel2bpmn.mapping.ElementMapping;
import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.XOREventBasedGateway;

public class PickMapping extends StructuredActivityMapping {

	private static ElementMapping instance = null;
	
	public static ElementMapping getInstance() {
      if(null == instance) {
          instance = new PickMapping();
       }
       return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		
		XOREventBasedGateway eventGateway = mappingContext.getFactory().createXOREventBasedGateway();
		XORDataBasedGateway dataGateway = mappingContext.getFactory().createXORDataBasedGateway();
		
		setConnectionPointsWithControlLinks(node, eventGateway, dataGateway, null, mappingContext);

		if (BPEL2BPMNMappingUtil.isCreateInstanceSet(node)) {
			eventGateway.setInstantiate(true);
		}
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child instanceof Text)
				continue;
			createSequenceFlowBetweenDiagramObjects(eventGateway, mappingContext.getMappingConnectionIn().get(child), 
					null,
					mappingContext);
		}
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child instanceof Text)
				continue;
			createSequenceFlowBetweenDiagramObjects(mappingContext.getMappingConnectionOut().get(child), dataGateway,
					mappingContext.getMappingConnectionOutExpression().get(child),
					mappingContext);
		}
		
		mappingContext.addMappingElementToSet(node,eventGateway);
		mappingContext.addMappingElementToSet(node,dataGateway);

	}

}
