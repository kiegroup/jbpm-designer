package de.hpi.bpel4chor.transformation.factories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.Pool;
import de.hpi.bpel4chor.model.PoolSet;
import de.hpi.bpel4chor.model.Swimlane;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.activities.ServiceTask;
import de.hpi.bpel4chor.model.activities.SendTask;
import de.hpi.bpel4chor.model.artifacts.DataObject;
import de.hpi.bpel4chor.model.artifacts.ParticipantReferenceDataObject;
import de.hpi.bpel4chor.model.artifacts.ParticipantSetDataObject;
import de.hpi.bpel4chor.model.connections.Association;
import de.hpi.bpel4chor.model.connections.MessageFlow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.hpi.bpel4chor.util.ListUtil;
import de.hpi.bpel4chor.util.Output;

/**
 * This factory generates the BPEL4Chor topology with the participant types
 * participant references, participant sets and message links. The factory
 * uses the {@link ParticipantsFactory} to generate the participant references
 * and participant sets.
 * 
 * <p>An instance of this class can only be used for one diagram.</p>
 */
public class TopologyFactory {
	
	private Diagram diagram = null;
	private List<MessageFlow> createdMessageFlows = new ArrayList<MessageFlow>();
	private Output output = null;
	private ParticipantsFactory participantsFact = null;
	
	// swimlane names to check if the types are equal 
	//(every PBD represents one participant type)
	private List<String> createdTypes = new ArrayList<String>();
	
	/**
	 * Constructor. Initializes the topology with the diagram that 
	 * expresses the topology to be generated.
	 * 
	 * @param diagram The diagram to generate the topology for.
	 * @param output  The Output to print errors to.
	 */
	public TopologyFactory(Diagram diagram, Output output) {
		this.diagram = diagram;
		this.output = output;
		this.participantsFact = new ParticipantsFactory(this.diagram, this.output);
	}
	
	/**
	 * Creates a "participantType" element from a swimlane. The namespace
	 * of the swimlane is added to the "topology" element such that it
	 * can be referenced in the topology. The participant behavior
	 * description of the participant type is the process contained
	 * in the swimlane.
	 * 
	 * @param document The document to create the "participantType" element in.
	 * @param swimlane The swimlane to create the "participantType" element for
	 * 
	 * @return The created "participantType" element.
	 */
	private Element createParticipantType(Document document, Swimlane swimlane) {
		Element root = document.getDocumentElement();
		root.setAttribute("xmlns:" + swimlane.getPrefix(), 
				swimlane.getTargetNamespace());
		
		Element participantType = document.createElement("participantType");
		participantType.setAttribute("name", swimlane.getName());
		
		String processName = swimlane.getProcess().getName();
		if (processName == null || processName.equals("")) {
			processName = swimlane.getName();
		}
		participantType.setAttribute("participantBehaviorDescription", 
				swimlane.getPrefix()+ ":" + processName);
		return participantType;
	}
	
	/**
	 * Creates a "participantTypes" element containing a "participantType"
	 * element for each swimlane of the diagram. If there are multiple 
	 * swimlanes with the same name, only one participant type will be 
	 * generated for them.
	 *  
	 * @param document The document to create the "participantTypes" element in
	 * 
	 * @return The created "participantTypes" element.
	 */
	private Element createParticipantTypes(Document document) {
		Element participantTypes = document.createElement("participantTypes");
		
		for (Iterator<Pool> it = this.diagram.getPools().iterator(); it.hasNext();) {
			Pool pool = it.next();
			if (!this.createdTypes.contains(pool.getName())) {
				this.createdTypes.add(pool.getName());
				participantTypes.appendChild(createParticipantType(document, pool));
			}
		}
		
		for (Iterator<PoolSet> it = this.diagram.getPoolSets().iterator(); it.hasNext();) {
			PoolSet poolSet = it.next();
			if (!this.createdTypes.contains(poolSet.getName())) {
				this.createdTypes.add(poolSet.getName());
				participantTypes.appendChild(createParticipantType(document, poolSet));
			}
		}
		return participantTypes;
	}
	
	/**
	 * Determines the participant reference data objects that are associated
	 * with the given activity.
	 * 
	 * @param activity  The activity the associated references will be 
	 *                  determined for
	 * @param direction The direction of the association connecting the activity
	 *                  with the reference.
	 *                  
	 * @return A list with participant reference data objects associated with 
	 * the given activity. If no participant reference data object was found, 
	 * an emtpy list will be returned.
	 */
	private List<ParticipantReferenceDataObject> getAssociatedParticipantReferences(
			Activity activity, String direction) {
		
		List<ParticipantReferenceDataObject> result = 
			new ArrayList<ParticipantReferenceDataObject>();
		List<Association> associations = this.diagram.getAssociationsWithSource(
					activity.getId(), direction, ParticipantReferenceDataObject.class);
		
		for (Iterator<Association> it = associations.iterator(); it.hasNext();) {
			Association assoc = it.next();
			result.add((ParticipantReferenceDataObject)assoc.getTarget());
		}
		
		return result;
	}
	
