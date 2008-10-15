package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;

public class EmptyMapping extends BasicActivityMapping {
	
	static private EmptyMapping instance = null;
	
	static public EmptyMapping getInstance() {
		if(null == instance) {
			instance = new EmptyMapping();
		}
		return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		//
	}

}
