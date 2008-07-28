package de.hpi.bpel4chor.transformation.factories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.GraphicalObject;
import de.hpi.bpel4chor.model.Pool;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.activities.BlockActivity;
import de.hpi.bpel4chor.model.activities.ServiceTask;
import de.hpi.bpel4chor.model.activities.SendTask;
import de.hpi.bpel4chor.model.activities.Task;
import de.hpi.bpel4chor.model.artifacts.DataObject;
import de.hpi.bpel4chor.model.artifacts.ParticipantReferenceDataObject;
import de.hpi.bpel4chor.model.artifacts.ParticipantSetDataObject;
import de.hpi.bpel4chor.model.artifacts.VariableDataObject;
import de.hpi.bpel4chor.model.connections.Association;
import de.hpi.bpel4chor.model.connections.MessageFlow;
import de.hpi.bpel4chor.model.supporting.Loop;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import de.hpi.bpel4chor.util.Output;

/**
 * This factory creates creates the participants of the topology from the
 * participant reference and participant set data object in the diagram.
 *  
 * <p>An instance of this class can only be used for one diagram.</p>
 */
public class ParticipantsFactory {
	
	private List<String> referenceNames = null;
	private List<String> setNames = null;
	private Diagram diagram = null;
	private Output output = null;
	
	/**
	 * Constructor. Initializes the factory with the diagram that
	 * containes the participant reference and participant set data objects.
	 * 
	 * @param diagram The diagram to generate the participants for.
	 * @param output  The Output to print errors to.
	 */
	public ParticipantsFactory(Diagram diagram, Output output) {
		this.referenceNames = new ArrayList<String>();
		this.setNames = new ArrayList<String>();
		this.diagram = diagram;
		this.output = output;
	}
	
	/**
	 * <p>Calculates the value of the selects attribute from
	 * participant references with the given name.</p> 
	 * 
	 * <p>The value consists of each entry in the selects list 
	 * of participant references from pool and participant
	 * reference data objects with this name. 
	 * Each entry is separated with a whitespace.</p>
	 * 
	 * @param name The name of the participant reference to generate the
	 * selects value for
	 * 
	 * @return The generated value for the selects attribute.
	 */
	private String getSelectsValue(String name) {
		// search for participant references or pools with this name
		String selectsValue = "";
		for (Iterator<Pool> it = 
			this.diagram.getPools().iterator(); it.hasNext();) {
			
			Pool pool = it.next();
			String refName = pool.getParticipantName();			
			if (refName.equals(name)) {
				if (pool.getSelects() != null) {
					String value = getSelectsValue(pool.getSelects()); 
					if (value != null) {
						selectsValue += " " + value;
					}
						
				}
				// only one pool with this name should exist
				break;
			}
		}
		
		List<ParticipantReferenceDataObject> references = 
			this.diagram.getParticipantReferencesWithName(name);
		for (Iterator<ParticipantReferenceDataObject> it = 
			references.iterator(); it.hasNext();) {
			String result = getSelectsValue(it.next().getSelects());
			if (result != null) {
				selectsValue += " " + result; 
			}
		}
		
		return selectsValue.trim();		
	}
	
	/**
	 * Calculates the value of the BPEL4Chor selects attribute from
	 * a given String list.
	 * Each entry is separated with a whitespace.
	 * 
	 * @param selects The selects value for a participant reference data object
	 * 
	 * @return  the value of the BPEL4Chor selects attribute. The result is 
	 * null, if the given list is null. 
	 */
	private String getSelectsValue(List<String> selects) {
		String selectsValue = null;
		if (selects != null) {
			selectsValue = "";
			for (Iterator<String> it = 
				selects.iterator(); it.hasNext();) {
				selectsValue += it.next() + " ";
			}
			return selectsValue.trim();	
		}
		return null;
	}
	
	/**
	 * <p>Creates a BPEL4Chor "participant" element from a pool.</p>
	 * 
	 * <p>The reference is only created if it was not created by now.
	 * If the pool does not define a name for the participant, the participant
	 * gets the name of the pool. The participant type is determined from the
	 * pool name as well.</p>
	 * 
	 * @param document 	the document to create the element for.
	 * @param pool 		the pool, that represents the participant reference.
	 * 
	 * @return the created BPEL4Chor "participant" element. The result is null,
	 * if the participant was already generated before.
	 */
	private Element createReferenceElement(Document document, Pool pool) {
		String name = pool.getName();
 		if (this.referenceNames.contains(name)) {
			return null;
		}
		this.referenceNames.add(name);		
		
		if (pool.getParticipantName() != null) {
			name = pool.getParticipantName();
		}
		String type = pool.getName();
		
		Element element = document.createElement("participant");
		element.setAttribute("name", name);		
		element.setAttribute("type", type);
		
		String selectsValue = getSelectsValue(pool.getParticipantName());
		if ((selectsValue != null) && !(selectsValue.equals(""))) {
			element.setAttribute("selects", selectsValue);
		}
		
		return element;	
	}
	
	/**
	 * Determines the BPEL4Chor "participant" elements from each pool
	 * in the diagram and appends them to the childs of the parent node.
	 * 
	 * @param document	the document to create the elements for.
	 * @param parent 	the parent node for the created "participant" elements 
	 * 					(should be a "participants" element)
	 */
	private void transformParticipantReferencesFromPools(
			Document document, Node parent) {
		for (Iterator<Pool> it = 
			this.diagram.getPools().iterator(); it.hasNext();) {
			Pool pool = it.next();
			Element ref = createReferenceElement(document, pool);
			if (ref == null) {
				this.output.addError("There are multiple pools defining "+
						"the same participant or pool name.");
			} else {
				parent.appendChild(ref);
			}
		}
	}
	
