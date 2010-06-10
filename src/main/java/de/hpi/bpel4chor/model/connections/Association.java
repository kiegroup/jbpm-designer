package de.hpi.bpel4chor.model.connections;

import de.hpi.bpel4chor.model.GraphicalObject;
import de.hpi.bpel4chor.util.Output;

/**
 * <p>An association connects artifacts with other graphical objects. 
 * Moreover, an association can connect attached compensation events
 * with a compensation handler.</p>
 * 
 * <p>An association can be directed or non-directed. If the association
 * connects a data object, the data object must always be the target object.
 * </p>
 */
public class Association extends GraphicalObject {

	public static final String DIRECTION_NONE = "None";
	public static final String DIRECTION_TO = "To";
	public static final String DIRECTION_FROM = "From";
	
	private GraphicalObject source = null;
	private GraphicalObject target = null;
	private String direction = DIRECTION_NONE;
	
	/**
	 * Constructor. Initializes the association and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public Association(Output output) {
		super(output);
	}

	/**
	 * @return The direction of the association ({@link #DIRECTION_FROM}, 
	 * {@link #DIRECTION_TO} or {@link #DIRECTION_NONE}).
	 */
	public String getDirection() {
		return this.direction;
	}

	/**
	 * @return The source object of the association.
	 */
	public GraphicalObject getSource() {
		return this.source;
	}

	/**
	 * @return The target object of the association.
	 */
	public GraphicalObject getTarget() {
		return this.target;
	}

	/**
	 * Sets the direction of the association.
	 * 
	 * @param direction The direction of the association 
	 * ({@link #DIRECTION_FROM}, {@link #DIRECTION_TO} or 
	 * {@link #DIRECTION_NONE}).
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * Sets the source object of the association.
	 * 
	 * @param source The source object to set.
	 */
	public void setSource(GraphicalObject source) {
		this.source = source;
	}

	/**
	 * Sets the target object of the association. The target
	 * should be a data object, a compensation intermediate event or a 
	 * compensation handler.
	 * 
	 * @param target The target object to set.
	 */
	public void setTarget(GraphicalObject target) {
		this.target = target;
	}
}
