package de.hpi.bpel4chor.model.artifacts;

import de.hpi.bpel4chor.model.activities.BlockActivity;
import de.hpi.bpel4chor.util.Output;

/**
 * <p>A paricipant set data object can be associated with communicating
 * activities to store the senders of a message in it. It can be associated 
 * with a mulit-instance loop to iterate over the participants contained in the
 * set. Moreover, it can be associated with a message flow to pass it to 
 * another participant. In this case the participant set, which it will be
 * copied to, must be specified.</p>
 * 
 * <p>The context of a participant set can be limited to a scope.</p> 
 * 
 * <p>The type of a participant set is determined during the transformation.
 * Thus, it must not be set by the parser.</p>
 */
public class ParticipantSetDataObject extends DataObject {
	
	private String copyTo = null;
	private BlockActivity scope = null;
	private String type = null;
	
	/**
	 * Constructor. Initializes the data object and generates a unique id.
	 * 
	 * @param output      The output to print errors to.
	 */
	public  ParticipantSetDataObject(Output output) {
		super(output);
	}
	
	/**
	 * @return The name of the participant set this set will
	 * be copied to when it is passed over a message flow.
	 */
	public String getCopyTo() {
		return this.copyTo;
	}

	/**
	 * Sets the name of the participant set this set will
	 * be copied to when it is passed over a message flow.
	 * 
	 * @param copyTo The name of the participant set to copy it to.
	 */
	public void setCopyTo(String copyTo) {
		this.copyTo = copyTo;
	}

	/**
	 * @return The scope that limits the context of this set.
	 */
	public BlockActivity getScope() {
		return this.scope;
	}

	/**
	 * Sets the scope that limits the context of this set.
	 * 
	 * @param scope The scope that limits the context of this set.
	 */
	public void setScope(BlockActivity scope) {
		this.scope = scope;
	}
	
	/**
	 * Sets the participant type of this participant set.
	 *  
	 * @param type The type of this participant set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return The type of this participant set.
	 */
	public String getType() {
		return this.type;
	}
}
