package de.hpi.bpel4chor.model;

import java.util.ArrayList;
import java.util.List;
import de.hpi.bpel4chor.util.Output;

/**
 * A pool is a special swimlane that represents a single participant in the 
 * choreography. The process contained in the pool defines the participant
 * behaviour description of this participant. The participant a pool represents
 * is similar to a participant expressed using a participant reference data
 * object.
 * 
 */
public class Pool extends Swimlane {
	
	private String containment;
	private List<String> selects = new ArrayList<String>();
	private String participantName = null;
	
	/**
	 * Constructor. Initializes the pool and generates a unique id.
	 * 
	 * @param output The output to print errors to.
	 */
	public Pool(Output output) {
		super(output);
	}
	
	/**
	 * @return The name of the participant the pool represents. If the pool
	 * does not define a participant name or the participant name is empty,
	 * the name of the pool is returned.
	 */
	public String getParticipantName() {
		if ((this.participantName == null) || this.participantName.equals("")) {
			return getName();
		}
		return this.participantName;
	}

	/**
	 * Sets the name of the participant the pool represents.
	 * 
	 * @param participantName The new participant name.
	 */
	public void setParticipantName(String participantName) {
		this.participantName = participantName;
	}
	
	/**
	 * @return The containment value defined for the participant
	 * the pool represents.
	 */
	public String getContainment() {
		return this.containment;
	}

	/**
	 * @return The participant names, the participant represented by the pool
	 * selects. An empty list if the participant does not select any other
	 * participants.
	 */
	public List<String> getSelects() {
		return this.selects;
	}
	
	/**
	 * Sets the containment value for the participant represented by the pool.
	 * 
	 * @param containment The new containment value.
	 */
	public void setContainment(String containment) {
		this.containment = containment;
	}
	
	/**
	 * Sets the participant names that are selected by the participant the pool
	 * represents. The already existing list is overwritten.
	 * 
	 * @param selects The new selected participants.
	 */
	public void setSelects(List<String> selects) {
		this.selects = selects;
	}
	
	/**
	 * Adds a participant name to the list of participants that are selected
	 * by the participant represented by the pool.
	 * 
	 * @param select The participant name to add.
	 */
	public void addSelect(String select) {
		this.selects.add(select);
	}
}
