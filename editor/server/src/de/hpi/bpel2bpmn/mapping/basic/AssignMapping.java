package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpmn.Task;

public class AssignMapping extends BasicActivityMapping {
	
	static private AssignMapping instance = null;
	
	static public AssignMapping getInstance() {
		if(null == instance) {
			instance = new AssignMapping();
		}
		return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		//
	}
	

}
