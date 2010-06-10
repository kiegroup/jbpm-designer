package de.hpi.bpmn2bpel.model;

import de.hpi.bpmn.Task;
import de.hpi.bpmn2bpel.model.Container4BPEL;

import org.w3c.dom.Element;

/**
 * A folded task is generated during the transformation and
 * replaces a pattern in the sequence flow. The task holds 
 * the bpel mapping of the pattern.
 */
public class FoldedTask extends Task {
	
	private Element bpelElement;
	
	/**
	 * Constructor. Initializes the event with the bpel mapping and generates
	 * a unique id
	 * 
	 * @param element The element that holds the bpel mapping
	 * represented by this task.
	 * @param parentContainer The process or sub-process the task is located in.
	 * @param output The output to print errors to.
	 */
	public FoldedTask(Element element, Container4BPEL parentContainer) {
		this.bpelElement = element;
		setParent(parentContainer);
//		setParentContainer(parentContainer);
	}
	
	/**
	 * @return The bpel element that holds the bpel
	 * mapping represented by this task.
	 */
	public Element getBPELElement() {
		return this.bpelElement;
	}
}
