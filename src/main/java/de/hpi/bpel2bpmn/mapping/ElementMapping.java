package de.hpi.bpel2bpmn.mapping;

import org.w3c.dom.Node;

public interface ElementMapping {

	public void mapElement(Node node, MappingContext mappingContext);

}
