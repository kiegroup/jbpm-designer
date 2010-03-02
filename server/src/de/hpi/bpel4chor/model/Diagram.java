package de.hpi.bpel4chor.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.activities.BlockActivity;
import de.hpi.bpel4chor.model.activities.Event;
import de.hpi.bpel4chor.model.activities.Handler;
import de.hpi.bpel4chor.model.activities.IntermediateEvent;
import de.hpi.bpel4chor.model.activities.StartEvent;
import de.hpi.bpel4chor.model.activities.Task;
import de.hpi.bpel4chor.model.activities.TriggerResultMessage;
import de.hpi.bpel4chor.model.artifacts.ParticipantReferenceDataObject;
import de.hpi.bpel4chor.model.artifacts.ParticipantSetDataObject;
import de.hpi.bpel4chor.model.artifacts.VariableDataObject;
import de.hpi.bpel4chor.model.connections.Association;
import de.hpi.bpel4chor.model.connections.MessageFlow;

/**
 * This class represents a BPMN+ diagram. A BPMN+ diagram
 * containes pools, pool sets, data objects, message flows 
 * and associations.
 * 
 */
public class Diagram {
	
	private String id = null;
	private String name = null;
	private URI expressionLanguage = null;
	private URI queryLanguage = null;
	private String targetNamespace = null;
	private String groundingFile = null;
	
	private List<Pool> pools = new ArrayList<Pool>();
	private List<PoolSet> poolSets = new ArrayList<PoolSet>();
	private List<VariableDataObject> variableDataObjects = 
		new ArrayList<VariableDataObject>();	
	private List<ParticipantReferenceDataObject> participantRefDataObjects = 
		new ArrayList<ParticipantReferenceDataObject>();
	private List<ParticipantSetDataObject> participantSetDataObjects = 
		new ArrayList<ParticipantSetDataObject>();
	private List<MessageFlow> messageFlows = new ArrayList<MessageFlow>();
	private List<Association> associations = new ArrayList<Association>();
	
	/**
	 * The key is the id of a diagram element. 
	 * The value is the corresponding diagram element.
	 * Used to reach every diagram element.
	 */
	private HashMap<String, Object> objects = new HashMap<String, Object>();
	
	/**
	 * Constructor. Initializes the expressionLanguage and the queryLanguage 
	 * with their default values.
	 */
	public Diagram() {}
	