	/**
	 * Determines the scope value from the given block activity.
	 * The prefix of the scope value is generated from the
	 * prefix of the parent swimlane. 
	 * 
	 * @param act the block activity to genereate the scope value from
	 * @return the scope value with prefix or null, if the prefix
	 * could not be determined
	 */
	private String getScopeWithPrefix(BlockActivity act) {
		if (act == null) {
			return null;
		}
		
		String prefix = act.getParentSwimlane().getPrefix();

		if (prefix == null) {
			this.output.addError("The prefix for blockactivity " + 
					act.getId() + " could not be determined.");
			return null;
		}
		return prefix + ":" + act.getName();
	}
	
	/**
	 * <p>Creates the "scope" attribute value for a participant set
	 * data object. If there are multiple participant sets
	 * data objects with the same name defining, different scopes the result
	 * is null.</p>
	 * 
	 * <p>A prefix is generated for the block activitie's name, to 
	 * identify the pool or pool set, this activity belongs to.</p>
	 * 
	 * @param dataObject The data object, to create the 
	 * @return The "scope" attribute value for the participant represented
	 * by the data object. The result is null, if the attribute value could
	 * not be determined.
	 */
	private String getScope(ParticipantSetDataObject dataObject) {
		String name = dataObject.getName();
		List<ParticipantSetDataObject> dataObjects = 
			this.diagram.getParticipantSetsWithName(name);
		BlockActivity scope = null;
		for (Iterator<ParticipantSetDataObject> it = 
			dataObjects.iterator(); it.hasNext();) {
			ParticipantSetDataObject ref = it.next();
			if (ref.getScope() != null) {
				if (scope == null) {
					scope = ref.getScope();
				} else if (!scope.equals(ref.getScope())) {
					this.output.addError("There are participant reference "+
							"data objects of the same name defining different scopes.");
					return null;
				}
			}
		}
		
		// determine scope value with prefix
		return getScopeWithPrefix(scope);
	}
	
	/**
	 * <p>Creates the "scope" attribute value for a participant reference
	 * data object. If there are multiple participant references 
	 * data objects with the same name defining, different scopes the result
	 * is null.</p>
	 * 
	 * <p>A prefix is generated for the block activitie's name, to 
	 * identify the pool or pool set, this activity belongs to.</p>
	 * 
	 * @param dataObject The data object, to create the 
	 * @return The "scope" attribute value for the participant represented
	 * by the data object. The result is null, if the attribute value could
	 * not be determined.
	 */
	private String getScope(ParticipantReferenceDataObject dataObject) {
		String name = dataObject.getName();
		List<ParticipantReferenceDataObject> dataObjects = 
			this.diagram.getParticipantReferencesWithName(name);
		BlockActivity scope = null;
		for (Iterator<ParticipantReferenceDataObject> it = 
			dataObjects.iterator(); it.hasNext();) {
			ParticipantReferenceDataObject ref = it.next();
			if (ref.getScope() != null) {
				if (scope == null) {
					scope = ref.getScope();
				} else if (!scope.equals(ref.getScope())) {
					this.output.addError("There are participant reference "+
						"data objects of the same name defining different scopes.");
					return null;
				}
			}
		}
		
		// determine scope value with prefix
		return getScopeWithPrefix(scope);
	}
	
	/**
	 * Gets the multi-instance activity in which the participant reference
	 * acts as loop counter. For this purpose all participant reference data
	 * objects with the same name are checked. If there is one that has
	 * an association to a multi-instance loop, this loop will be returned.
	 * 
	 * <p>If a participant reference data object is associated with multiple 
	 * multi-instance loops, an error is added to the output. If there are
	 * several participant references with the same name that are associated
	 * with different multi-instance loops, an error is added to the output
	 * to.</p> 
	 * 
	 * @param dataObject The participant reference data object to get the 
	 * 	                 multi-instance for
	 * @return The determined multi-instance loop or null, if a loop could
	 * not be determined.
	 */
	private Activity getForEach(ParticipantReferenceDataObject dataObject) {
		Activity result = null;
		List<ParticipantReferenceDataObject> dataObjects = 
			this.diagram.getParticipantReferencesWithName(dataObject.getName());
		for (Iterator<ParticipantReferenceDataObject> it = 
			dataObjects.iterator(); it.hasNext();) {
			ParticipantReferenceDataObject ref = it.next();
			List<Activity> activities = getForEach(ref.getId());
			if (activities.size() > 1) {
				this.output.addError("The participant reference " + 
						dataObject.getName() + "can only be connected with " +
						"one multiple instance activity.");
			} else if (activities.size() == 1) {
				Activity act = activities.get(0);
				if (result == null) {
					result = act;
				} else if (!result.equals(act)) {
					this.output.addError("The participant references with the name " + 
							dataObject.getName() + "must be associated " +
							"with the same multiple instance activity.");
				}
			}
		}
		return result;
	}
	
	/**
	 * Gets the multi-instance activities that iterate over the given
	 * participant set. For this purpose all participant reference data
	 * objects with the same name are checked. If there is one that has
	 * an association to a multi-instance loop, this loop will be returned.
	 * 
	 * <p>If a participant reference data object is associated with multiple 
	 * multi-instance loops, an error is added to the output. If there are
	 * several participant references with the same name that are associated
	 * with different multi-instance loops, an error is added to the output
	 * to.</p> 
	 * 
	 * @param dataObject The participant reference data object to get the 
	 * 	                 multi-instance for
	 * @return The determined multi-instance loop or null, if a loop could
	 * not be determined.
	 */
	private List<Activity> getForEach(ParticipantSetDataObject dataObject) {
		List<Activity> result = new ArrayList<Activity>();
		
		List<ParticipantSetDataObject> dataObjects = 
			this.diagram.getParticipantSetsWithName(dataObject.getName());
		
		for (Iterator<ParticipantSetDataObject> it = 
			dataObjects.iterator(); it.hasNext();) {
			ParticipantSetDataObject set = it.next();
			List<Activity> activities = getForEach(set.getId());
			for (Iterator<Activity> itAct = activities.iterator(); itAct.hasNext();) {
				Activity act = itAct.next();
				if (!result.contains(act)) {
					result.add(act);
				}
			}
		}
		return result;
	}
	
