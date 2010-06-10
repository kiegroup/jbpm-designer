package de.hpi.bpel4chor.model.artifacts;

import de.hpi.bpel4chor.model.Container;
import de.hpi.bpel4chor.model.GraphicalObject;
import de.hpi.bpel4chor.util.Output;

/**
 * An artifact adds additional information to the diagram that is independent 
 * from the control flow. Artifact is the base class for all data objects.
 */
public abstract class Artifact extends GraphicalObject {
	
	private Container container = null;
	private String name = null;	
	
	/**
	 * Constructor. Initializes the artifact and generates a unique id.
	 * 
	 * @param output The output to print the errors to.
	 */
	public Artifact(Output output) {
		super(output);
	}

	/**
	 * @return The name of the artifact.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return The process or sub-process the artifact is located in.
	 */
	public Container getContainer() {
		return this.container;
	}

	/**
	 * Sets the process or sub-process the artifact is located in.
	 * 
	 * @param container The parent container of the artifact.
	 */
	public void setContainer(Container container) {
		this.container = container;
	}

	/**
	 * Sets the name of the artifact.
	 * 
	 * @param name The new name of the artifact.
	 */
	public void setName(String name) {
		this.name = name;
	}
}
