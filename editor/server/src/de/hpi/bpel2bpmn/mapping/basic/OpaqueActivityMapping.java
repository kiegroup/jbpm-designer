package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpmn.Task;

public class OpaqueActivityMapping extends BasicActivityMapping {

	static private OpaqueActivityMapping instance = null;
	
	static public OpaqueActivityMapping getInstance() {
		if(null == instance) {
			instance = new OpaqueActivityMapping();
		}
		return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		Task task = mappingContext.getFactory().createTask();
		task.setLabel("Opaque Activity");
		setConnectionPointsWithControlLinks(node, task, task, null, mappingContext);
		mappingContext.addMappingElementToSet(node,task);

	}
}