	/**
	 * Determines whether the data object is associated with multiple
	 * instance loops (association from data object to loop). 
	 * 
	 * @param id The id of the participant data object to determine the
	 *           associated multi-instance loops for. 
	 * 
	 * @return A list with the associated multiple instance loops. 
	 * The list is empty, if the data object is not associated with 
	 * a multiple instance loop. 
	 */
	private List<Activity> getForEach(String id) {
		List<Activity> result = new ArrayList<Activity>();
		List<Association> associations = this.diagram.getAssociationsWithTarget(
				id, Association.DIRECTION_TO, Activity.class);
		for (Iterator<Association> itAssoc = 
			associations.iterator(); itAssoc.hasNext();) {
			Association association = itAssoc.next();
			Activity activity = (Activity)association.getSource();
			if ((activity.getLoop() != null) && (activity.getLoop()
					.getLoopType().equals(Loop.TYPE_MULITPLE))) {
				result.add(activity);
			}
		}
		return result;
	}
	
	/**
	 * Checks, if the multiple instance loop corresponds a forEach that iterates
	 * over a set of participants (BPEL4Chor forEach). In this case
	 * there is no counter variable data object defined for the loop.
	 * 
	 * @param activity The activity to check
	 * 
	 * @return True if the activity is a BPEL4Chor forEach (no counter variable
	 * associated with the loop). False otherwise.
	 */
	private boolean isBPEL4ChorForEach(Activity activity) {
		if ((activity.getLoop() != null) && 
				(activity.getLoop().getLoopType().equals(Loop.TYPE_MULITPLE))) {
			// if there is a counter variable data object defined 
			// the loop is not a BPEL4ChorForEach			
			List<Association> associations = 
				this.diagram.getAssociationsWithSource(activity.getId(), 
						Association.DIRECTION_TO, VariableDataObject.class);
			
			for (Iterator<Association> it = associations.iterator(); it.hasNext();) {
				VariableDataObject object = 
					(VariableDataObject)it.next().getSource();
				if (object.getType().equals(VariableDataObject.TYPE_COUNTER)) {
					return false;
				}
			}	
		}
		return true;
	}
	
	/**
	 * Determines the reference, the sender of the received message 
	 * should be bind to. This is a participant reference data object
	 * with an association from the receiving activity.
	 * 
	 * @param receivingActivity The activity to get the participant reference for
	 * 
	 * @return The first participant reference data object with an association 
	 * from the receiving activity. The result is null, if such an association
	 * was not found.
	 */
	private ParticipantReferenceDataObject getReferenceToBind(
			Activity receivingActivity) {
		List<Association> associations = this.diagram.getAssociationsWithSource(
				receivingActivity.getId(), Association.DIRECTION_FROM, 
				ParticipantReferenceDataObject.class);
		
		if (!associations.isEmpty()) {
			return (ParticipantReferenceDataObject)associations.get(0).getTarget();
		}
		return null;		
	}
	
	/**
	 * Determines the type of a participant reference from 
	 * the copy to attribute. The type is represented by 
	 * the type of the participant reference the copy to
	 * attribute denotes. 
	 * 
	 * @param dataObject the data object to get the type for
	 * 
	 * @return The name of the type or null, if the type could not be
	 * determined.
	 */
	private String getTypeFromCopyTo(ParticipantReferenceDataObject dataObject) {
		if (dataObject.getCopyTo() != null) {
			List<ParticipantReferenceDataObject> references = 
				this.diagram.getParticipantReferencesWithName(dataObject.getCopyTo());
			for (Iterator<ParticipantReferenceDataObject> it = 
				references.iterator(); it.hasNext();) {
				String type = getType(it.next());
				if (type != null) {
					return type;
				}
			}
		}
		return null;
	}
	