	/**
	 * Determines the first participant set data object that is associated 
	 * with the given activity
	 * 
	 * @param activity  The activity the associated sets will be determined for
	 * @param direction The direction of the association connecting the activity
	 *                  with the set.
	 *                  
	 * @return The first participant set data object that is associated
	 * with the activity.
	 */
	private ParticipantSetDataObject getAssociatedParticipantSet(
			Activity activity, String direction) {
		
		List<Association> associations = this.diagram.getAssociationsWithSource(
				activity.getId(), direction, ParticipantSetDataObject.class);
		
		if (!associations.isEmpty()) {
			return (ParticipantSetDataObject)associations.get(0).getTarget();
		}
		return null;
	}
	
	/**
	 * Determines all participant set data objects that are associated
	 * with the given activity.
	 * 
	 * @param activity  The activity the associated sets will be determined for
	 * @param direction The direction of the association connecting the activity
	 *                  with the set.
	 *                  
	 * @return A list with participant set data objects associated with 
	 * the given activity. If no participant set data object was found, 
	 * an emtpy list will be returned.
	 */
	private List<ParticipantSetDataObject> getAssociatedParticipantSets(
			Activity activity, String direction) {
		List<ParticipantSetDataObject> result = 
			new ArrayList<ParticipantSetDataObject>();
		List<Association> associations = 
			this.diagram.getAssociationsWithSource(
					activity.getId(), direction, ParticipantSetDataObject.class);
		for (Iterator<Association> it = associations.iterator(); it.hasNext();) {
			Association assoc = it.next();
			result.add((ParticipantSetDataObject)assoc.getTarget());
		}
		return result;
	}
	
	/**
	 * Determines the participant reference and participant set data objects 
	 * that are associated with the given message flow. Only undirected
	 * associations are considered.
	 * 
	 * @param messageFlow  The message flow the associated references and sets
	 *                     will be determined for
	 *                  
	 * @return A list with participant reference and participant set data 
	 * objects associated with the given message flow. If no participant 
	 * reference or participant set data object was found, an emtpy list will
	 * be returned.
	 */
	private List<DataObject> getAssociatedParticipants(MessageFlow messageFlow) {
		List<DataObject> result = new ArrayList<DataObject>();
		List<Association> associations = this.diagram.getAssociationsWithSource(
				messageFlow.getId(), Association.DIRECTION_NONE, DataObject.class);
		
		for (Iterator<Association> it = associations.iterator(); it.hasNext();) {
			Association assoc = it.next();
			DataObject target = (DataObject)assoc.getTarget();
			if ((target instanceof ParticipantReferenceDataObject) ||
					(target instanceof ParticipantSetDataObject)) {
					result.add(target);
			}
		}
		return result;
	}
	
	/**
	 * Determines the name of a participant reference the sender of the message
	 * received by the given activity will be bound to. This is the name of the 
	 * first participant reference data object that has an association from the 
	 * receiving activity.
	 * 
	 * @param receivingActivity The activity that receives the message of the
	 *                          sender.
	 * @return The name of the participant reference the sender will be bound 
	 *         to.
	 */
	private String getBindSenderTo(Activity receivingActivity) {
		// get associations from receiving activity to 
		// participant reference data object
		List<Association> associations = this.diagram.getAssociationsWithSource(
				receivingActivity.getId(), Association.DIRECTION_FROM, 
				ParticipantReferenceDataObject.class);
		if (!associations.isEmpty()) {
			return ((ParticipantReferenceDataObject)associations.get(0)
					.getTarget()).getName();
		}
		return null;
	}
	
	/**
	 * Adds a "sendActivity" attribute to the given element. The 
	 * attribute value is the name of the source activity of the message
	 * flow. If the source activity of the message flow is not a sending 
	 * activity, an error is added to the output and the attribute will
	 * not be added to the element.
	 * 
	 * @param messageFlow The message flow to determine the sending activity
	 *                    for.
	 * @param element     The element to add the "sendActivity" attribute to.
	 */
	private void createSendActivity(MessageFlow messageFlow, Element element) {
		Activity source = messageFlow.getSource();
		if ((source instanceof ServiceTask) || (source instanceof SendTask)) {
			element.setAttribute("sendActivity", source.getName());
		} else {
			this.output.addError("The source object of message flow " +
					"is not a sending activity.", messageFlow.getId());
		}
	}
	
	/**
	 * Adds a "receiveActivity" attribute to the given element. The 
	 * attribute value is the name of the target activity of the message
	 * flow. If the target activity of the message flow is not a receiving 
	 * activity, an error is added to the output and the attribute will
	 * not be added to the element.
	 * 
	 * @param messageFlow The message flow to determine the receiving activity
	 *                    for.
	 * @param element     The element to add the "receiveActivity" attribute to
	 */
	private void createReceiveActivity(MessageFlow messageFlow, Element element) {
		Activity target = messageFlow.getTarget();
		if (target.isReceiving()) {
			element.setAttribute("receiveActivity", target.getName());
		} else {
			this.output.addError("The target object of message flow " +
					"is not a receiving activity.", messageFlow.getId());
		}
	}
	
