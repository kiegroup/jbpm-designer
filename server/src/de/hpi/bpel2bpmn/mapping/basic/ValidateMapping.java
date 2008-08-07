package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpmn.Task;

public class ValidateMapping extends BasicActivityMapping {
	
	static private ValidateMapping instance = null;
	
	static public ValidateMapping getInstance() {
		if(null == instance) {
			instance = new ValidateMapping();
		}
		return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		Task task = mappingContext.getFactory().createTask();
		task.setParent(mappingContext.getDiagram());
		
		setConnectionPointsWithControlLinks(node, task, task, null, mappingContext);
		
	}

}
