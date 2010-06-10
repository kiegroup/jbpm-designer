package de.hpi.bpel4chor.model.activities;

import de.hpi.bpel4chor.model.Container;

import org.w3c.dom.Element;

import de.hpi.bpel4chor.util.Output;

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
	public FoldedTask(Element element, Container parentContainer, Output output) {
		super(true, output);
		this.bpelElement = element;
		setParentContainer(parentContainer);
	}
	
	/**
	 * @return The bpel element that holds the bpel
	 * mapping represented by this task.
	 */
	public Element getBPELElement() {
		return this.bpelElement;
	}
}