	/**
	 * Adds a "receiveActivity" attribute to the given element.  The attribute
	 * value is the name of the target activity of each message flow. The name 
	 * has to be equal for each target activity. If they are not equal, an 
	 * error is added to the output. If the target activity of one of the 
	 * message flows is not a receiving activity, an error is added to the 
	 * output and the attribute will not be added to the element.
	 * 
	 * @param messageFlows The message flows to determine the receiving 
	 *                     activity for.
	 * @param element      The element to add the "receiveActivity" attribute 
	 *                     .to
	 */
	private void createReceiveActivity(List<MessageFlow> messageFlows, Element element) {
		String name = null;

		// check if target name is equal for all message flows 
		for (Iterator<MessageFlow> it = messageFlows.iterator(); it.hasNext();) {
			MessageFlow flow = it.next();
			Activity target = flow.getTarget();
			// target must be a receiving activity
			if (!target.isReceiving()) {
				this.output.addError("The target object of message flow " +
						"is not a receiving activity.", flow.getId());
				return;
			}
			
			// name must be equal for all target activities
			if (name == null) {
				name = target.getName();
			} else if (!name.equals(target.getName())) {
				this.output.addError("There are message flows with the same" +
						" source but different target.", flow.getId());
				return;
			}
		}
		
		if (name!= null) {
			element.setAttribute("receiveActivity", name);
		}			
	}
	