	/**
	 * Determines the type of a participant set from 
	 * the copy to attribute. The type is represented by 
	 * the type of the participant set the copy to
	 * attribute denotes. 
	 * 
	 * @param dataObject the data object to get the type for
	 * 
	 * @return The name of the type or null, if the type could not be
	 * determined.
	 */
	private String getTypeFromCopyTo(ParticipantSetDataObject dataObject) {
 		if (dataObject.getCopyTo() != null) {
 			List<ParticipantSetDataObject> sets = 
 				this.diagram.getParticipantSetsWithName(dataObject.getCopyTo());
 			for (Iterator<ParticipantSetDataObject> it = 
				sets.iterator(); it.hasNext();) {
				String type = getType(it.next());
				if (type != null) {
					return type;
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks if the type can be determined from participant reference
	 * data objects with the same name like the given reference.
	 * 
	 * @param dataObject The reference to determine the type for
	 * 
	 * @return The determined type or null, if the type could not
	 * be determined from participant reference data objects with the 
	 * same name. 
	 */
	private String getTypeFromOtherReferences(ParticipantReferenceDataObject dataObject) {
		List<ParticipantReferenceDataObject> references = 
			this.diagram.getParticipantReferencesWithName(dataObject.getName());
		for (Iterator<ParticipantReferenceDataObject> it = 
			references.iterator(); it.hasNext();) {
			ParticipantReferenceDataObject next = it.next();
			if (!next.equals(dataObject)) {
				String type = getType(it.next());
				if (type != null) {
					return type;
				}	
			}
		}
		return null;
	}
	
	/**
	 * Checks if the type can be determined from participant set
	 * data objects with the same name like the given set.
	 * 
	 * @param dataObject The set to determine the type for
	 * 
	 * @return The determined type or null, if the type could not
	 * be determined from participant set data objects with the 
	 * same name. 
	 */
	private String getTypeFromOtherSets(ParticipantSetDataObject dataObject) {
		List<ParticipantSetDataObject> sets = 
			this.diagram.getParticipantSetsWithName(dataObject.getName());
		for (Iterator<ParticipantSetDataObject> it = 
			sets.iterator(); it.hasNext();) {
			ParticipantSetDataObject next = it.next();
			if (!next.equals(dataObject)) {
				String type = getType(it.next());
				if (type != null) {
					return type;
				}	
			}
		}
		return null;
	}
	
	/**
	 * <p>Tries to determine the type of the data object from the activities it is
	 * associated with.</p> 
	 * 
	 * <p>If there is an association from the data object to 
	 * a sending task (invoke, reply), the type is determined from the pool,
	 * the receiving activity of the sent message belongs to.</p>
	 * 
	 * <p>If there is an association from or to a receiving task 
	 * (invoke, receive), the type is determined from the pool, the sending 
	 * activity of the received message belongs to.</p>
	 * 
	 * @param dataObject
	 * 
	 * @return The type that was determined or null, if the type could not be 
	 * determined.
	 */
	private String getTypeFromAssociations(ParticipantReferenceDataObject dataObject) {
		String id = dataObject.getId();
		List<Association> outgoingAssociations = 
			this.diagram.getAssociationsWithTarget(id, null, Activity.class);
		String targetId = null;
		String sourceId = null;
		Iterator<Association> it = outgoingAssociations.iterator();
		while ((targetId == null) && (sourceId == null) && it.hasNext()) {
			Association association = it.next();
			if (association.getDirection().equals(Association.DIRECTION_NONE)) {
				// if direction is none, the type can not be determined
				continue;
			} else if (association.getDirection().equals(Association.DIRECTION_TO)) {
				// check if source object of the association can be used to
				// determine the type of the reference
				GraphicalObject source = association.getSource();
				if ((source instanceof ServiceTask) || (source instanceof SendTask)) {
					// if outgoing association to sending task (reply, invoke)
					// source object of the association is the source of a message flow
					sourceId = ((Task)source).getId();
				}
			}
			// if incoming or outgoing association from data object to a receiving activity
			// the association is the target of a message flow
			Activity source = (Activity)association.getSource();
			if (source.isReceiving()) {
				targetId = source.getId();
			}
		}
		

		if (sourceId != null) {
			// if the source object of an association is a source of a message flow
			// the type can be determined from the swimlane the target of the message flow
			// is located in
			MessageFlow messageFlow = this.diagram.getMessageFlowWithSource(sourceId);
			if ((messageFlow != null) && (messageFlow.getTarget() != null)) {
				return messageFlow.getTarget().getParentSwimlane().getName();
			}
		} 
		if (targetId != null) {
			// if the source object of an association is the target of a message flow
			// the type can be determined from swimlane the source of 
			// the message flow is located in
			List<MessageFlow> messageFlows = 
				this.diagram.getMessageFlowsWithTarget(targetId);
			if (!messageFlows.isEmpty()) {
				return messageFlows.get(0).getSource().getParentSwimlane().getName();
			}
		}
		return null;
	}
	
	/**
	 * <p>Determines the type for the given participant reference data object.</p>
	 * 
	 * <p>There are several possibilities to determine the type:
	 * <ul>
	 * 	<li> Association from the data object to a sending task (invoke, reply):
	 * 	The type is determined from the pool, the receiving activity of the
	 *  connected message flow belongs to.
	 *  <li> Association from or to a receiving task (invoke, receive): The type
	 *  is determined from the pool, the sending activity of the connected
	 *  message flow belongs to.
	 *  <li> A reference is defined the given reference should be copied To: The 
	 *  type can be determined from the reference to copy this reference to
	 *  <li> There are other references with the same name in the diagram:
	 *  The type can be determined from these references.
	 * </ul>
	 * </p>
	 * 
	 * @param dataObject The participant reference data object to determine the
	 * type for
	 * 
	 * @return the determined type of null, if no type could be determined
	 */
	private String getType(ParticipantReferenceDataObject dataObject) {
		// data object is always target thus we only check 
		// associations with the data object as target
		if (dataObject.getType() != null) {
			return dataObject.getType();
		}
		
		String type = getTypeFromAssociations(dataObject);
		
		if (type == null) {
			// determine type from CopyTo attribute
			type = getTypeFromCopyTo(dataObject);
		}
		
		if (type == null) {
			// determine type from participant reference data objects 
			// with the same name
			type = getTypeFromOtherReferences(dataObject);
		}
		
	
		if (type != null) {
			// set type for all set data objects with this name
			// (can be found easier the next time)
			List<ParticipantReferenceDataObject> dataObjects = 
				this.diagram.getParticipantReferencesWithName(dataObject.getName());
			for (Iterator<ParticipantReferenceDataObject> it = 
				dataObjects.iterator(); it.hasNext();) {
				it.next().setType(type);
			}
		}
		
		return type;
	}
	
	/**
	 * Determines the participant type of the given data object. If the data object
	 * is a participant reference data object the type is determined using
	 * {@link #getType(ParticipantReferenceDataObject)} and if the data object
	 * is a participant set data object it is determined using 
	 * {@link #getType(ParticipantSetDataObject)}. Otherwise the result is null.
	 * 
	 * @param dataObject The data object to determine the participant type for.
	 * 
	 * @return The determined participant type or null if the type could not be 
	 * determined.
	 */
	public String getType(DataObject dataObject) {
		if (dataObject instanceof ParticipantReferenceDataObject) {
			return getType((ParticipantReferenceDataObject)dataObject);
		} else if (dataObject instanceof ParticipantSetDataObject) {
			return getType((ParticipantSetDataObject)dataObject);
		} else {
			return null;
		}
	}
	
	/**
	 * Tries to determine the type of the participant set from participant
	 * references contained in the set. 
	 * 
	 * @param dataObject the participant set data object to determine the type
	 * 					 for
	 *  
	 * @return the determined type of null, if no type could be determined
	 */
	private String getTypeFromContainedReferences(
			ParticipantSetDataObject dataObject) {
		List<ParticipantReferenceDataObject> references = 
			getContainedReferences(dataObject);
		
		for (Iterator<ParticipantReferenceDataObject> it = 
			references.iterator(); it.hasNext();) {
			String type = getType(it.next());
			if (type != null) {
				return type;
			}
		}
		return null;
	}
	
	/**
	 * Tries to determine the type of the participant set from participant
	 * sets contained in the set. 
	 * 
	 * @param dataObject the participant set data object to determine the type
	 * 					 for
	 *  
	 * @return the determined type of null, if no type could be determined
	 */
	private String getTypeFromContaintedSets(ParticipantSetDataObject dataObject) {
		List<ParticipantSetDataObject> sets = 
			getContainedSets(dataObject);
		
		for (Iterator<ParticipantSetDataObject> it = 
			sets.iterator(); it.hasNext();) {
			String type = getType(it.next());
			if (type != null) {
				return type;
			}
		}
		return null;
	}
	
	/**
	 * <p>Tries to determine the type of the data object from the activities it is
	 * associated with.</p> 
	 * 
	 * <p>If there is an association from the data object to 
	 * a multiple instance loop, the type is determined from the participant reference 
	 * that acts as loop counter.</p>
	 * 
	 * <p>If there is an association from a receiving task to the data object
	 * (invoke, receive), the type is determined from the participant reference,
	 * the actual sender will be bind to.</p>
	 * 
	 * @param dataObject
	 * 
	 * @return The type that was determined or null, if the type could not be 
	 * determined.
	 */
	private String getTypeFromAssociations(ParticipantSetDataObject dataObject) {
		List<Association> associations = this.diagram.getAssociationsWithTarget(
				dataObject.getId(), null, Activity.class);
		
		for (Iterator<Association> it = associations.iterator(); it.hasNext();) {
			Association association = it.next();
			Activity source = (Activity)association.getSource();
			ParticipantReferenceDataObject refDataObject = null;
			
			if (association.getDirection().equals(Association.DIRECTION_NONE)) {
				// if direction is none, the type can not be determined
				continue;
			} else if (association.getDirection().equals(Association.DIRECTION_TO)) {
				// if loop that is a BPEL4Chor forEach loop the counter reference
				// associated with the loop, too, can be used to determine the type
				if ((source.getLoop() != null) && (isBPEL4ChorForEach(source))) {
					refDataObject = this.diagram.getLoopCounter(source);
					if (refDataObject == null) {
						refDataObject = generateLoopCounterRef(source);
						// eventuell automatisch generieren, aber dann auch in Diagram einfuegen,
						// damit er bei der Generierung des message flows bekannt ist
					}
				}
			} else if (association.getDirection().equals(Association.DIRECTION_FROM)) {
				// if the source of the association is a receiving activity
				// the type can be determined from the participant reference,
				// that is also connected with the set and represents the
				// reference the sender will be bind to
				if (source.isReceiving()) {
					refDataObject = getReferenceToBind(source);
				}
			}
			if (refDataObject != null) {
				String type = getType(refDataObject);
				if (type != null) {
					return type;
				}
			}
		}
		return null;
	}
	
	/**
	 * <p>Determines the type for the given participant set data object.</p>
	 * 
	 * <p>There are several possibilities to determine the type:
	 * <ul>
	*  	<li>Association from the data object to a multiple instance loop:
	*  The type is determined from the participant reference 
	 * that acts as loop counter.
	 * 
	 * 	<li>Association from a receiving task to the data object
	 * (invoke, receive): The type is determined from the participant reference,
	 * the actual sender will be bind to.
	 * 
	 * 	<li> A set is defined the given set should be copied To: The 
	 *  type can be determined from the set to copy this set to
	 *  
	 *  <li> There are other sets or references contained in this set:
	 *  The type can be determined from these sets or references.
	 *  
	 *  <li> There are other sets with the same name in the diagram:
	 *  The type can be determined from these sets.
	 * </ul>
	 * </p>
	 * 
	 * @param dataObject The participant reference data object to determine the
	 * type for
	 * 
	 * @return the determined type of null, if no type could be determined
	 */
	private String getType(ParticipantSetDataObject dataObject) {
		// data object is always the target thus we only check 
		// associations with the data object as target
		if (dataObject.getType() != null) {
			return dataObject.getType();
		}
		String type = getTypeFromAssociations(dataObject);
		
		// the copyTo attribute can be used to determine the type as well
		if (type == null) {
			type = getTypeFromCopyTo(dataObject);
		}
	
		if (type == null) {
			type = getTypeFromContainedReferences(dataObject);
		}
		
		if (type == null) {
			type = getTypeFromContaintedSets(dataObject);
		}
		
		if (type == null) {
			type = getTypeFromOtherSets(dataObject);
		}
		
		// set type for all set data objects with this name
		// (can be found easier the next time)
		if (type != null) {
			List<ParticipantSetDataObject> dataObjects = 
				this.diagram.getParticipantSetsWithName(dataObject.getName());
			for (Iterator<ParticipantSetDataObject> it = 
				dataObjects.iterator(); it.hasNext();) {
				it.next().setType(type);
			}
		}
		return type;
	}
	
	/**
	 * Builds the BPEL4Chor forEach value for the given participant
	 * reference. For this purpose the multi-instance loop is determined in
	 * which the reference acts as loop counter 
	 * (see {@link #getForEach(ParticipantReferenceDataObject)}).
	 * 
	 * The name of this multi-instance loop is combined with the prefix of the
	 * process the loop is contained in.
	 * 
	 * @param dataObject The participant reference data object to determine the
	 *                   forEach for.
	 * 
	 * @return The BPEL4Chor forEach value for the participant reference. The 
	 * result is null, if the reference does not act as loop counter or the 
	 * loop could not be determined.
	 */
	private String getForEachWithPrefix(ParticipantReferenceDataObject dataObject) {
		Activity forEach = getForEach(dataObject);
	
		if (forEach instanceof BlockActivity) {
			return forEach.getParentSwimlane().getPrefix() + ":" + forEach.getName();
		} else if (forEach instanceof Task){
			// forEach containing the activity will be generated during the transformation of the process
			// the name of the generated forEach will have "forEach_" as name prefix
			return forEach.getParentSwimlane().getPrefix() + ":" + "forEach_" + forEach.getName();
		}
		return null;
	}
	
	/**
	 * Builds the BPEL4Chor forEach value for the given participant set. 
	 * For this purpose the multi-instance loop is determined that iterates
	 * over the participant set 
	 * (see {@link #getForEach(ParticipantSetDataObject)}).
	 * 
	 * The name of this multi-instance loop is combined with the prefix of the
	 * process the loop is contained in.
	 * 
	 * @param dataObject The participant set data object to determine the
	 *                   forEach for.
	 * 
	 * @return The BPEL4Chor forEach value for the participant set. The 
	 * result is null, if there is no loop iterating over the set.
	 */
	private String getForEachWithPrefix(ParticipantSetDataObject dataObject) {
		List<Activity> forEach = getForEach(dataObject);
		if ((forEach == null) || forEach.isEmpty()) {
			return null;
		}
		String forEachValue = "";
		
		for (Iterator<Activity> it = forEach.iterator(); it.hasNext();) {
			Activity act = it.next();

			if (act instanceof BlockActivity) {
				forEachValue += 
					act.getParentSwimlane().getPrefix() + ":" + act.getName();
			} else if (act instanceof Task){
				// forEach containing the activity will be generated during 
				// the transformation of the process the name of the generated
				// forEach will have "forEach_" as name prefix
				forEachValue += 
					act.getParentSwimlane().getPrefix() + ":" + "forEach_" + act.getName();
			}
			
			if (it.hasNext()) {
				forEachValue += " ";
			}
		}
		return forEachValue;
	}
	
	/**
	 * Creates a participant reference element for the given participant reference
	 * data object. For this purpose the attribute values for a participant reference
	 * elements are determined (e.g. type, forEach, scope).   
	 *  
	 * <p>If the participant reference is contained in a set, the participant
	 * type does not need to be determined.</p>  
	 * 
	 * <p>If a participant reference with this name was already created the result
	 * is null</p>
	 *  
	 * @param document   The parent document to create the element in.
	 * @param dataObject The data object that provides the information for the
	 *                   participant reference.
	 * @param contained  True, if the participant reference is contained in a set,
	 *                   false otherwise.
	 * 
	 * @return The created participant reference element or null if the reference was
	 * already created before.
	 */
	private Element createReferenceElement(Document document, 
			ParticipantReferenceDataObject dataObject, boolean contained) {
		
		// check if already created
		String name = dataObject.getName();
		if (this.referenceNames.contains(name)) {
			return null;
		}
		this.referenceNames.add(name);
		
		Element element = document.createElement("participant");
		// create name attribute
		element.setAttribute("name", name);
		
		// create type attribute
		if (!contained)  {
			String type = getType(dataObject);
			if (type != null) {
				element.setAttribute("type", type);
			}
		}
		
		// create forEach
		String forEach = getForEachWithPrefix(dataObject);
		if (forEach != null) {
			element.setAttribute("forEach", forEach);
		} else {
			// create Scope if the element is not the loop counter of a forEach
			String scope = getScope(dataObject);
			if (scope != null) {
				element.setAttribute("scope", scope);
			}
		}
		
		// create containment and selects attribute
		if (contained) {
			String containment = dataObject.getContainment();
			if (containment != null) {
				element.setAttribute("containment", containment);
			}
		}
		
		String selectsValue = getSelectsValue(dataObject.getName());
		if ((selectsValue != null) && !selectsValue.equals("")) {
			element.setAttribute("selects", selectsValue);
		}
		return element;	
	}
	
	/**
	 * Generates a participant reference data object that acts
	 * as loop counter for the given loop. This method should only
	 * be used if there is no loop counter associated with the loop
	 * and if the loop does not define a loop counter variable.
	 * 
	 * <p>An association will be created that connects the participant
	 * reference data object with the loop.</p>
	 * 
	 * @param loop The loop to create the counter for.
	 * 
	 * @return The created participant reference data object that 
	 * acts as loop counter.
	 */
	private ParticipantReferenceDataObject generateLoopCounterRef(Activity loop) {
		ParticipantReferenceDataObject ref = 
			new ParticipantReferenceDataObject(this.output);
		ref.setName("generatedLoopCounter_" + loop.getName());
		ref.setContainer(loop.getParentContainer());
		this.diagram.addParticipantReferenceDataObject(ref);
		
		// create association 
		Association assoc = new Association(this.output);
		assoc.setTarget(ref);
		assoc.setSource(loop);
		assoc.setDirection(Association.DIRECTION_TO);
		this.diagram.addAssociation(assoc);
		return ref;
	}
	
	/**
	 * Generates a participant reference data object that is
	 * used to bind the participant to that should be stored in 
	 * a set. This method should only be used if there is no participant 
	 * reference data object associated with the given activity and 
	 * the participant could be bound to.
	 * 
	 * <p>An association will be created that connects the activity with the
	 * participant reference data object.</p>
	 * 
	 * @param act The receiving activity the participant reference will 
	 * be created for.
	 * 
	 * @return The created participant reference data object.
	 */
	private ParticipantReferenceDataObject generateBindToReferene(Activity act) {
		ParticipantReferenceDataObject ref = 
			new ParticipantReferenceDataObject(this.output);
		ref.setName("generatedBindTo_" + act.getName());
		ref.setContainer(act.getParentContainer());
		this.diagram.addParticipantReferenceDataObject(ref);
		
		// create association 
		Association assoc = 
			new Association(this.output);
		assoc.setTarget(ref);
		assoc.setSource(act);
		assoc.setDirection(Association.DIRECTION_FROM);
		this.diagram.addAssociation(assoc);
		
		return ref;
	}
	
	/**
	 * Determines the participant references that are contained in the given
	 * participant set.
	 * 
	 * <p>Participant references are contained in a set if there is an 
	 * association from the reference to the set. A participant reference
	 * that acts as loop counter is contained in the participant set 
	 * this loop is iterating over. A participant reference with an association
	 * from a receiving as is contained in the participant set that is also
	 * associated with this task (bind-to-relationship).</p>
	 * 
	 * @param dataObject The participant set to get the contained references for
	 * 
	 * @return A list with the determined participant reference data objects. The list
	 * is empty if no participant references were found. 
	 */
	private List<ParticipantReferenceDataObject> getContainedReferences(
			ParticipantSetDataObject dataObject) {
		List<ParticipantReferenceDataObject> result = 
			new ArrayList<ParticipantReferenceDataObject>();
		
		// containment relationship can be expressed using directs associations
		// between data objects
		List<Association> associations = this.diagram.getAssociationsWithTarget(
					dataObject.getId(), Association.DIRECTION_FROM, 
					ParticipantReferenceDataObject.class);
		for (Iterator<Association> itAssoc = associations.iterator(); itAssoc.hasNext();) {
			Association association = itAssoc.next();
			result.add((ParticipantReferenceDataObject)association.getSource());
		}
		
		associations = this.diagram.getAssociationsWithSource(dataObject.getId(), 
					Association.DIRECTION_TO, ParticipantReferenceDataObject.class);
		for (Iterator<Association> itAssoc = associations.iterator(); itAssoc.hasNext();) {
			Association association = itAssoc.next();
			result.add((ParticipantReferenceDataObject)association.getTarget());
		}
		
		// special containment rules: 
		associations = this.diagram.getAssociationsWithTarget(
				dataObject.getId(), null, Activity.class);
		for (Iterator<Association> itAssoc = associations.iterator(); itAssoc.hasNext();) {
			Association association = itAssoc.next();
			Activity source = (Activity)association.getSource();
			if (association.getDirection().equals(Association.DIRECTION_NONE)) {
				continue;
			} else if (association.getDirection().equals(Association.DIRECTION_TO)) {	
				// if set is associated with a multiple instance loop the 
				// loop counter is contained in the set
				if ((source.getLoop() != null) && 
						source.getLoop().getLoopType().equals(Loop.TYPE_MULITPLE)) {
					ParticipantReferenceDataObject counter = 
						this.diagram.getLoopCounter(source);
					if (counter == null) {
						counter = generateLoopCounterRef(source);						
					}
					result.add(counter);
				}
			} else if (association.getDirection().equals(Association.DIRECTION_FROM)) {
				// if set has association from a receive task another 
				// reference associated (outgoing) with this task is contained in the set 
				// (bindSenderTo)
				if (source.isReceiving()) {
					ParticipantReferenceDataObject bindTo = 
						getReferenceToBind(source);
					if (bindTo == null) {
						// automatisch generieren -> must be known during generation of message link
						bindTo = generateBindToReferene(source);
					}
					result.add(bindTo);
				}
			}
		}
		return result;
	}
	
	/**
	 * Creates the participant referecnce elements for each participant
	 * reference that is contained in the given participant set.
	 * (see {@link #getContainedReferences(ParticipantSetDataObject)}). 
	 * 
	 * @param document   The document to create the elements in.
	 * @param dataObject The participant set data object that containes the 
	 *                   participant references to create.
	 *                   
	 * @return A list with the created participant reference elements.
	 */
	private List<Element> createContainedReferences(
		Document document, ParticipantSetDataObject dataObject) {
		
		// references associated with a non-directed association
		List<Element> result = new ArrayList<Element>();
		List<ParticipantReferenceDataObject> toCreate = 
			getContainedReferences(dataObject);
			
		for (Iterator<ParticipantReferenceDataObject> it = 
			toCreate.iterator(); it.hasNext();) {
			Element ref = createReferenceElement(document, 
					it.next(), true);
			if (ref != null) {
				result.add(ref);
			}
		}
		
		return result;
	}
	
	/**
	 * Creates the participant set elements for each participant
	 * set that is contained in the given participant set.
	 * (see {@link #getContainedSets(ParticipantSetDataObject)}). 
	 * 
	 * @param document   The document to create the elements in.
	 * @param dataObject The participant set data object that containes the 
	 *                   participant sets to create.
	 *                   
	 * @return A list with the created participant set elements.
	 */
	private List<Element> createContainedSets(Document document, 
			ParticipantSetDataObject dataObject) {
		
		List<Element> result = new ArrayList<Element>();
		List<ParticipantSetDataObject> toCreate =  getContainedSets(dataObject);
		
		for (Iterator<ParticipantSetDataObject> it = toCreate.iterator(); it.hasNext();) {
			Element ref = createSetElement(document, 
					it.next(), true);
			if (ref != null) {
				result.add(ref);
			}
		}
		return result;
		
	}
	
	/**
	 * Determines the participant sets that are contained in the given
	 * participant set.
	 * 
	 * <p>Participant sets are contained in another set if there is an 
	 * association from the set to the containing set.</p>
	 * 
	 * @param dataObject The participant set to get the contained references for
	 * 
	 * @return A list with the determined participant set data objects. The list
	 * is empty if no participant sets were found. 
	 */
	private List<ParticipantSetDataObject> getContainedSets(
			ParticipantSetDataObject dataObject) {
			
		List<ParticipantSetDataObject> result = 
			new ArrayList<ParticipantSetDataObject>();
		
		List<Association> associations = this.diagram.getAssociationsWithTarget(
					dataObject.getId(), Association.DIRECTION_FROM, 
					ParticipantSetDataObject.class);
		for (Iterator<Association> itAssoc = associations.iterator(); itAssoc.hasNext();) {
			Association association = itAssoc.next();
			result.add((ParticipantSetDataObject)association.getSource());
		}
		
		associations = this.diagram.getAssociationsWithSource(
				dataObject.getId(), Association.DIRECTION_TO, 
				ParticipantSetDataObject.class);
		for (Iterator<Association> itAssoc = associations.iterator(); itAssoc.hasNext();) {
			Association association = itAssoc.next();
			result.add((ParticipantSetDataObject)association.getTarget());
		}
			
		return result;	
	}
	
	/**
	 * Creates a participant set element for the given participant set
	 * data object. For this purpose the attribute values of a participant set
	 * element are determined (e.g. type, forEach, scope).   
	 *  
	 * <p>If the participant set is contained in another set, the participant
	 * type does not need to be determined.</p>  
	 * 
	 * <p>If a participant set with this name was already created the result
	 * is null</p>
	 *  
	 * @param document      The parent document to create the element in.
	 * @param setDataObject The data object that provides the information for the
	 *                      participant set.
	 * @param contained     True, if the participant set is contained in another set,
	 *                      false otherwise.
	 * 
	 *  @return The created participant set element or null if the set was
	 * already created before.
	 */
	private Element createSetElement(Document document, 
			ParticipantSetDataObject setDataObject, boolean contained) {
		
		Element result = document.createElement("participantSet");
		String name = setDataObject.getName();
		
		// check if already generated
		if (this.setNames.contains(name)) {
			return null;
		}
		this.setNames.add(name);
		
		// create name attribute
		result.setAttribute("name", name);
		
		// create forEach attribute
		String forEach = getForEachWithPrefix(setDataObject);
		if (forEach != null) {
			result.setAttribute("forEach", forEach);
		}
		
		// create scope
		String scope = getScope(setDataObject);
		if (scope != null) {
			result.setAttribute("scope", scope);
		}
		
		// create type
		if (!contained) {
			String type = getType(setDataObject);			
			if (type == null) {
				this.output.addError("Type for participant set " + 
						setDataObject.getName() + " could not be determined." );
			} else {
				result.setAttribute("type", type);
			}
		}
		
		// create contained references
		List<Element> containedReferences = 
			createContainedReferences(document, setDataObject);
		
		for (Iterator<Element> it = containedReferences.iterator(); it.hasNext();) {
			result.appendChild(it.next());
		}
		
		// create contained sets
		List<Element> containedSets = createContainedSets(document, setDataObject);
		
		for (Iterator<Element> it = containedSets.iterator(); it.hasNext();) {
			result.appendChild(it.next());
		}
		
		return result;	
	}
	
	/**
	 * Checks if a participant set is contained in any participant set.
	 * In this case an association from this set to another set must exist.
	 * 
	 * @param dataObject The participant set data object to check the 
	 *                   containment for.
	 * @return True, if the participant set is contained in another participant
	 * set, false otherwise.
	 */
	private boolean isContained(ParticipantSetDataObject dataObject) {
		
		List<ParticipantSetDataObject> sets = 
			this.diagram.getParticipantSetsWithName(dataObject.getName());
		
		for (Iterator<ParticipantSetDataObject> it = 
			sets.iterator(); it.hasNext();) {
			ParticipantSetDataObject set = it.next();
			
			// check if association from set to other participant set data object
			List associations = this.diagram.getAssociationsWithSource(
						set.getId(), Association.DIRECTION_FROM, 
						ParticipantSetDataObject.class);
			if (!associations.isEmpty()) {
				return true;
			}

			// check if association from other participant set data object to set
			associations = this.diagram.getAssociationsWithTarget(
					set.getId(), Association.DIRECTION_TO, 
					ParticipantSetDataObject.class);
			
			if (!associations.isEmpty()) {
				return true;
			} 
		}
		return false;
	}
	
	/**
	 * Generates the participant set elements from the participant set data
	 * objects. First of all the participant sets are created that are not
	 * contained in any other set. The contained participant references
	 * and sets will be created during the generation of this set.
	 * The created participant set elements will be appended to the given
	 * parent node.
	 * 
	 * @param document The document to create the elements in.
	 * @param parent   The parent node of the created participant set elements.
	 */
	private void transformParticipantSets(Document document, Node parent) {
		for (Iterator<ParticipantSetDataObject> it = 
			this.diagram.getParticipantSetDataObjects().iterator(); it.hasNext();) {
			ParticipantSetDataObject setDataObject = it.next();
			
			// create only those that are not contained in another set
			if(!isContained(setDataObject)) {
				Element setElement = createSetElement(document, setDataObject, false);
				if (setElement != null) {
					parent.appendChild(setElement);
				}
			}
		}
	}
	
	/**
	 * Creates the participant reference elements from the participant
	 * reference data objects. The created participant reference elements will 
	 * be appended to the given parent node.
	 * 
	 * @param document The document to create the elements in.
	 * @param parent   The parent node of the created participant 
	 *                 reference elements.
	 */
	private void transformParticipantReferences( 
			Document document, Node parent) {
		for (Iterator<ParticipantReferenceDataObject> it = 
			this.diagram.getParticipantRefDataObjects().iterator(); it.hasNext();) {
			ParticipantReferenceDataObject dataObject = it.next();
			Element ref = createReferenceElement(document, dataObject, false);
			if (ref != null)
				parent.appendChild(ref);
		}
	}

	/**
	 * Generates the participant reference and participant set elements of the 
	 * choreography topology.
	 * 
	 * First of all the participant references will be created from the pools.
	 * After that the participant set elements will be created from the participant
	 * set data objects (inclusive contained references and sets). At the end the
	 * remaining participant references will be created from the participant
	 * reference data objects.
	 * 
	 * @param document The document to create the elements in.
	 * 
	 * @return The created participants element containing the created
	 * participant reference and participant set elements. 
	 */
	public Element transformParticipants(Document document) {
		Element participants = document.createElement("participants");
		
		// create participant references from pools
		transformParticipantReferencesFromPools(document, participants);
		
		// create participant sets (including contained participant references) from data objects
		transformParticipantSets(document, participants);
		
		// create remaining participants
		transformParticipantReferences(document, participants);
		
		return participants;
	}
}