	/**
	 * Collects the associations that have the specified source object and
	 * direction. The targetClass determines the class the target object
	 * of the association should be instance of.
	 * 
	 * @param sourceId     The id of the source object.
	 * @param direction    The direction of the association 
	 * ({@link Association#DIRECTION_FROM}, {@link Association#DIRECTION_TO} or
	 *  {@link Association#DIRECTION_NONE}). This parameter may be null.
	 * @param targetClass  The class the target object should be instance of.
	 * This parameter may be null. 
	 * 
	 * @return A list with the collected associations. Or an empty list of no
	 * association was found.
	 */
	public List<Association> getAssociationsWithSource(
			String sourceId, String direction, Class targetClass) {
		List<Association> result = new ArrayList<Association>();
		if (sourceId != null) { 
			for (Iterator<Association> it = this.associations.iterator(); it.hasNext();) {
				Association assoc = it.next();
				if ((direction != null) && 
						!assoc.getDirection().equals(direction)) {
					continue;
				}
				GraphicalObject source = assoc.getSource();
				if (source.getId().equals(sourceId)) {
					if (targetClass != null) {
						if (targetClass.isInstance(assoc.getTarget())) {
							result.add(assoc);
						}
					} else {
						result.add(assoc);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Collects the associations that have the specified target object and
	 * direction. The sourceClass determines the class the source object
	 * of the association should be instance of.
	 * 
	 * @param targetId     The id of the target object.
	 * @param direction    The direction of the association 
	 * ({@link Association#DIRECTION_FROM}, {@link Association#DIRECTION_TO} or
	 *  {@link Association#DIRECTION_NONE}). This parameter may be null.
	 * @param sourceClass  The class the source object should be instance of.
	 * This parameter may be null. 
	 * 
	 * @return A list with the collected associations. Or an empty
	 * list if no association was found.
	 */
	public List<Association> getAssociationsWithTarget(
			String targetId, String direction, Class sourceClass) {
		List<Association> result = new ArrayList<Association>();
		if (targetId != null) { 
			for (Iterator<Association> it = this.associations.iterator(); it.hasNext();) {
				Association assoc = it.next();
				if ((direction != null) && 
						!assoc.getDirection().equals(direction)) {
					continue;
				}
				GraphicalObject target = assoc.getTarget();
				if (target.getId().equals(targetId)) {
					if (sourceClass != null) {
						if (sourceClass.isInstance(assoc.getSource())) {
							result.add(assoc);
						}
					} else {
						result.add(assoc);
					}	
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns the first message flow with the specified source object.
	 * 
	 * @param sourceId     The id of the source object.
	 * 
	 * @return The first message flow found. Or null if no message flow
	 * was found.
	 */
	public MessageFlow getMessageFlowWithSource(String sourceId) {
		if (sourceId != null) { 
			for (Iterator<MessageFlow> it = this.messageFlows.iterator(); it.hasNext();) {
				MessageFlow messageFlow = it.next();
				Activity source = messageFlow.getSource();
				if (source.getId().equals(sourceId)) {
					return messageFlow;
				}
			}
		}
		return null;
	}
	
	/**
	 * Collects the message flows that have the specified source object.
	 * 
	 * @param sourceId     The id of the source object.
	 * 
	 * @return A list with the collected message flows. Or an empty
	 * list of no message flow was found.
	 */
	public List<MessageFlow> getMessageFlowsWithSource(String sourceId) {
		List<MessageFlow> result = new ArrayList<MessageFlow>();
		if (sourceId != null) { 
			for (Iterator<MessageFlow> it = this.messageFlows.iterator(); it.hasNext();) {
				MessageFlow messageFlow = it.next();
				Activity source = messageFlow.getSource();
				if (source.getId().equals(sourceId)) {
					result.add(messageFlow);
				}
			}
		}
		return result;
	}
	
	/**
	 * Collects the message flows that have the specified target object.
	 * 
	 * @param targetId     The id of the target object.
	 * 
	 * @return A list with the collected message flows. Or an empty
	 * list of no message flow was found.
	 */
	public List<MessageFlow> getMessageFlowsWithTarget(String targetId) {
		List<MessageFlow> result = new ArrayList<MessageFlow>();
		if (targetId != null) { 
			for (Iterator<MessageFlow> it = this.messageFlows.iterator(); it.hasNext();) {
				MessageFlow messageFlow = it.next();
				Activity target = messageFlow.getTarget();
				if (target.getId().equals(targetId)) {
					result.add(messageFlow);
				}
			}
		}
		return result;
	}
	
	/**
	 * Collects all participant reference data objects with the given name.
	 * 
	 * @param name The name of the participant reference data objects.
	 * 
	 * @return A list with the collected participant reference data objects.
	 * Or an empty list of no participant reference data object was found.
	 **/
	public List<ParticipantReferenceDataObject> 
		getParticipantReferencesWithName(String name) {
		
		List<ParticipantReferenceDataObject> result = 
			new ArrayList<ParticipantReferenceDataObject>(); 
		
		for (Iterator<ParticipantReferenceDataObject> it = 
				this.participantRefDataObjects.iterator(); it.hasNext();) {
			ParticipantReferenceDataObject dataObject = it.next();
			if (dataObject.getName().equals(name)) {
				result.add(dataObject);
			}
		}
		return result;
	}
	
	/**
	 * Collects all participant set data objects with the given name.
	 * 
	 * @param name The name of the participant set data objects.
	 * 
	 * @return A list with the collected participant set data objects.
	 * Or an empty list of no participant set data object was found.
	 */
	public List<ParticipantSetDataObject> 
		getParticipantSetsWithName(String name) {
		
		List<ParticipantSetDataObject> result = 
			new ArrayList<ParticipantSetDataObject>();
		
		for (Iterator<ParticipantSetDataObject> it = 
				this.participantSetDataObjects.iterator(); it.hasNext();) {
			ParticipantSetDataObject dataObject = it.next();
			if (dataObject.getName().equals(name)) {
				result.add(dataObject);
			}
		}
		return result;
	}
	
	/**
	 * Returns the variable data objects of the specified type that 
	 * are defined for the given container. If no container
	 * is specified each variable data object is added to the result list.
	 * 
	 * @param type      The type of the variable data objects
	 * @param container The container, the variable data objects are defined 
	 * 					in. This parameter may be null.
	 * 
	 * @return The collected variable data objects. Or an empty list
	 * if no variable data object was found.
	 */
	private List<VariableDataObject> 
		getVariableDataObjects(String type, Container container) {
		
		List<VariableDataObject> result = new ArrayList<VariableDataObject>();
		for (Iterator<VariableDataObject> it = 
				this.variableDataObjects.iterator(); it.hasNext();) {
			VariableDataObject varObject = it.next();
			if (type == null) {
				if (container == null) {
					result.add(varObject);
				} else if ((varObject.getContainer().equals(container))) {
					result.add(varObject);
				}
			} else if (varObject.getType().equals(type)) {
				if (container == null) {
					result.add(varObject);
				} else if ((varObject.getContainer().equals(container))) { 
					result.add(varObject);
				}
			}
		}
		return result;
	}
	
	/**
	 * Determines the first handler that is connected the given event by an
	 * association. Only association with the direction To or From are
	 * considered. Moreover the arrow head of the association must always
	 * be on the handler side.
	 * 
	 * @param event The intermediate event to determine the connected handler
	 * for.
	 * 
	 * @return The first handler that is connected with the event. Or null if no
	 * handler was found.
	 */
	public Handler getAssociatedCompensationHandler(IntermediateEvent event) {
		List<Association> associations = 
			getAssociationsWithSource(event.getId(), Association.DIRECTION_FROM, Handler.class);
		for (Iterator<Association> it = associations.iterator(); it.hasNext();) {
			Handler handler = (Handler)it.next().getTarget();
			if (handler.getHandlerType().equals(Handler.TYPE_COMPENSATION)) {
				return handler;
			}
		}
		
		associations = 
			getAssociationsWithTarget(event.getId(), Association.DIRECTION_TO, Handler.class);
		for (Iterator<Association> it = associations.iterator(); it.hasNext();) {
			Handler handler = (Handler)it.next().getSource();
			if (handler.getHandlerType().equals(Handler.TYPE_COMPENSATION)) {
				return handler;
			}
		}
		return null;
	}
	
	/**
	 * Determines the first variable data object of the given type that is 
	 * associated with the given event. Only associations with the direction
	 * From are considered (association from event to data object).
	 * 
	 * @param type  The type of the variable data object 
	 * ({@link VariableDataObject#TYPE_COUNTER}, {@link VariableDataObject#TYPE_FAULT},
	 * {@link VariableDataObject#TYPE_MESSAGE} or {@link VariableDataObject#TYPE_STANDARD})
	 * @param event The event the data object must be associated with
	 * 
	 * @return The first found variable data object. Or null if no variable data
	 * object was found.
	 */
	private VariableDataObject getVariableDataObject(String type, Event event) {		
		List<VariableDataObject> vars = getVariableDataObjects(type, null);
		
		// check if variable data object is associated with the event
		for (Iterator<VariableDataObject> it = vars.iterator(); it.hasNext();) {
			VariableDataObject var = it.next();
			List<Association> assocs = getAssociationsWithTarget(
					var.getId(), Association.DIRECTION_FROM, event.getClass());
			for (Iterator<Association> itAssocs = assocs.iterator(); itAssocs.hasNext();) {
				Association assoc = itAssocs.next();
				if (assoc.getSource().equals(event)) {
					return var;
				}
			}
		}
		return null;
	}
	
	/**
	 * Determines the first fault variable data object that is 
	 * associated with the given event. Only associations with the direction
	 * From are considered (association from event to data object).
	 * 
	 * @param errorEvent The event the data object must be associated with. 
	 * This must be an intermediate event with the trigger ResultError.
	 * 
	 * @return The first found fault variable data object. Or null if 
	 * no variable data object was found.
	 */
	public VariableDataObject getFaultVariable(IntermediateEvent errorEvent) {
		return getVariableDataObject(VariableDataObject.TYPE_FAULT, errorEvent);
	}
	
	/**
	 * Determines the first message variable data object that is 
	 * associated with the given start event. Only associations with the direction
	 * From are considered (association from event to data object).
	 * 
	 * @param start The start event the data object must be associated with. 
	 * This must be a start event with the trigger {@link TriggerResultMessage}.
	 * 
	 * @return The first found fault variable data object. Or null if 
	 * no variable data object was found.
	 */
	public VariableDataObject getMessageVariable(StartEvent start) {
		return getVariableDataObject(VariableDataObject.TYPE_MESSAGE, start);
	}
	
	/**
	 * Determines the first counter variable data object that is 
	 * located in the given block activity.
	 * 
	 * @param loopingActivity The looping block activity the counter variable is located in
	 * 
	 * @return The first found counter variable data object. Or null if no
	 * counter variable data object was found.
	 */
	public VariableDataObject getCounterVariable(BlockActivity loopingActivity) {
		List<VariableDataObject> counterVars = getVariableDataObjects(
					VariableDataObject.TYPE_COUNTER, loopingActivity.getSubProcess());
		
		if (counterVars.isEmpty()) {
			return null;
		}
		return counterVars.get(0);
	}
	
	/**
	 * Determines the first counter variable data object that is 
	 * associated with the given looping task. Only associations with the direction
	 * To are considered (association from data object to task). 
	 * 
	 * @param loopingActivity The looping task the counter variable is associated with
	 * 
	 * @return The first found counter variable data object. Or null if
	 * no counter variable data object was found.
	 */
	public VariableDataObject getCounterVariable(Task loopingActivity) {
		List<Association> associations = 
			getAssociationsWithSource(loopingActivity.getId(), 
					Association.DIRECTION_TO, VariableDataObject.class);
		for (Iterator<Association> it = associations.iterator(); it.hasNext();) {
			VariableDataObject object = (VariableDataObject)it.next().getTarget();
			if (object.getType().equals(VariableDataObject.TYPE_COUNTER)) {
				return object;
			}
		}
		return null;
	}
	
	/**
	 * Determines all standard variable data objects that are associated with
	 * the given activity. If the activity is a receiving activity, then 
	 * associations from the activity to the data object are considered. If
	 * the activity is not receiving, associations from the data object to the 
	 * activity are considered.
	 * 
	 * 
	 * @param activity  The activity the data objects are associated with
	 * @param receiving True, if the activity is a receiving activity, 
	 * false otherwise.
	 * 
	 * @return A list with the associated standard variable data objects.
	 * Or an empty list if no variable data object was found.
	 */
	public List<VariableDataObject> getStandardVariables(
			Activity activity, boolean receiving) {
		
		List<VariableDataObject> result = new ArrayList<VariableDataObject>();
		List<VariableDataObject> standardVars = getVariableDataObjects(
					VariableDataObject.TYPE_STANDARD, null);
		
		for (Iterator<VariableDataObject> it = standardVars.iterator(); it.hasNext();) {
			VariableDataObject standardVar = it.next();
			
			if (receiving) {
				List<Association> assocs = getAssociationsWithTarget(
						standardVar.getId(), Association.DIRECTION_FROM, activity.getClass());
				if (!assocs.isEmpty()) {
					for (Iterator<Association> itAssoc = assocs.iterator(); itAssoc.hasNext();) {
						result.add((VariableDataObject)itAssoc.next().getTarget());
					}
				}
			} else {
				List<Association> assocs = getAssociationsWithTarget(
						standardVar.getId(), Association.DIRECTION_TO, activity.getClass());
				if (!assocs.isEmpty()) {
					for (Iterator<Association> itAssoc = assocs.iterator(); itAssoc.hasNext();) {
						result.add((VariableDataObject)itAssoc.next().getTarget());
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Determines the first standard variable data objects that is associated with
	 * the given activity. If the activity is a receiving activity, then 
	 * associations from the activity to the data object are considered. If
	 * the activity is not receiving, associations from the data object to the 
	 * activity are considered.
	 * 
	 * 
	 * @param activity  The activity the data objects are associated with
	 * @param receiving True, if the activity is a receiving activity, 
	 * false otherwise.
	 * 
	 * @return The first standard variable data object associated with the
	 * activity. Or null if no variable data object was found.
	 */
	public VariableDataObject getStandardVariable(Activity activity, boolean receiving) {
		List<VariableDataObject> standardVars = getVariableDataObjects(
					VariableDataObject.TYPE_STANDARD, null);
		
		for (Iterator<VariableDataObject> it = 
				standardVars.iterator(); it.hasNext();) {
			
			VariableDataObject standardVar = it.next();
			
			if (receiving) {
				List<Association> assocs = getAssociationsWithTarget(
						standardVar.getId(), 
						Association.DIRECTION_FROM, activity.getClass());
				if (assocs.isEmpty()) {
					continue;
				}
				return (VariableDataObject)assocs.get(0).getTarget();
			} else {
				List<Association> assocs = getAssociationsWithTarget(
						standardVar.getId(), 
						Association.DIRECTION_TO, activity.getClass());
				if (assocs.isEmpty()) {
					continue;
				}
				return (VariableDataObject)assocs.get(0).getTarget();
			}
		}
		return null;
	}
	
	/**
	 * Determines the first participant set data object that is associated
	 * with the given activity. Only associations with direction To are
	 * considered (association from data object to activity).
	 * 
	 * @param loopingActivity A multiple instance looping activity.
	 * 
	 * @return The first participant set data object associated with the
	 * given activity. Or null of no participant set was found.
	 */
	public ParticipantSetDataObject getLoopCounterSet(
			Activity loopingActivity) {
		List<Association> associations = getAssociationsWithSource(
				loopingActivity.getId(), Association.DIRECTION_TO, 
				ParticipantSetDataObject.class);
		
		if (!associations.isEmpty()) {
			return (ParticipantSetDataObject)associations.get(0).getTarget();
		} 
		return null;
	}
	
	/**
	 * Determins the first participant reference data object that is associated
	 * with the given activity. Only associations with direction To are
	 * considered (association from data object to activity).
	 * 
	 * @param loopingActivity A multiple instance looping activity.
	 * 
	 * @return The first participant reference data object associated with the
	 * given activity. Or null if no participant reference was found.
	 */
	public ParticipantReferenceDataObject getLoopCounter(
			Activity loopingActivity) {
		List<Association> associations = getAssociationsWithSource(
				loopingActivity.getId(), Association.DIRECTION_TO, 
				ParticipantReferenceDataObject.class);
		
		if (!associations.isEmpty()) {
			return (ParticipantReferenceDataObject)associations.get(0).getTarget();
		} 
		return null;
	}
	
	/**
	 * Adds a pool to the list of pools in the diagram.
	 *  
	 * @param pool The pool to add.
	 */
	public void addPool(Pool pool) {
		this.pools.add(pool);
	}
	
	/**
	 * Adds a pool set to the list of pool sets in the diagram.
	 *  
	 * @param poolSet The pool set to add.
	 */
	public void addPoolSet(PoolSet poolSet) {
		this.poolSets.add(poolSet);
	}
	
	/**
	 * Adds an association to the list of associations in the diagram.
	 *  
	 * @param assoc The association to add.
	 */
	public void addAssociation(Association assoc) {
		this.associations.add(assoc);
	}
	
	/**
	 * Adds a message flow to the list of message flows in the diagram.
	 *  
	 * @param flow The message flow to add.
	 */
	public void addMessageFlow(MessageFlow flow) {
		this.messageFlows.add(flow);
	}
	
	/**
	 * Adds a variable data object to the list of variable data objects 
	 * in the diagram.
	 *  
	 * @param data The variable data object to add.
	 */
	public void addVariableDataObject(VariableDataObject data) {
		this.variableDataObjects.add(data);
	}
	
	/**
	 * Adds a participant reference data object to the list of participant
	 * reference data objects in the diagram.
	 *  
	 * @param obj The participant reference data object to add.
	 */
	public void addParticipantReferenceDataObject(
			ParticipantReferenceDataObject obj) {
		this.participantRefDataObjects.add(obj);
	}
	
	/**
	 * Adds a participant set data object to the list of participant
	 * reference data objects in the diagram.
	 *  
	 * @param obj The participant set data object to add.
	 */
	public void addParticipantSetDataObject(ParticipantSetDataObject obj) {
		this.participantSetDataObjects.add(obj);
	}
	
	/**
	 * @return The variable data objects in the diagram.
	 */
	public List<VariableDataObject> getVariableDataObjects() {
		return this.variableDataObjects;
	}
	
	/**
	 * @return The participant reference data objects in the diagram.
	 */
	public List<ParticipantReferenceDataObject> getParticipantRefDataObjects() {
		return this.participantRefDataObjects;
	}
	
	/**
	 * @return The participant set data objects in the diagram.
	 */
	public List<ParticipantSetDataObject> getParticipantSetDataObjects() {
		return this.participantSetDataObjects;
	}

	/**
	 * @return The expression language defined for the diagram.
	 */
	public URI getExpressionLanguage() {
		return this.expressionLanguage;
	}

	/**
	 * @return The grounding file defined for the diagram.
	 */
	public String getGroundingFile() {
		return this.groundingFile;
	}

	/**
	 * @return The id of the diagram.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return The message flows in the diagram.
	 */
	public List<MessageFlow> getMessageFlows() {
		return this.messageFlows;
	}

	/**
	 * @return The name of the diagram.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * The diagram element with the given id.
	 * 
	 * @param id The id of the diagram element 
	 * 
	 * @return The diagram element with the given id. 
	 * Or null if the element does not exist.
	 */
	public Object getObject(String id) {
		return this.objects.get(id);
	}
	
	/**
	 * Adds an element to the diagram objects.
	 * 
	 * @param id     The id of the new element.
	 * @param object The actual object to add.
	 */
	public void putObject(String id, Object object) {
		this.objects.put(id, object);
	}
	
	/**
	 * @return The pools in the diagram.
	 */
	public List<Pool> getPools() {
		return this.pools;
	}

	/**
	 * @return The pool sets in the diagram.
	 */
	public List<PoolSet> getPoolSets() {
		return this.poolSets;
	}

	/**
	 * @return The query language of the diagram.
	 */
	public URI getQueryLanguage() {
		return this.queryLanguage;
	}

	/**
	 * @return The target namespace of the diagram.
	 */
	public String getTargetNamespace() {
		return this.targetNamespace;
	}

	/**
	 * Sets the grounding file of the diagram.
	 * 
	 * @param groundingFile The new grounding file.
	 */
	public void setGroundingFile(String groundingFile) {
		this.groundingFile = groundingFile;
	}

	/**
	 * Sets the id of the diagram.
	 * 
	 * @param id The new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the name of the diagram.
	 * 
	 * @param name The name of the diagram.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the query language of the diagram.
	 * 
	 * @param queryLanguage The new query language.
	 */
	public void setQueryLanguage(URI queryLanguage) {
		this.queryLanguage = queryLanguage;
	}

	/**
	 * Sets the target namespace of the diagram.
	 * 
	 * @param targetNamespace The new target namespace.
	 */
	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	/**
	 * Sets the expression language of the diagram.
	 * 
	 * @param expressionLanguage The new expression language.
	 */
	public void setExpressionLanguage(URI expressionLanguage) {
		this.expressionLanguage = expressionLanguage;
	}
}