	/**
	 * Checks if the sender of the message flow has a valid type. The sender
	 * type is valid if it is equal to the parent swimlane of the source 
	 * activity of the message flow.
	 * 
	 * @param flow   The message flow the sender belongs to.
	 * @param sender The sender whose type will be checked.
	 * 
	 * @return True if the sender type is valid, false otherwise.
	 */
	private boolean validSenderType(MessageFlow flow, DataObject sender) {
		String type = this.participantsFact.getType(sender);
		if ((type != null) && 
				(type.equals(flow.getSource().getParentSwimlane().getName()))) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the sender of all message flows has a valid type. A sender
	 * type is valid if it is equal to the parent swimlane of the source 
	 * activity of the message flow.
	 * 
	 * @param flow    The message flow the senders belong to.
	 * @param senders The senders whoe type will be checked.
	 * 
	 * @return True if the types of all senders are valid, false otherwise.
	 */
	private boolean validSenderType(MessageFlow flow, List<ParticipantReferenceDataObject> senders) {
		String sourceSwimlane = flow.getSource().getParentSwimlane().getName();
		for (Iterator<ParticipantReferenceDataObject> it = 
			senders.iterator(); it.hasNext();) {
			String type = this.participantsFact.getType(it.next());
			if ((type == null) || (!type.equals(sourceSwimlane))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the receiver of the message flow has a valid type. The 
	 * receiver type is valid if it is equal to the parent swimlane of the 
	 * target activity of the message flow.
	 * 
	 * @param flow     The message flow the sender belongs to.
	 * @param receiver The receiver whose type will be checked.
	 * 
	 * @return True if the receiver type is valid, false otherwise.
	 */
	private boolean validReceiverType(MessageFlow flow, DataObject receiver) {
		String type = this.participantsFact.getType(receiver);
		if ((type != null) && 
				(type.equals(flow.getTarget().getParentSwimlane().getName()))) {
			return true;
		}
		return false;
	}
	
	/**
	 * Adds the bindSenderTo attribute to the given element 
	 * (see {@link #getBindSenderTo(Activity)}).  If the reference to bind
	 * the sender to could not be determined, an error is added to the output
	 * and the attribute will not be added to the element. 
	 * 
	 * @param flow    The message flow whose sender will be bound the the 
	 *                reference. 
	 * @param element The element to add the attribute to.
	 */
	private void createBindSenderTo(MessageFlow flow, Element element) {
		String bindSenderTo = getBindSenderTo(flow.getTarget());
		if (bindSenderTo == null) {
			this.output.addError("The reference to bind the sender to could "+
					"not be defined for this message flow ", flow.getId());
		} else {
			element.setAttribute("bindSenderTo", bindSenderTo);
		}
	}
	
	/**
	 * Determines the value of the "senders" attribute of a message flow.
	 * These are the names of all participant references in the given list
	 * separated by a whitespace.
	 * 
	 * @param participants The participants that are the sender of one message
	 *                     flows 
	 * 
	 * @return The "senders" value of a message flow.
	 */
	private String getSendersValue(
			List<ParticipantReferenceDataObject> participants) {
		String senders = "";
		for (Iterator<ParticipantReferenceDataObject> it = 
			participants.iterator(); it.hasNext();) {
			senders += it.next().getName() + " ";
		}
		return senders.trim();
	}
	
	/**
	 * Determines the sending participant(s) of the message flow from
	 * the data objects associated with the target activity. First of all
	 * a participant set is determined that has an association to the receiving 
	 * activity. If such a set does not exist, the participant references are
	 * determined that have an association to or from the receiving activity.
	 * If none of these participants can be found an error is added to the 
	 * output. 
	 * 
	 * <p>If a participant set or multiple participant references were found,
	 * a "senders" attribute is added to the element listing all possible 
	 * sending participant names. If a single participant reference was found,
	 * a "sender" attribute is added to the element naming the sender.</p>
	 * 
	 * @param flow    The message flow to determine the message sender for.
	 * @param element The element to add the "sender" or "senders" attribute 
	 *                to.
	 */
	private void createSenderFromTarget(MessageFlow flow, Element element) {
		Activity target = flow.getTarget();
		
		// check participant sets
		ParticipantSetDataObject participant = 
			getAssociatedParticipantSet(target, Association.DIRECTION_FROM);
		if (participant != null) {
			// check associations from target to participant set data object
			if (validSenderType(flow, participant)) {
				element.setAttribute("senders", participant.getName());
				// actual sender must be bound to participant reference
				createBindSenderTo(flow, element);
			} else {
				this.output.addError("The type of the senders of this message flow " +
						"is not valid.", flow.getId());
			}
		} else {
			// check participant reference data objects 
			List<ParticipantReferenceDataObject> participants = 
				getAssociatedParticipantReferences(target, Association.DIRECTION_TO);
			if (participants.isEmpty()) {
				participants = getAssociatedParticipantReferences(
						target, Association.DIRECTION_FROM);
			}
			
			if (participants.size() == 1) {
				if (validSenderType(flow, participants.get(0))) {
					element.setAttribute("sender", participants.get(0).getName());
				} else {
					this.output.addError("The type of the sender of this message flow " +
							"is not valid.", flow.getId());
				}
			} else if (participants.size() > 1) {
				// add name of each associated participant reference to senders 
				if (validSenderType(flow, participants)) { 
					element.setAttribute("senders", 
							getSendersValue(participants));
					createBindSenderTo(flow, element);
				} else {
					this.output.addError("The type of the senders of this message" +
							" flow is not valid.", flow.getId());
				}
			} else {
				this.output.addError("Sender of this message flow " +
						"could not be determined.", flow.getId());
			}
		}
	}
	
	/**
	 * Determines the sender of a message. If the source activity of the 
	 * message flow is a pool, the sender can be determined from this pool
	 * Otherwise the sender will be determined from the participant data 
	 * objects associated with the target activity of the message flow
	 * (see {@link #createSenderFromTarget(MessageFlow, Element)}).
	 * 
	 * <p>The determined sender will be added as "sender" or "senders"
	 * attribute to the given element.</p>
	 * 
	 * @param messageFlow The message flow to determine the sender for.
	 * @param element     The element to add the "sender" or "senders"
	 *                    attribute to.
	 */
	private void createSender(MessageFlow messageFlow, Element element) {
		Activity source = messageFlow.getSource();
		if (source.getParentSwimlane() instanceof Pool) {
			// determine sender from pool
			Pool pool = (Pool)source.getParentSwimlane();
			element.setAttribute("sender", pool.getParticipantName());
		} else {
			createSenderFromTarget(messageFlow, element);
		}
	}
	
	/**
	 * Creates the sender for message flows that have the same source activity.
	 * To generate the sender for multiple message flows all participant 
	 * references and participant sets associated with the target 
	 * activity of the message flow need to be determined. All target
	 * activities must be associated with the same references and sets
	 * using associations with the same direction.
	 * 
	 * <p>If this precondition is fulfilled the sender can be created as for
	 * one message flow.</p>
	 * 
	 * @param flows   The message flows that have the same sender.
	 * @param element The element to add the "sender" or "senders" element to.
	 */
	private void createSenderWithSameSource(List<MessageFlow> flows, Element element) {
		// check if all receiving activities are associated with the
		// same participant reference and set data objects of the same name 
		// in the same direction
		List<ParticipantReferenceDataObject> referencesTo = null;
		List<ParticipantReferenceDataObject> referencesFrom = null; 
		List<ParticipantSetDataObject> setsFrom = null;
		boolean valid = true;
		for (Iterator<MessageFlow> it = flows.iterator(); it.hasNext();) {
			MessageFlow flow = it.next();
			Activity target = flow.getTarget();
			
			// get associated participant references
			List<ParticipantReferenceDataObject> refsTo = 
				getAssociatedParticipantReferences(target, Association.DIRECTION_TO);
			if (referencesTo == null) {
				referencesTo = refsTo;
			} else {
				if (!isEqual(referencesTo, refsTo, false)) {
					valid = false;
					break;
				}
			}
			
			List<ParticipantReferenceDataObject> refsFrom = 
				getAssociatedParticipantReferences(target, Association.DIRECTION_FROM);
			if (referencesFrom == null) {
				referencesFrom = refsFrom;
			} else {
				if (!isEqual(referencesFrom, refsFrom, false)) {
					valid = false;
					break;
				}
			}
			
			List<ParticipantSetDataObject> sFrom =
				getAssociatedParticipantSets(target, Association.DIRECTION_FROM);
			if (setsFrom == null) {
				setsFrom = sFrom;
			} else {
				if (!isEqual(setsFrom, sFrom, false)) {
					valid = false;
					break;
				}
			}
		}
		if (valid) {
			createSender(flows.get(0), element);
		} else {
			this.output.addError("The target activities of the message flows " + 
					ListUtil.toString(flows) + 
					" are not associated with the same participants.", flows.get(0).getId());
		}
	}
	
	/**
	 * Creates the sender for the message flows that have the same target
	 * activity. For this purpose it has to be checked if each source is
	 * contained in the same swimlane, because each sender must be of the 
	 * same participant type.
	 * 
	 * <p>If this precondition is fulfilled the sender can be created as for
	 * one message flow.</p>
	 * 
	 * @param flows   The message flows that have the same sender.
	 * @param element The element to add the "sender" or "senders" element to.
	 */
	private void createSenderWithSameTarget(
			List<MessageFlow> flows, Element element) {
		// check if sources are located in the same swimlane 
		// because each sender must be of the same participant type
		Swimlane swimlane = null;
		for (Iterator<MessageFlow> it = flows.iterator(); it.hasNext();) {
			MessageFlow flow = it.next();
			if (swimlane == null) {
				swimlane = flow.getSource().getParentSwimlane();
			} else if (!flow.getSource().getParentSwimlane().equals(swimlane)) {
				this.output.addError("The sources of the message flows " + 
						ListUtil.toString(flows) + 
						" must be located in the same swimlane.", flows.get(0).getId());
			}
		}
		if (!flows.isEmpty()) {
			createSender(flows.get(0), element);
		}
	}
	
	/**
	 * Creates the receiver for the given message flow. If the target of the 
	 * message flow is located in a pool, the receiver is represented by this
	 * pool. Otherwise the receiver is determined from the participant 
	 * reference data object that have an associaton to the source activity of
	 * the message flow. If the receiver was determined an appropriate 
	 * "receiver" attribute is added to the given element.
	 * 
	 * <p>If the target activity is not located in a pool and there are no
	 * participant reference data objects associated with the source activity,
	 * an error is added to the output. If the receiver type is not valid
	 * (see {@link #validReceiverType(MessageFlow, DataObject)}) an error is
	 * added to the output, too.
	 * 
	 * @param messageFlow The message flow to determine the receiver for.
	 * @param element     The element to add the "receiver" to.
	 */
	private void createReceiver(MessageFlow messageFlow, Element element) {
		Activity source = messageFlow.getSource();
		Activity target = messageFlow.getTarget();
		
		if (target.getParentSwimlane() instanceof Pool) {
			Pool pool = (Pool)target.getParentSwimlane();
			element.setAttribute("receiver", pool.getParticipantName());
		} else {
			List<ParticipantReferenceDataObject> participants = 
				getAssociatedParticipantReferences(
						source, Association.DIRECTION_TO);
			if (participants.size() == 0) {
				this.output.addError("Receiver for this message flow " +
						"could not be determined.", messageFlow.getId());
			} else {
				ParticipantReferenceDataObject participant = 
					participants.get(0);
				if (validReceiverType(messageFlow, participant)) {
					element.setAttribute("receiver", participant.getName());
				} else {
					this.output.addError(
							"The type of the receiver of message flow " +
							"is not valid.", messageFlow.getId());
				}
			}
		}
	}
	
	/**
	 * Creates the receiver for the message flows that have the same target
	 * activity. Of the target activity of the message flows is located in a
	 * pool, the receiver is represented by this pool. Otherwise the receiver
	 * is determined from the participant reference data object that has an
	 * associaton to the source activity of the message flow. This participant
	 * reference data object has to be associated with the source activities of
	 * all message flows. Otherwise an error will be added to the output. If 
	 * the receiver was determined an appropriate "receiver" attribute is added
	 * to the given element.
	 * 
	 * <p>If there are none or multiple participant references associated 
	 * with the source activity, an error is added to the output.</p>
	 * 
	 * @param flows   The message flows that have the same target activity.
	 * @param element The element to add the "receiver" element to.
	 */
	private void createReceiverForSameTarget(List<MessageFlow> flows, Element element) {
		String ref = null;
		for (Iterator<MessageFlow> it = flows.iterator(); it.hasNext();) {
			MessageFlow messageFlow = it.next();
			Activity target = messageFlow.getTarget();
			
			if (target.getParentSwimlane() instanceof Pool) {
				Pool pool = (Pool)target.getParentSwimlane();
				ref = pool.getParticipantName();
				break;
			} else {
				// data object representing the receiver must be associated with
				// all source activities of the message flows 
				Activity source = messageFlow.getSource();
				List<ParticipantReferenceDataObject> participants = 
					getAssociatedParticipantReferences(
							source, Association.DIRECTION_TO);
				if (participants.size() == 0) {
					this.output.addError("Receiver for this message flow " + 
							 "could not be determined", messageFlow.getId());
				} else if (participants.size() > 1) {
					this.output.addError("There are multiple " +
							"receivers defined", messageFlow.getId()); 
				} else {
					if (ref == null) {
						ref = participants.get(0).getName();
					} else if (!ref.equals(participants.get(0).getName())) {
						this.output.addError("The receiver of this message flow " +
								"must be equal to "+ ref, messageFlow.getId());
					}
				}
			}
		}
		
		if (ref != null) {
			element.setAttribute("receiver", ref);
		} else {
			if (flows.isEmpty()) {
				this.output.addGeneralError("The Receiver of a message link could not be determined.");
			} else {
				this.output.addError("The Receiver of a message link could not be determined.", flows.get(0).getId());
			}
		}
	}
	
	/**
	 * Determins the participipant references and participant sets that are
	 * passed over the message flow. These are participant reference and
	 * participant set data objects associated with the message flow by a
	 * non-directed association. Either all or none of these participants
	 * must define a copyTo attribute. If this condition is not fulfilled, 
	 * an error is added to the output.
	 * 
	 * If the passed participants and the copyTo attribute was determined
	 * an appropriate "participantRefs" and a "copyParticipantRefsTo" attribute
	 * will be added to the given element.
	 * 
	 * @param messageFlow The message flow to determine the passed participants
	 *                    for
	 * @param element     The element to add the "participantRefs" and the 
	 *                    "copyParticipantRefsTo" attribute to.
	 */
	private void createPassedRefsAndSets(MessageFlow messageFlow, Element element) {
		List<DataObject> passedLinks = getAssociatedParticipants(messageFlow);
		String participantRefs = "";
		String copyParticipantRefsTo = "";
		
		boolean copy = false;
		for (Iterator<DataObject> it = passedLinks.iterator(); it.hasNext();) {
			DataObject object = it.next();
			participantRefs += object.getName() + " ";
			// either all or none should be copied
			String copyTo = null;
			if (object instanceof ParticipantReferenceDataObject) {
				ParticipantReferenceDataObject ref = 
					(ParticipantReferenceDataObject)object;
				copyTo = ref.getCopyTo();		
			} else if (object instanceof ParticipantSetDataObject) {
				ParticipantSetDataObject ref = 
					(ParticipantSetDataObject)object;
				copyTo = ref.getCopyTo();
			}
			
			if (copyTo == null) {
				if (copy) {
					this.output.addError("Either all or none of the " +
							"passed participant references and sets for this message flow "+ 
							"must define the CopyTo attribute.", messageFlow.getId());
					return;
				}
			} else {
				copyParticipantRefsTo += copyTo + " ";
				copy = true;
			}			
		}
		if (!participantRefs.equals("")) {
			element.setAttribute("participantRefs", participantRefs.trim());
			
			if (copy && !copyParticipantRefsTo.equals("")) {
				element.setAttribute("copyParticipantRefsTo", 
						copyParticipantRefsTo.trim());
			}
		}
	}

	/**
	 * Compares two lists with participant reference and participant set data objects.
	 * The lists are equal of they have the same size and if all participants
	 * of one list are contained in the other list. For this purpose the type,
	 * name of the participants are compared.
	 * 
	 * <p>If there are objects contained in the lists that are no data objects,
	 * they will be omitted.</p>
	 * 
	 * @param list1   The first list with participant data objects
	 * @param list2   The second list with participant data objects
	 * @param copyTo  True, if the copyTo attribute should be compared too.
	 * 
	 * @return True, if both lists contain the same participants, false otherwise.
	 */
	private boolean isEqual(List list1, List list2, boolean copyTo) {
		if (list1.size() != list2.size()) {
			return false;
		}
		
		for (Iterator itList1 = list1.iterator(); itList1.hasNext();) {
			Object obj = itList1.next();
			if (!(obj instanceof DataObject)) {
				continue;
			}
			DataObject obj1 = (DataObject)obj;
			
			// check if data object with this type, name and copyTo is contained in list2
			boolean found = false;
			for (Iterator itList2 = list2.iterator(); itList2.hasNext();) {
				obj = itList2.next();
				if (!(obj instanceof DataObject)) {
					continue;
				}
				DataObject obj2 = (DataObject)obj;
				if (obj1.getClass().equals(obj2.getClass()) && 
						obj1.getName().equals(obj2.getName())) {
					
					found = true;
					if (copyTo) {
						if (obj1 instanceof ParticipantReferenceDataObject) {
							ParticipantReferenceDataObject ref1 = 
								(ParticipantReferenceDataObject)obj1;
							ParticipantReferenceDataObject ref2 = 
								(ParticipantReferenceDataObject)obj2;
							if (!ref1.getCopyTo().equals(ref2)) {
								return false;
							}
						} else if (obj1 instanceof ParticipantSetDataObject) {
							ParticipantSetDataObject ref1 = 
								(ParticipantSetDataObject)obj1;
							ParticipantSetDataObject ref2 = 
								(ParticipantSetDataObject)obj2;
							if (!ref1.getCopyTo().equals(ref2)) {
								return false;
							}
						}
					}
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Determins the participipant references and participant sets that are
	 * passed over the message flows. These are participant reference and
	 * participant set data objects associated with the message flows by a
	 * non-directed association. For this purpose it needs to be checked that
	 * all message flows are associated with the same participant reference and
	 * participant set data objects. If this is not fulfilled, an error is 
	 * added to the output. Otherwise the passed participants can be determined
	 * as for one message flow.
	 * 
	 * <p>If the passed participants and the copyTo attribute was determined
	 * an appropriate "participantRefs" and a "copyParticipantRefsTo" attribute
	 * will be added to the given element.</p>
	 * 
	 * @param flows   The message flows to determine the passed 
	 *                participants for.
	 * @param element The element to add the "participantRefs" and the 
	 *                "copyParticipantRefsTo" attribute to.
	 */
	private void createPassedRefsAndSets(List<MessageFlow> flows, Element element) {
		// check if all flows are associated with data objects of the same type 
		// and with the same name and copyTo attribute
		List<DataObject> dataObjects = null;
		for (Iterator<MessageFlow> it = flows.iterator(); it.hasNext();) {
			MessageFlow flow = it.next();
			List<DataObject> passed = getAssociatedParticipants(flow);

			if (dataObjects == null) {
				dataObjects = passed;
			} else if (!isEqual(dataObjects, passed, true)) {
				this.output.addError("The message flows " +
					ListUtil.toString(flows) + " are not associated with the"+
							" same participants or the participants do not" +
							" specify the same CopyTo value.", flows.get(0).getId());
					return;
			}
		}
	
		// Then take the first message flow and generate the passed references and sets
		createPassedRefsAndSets(flows.get(0), element);
	}
	
	/**
	 * Determines the sending activities of the given message flows. If they 
	 * were determined a "sendActivity" attribute will be added to the element.
	 * 
	 * <p>Multiple sending activity names will be separated with a whitespace.
	 * If there are sending activities with the same name they will onyl occur
	 * once in the resulting attribute. If one of the message flows has a 
	 * source object that is not a sending activity, an error will be added to
	 * the output.</p> 
	 * 
	 * @param flows
	 * @param element
	 */
	private void createSendActivities(List<MessageFlow> flows, Element element) {
		String sendActivities = "";
		
		// if send activities have the same name than create sendingActivity with one name
		// otherwise concat the names
		List<String> names = new ArrayList<String>();
		for (Iterator<MessageFlow> it = flows.iterator(); it.hasNext();) {
			MessageFlow flow = it.next();
			Activity source = flow.getSource();
			if ((source instanceof ServiceTask) || (source instanceof SendTask)) {
				if (!names.contains(source.getName())) {
					sendActivities += source.getName() + " ";
					names.add(source.getName());
				}
			} else {
				this.output.addError("The source object of this message flow " + 
						"is not a sending activity.", flow.getId());
			}
		}
		element.setAttribute("sendActivity", sendActivities.trim());
	}
	
	/**
	 * Determines the name of the message link that will be created from the 
	 * given message flows. If more than one message flow defines a name, these
	 * names have to be equal.
	 * 
	 * @param flows The message flows to determine the name from
	 * 
	 * @return The name of the message link or null, if none of the message
	 * flows defines a name.
	 */
	private String getNameAttribute(List<MessageFlow> flows) {
		String name = null;
		for (Iterator<MessageFlow> it = flows.iterator(); it.hasNext();) {
			MessageFlow flow = it.next();
			if (name == null) {
				name = flow.getName();
			} else {
				if ((flow.getName() != null) && 
						(!flow.getName().equals(name))) {
					this.output.addError("This message flow " +
							"must define no name or the name "+ name, flow.getId());
					return null;
				}
			}
		}
		return name;
	}
	
	/**
     * Determines the message name of the message link that will be created
     * from the given message flows. Each message flow has to define a 
     * message name. So if a message name is missing for a message flow an
     * error is added to the output. Moreover, the message name must be 
     * equal for each message flow. If this is not fulfilled an error
     * is added to the output, too.
	 * 
	 * @param flows The flows to determine the message name from
	 * @return The determined message name or null if an error occured.
	 */
	private String getMessageNameAttribute(List<MessageFlow> flows) {
		// message name defined for flows must be equal
		// no missing message name allowed
		String messageName = null;
		for (Iterator<MessageFlow> it = flows.iterator(); it.hasNext();) {
			MessageFlow flow = it.next();
			if (flow.getMessageName() == null) {
				this.output.addError("This message flow " +
						"must define a message name", flow.getId());
				return null;
			} else if (messageName == null) {
				messageName = flow.getMessageName();
			} else {
				if (!flow.getMessageName().equals(messageName)) {
					this.output.addError("The message flows " + 
							ListUtil.toString(flows) + 
							" must define the same message name.", flows.get(0).toString());
					return null;
				}
			}
		}
		return messageName;
	}
	
	/**
	 * Creates the common message link attributes "name" and "messageName" 
	 * attribute for the given element.
	 * 
	 * @param flows   The flow to determine the attribute values from.
	 * @param element The element to add the attributes to.
	 */
	private void createCommonMessageLinkAttr(List<MessageFlow> flows, Element element) {
		String name = getNameAttribute(flows);
		if (name != null) {
			element.setAttribute("name", name);
		}
		
		String messageName = getMessageNameAttribute(flows);
		if (messageName != null) {
			element.setAttribute("messageName", messageName);
		}
	}
	
	/**
	 * Creates a "messageLink" element for the given message flows. These 
	 * flows should have the same target activity.
	 * 
	 * @param document The document to create the "messageLink" element in.
	 * @param flows    The message flows to create the message link from.
	 * @return The created "messageLink" element.
	 */
	private Element createLinkWithSameTarget(
			Document document, List<MessageFlow> flows) {
		Element element = document.createElement("messageLink");
		
		createCommonMessageLinkAttr(flows, element);
		
		createSendActivities(flows, element);
		createReceiveActivity(flows.get(0), element);
		
		createSenderWithSameTarget(flows, element);
		createReceiverForSameTarget(flows, element);
		createPassedRefsAndSets(flows, element);
		
		this.createdMessageFlows.addAll(flows);
		return element;
	}
	
	/**
	 * Creates a "messageLink" element for the given message flows. These 
	 * flows should have the same source activity.
	 * 
	 * @param document The document to create the "messageLink" element in.
	 * @param flows    The message flows to create the message link from.
	 * 
	 * @return The created "messageLink" element.
	 */
	private Element createLinkWithSameSource(
			Document document, List<MessageFlow> flows) {
		Element element = document.createElement("messageLink");
		
		createCommonMessageLinkAttr(flows, element);
		
		createSendActivity(flows.get(0), element);
		createReceiveActivity(flows, element);
		
		createSenderWithSameSource(flows, element);
		createReceiver(flows.get(0), element);
		createPassedRefsAndSets(flows, element);
		
		this.createdMessageFlows.addAll(flows);
		return element;
	}
	
	/**
	 * Creates a "messageLink" element from a single message flow.
	 * If the message flow does not define a message name, an error is added
	 * to the output.
	 * 
	 * @param document The document to create the "messageLink" element in.
	 * @param flow     The message flow to create the "messageLink" from
	 * 
	 * @return The created "messageLink" element.
	 */
	private Element createMessageLinkForSingle(Document document, MessageFlow flow) {
		Element element = document.createElement("messageLink");
		
		// create name attribute
		String name = flow.getName();
		if (name != null) {
			element.setAttribute("name", name);
		}
		
		if (flow.getMessageName() == null) {
			this.output.addError("This message flow " +
					"does not specify a message name.", flow.getId());
		} else {
			element.setAttribute("messageName", flow.getMessageName());
		}
		
		createSendActivity(flow, element);
		createSender(flow, element);
		createReceiveActivity(flow, element);
		createReceiver(flow, element);
		createPassedRefsAndSets(flow, element);
		
		this.createdMessageFlows.add(flow);
		return element;
	}
	
	/**
	 * Creates the a "messageLink" element for the given message flow.
	 * It is determined if there are other message flows with the same
	 * source and the same target. The "messageLink" element will be
	 * created for all these message flows.
	 * 
	 * @param document    The document to create the "messageLink" element in.
	 * @param messageFlow One of the message flows to create the "messageLink" 
	 *                    element from.
	 *                    
	 * @return The created "messageLink" element.
	 */
	private Element createMessageLink(Document document, MessageFlow messageFlow) {
		
		// collect message flows with the same source or the same target
		// check that not both
		List<MessageFlow> sameTarget = 
			this.diagram.getMessageFlowsWithTarget(messageFlow.getTarget().getId());
		
		if (sameTarget.size() > 1) {
			return createLinkWithSameTarget(document, sameTarget);
		} 
		
		List<MessageFlow> sameSource = 
			this.diagram.getMessageFlowsWithSource(messageFlow.getSource().getId());
		if (sameSource.size() > 1) {
			return createLinkWithSameSource(document, sameSource);
		}
		
		return createMessageLinkForSingle(document, messageFlow);
	}
	
	/**
	 * Transforms all the message flows contained in the diagram to "messageLink"
	 * elements contained in a "messageLinks" element.
	 * 
	 * @param document The document to create the "messageLinks" element in.
	 * 
	 * @return The created "messageLinks" element.	
	 */
	private Element transformMessageLinks(Document document) {
		if (this.diagram.getMessageFlows().isEmpty()) {
			this.output.addError(
					"There are no message flows in the choreography", this.diagram.getId());
		}
		
		Element messageLinks = document.createElement("messageLinks");
		for (Iterator<MessageFlow> it = 
			this.diagram.getMessageFlows().iterator(); it.hasNext();) {
			MessageFlow flow = it.next();
			if (!this.createdMessageFlows.contains(flow)) {
				Element messageLink = 
					createMessageLink(document, flow);
				if (messageLink != null) {
					messageLinks.appendChild(messageLink);
				}
			}
		}
		return messageLinks;
	}
	
	/**
	 * Generates the BPEL4Chor topology from the swimlanes and
	 * participant data objects contained in the diagram. For this purpose
	 * the participant types, participants and message links are generated.
	 * 
	 * <p>For generating the participants the {@link ParticipantsFactory} is
	 * used.</p>
	 *  
	 * @return The document that contains the generated BPEL4Chor topology.
	 */
	public Document transformTopology() {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			Document topology = builder.newDocument();
			
			Element root = topology.createElement("topology");
			root.setAttribute("name", this.diagram.getName());
			root.setAttribute("targetNamespace", this.diagram.getTargetNamespace());
			root.setAttribute("xmlns", 
					"urn:HPI_IAAS:choreography:schemas:choreography:topology:2006/12");
			root.setAttribute("xmlns:xsi", 
					"http://www.w3.org/2001/XMLSchema-instance");
			root.setAttribute("xsi:schemaLocation", 
					"urn:HPI_IAAS:choreography:schemas:choreography:" +
					"topology:2006/12 http://www.iaas.uni-stuttgart.de/" +
					"schemas/bpel4chor/topology.xsd");
			
			topology.appendChild(root);
			
			Element participantTypes = createParticipantTypes(topology);
			root.appendChild(participantTypes);
			
			Element participants = 
				this.participantsFact.transformParticipants(topology);
			root.appendChild(participants);
			
			Element messageLinks = transformMessageLinks(topology);
			root.appendChild(messageLinks);

			return topology;
		} catch (ParserConfigurationException e) {
			this.output.addError(e);
		}
		return null;
	}
}
