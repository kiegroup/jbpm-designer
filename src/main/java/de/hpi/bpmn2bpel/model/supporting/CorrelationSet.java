package de.hpi.bpmn2bpel.model.supporting;

import java.util.ArrayList;
import java.util.List;

/**
 * A correlation set is defined for a process and can be referenced
 * by communicating activities, to identify the process instance for the 
 * communication.
 */
public class CorrelationSet {

	private String name = null;
	private List<String> properties = new ArrayList<String>();

	/**
	 * Constructor. Initializes the correlation set object.
	 */
	public CorrelationSet() {}
	
	/**
	 * Adds a property to the list of correlation properties for the set.
	 * 
	 * @param property A correlation property.
	 */
	public void addProperty(String property) {
		this.properties.add(property);
	}

	/**
	 * @return The name of the correlation set.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return A list with properties defined for the correlation set. The 
	 * result is an empty list of no properties were defined.
	 */
	public List<String> getProperties() {
		return this.properties;
	}

	/**
	 * Sets the name of the correlation set.
	 * 
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
}
