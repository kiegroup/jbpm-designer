package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;

public class Compensate extends BasicActivityMapping {
	
	static private Compensate instance = null;
	
	static public Compensate getInstance() {
		if(null == instance) {
			instance = new Compensate();
		}
		return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		//
	}
	
}
