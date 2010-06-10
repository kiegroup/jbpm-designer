package de.hpi.bpel4chor.model.artifacts;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpel4chor.model.activities.BlockActivity;
import de.hpi.bpel4chor.util.Output;

/**
 * <p>A paricipant reference data object can be associated with communicating
 * activities to indicate the sender or receiver of a message. Moreover, 
 * it can be associated with a message flow to pass it to another participant. 
 * In this case the participant reference it will be copied to must be 
 * specified.</p>
 * 
 * <p>The context of a participant reference can be limited to a scope. A 
 * participant reference can select other participants and it can be contained
 * in a participant set.</p> 
 * 
 * <p>The type of a participant reference is determined during the transformation.
 * Thus, it must not be set by the parser.</p>
 */
public class ParticipantReferenceDataObject extends DataObject {
	
	public static final String CONTAINMENT_REQUIRED = "required";
	public static final String CONTAINMENT_MUST_ADD = "must-add";
	public static final String CONTAINMENT_ADD_IF_NOT_EXISTS = "add-if-not-exists";
	
	private String copyTo = null;
	private BlockActivity scope = null;
	private String containment;
	private List<String> selects = new ArrayList<String>();
	private String type = null;
	
	/**
	 * Constructor. Initializes the data object and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public  ParticipantReferenceDataObject(Output output) {
		super(output);
	}
	
	/**
	 * @return The name of the participant reference this reference will
	 * be copied to when it is passed over a message flow.
	 */
	public String getCopyTo() {
		return this.copyTo;
	}

	/**
	 * Sets the name of the participant reference this reference will
	 * be copied to when it is passed over a message flow.
	 * 
	 * @param copyTo The name of the participant reference to copy it to.
	 */
	public void setCopyTo(String copyTo) {
		this.copyTo = copyTo;
	}
	
	/**
	 * @return The scope that limits the context of this reference.
	 */
	public BlockActivity getScope() {
		return this.scope;
	}

	/**
	 * Sets the scope that limits the context of this reference.
	 * 
	 * @param scope The scope that limits the context of this reference.
	 */
	public void setScope(BlockActivity scope) {
		this.scope = scope;
	}
	
	/**
	 * @return The property value for the containment of the reference
	 * in the set.
	 */
	public String getContainment() {
		return this.containment;
	}

	/**
	 * @return The list of participants that are selected by this reference.
	 */
	public List<String> getSelects() {
		return this.selects;
	}
	
	/**
	 * Sets the property value for the containment of the reference
	 * in the set.
	 * 
	 * @param containment The containment property value.
	 */
	public void setContainment(String containment) {
		this.containment = containment;
	}
	
	/**
	 * Adds a participant name to the list of participants that are selected 
	 * by this reference.
	 * 
	 * @param select The name of a selected participant.
	 */
	public void addSelect(String select) {
		this.selects.add(select);
	}
	
	/**
	 * Sets the participant type of this participant reference.
	 *  
	 * @param type The type of this participant reference.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return The type of this participant reference.
	 */
	public String getType() {
		return this.type;
	}
}
