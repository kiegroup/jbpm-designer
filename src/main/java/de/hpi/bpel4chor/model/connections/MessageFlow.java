package de.hpi.bpel4chor.model.connections;

import de.hpi.bpel4chor.model.GraphicalObject;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.util.Output;

/**
 * A message flow connects communication activities to express
 * the message exchange between them.
 */
public class MessageFlow extends GraphicalObject {

	private String name = null;
	private Activity source = null;
	private Activity target = null;
	private String messageName = null;
	
	/**
	 * Constructor. Initializes the message flow and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public MessageFlow(Output output) {
		super(output);
	}

	/**
	 * @return The name of the message flow.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The source activity of the message flow.
	 */
	public Activity getSource() {
		return this.source;
	}

	/**
	 * @return The target activity of the message flow.
	 */
	public Activity getTarget() {
		return this.target;
	}
	
	/**
	 * @return The name of the message that is passed over the message flow.
	 */
	public String getMessageName() {
		return this.messageName;
	}

	/**
	 * Sets the name of the message that is passed over the message flow.
	 * 
	 * @param messageName The message flow to set.
	 */
	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	/**
	 * Sets the name of the message flow.
	 * 
	 * @param name The name to set. 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the source activity of the message flow. The source activity
	 * should be a service task or a send task.
	 * 
	 * @param source The source activity to set.
	 */
	public void setSource(Activity source) {
		this.source = source;
	}

	/**
	 * Sets the target activity of the message flow. The target activity
	 * should be a receive task or a message start or intermediate event.
	 * 
	 * @param target The target activity to set.
	 */
	public void setTarget(Activity target) {
		this.target = target;
	}
}
