package de.hpi.bpmn.serialization;

import de.hpi.bpmn.BPMNDiagram;

/**
 * Just a generic interface for all classes actually 
 * serializing a BPMN model. Please also have a look at
 * class BPMNSerialization in the same package.
 * 
 * @author matthias.weidlich
 *
 */
public interface BPMNSerializer {
	
	/**
	 * Serializes a BPMN diagram.
	 * 
	 * @param bpmnDiagram
	 * @return the string representation of the serialization
	 */
	public String serializeBPMNDiagram(BPMNDiagram bpmnDiagram);

}
