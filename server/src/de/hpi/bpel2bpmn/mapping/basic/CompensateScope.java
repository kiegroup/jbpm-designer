package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;

public class CompensateScope extends BasicActivityMapping {
	
	static private CompensateScope instance = null;
	
	static public CompensateScope getInstance() {
		if(null == instance) {
			instance = new CompensateScope();
		}
		return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		//
	}
	
}
