package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.IntermediatePlainEvent;

/**
 * Class for the mapping of an 'empty' activity. Actually, this activity 
 * does not have to be mapped, as it is applied solely in the following 
 * scenarios in BPEL:
 * <li>to provide an explicit synchronisation point for control links</li>
 * <li>to specify an empty fault, compensation, termination or event handler</li>
 * In both scenarios a mapping is not needed. For convenience, however, we map 
 * the 'empty' activity to a plain intermediate event, which might be removed again 
 * as part of the postprocessing.
 * 
 * @author matthias.weidlich
 *
 */
public class EmptyMapping extends BasicActivityMapping {
	
	static private EmptyMapping instance = null;
	
	static public EmptyMapping getInstance() {
		if(null == instance) {
			instance = new EmptyMapping();
		}
		return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		
		String name = BPEL2BPMNMappingUtil.getRealNameOfNode(node);
		
		IntermediatePlainEvent event = mappingContext.getFactory().createIntermediatePlainEvent();
		event.setParent(mappingContext.getDiagram());
		event.setLabel(name);
		
		mappingContext.addMappingElementToSet(node,event);
		
		setConnectionPointsWithControlLinks(node, event, event, null, mappingContext);
	}

}
