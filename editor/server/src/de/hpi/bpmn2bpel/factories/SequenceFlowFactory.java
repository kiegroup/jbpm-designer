package de.hpi.bpmn2bpel.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.hpi.bpmn.Process;
import de.hpi.bpel4chor.model.SubProcess;
import de.hpi.bpmn.Activity;
import de.hpi.bpel4chor.model.activities.BlockActivity;
import de.hpi.bpmn2bpel.model.BPELDataObject;
import de.hpi.bpmn2bpel.model.FoldedTask;
import de.hpi.bpel4chor.model.activities.Gateway;
import de.hpi.bpel4chor.model.activities.Handler;
import de.hpi.bpel4chor.model.activities.IntermediateEvent;
import de.hpi.bpel4chor.model.activities.ReceiveTask;
import de.hpi.bpmn.Task;
import de.hpi.bpel4chor.model.connections.Transition;
import de.hpi.bpel4chor.model.supporting.Expression;
import de.hpi.bpel4chor.util.Output;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.DataObject;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.StartMessageEvent;
import de.hpi.bpmn2bpel.model.Container4BPEL;

/**
 * <p>
 * This factory transforms the sequence flow contained in processes or
 * sub-processes. This transformation is based on the Ouyang et al.
 * transformation. This transformation was extended with a support for multiple
 * start and end events and with a support of inclusive gateways.
 * </p>
 * 
 * <p>
 * The factory uses the {@link Componentizer} to determine patterns in the
 * sequence flow. These patterns are mapped to its BPEL representation and
 * folded to a single task. This task replaces the pattern in the sequence flow
 * and represents the mapped BPEL code.
 * </p>
 * 
 * <p>
 * An instance of this class can only be used for one container (process,
 * sub-process) in a certain diagram.
 * </p>
 */
public class SequenceFlowFactory {

	private Container4BPEL container = null;
	private boolean errorHandler = false;
	private boolean messageHandler = false;
	private BPMNDiagram diagram = null;
	private Document document = null;
	private BasicActivityFactory basicFactory = null;
	private SupportingFactory supportingFactory = null;
	private StructuredElementsFactory structuredFactory = null;
	private Componentizer componentizer = null;
	private Output output = null;

	/**
	 * Constructor. Initializes the sequence flow factory with the diagram and
	 * the container that contains the sequence flow to be transformed.
	 * 
	 * @param diagram
	 *            The diagram the container belongs to.
	 * @param document
	 *            The document to create the BPEL elements for.
	 * @param container
	 *            The container that contains the sequence flow that will be
	 *            transformed.
	 * @param output
	 *            The {@link Output} to print errors to.
	 */
	public SequenceFlowFactory(BPMNDiagram diagram, Document document,
			Container4BPEL container, Output output) {
		this.diagram = diagram;
		this.container = container;
		this.document = document;
		this.output = output;
		this.basicFactory = new BasicActivityFactory(diagram, document,
				this.output);
		this.supportingFactory = new SupportingFactory(diagram, document,
				this.output);
		this.structuredFactory = new StructuredElementsFactory(diagram,
				document, this.output);
		this.componentizer = new Componentizer(diagram, container, this.output);

		// // determine if container is error or message handler
		// if (container instanceof SubProcess) {
		// SubProcess sub = (SubProcess)container;
		// if (sub.getBlockActivity() instanceof Handler) {
		// Handler handler = (Handler)sub.getBlockActivity();
		// if (handler.getHandlerType().equals(Handler.TYPE_FAULT)) {
		// this.errorHandler = true;
		// this.messageHandler = false;
		// } else if (handler.getHandlerType().equals(Handler.TYPE_MESSAGE)){
		// this.messageHandler = true;
		// this.errorHandler = false;
		// } else {
		// this.messageHandler = false;
		// this.errorHandler = false;
		// }
		// }
		// }
	}
	
	/**
	 * Constructor. Initializes the sequence flow factory with the diagram and
	 * the container that contains the sequence flow to be transformed. Also 
	 * passes BPEL process element because of namespace prefix issues.
	 * 
	 * @param diagram
	 *            The diagram the container belongs to.
	 * @param document
	 *            The document to create the BPEL elements for.
	 * @param container
	 *            The container that contains the sequence flow that will be
	 *            transformed.
	 * @param output
	 *            The {@link Output} to print errors to.
	 */
	public SequenceFlowFactory(BPMNDiagram diagram, Document document,
			Container4BPEL container, Output output, Element processElement) {
		this.diagram = diagram;
		this.container = container;
		this.document = document;
		this.output = output;
		this.basicFactory = new BasicActivityFactory(diagram, document,
				this.output, processElement);
		this.supportingFactory = new SupportingFactory(diagram, document,
				this.output);
		this.structuredFactory = new StructuredElementsFactory(diagram,
				document, this.output);
		this.componentizer = new Componentizer(diagram, container, this.output);

		// // determine if container is error or message handler
		// if (container instanceof SubProcess) {
		// SubProcess sub = (SubProcess)container;
		// if (sub.getBlockActivity() instanceof Handler) {
		// Handler handler = (Handler)sub.getBlockActivity();
		// if (handler.getHandlerType().equals(Handler.TYPE_FAULT)) {
		// this.errorHandler = true;
		// this.messageHandler = false;
		// } else if (handler.getHandlerType().equals(Handler.TYPE_MESSAGE)){
		// this.messageHandler = true;
		// this.errorHandler = false;
		// } else {
		// this.messageHandler = false;
		// this.errorHandler = false;
		// }
		// }
		// }
	}

	/**
	 * <p>
	 * Transforms the given start events to intermediate events. The start
	 * events will be removed from the container and the intermediate events
	 * will be added.
	 * </p>
	 * 
	 * <p>
	 * Start events of trigger type none will not be transformed, because
	 * intermediate events with trigger type none are not allowed. The id, name
	 * and trigger of the start events is assigned to the intermediate events.
	 * The transitions emanating from the start event are changed to transitions
	 * that emanate from the intermediate events. If the start event is
	 * contained in a process and the createInstance attribute is true, the
	 * createInstance attribute of the intermediate event will be set to true,
	 * too.
	 * </p>
	 * 
	 * @param toTransform
	 *            The start events to be transformed to intermediate events.
	 * @param createInstance
	 *            True, if the start events are allowed to instantiate a
	 *            process.
	 * 
	 * @return A list with the transformed intermediate events.
	 */
	private List<IntermediateEvent> transfromToIntermiateEvent(
			List<StartEvent> toTransform, boolean createInstance) {
		// List<IntermediateEvent> result = new ArrayList<IntermediateEvent>();
		//		
		// for (Iterator<StartEvent> it = toTransform.iterator(); it.hasNext();)
		// {
		// StartEvent event = it.next();
		//			
		// if (event.getTriggerType().equals(StartEvent.TRIGGER_NONE)) {
		// continue;
		// }
		//			
		// List<Transition> toKeep = event.getSourceFor();
		//			
		// IntermediateEvent newEvent =
		// new IntermediateEvent(event.getId(), event.getName(),
		// event.getTriggerType(), event.getTrigger(), this.output);
		// if ((this.container instanceof Process) && (createInstance)) {
		// newEvent.setCreateInstance(true);
		// }
		//			
		// this.container.removeActivity(event);
		// this.container.addActivity(newEvent);
		//			
		// // keep transition from event to next activity
		// for (Iterator<Transition> itTrans = toKeep.iterator();
		// itTrans.hasNext();) {
		// Transition transition = itTrans.next();
		// transition.getTarget().addTargetFor(transition, this.output);
		// transition.setSource(newEvent, this.output);
		// newEvent.addSourceFor(transition, this.output);
		// }
		// this.container.addTransitions(toKeep);
		// result.add(newEvent);
		// }
		//		
		// return result;
		return null;
	}

	/**
	 * <p>
	 * Determines the gateways on the paths from the start activity to an end
	 * event in the sequence flow using depth-first search. If there is a path
	 * that does not reach an end event or an already visited activity false
	 * will be returned. If an end event is reached or a cycle was detected, the
	 * result will contain all gateways found except the end activity. If a
	 * cycle was detected, then the result contains the current list of gateways
	 * and true is returned.
	 * </p>
	 * 
	 * <p>
	 * The order of the returned gateways is determined by the depth of the
	 * gatway's position in the sequence flow.
	 * </p>
	 * 
	 * @param start
	 *            The activity to start the with
	 * @param visited
	 *            The already visited activities.
	 * @param result
	 *            This should be an empty list when the method is called from
	 *            the outside. After the call the list will contain the found
	 *            gateways on the paths from start activity to an end event.
	 * 
	 * @return True, if an end event was reached or a cycle was detected. False,
	 *         otherwise.
	 */
	private boolean getGatewaysTo(Activity start, List<Activity> visited,
			final List<Gateway> result) {
		// if (visited == null) {
		// visited = new ArrayList<Activity>();
		// }
		// if (start == null) {
		// return false;
		// }
		// if (visited.contains(start)) {
		// return true;
		// }
		//		
		// visited.add(start);
		// if (start instanceof EndEvent) {
		// return true;
		// }
		//		
		// List<Activity> successors = start.getSuccessors();
		// if (successors == null || successors.isEmpty()) {
		// return false;
		// }
		//		
		// if (start instanceof Gateway) {
		// // check if reachable from all paths and collect gateways
		// boolean reached = true;
		// for (Iterator<Activity> it = successors.iterator(); it.hasNext();) {
		// boolean endReached = getGatewaysTo(it.next(), visited, result);
		// if (!endReached) {
		// reached = false;
		// }
		// }
		// // add gateway if reachable
		// if (reached) {
		// result.add(0, (Gateway)start);
		// return true;
		// }
		// } else {
		// return getGatewaysTo(successors.get(0), visited, result);
		// }

		return false;
	}

	/**
	 * Determines a map that contains the reachable gateways for each start
	 * event. The order of the gateways is determined by the depth of the
	 * gatway's position in the sequence flow. If a start event does not lead to
	 * an end event an error is added to the output and the result will be null.
	 * 
	 * @param startEvents
	 *            The start events to determine the reachable gateways for.
	 * 
	 * @return The map with the reachable gateways for each start event.
	 */
	private Map<StartEvent, List<Gateway>> getStartGatewayMap(
			List<StartEvent> startEvents) {
		// Map<StartEvent, List<Gateway>> startGatewayMap =
		// new HashMap<StartEvent, List<Gateway>>();
		//		
		// for (Iterator<StartEvent> it = startEvents.iterator(); it.hasNext();)
		// {
		// StartEvent start = it.next();
		// List<Gateway> gateways = new ArrayList<Gateway>();
		// boolean reached = getGatewaysTo(start, null, gateways);
		// if (reached) {
		// startGatewayMap.put(start, gateways);
		// } else {
		// this.output.addError("The start event " +
		// "does not lead to an end event", start.getId());
		// return null;
		// }
		// }
		// return startGatewayMap;
		return null;
	}

	// /**
	// * <p>Creates a new gateway to combine the given start events. The
	// * gateway type parameter determines the type of the created gateway.
	// * The created gateway is added to the container.</p>
	// *
	// * <p>Inclusive gateways are not allowed for the combination. Thus, if the
	// * gateway type is {@link Gateway#TYPE_OR} an error is added to the output
	// * and the result will be null.</p>
	// *
	// * @param startEvents Start events to be combined.
	// * @param gatewayType The gateway type of the gateway to create.
	// *
	// * @return The created gateway of null if an error occurred.
	// */
	// private Gateway createCombinatingGateway(List<StartEvent> startEvents,
	// String gatewayType) {
	//		
	// // createInstance for exclusive gateway allowed if it is at the start of
	// the process
	// boolean createInstance = this.container instanceof Process;
	//		
	// // create gateway
	// Gateway newGateway = null;
	// if (gatewayType.equals(Gateway.TYPE_OR)) {
	// this.output.addError("The start events " +
	// ListUtil.toString(startEvents) + "could not be combined: "+
	// "no inclusive gateways allowed for combining start events",
	// startEvents.get(0).getId());
	// return null;
	// } else if (gatewayType.equals(Gateway.TYPE_AND)) {
	// newGateway = new Gateway(Gateway.TYPE_AND, null, true, this.output);
	// } else if (gatewayType.equals(Gateway.TYPE_XOR)) {
	// newGateway = new Gateway(Gateway.TYPE_XOR, Gateway.SPLIT_XOREVENT,
	// createInstance, true, this.output);
	// } else {
	// this.output.addError("The start events " +
	// ListUtil.toString(startEvents) +
	// "could not be combined: unknown gateway type.",
	// startEvents.get(0).getId());
	// return null;
	// }
	// this.container.addActivity(newGateway);
	// return newGateway;
	// }

	// /**
	// * <p>Combines the given start events with a gateway of the given type.
	// * For this purpose a new Gateway with the given type will be created
	// * (see {@link #createCombinatingGateway(List, String)}).</p>
	// *
	// * <p>The given start events will be transformed into intermediate events
	// * and a new non-triggered start event will be created as new starting
	// * point for the sequence flow. Moreover, transitions from the
	// * new start event to the new gateway and from the gateway to the
	// * intermediate events will be created.</p>
	// *
	// * <p>As a result of previous combinations non-triggered start
	// * events may be in the list of the start events to be combined. These
	// * start events will not be transformed to intermediate events. They will
	// * be removed and a transition from the combining gateway to the successor
	// of
	// * the start event will be created. If the successor is a gateway an the
	// * combining gateway is an event-based decision gateway, an error is added
	// * to the output.
	// *
	// * @param startEvents The start events to combine.
	// * @param gatewayType The gateway type of the gateway that combines the
	// * start events.
	// *
	// * @return The new start event that was created during the combination.
	// */
	// private StartEvent combineWithGateway(List<StartEvent> startEvents,
	// String gatewayType) {
	//		
	// Gateway newGateway = createCombinatingGateway(startEvents, gatewayType);
	// if (newGateway == null) {
	// return null;
	// }
	//		
	// // create new non-triggered start event
	// StartEvent start = new StartEvent(StartEvent.TRIGGER_NONE, null,
	// true, this.output);
	// this.container.addActivity(start);
	//		
	// // create intermediate events from start events
	// List<IntermediateEvent> newEvents =
	// transfromToIntermiateEvent(startEvents,
	// !gatewayType.equals(Gateway.TYPE_XOR));
	//		
	// // insert transitions between new activities
	// Transition transFromStart =
	// new Transition(start, newGateway, this.output);
	// this.container.addTransition(transFromStart);
	//		
	// for (Iterator<IntermediateEvent> it = newEvents.iterator();
	// it.hasNext();) {
	// Transition transition =
	// new Transition(newGateway, it.next(), this.output);
	// this.container.addTransition(transition);
	// }
	//		
	// // remove non-triggered start events and add transition from gateway to
	// successor
	// // this is not allowed if the gateway is an event-based decision gateway
	// and
	// // the successor is a gateway, too
	// for (Iterator<StartEvent> it = startEvents.iterator(); it.hasNext();) {
	// StartEvent startEvent = it.next();
	// if (startEvent.getTriggerType().equals(StartEvent.TRIGGER_NONE)) {
	// Activity successor = startEvent.getSuccessor();
	// if (gatewayType.equals(Gateway.TYPE_XOR) && (successor instanceof
	// Gateway)) {
	// this.output.addError("The multiple start events could not " +
	// "be combined.", startEvent.getId());
	// return null;
	// }
	//				
	// this.container.removeActivity(startEvent);
	// Transition transition =
	// new Transition(newGateway, successor, this.output);
	// this.container.addTransition(transition);
	// }
	// }
	// return start;
	// }

	/**
	 * Counts the lists contained in the map that contain the given gateway.
	 * 
	 * @param map
	 *            The map that has lists as values.
	 * @param gateway
	 *            The gateway that may be contained in the lists.
	 * 
	 * @return The number of lists in the map, the gateway is contained in.
	 */
	private int countListsWithGateway(Map<StartEvent, List<Gateway>> map,
			Gateway gateway) {
		int counter = 0;
		for (Iterator<List<Gateway>> it = map.values().iterator(); it.hasNext();) {
			List<Gateway> gateways = it.next();
			if (gateways.contains(gateway)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Gets the first gateway that is contained in at least lists as possible.
	 * This means the first gateway is determined that is reachable by the
	 * fewest start events. This is a gateway that merges the sequence flow
	 * starting in these start events. All the gateways following this gateway
	 * are at least contained in the lists of these start events, because after
	 * this gateway the lists of the start events are equal.
	 * 
	 * If there is only one key in the map the result is null.
	 * 
	 * @param map
	 *            The map that maps the start events to the list of the
	 *            reachable gateways.
	 * 
	 * @return The first found gateway that is contained in the fewest lists.
	 */
	private Gateway determineGateway(Map<StartEvent, List<Gateway>> map) {
		if (map.keySet().size() < 2) {
			return null;
		}
		for (int i = 2; i <= map.keySet().size(); i++) {
			// check if gateway exists, that is contained in only i lists
			for (Iterator<List<Gateway>> it = map.values().iterator(); it
					.hasNext();) {
				List<Gateway> gateways = it.next();
				for (Iterator<Gateway> itGat = gateways.iterator(); itGat
						.hasNext();) {
					Gateway gateway = itGat.next();
					int countedLists = countListsWithGateway(map, gateway);
					if (countedLists == i) {
						return gateway;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Determines the map value that is the longest list. The map key of the
	 * value must be contained in list of the given keys.
	 * 
	 * @param keys
	 *            The keys that restrict the set of lists to search for.
	 * @param map
	 *            The map to determine the longest list in.
	 * 
	 * @return The determined list.
	 */
	private List<Gateway> getLongestList(List<StartEvent> keys,
			Map<StartEvent, List<Gateway>> map) {
		List<Gateway> result = null;
		for (Iterator<StartEvent> it = keys.iterator(); it.hasNext();) {
			StartEvent key = it.next();
			List<Gateway> gateways = map.get(key);
			if ((result == null) || (gateways.size() > result.size())) {
				result = gateways;
			}
		}
		return result;
	}

	// /**
	// * Combines the start events in the map that have a path to the
	// * given gateway. If there are multiple gateways with a path
	// * to the given gateway, they are combined using
	// * {@link #combineWithGateway(List, String)}. After that the map is
	// * updated by removing the entries for the combined gateways and inserting
	// * an entry for the newly created start event.
	// *
	// * @param map A map that maps the start events to the list of reachable
	// * gateways.
	// * @param gateway The gateway that merges the sequence flow of the start
	// events
	// * to be combined.
	// *
	// * @return The new start events that was created during the combination.
	// */
	// private StartEvent combineStartEventsForGateway(
	// Map<StartEvent, List<Gateway>> map, Gateway gateway) {
	//		
	// StartEvent start = null;
	//		
	// // get start events that have a path to this gateway, too
	// List<StartEvent> toCombine = new ArrayList<StartEvent>();
	// for (Iterator<StartEvent> it = map.keySet().iterator(); it.hasNext();) {
	// StartEvent nextStart = it.next();
	// if (map.get(nextStart).contains(gateway)) {
	// toCombine.add(nextStart);
	// }
	// }
	//		
	// // combine start events using the appropriate gateway type
	// if (toCombine.size() > 1) {
	// start = combineWithGateway(toCombine, gateway.getGatewayType());
	// // replace lists in map with new list for new start event (without
	// // given gateway) - take the longest list from those that were combined
	// and
	// // put to the map for the new start gateway
	// List<Gateway> newList = getLongestList(toCombine, map);
	// newList.remove(gateway);
	//			
	// // remove old lists for combined start events
	// for (Iterator it = toCombine.iterator(); it.hasNext();) {
	// map.remove(it.next());
	// }
	//			
	// if (start != null) {
	// // add new list for new start event
	// map.put(start, newList);
	// }
	// } else if (toCombine.size() == 1) {
	// StartEvent key = toCombine.get(0);
	// map.get(key).remove(gateway);
	// }
	// return start;
	// }

	// /**
	// * <p>Combines the given start events to one start event. For this purpose
	// * the gateways need to be determined that merge the sequence flow
	// * of certain start events. The type of this gateway determines the type
	// * of the gateway that will be used to combine these start events. Not all
	// * start events will be merged with the same gateway, so this combination
	// * has to be done multiple times until no start event from the list is
	// left.</p>
	// *
	// * <p>The method assumes that there is only one end event in the sequence
	// * flow. Thus, multiple end events have to be combined first.</p>
	// *
	// * @param startEvents The start events to be combined.
	// *
	// * @return The new start event that is now the single start event in the
	// * sequence flow or null if an error occured during the combination.
	// */
	// private StartEvent combineMultipleStartEvents(List<StartEvent>
	// startEvents) {
	// if (startEvents.size() == 1) {
	// // check if start events has the right trigger in handlers and scopes
	// return startEvents.get(0);
	// }
	//		
	// // get all gateways on the ways from start events to the end event
	// Map<StartEvent, List<Gateway>> map = getStartGatewayMap(startEvents/*,
	// end*/);
	// if (map == null) {
	// return null;
	// }
	//		
	// Gateway next = determineGateway(map);
	// StartEvent start = null;
	// while (next != null) {
	// start = combineStartEventsForGateway(map, next);
	// if (start == null) {
	// // error occurred during generation
	// break;
	// }
	// next = determineGateway(map);
	// }
	// return start;
	// }

	/**
	 * Checks if the start events alre valid. The validity depends on the start
	 * event trigger and the container it is contained in:
	 * <ul>
	 * <li>Message handlers are only allowed to contain message start events
	 * <li>Timer start events are only allowed in timer handlers
	 * </ul>
	 * 
	 * @param start
	 *            The start event to be checked.
	 * 
	 * @return True if the start event is valid, false otherwise.
	 */
	private boolean isValidStartEvent(StartEvent start) {
		// if (this.container instanceof SubProcess) {
		// BlockActivity act = ((SubProcess)this.container).getBlockActivity();
		// if (act instanceof Handler) {
		// Handler handler = (Handler)act;
		// if (handler.getHandlerType().equals(Handler.TYPE_MESSAGE)) {
		// // message triggered start event
		// if (!start.getTriggerType().equals(StartEvent.TRIGGER_MESSAGE)) {
		// this.output.addError("The message event handler " +
		// "must have a message start event.", handler.getId());
		// return false;
		// }
		// return true;
		//					
		// } else if (handler.getHandlerType().equals(Handler.TYPE_TIMER)) {
		// // timer triggered start event
		// if (!start.getTriggerType().equals(StartEvent.TRIGGER_TIMER)) {
		// this.output.addError("The timer event handler " +
		// "must have a timer start event.", handler.getId());
		// return false;
		// }
		// return true;
		// }
		// }
		// }
		//		
		// // timer-triggered start event
		// if (start.getTriggerType().equals(StartEvent.TRIGGER_TIMER)) {
		// this.output.addError("The container " +
		// "is not allowed to contain a timer start event.",
		// this.container.getId());
		// return false;
		// }
		return true;
	}

	/**
	 * Checks if the given start events are valid. The validity depends on the
	 * number of start events, on the start event trigger and on the container
	 * it is contained in:
	 * <ul>
	 * <li>Each process or sub-process must contain a least one start event
	 * <li>Message and Timer handlers are not allowed to contain multiple start
	 * events
	 * <li>If there are multiple start events they are not allowed to be
	 * non-triggered (timer triggers are only allowed in timer handlers, that do
	 * not allow multiple start events)
	 * <li>see {@link #isValidStartEvent(StartEvent)}
	 * </ul>
	 * 
	 * @param startEvents
	 *            The start events to be checked
	 * 
	 * @return True if the start events are valid, false otherwise.
	 */
	private boolean isValidStartEvents(List<StartEvent> startEvents) {
		// // check if container contains at least one start event
		// if (startEvents.size() < 1) {
		// this.output.addError(
		// "The process or sub-process " +
		// "must contain at least one start event.", this.container.getId());
		// return false;
		// } else if (startEvents.size() == 1) {
		// StartEvent start = startEvents.get(0);
		// return isValidStartEvent(start);
		// } else {
		// if (this.container instanceof SubProcess) {
		// BlockActivity act = ((SubProcess)this.container).getBlockActivity();
		// if (act instanceof Handler) {
		// Handler handler = (Handler)act;
		// if (handler.getHandlerType().equals(Handler.TYPE_MESSAGE) ||
		// handler.getHandlerType().equals(Handler.TYPE_TIMER)) {
		// this.output.addError("The handler " +
		// "is not allowed to have multiple start events.", act.getId());
		// return false;
		// }
		// }
		// }
		// // check if each start event has a message or timer trigger defined
		// for (Iterator<StartEvent> it = startEvents.iterator(); it.hasNext();)
		// {
		// StartEvent start = it.next();
		// if (start.getTriggerType().equals(StartEvent.TRIGGER_NONE)) {
		// this.output.addError("If there are multiple start events " +
		// "defined, the start events are not allowed to be non-triggered.",
		// start.getId());
		// return false;
		// }
		// }
		//
		// }
		return true;
	}

	/**
	 * Conbines multiple end events to one end event using an inclusive merge
	 * gateway. If there is no end event in the container, an error is added to
	 * the output and null will be returned.
	 * 
	 * <p>
	 * The existing end events are removed from the container. The incoming
	 * sequence flows of these events are combined with an inclusive merge
	 * gateway. A new end event is generated that follows the merge gateway.
	 * 
	 * @return The new end event that was created during the combination.
	 */
	private EndEvent combineMultipleEndEvents() {
		// List<EndEvent> endEvents = this.container.getEndEvents();
		// if (endEvents.size() < 1) {
		// this.output.addError(
		// "The process or sub-process " +
		// "must contain at least one end event.", this.container.getId());
		// return null;
		// } else if (endEvents.size() == 1) {
		// return endEvents.get(0);
		// }
		//		
		// // create new OR-gateway
		// Gateway gateway = new Gateway(Gateway.TYPE_OR, null, true,
		// this.output);
		// this.container.addActivity(gateway);
		//		
		// // create new non-triggered start event
		// EndEvent endEvent = new EndEvent(this.output);
		//		
		// // remove end events
		// List<Transition> toKeep = new ArrayList<Transition>();
		// for (Iterator<EndEvent> it = endEvents.iterator(); it.hasNext();) {
		// EndEvent event = it.next();
		// toKeep.addAll(event.getTargetFor());
		// this.container.removeActivity(event);
		// }
		// this.container.addActivity(endEvent);
		//		
		// // add transition again
		// for (Iterator<Transition> it = toKeep.iterator(); it.hasNext();) {
		// Transition transition = it.next();
		// transition.setTarget(gateway, this.output);
		// gateway.addTargetFor(transition, this.output);
		// transition.getSource().addSourceFor(transition, this.output);
		// this.container.addTransition(transition);
		// }
		//		
		// Transition transition = new Transition(gateway, endEvent,
		// this.output);
		// this.container.addTransition(transition);
		// return endEvent;
		return null;
	}

	/**
	 * <p>
	 * Checks if the sequence flow is trivial. A trivial sequence flow has one
	 * start and one end event. Between these events there is at most one
	 * activity (no gateway). If there is no activity in between the start event
	 * has to be a message triggered start event.
	 * 
	 * @param start
	 *            The start event of the sequence flow.
	 * @param end
	 *            The end event of the sequence flow.
	 * 
	 * @return True if the sequence flow of the container is trivial, false
	 *         otherwise.
	 */
	private boolean isTrivial(StartEvent start, EndEvent end) {
		// TODO: Adapt to BPMN Model
		if (start.getSuccessor().equals(end)) {
			// start is followed directly by the end event
			// can only be mapped, if start is a message start event
			// if (start.getTriggerType().equals(StartEvent.TRIGGER_MESSAGE)) {
			if (start instanceof StartMessageEvent) {
				return true;
			}
			return false;
		} else if (start.getSuccessor().equals(end.getPredecessor())) {
			Node act = start.getSuccessor();

			if (act instanceof Task) {
				return true;
			}
			// if ((act instanceof Task) || (act instanceof BlockActivity) ||
			// (act instanceof IntermediateEvent)) {
			// if
			// (act.getAttachedEvents(IntermediateEvent.TRIGGER_ERROR).isEmpty())
			// {
			// return true;
			// }
			// }
		}
		return false;
	}

	/**
	 * Maps a task to its BPEL4Chor representation. If the task represents a
	 * looping activity a loop element is created containing the mapped task.
	 * The mapping of a folded task is just the BPEL element it represents.
	 * 
	 * @param task
	 *            The task to be mapped.
	 * 
	 * @return The BPEL4Chor element the task represents or null if the task
	 *         could not be mapped.
	 */
	private Element mapTask(Task task) {

		Element result = null;
		
		if (task instanceof FoldedTask) {
			result = ((FoldedTask)task).getBPELElement();
		} else {
			result = this.basicFactory.createInvokeElement(task);
		}
		
		// if (task instanceof ServiceTask) {
		// result = this.basicFactory.createInvokeElement((ServiceTask)task);
		// } else if (task instanceof ReceiveTask) {
		// result = this.basicFactory.createReceiveElement((ReceiveTask)task);
		// } else if (task instanceof SendTask) {
		// result = this.basicFactory.createSendingElement((SendTask)task);
		// } else if (task instanceof AssignTask) {
		// result = this.basicFactory.createAssignElement((AssignTask)task);
		// } else if (task instanceof ValidateTask) {
		// result = this.basicFactory.createValidateElement((ValidateTask)task);
		// } else if (task instanceof EmptyTask) {
		// result = this.basicFactory.createEmptyElement((EmptyTask)task);
		// } else if (task instanceof NoneTask) {
		// result = this.basicFactory.createOpaqueElement((NoneTask)task);
		// } else if (task instanceof FoldedTask) {
		// result = ((FoldedTask)task).getBPELElement();
		// } else {
		// return null;
		// }
		//				
		// if (task.getLoop() != null) {
		// Element loopElement =
		// this.structuredFactory.createLoopElement(task, result);
		// return loopElement;
		// }
		return result;
	}

	/**
	 * Maps a block activity to its BPEL4Chor representation. If the block
	 * activity is a looping activity a looping element will be created (see
	 * {@link StructuredElementsFactory#createLoopElement(Activity, Element)}),
	 * otherwise a scope element will be created (see
	 * {@link StructuredElementsFactory#createScopeElement(BlockActivity)}).
	 * Block activities that represent handlers will be mapped during the
	 * mapping of the activity they belong to. Thus, handlers should not be
	 * mapped with this method.
	 * 
	 * @param blockActivity
	 *            The block activity to be mapped.
	 * 
	 * @return The BPEL4Chor element the block activity represents.
	 */
	private Element mapBlockActivity(BlockActivity blockActivity) {
		if (blockActivity.getLoop() != null) {
			return this.structuredFactory
					.createLoopElement(blockActivity, null);
		}
		return this.structuredFactory.createScopeElement(blockActivity);
	}

	/**
	 * Checks if the location of the intermediate event is valid. Compensation
	 * intermediate events are only allowed in fault, compensation and
	 * termination handlers.
	 * 
	 * @param event
	 *            The event to be checked.
	 * 
	 * @return True if the location of the intermediate event is valid, false
	 *         otherwise.
	 */
	private boolean isValidLocation(IntermediateEvent event) {
		if (!event.getTriggerType().equals(
				IntermediateEvent.TRIGGER_COMPENSATION)) {
			return true;
		}
		// compensate event can only located in fault, compensation or
		// termination handlers
		if (event.getParentContainer() instanceof SubProcess) {
			BlockActivity parent = ((SubProcess) event.getParentContainer())
					.getBlockActivity();
			if (parent instanceof Handler) {
				Handler handler = (Handler) parent;
				if (handler.getHandlerType().equals(Handler.TYPE_COMPENSATION)
						|| handler.getHandlerType().equals(Handler.TYPE_FAULT)
						|| handler.getHandlerType().equals(
								Handler.TYPE_TERMINATION)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Maps an intermediate event to its BPEL4Chor representation. If the event
	 * is a compensation intermediate event it needs to be checked if it is
	 * located in a valid container. If the container is not valid and erros is
	 * added to the output and the result will be null. A rethrow element can
	 * only be generated if the error event is located in a fault handler.
	 * 
	 * @param event
	 *            The intermediate event to be mapped.
	 * 
	 * @return The BPEL4Chor element the event represents or null if the event
	 *         could not be mapped.
	 */
	private Element mapIntermediateEvent(IntermediateEvent event) {
		if (event.getTriggerType().equals(
				IntermediateEvent.TRIGGER_COMPENSATION)) {
			if (isValidLocation(event)) {
				return this.basicFactory.createCompensateElement(event);
			}
			this.output.addError("The compensation intermediate event "
					+ "must be located in a fault, "
					+ "compensation or termination handler.", event.getId());
		} else if (event.getTriggerType().equals(
				IntermediateEvent.TRIGGER_ERROR)) {
			return this.basicFactory.createThrowElement(event,
					this.errorHandler);
		} else if (event.getTriggerType().equals(
				IntermediateEvent.TRIGGER_MESSAGE)) {
			return this.basicFactory.createReceiveElement(event);
		} else if (event.getTriggerType().equals(
				IntermediateEvent.TRIGGER_TIMER)) {
			return this.basicFactory.createWaitElement(event);
		}
		return null;
	}

	/**
	 * Maps a trivial sequence flow (see
	 * {@link #isTrivial(StartEvent, EndEvent)}) to its BPEL4Chor elements it
	 * represents. For this purpose the start event and the activity in between
	 * is mapped.
	 * 
	 * @param start
	 *            The start event of the sequence flow.
	 * @param end
	 *            The end event of the sequence flow.
	 * 
	 * @return The created BPEL4Chor element the sequence flow represents.
	 */
	private Element mapTrivial(StartEvent start, EndEvent end) {
		if (start.getSuccessor().equals(end)) {
			// start is followed directly by the end event
			// can only be mapped, if start is a message start event
			// if (start.getTriggerType().equals(StartEvent.TRIGGER_MESSAGE)) {
			if (start instanceof StartMessageEvent) {
				return this.basicFactory.createReceiveElement(start,
						this.container instanceof Process);
			}
		} else if (start.getSuccessor().equals(end.getPredecessor())) {
			Node successor = start.getSuccessor();
			Element result = null;
			List<Element> resultElements = null;
			if (successor instanceof FoldedTask) {
				result = mapTask((Task) successor);
				// } else if (successor instanceof BlockActivity) {
				// return mapBlockActivity((BlockActivity)successor);
				// } else if (successor instanceof IntermediateEvent) {
				// return mapIntermediateEvent((IntermediateEvent)successor);
			} else if (successor instanceof Task) {
				/* Handles the trivial case Start, one task, end */
				resultElements = mapActivity(successor, null);
			}
			else {
				this.output.addError(
						"A trivial component was not generated correctly",
						successor.getId());
				return null;
			}

			if (start instanceof StartMessageEvent) {
				// if start event is a message event, map start event as
				// receive and create additional sequence element
				// containing the receive and the already mapped element
				Element receive = this.basicFactory.createReceiveElement(start,
						this.container instanceof Process);

				if (result != null && result.getNodeName().equals("sequence")) {
					result.insertBefore(receive, result.getFirstChild());
					return result;
				}
				
				
				Element resultSequence = this.document
						.createElement("sequence");
				resultSequence.appendChild(receive);
				for (Element e : resultElements) {
					resultSequence.appendChild(e);
				}
				return resultSequence;
			}

			/* Code from Kerstin */
			// if (start.getTriggerType().equals(StartEvent.TRIGGER_MESSAGE) &&
			// !this.messageHandler) {
			// // if start event is a message event, map start event as
			// // receive and create additional sequence element
			// // containing the receive and the already mapped element
			// Element receive = this.basicFactory.createReceiveElement(
			// start, this.container instanceof Process);
			//				
			// if (result.getNodeName().equals("sequence")) {
			// result.insertBefore(receive, result.getFirstChild());
			// return result;
			// }
			// Element resultSequence = this.document.createElement("sequence");
			// resultSequence.appendChild(receive);
			// resultSequence.appendChild(result);
			// return resultSequence;
			// }
			return result;
		}
		return null;
	}

	/**
	 * Creates the sources and targets element for the activity and the given
	 * links. If the activity is the source of a link, a source element will be
	 * created in the sources element. If the activity is the target of a link,
	 * a target element will be created in the targets element. The created
	 * sources and targets will be added to the given element.
	 * 
	 * @param act
	 *            The activity to create the so.urces and targets for.
	 * @param element
	 *            The element to add the created sources and targets to
	 * @param links
	 *            The links the activity can be source or target of.
	 * @param joinCond
	 *            The join condition for the targets element.
	 */
	private void createSourcesAndTargets(Node act, Element element,
			List<Link> links, Expression joinCond) {
		if (links == null) {
			return;
		}

		// // create targets element
		// Element targets = this.document.createElement("targets");
		// Element joinCondElement =
		// this.supportingFactory.createExpressionElement(
		// "joinCondition", joinCond);
		// if (joinCondElement != null) {
		// targets.appendChild(joinCondElement);
		// }
		//		
		// // create sources element
		// Element sources = this.document.createElement("sources");
		// for (Iterator<Link> itLink = links.iterator(); itLink.hasNext();) {
		// Link link = itLink.next();
		// if (link.getSource().equals(act)) {
		// // create source element and append it
		// Element source = this.document.createElement("source");
		// source.setAttribute("linkName", link.getName());
		//				
		// // create transitionConditions
		// if (link.getExpression() != null) {
		// Element transCond = this.supportingFactory.createExpressionElement(
		// "transitionCondition", link.getExpression());
		// if (transCond != null) {
		// source.appendChild(transCond);
		// }
		// }
		// sources.appendChild(source);
		// } else if (link.getTarget().equals(act)) {
		// // createTarget element and append it
		// Element target = this.document.createElement("target");
		// target.setAttribute("linkName", link.getName());
		// targets.appendChild(target);
		// }
		// }
		//		
		// // append targets and sources
		// if (element.getFirstChild() == null) {
		// if (targets.hasChildNodes()) {
		// element.appendChild(targets);
		// }
		// if (sources.hasChildNodes()) {
		// element.appendChild(sources);
		// }
		// } else {
		// if (sources.hasChildNodes()) {
		// element.insertBefore(sources, element.getFirstChild());
		// }
		// if (targets.hasChildNodes()) {
		// element.insertBefore(targets, element.getFirstChild());
		// }
		// }
	}

	/**
	 * Maps an activity to its BPEL representation. The mapped BPEL element will
	 * not define a join condition. If the activity is not a block activity,
	 * task or intermediate event, an error is added to the output and the
	 * result will be null.
	 * 
	 * @param act
	 *            The activity to be mapped.
	 * @param links
	 *            Links that are defined in the context of the activity.
	 * 
	 * @return The created BPEL element the activity represents or null if the
	 *         activity could not be mapped.
	 */
	public List<Element> mapActivity(Node act, List<Link> links) {
		return mapActivity(act, links, null);
	}

	/**
	 * Maps an activity to its BPEL4Chor representation. If the activity is not
	 * a block activity, task or intermediate event, an error is added to the
	 * output and the result will be null.
	 * 
	 * @param act
	 *            The activity to be mapped.
	 * @param links
	 *            Links that are defined in the context of the activity.
	 * @param joinCond
	 *            The join condition the mapped element should define.
	 * 
	 * @return The created BPEL4Chor element the activity represents or null if
	 *         the activity could not be mapped.
	 */
	public List<Element> mapActivity(Node act, List<Link> links, Expression joinCond) {
		ArrayList<Element> elements = new ArrayList<Element>();
		Element element = null;
		// if (act instanceof BlockActivity) {
		// element = mapBlockActivity((BlockActivity)act);
		/* } else */if (act instanceof Task) {
			
			/* Insert assign task, if an DataObject is connected */
			DataObject dataObject = ((Task)act).getFirstInputDataObject();
			if (dataObject instanceof BPELDataObject) {
				Element assign = this.basicFactory.createAssignElement((BPELDataObject) dataObject, (Task) act);
				if (assign != null) {
					elements.add(assign);
				}
			}
			
			
			elements.add(mapTask((Task) act));
		} // else if (act instanceof IntermediateEvent) {
		// element = mapIntermediateEvent((IntermediateEvent)act);
		// } else {
		// this.output.addError("Activity " +
		// "could not be transformed to BPEL4Chor.", act.getId());
		// return null;
		// }
		if (element != null) {
			createSourcesAndTargets(act, element, links, joinCond);
		}
		return elements;
	}

	/**
	 * Maps a sequence component to its BPEL4Chor representation. For this
	 * purpose the activities of the component are mapped to BPEL4Chor. After
	 * that a "sequence" element will be created that contains these mapped
	 * BPEL4Chor elements.
	 * 
	 * @param comp
	 *            The sequence component to be mapped.
	 * @param links
	 *            Links that are defined in the context of the component.
	 * 
	 * @return The created "sequence" element.
	 */
	private Element mapSequence(Component comp, List<Link> links) {
		Element result = this.document.createElement("sequence");
		List<Element> elements = mapActivity(comp.getSourceObject(), links);
		
		/* Append mapped elements. Typically this is a sequence of an assign and
		 * an invoke element */
		for(Element element : elements) {
			result.appendChild(element);			
		}
		
		for (Node node : comp.getChildNodes()) {
			elements = mapActivity(node, links);
			for(Element element : elements) {
				result.appendChild(element);			
			}
		}
//		for (Iterator<Activity> it = comp.getActivities().iterator(); it
//				.hasNext();) {
//			element = mapActivity(it.next(), links);
//			if (element != null) {
//				result.appendChild(element);
//			}
//		}

		elements = mapActivity(comp.getSinkObject(), links);
		for(Element element : elements) {
			result.appendChild(element);			
		}
		
		return result;
	}

	/**
	 * Maps an attached events component. Since the mapping of attached events
	 * is already included in the mapping of the activity the events are
	 * attached to just the activity needs to be mapped.
	 * 
	 * @param comp
	 *            The attached events component to be mapped.
	 * @param links
	 *            Links that are defined in the context of the component.
	 * 
	 * @return The created activity element or null if the activity could not be
	 *         mapped.
	 */
	private Element mapAttachedEvents(Component comp, List<Link> links) {
		// Element result = mapActivity(comp.getSourceObject(), links);
		// return result;
		return null;
	}

	/**
	 * Maps a flow component to its BPEL4Chor representation. For this purpose
	 * the activities of the component are mapped to BPEL4Chor. After that an
	 * "flow" element will be created that contains these mapped BPEL4Chor
	 * elements.
	 * 
	 * Directed sequence flows from the source gateway to the sink gateway are
	 * mapped to an empty activity.
	 * 
	 * @param comp
	 *            The if component to be mapped.
	 * @param links
	 *            Links that are defined in the context of the component.
	 * 
	 * @return The created "flow" element.
	 */
	private Element mapFlow(Component comp, List<Link> links) {
		// Element result = this.document.createElement("flow");
		// for (Iterator<Activity> it = comp.getActivities().iterator();
		// it.hasNext();) {
		// Activity act = it.next();
		// result.appendChild(mapActivity(act, links));
		// }
		//		
		// // direct association from source to sink object
		// for (Iterator<Activity> it =
		// comp.getSourceObject().getSuccessors().iterator(); it.hasNext();) {
		// Activity act = it.next();
		// if (act.equals(comp.getSinkObject())) {
		// result.appendChild(this.document.createElement("empty"));
		// break;
		// }
		// }
		//		
		// return result;
		return null;
	}

	/**
	 * Maps an if component to its BPEL4Chor representation. For this purpose
	 * the activities of the component are mapped to BPEL4Chor. After that an
	 * "if" element will be created that contains these mapped BPEL4Chor
	 * elements.
	 * 
	 * <p>
	 * Directed sequence flows from the source gateway to the sink gateway are
	 * mapped to an empty activity.
	 * </p>
	 * 
	 * <p>
	 * If the source gateway has an outgoing sequence flow that is not
	 * conditional or a default flow, an error will be added to the output and
	 * the result will be null. This will also be done if there are multiple
	 * default flows attached to the source gateway.
	 * </p>
	 * 
	 * @param comp
	 *            The if component to be mapped.
	 * @param links
	 *            Links that are defined in the context of the component.
	 * 
	 * @return The created "if" element.
	 */
	private Element mapIf(Component comp, List<Link> links) {
		// Element result = this.document.createElement("if");
		// Element defaultElement = null;
		//		
		// List<Transition> order = ((Gateway)
		// comp.getSourceObject()).determineEvaluationOrder();
		//		
		// boolean first = true;
		// for (Iterator<Transition> it = order.iterator(); it.hasNext();) {
		// Transition trans = it.next();
		// Activity act = trans.getTarget();
		// Element element = null;
		// if (act.equals(comp.getSinkObject())) {
		// element = this.document.createElement("empty");
		// } else {
		// element = mapActivity(act, links);
		// }
		// if (trans.getConditionType() == null) {
		// this.output.addError("Transition " +
		// "must be conditional or a default flow", trans.getId());
		// return null;
		// }
		// if (trans.getConditionType().equals(Transition.TYPE_OTHERWISE)) {
		// if (defaultElement == null) {
		// defaultElement = this.document.createElement("else");
		// if (element != null) {
		// defaultElement.appendChild(element);
		// }
		// } else {
		// this.output.addError("There is more than one " +
		// "default sequence flow defined for this gateway",
		// comp.getSourceObject().getId());
		// return null;
		// }
		// } else if
		// (!trans.getConditionType().equals(Transition.TYPE_EXPRESSION)) {
		// this.output.addError("A transition condition " +
		// "must be defined for transition ", trans.getId());
		// return null;
		// } else if (first) {
		// Element condition =
		// this.supportingFactory.createExpressionElement(
		// "condition", trans.getConditionExpression());
		// result.appendChild(condition);
		// if (element != null) {
		// result.appendChild(element);
		// }
		// first = false;
		// } else {
		// Element elseif = this.document.createElement("elseif");
		// Element condition =
		// this.supportingFactory.createExpressionElement(
		// "condition", trans.getConditionExpression());
		// elseif.appendChild(condition);
		// elseif.appendChild(element);
		// result.appendChild(elseif);
		// }
		// }
		// // default flow is appended at the end
		// if (defaultElement != null) {
		// result.appendChild(defaultElement);
		// }
		//		
		// return result;
		return null;
	}

	/**
	 * Maps a pick component to its BPEL4Chor representation. For this purpose
	 * the activities of the component are mapped to BPEL4Chor. After that a
	 * "pick" element will be created that contains these mapped BPEL4Chor
	 * elements.
	 * 
	 * <p>
	 * Directed sequence flows from a branch activity to the sink gateway are
	 * mapped to an empty activity.
	 * </p>
	 * 
	 * @param comp
	 *            The pick component to be mapped.
	 * @param links
	 *            Links that are defined in the context of the component.
	 * 
	 * @return The created "pick" element.
	 */
	private Element mapPick(Component comp, List<Link> links) {
		Element result = this.document.createElement("pick");
		// if (comp.getSourceObject() instanceof Gateway) {
		// if (((Gateway)comp.getSourceObject()).getCreateInstance()) {
		// result.setAttribute("createInstance", "yes");
		// }
		// }
		//			
		// for (Iterator<Transition> it =
		// comp.getSourceObject().getSourceFor().iterator(); it.hasNext();) {
		// Activity branchAct = it.next().getTarget();
		// Activity successor = branchAct.getSuccessor();
		//			
		// // follow each branch to the target element
		// Element branch = null;
		// if (branchAct instanceof ReceiveTask) {
		// ReceiveTask task = (ReceiveTask)branchAct;
		// Element content = null;
		// if ((successor != null) && !successor.equals(comp.getSinkObject())) {
		// content = mapActivity(successor, links);
		// } else {
		// content = this.document.createElement("empty");
		// }
		// branch = this.structuredFactory.createOnMessageBranch(task, content);
		// } else if (branchAct instanceof IntermediateEvent) {
		// IntermediateEvent event = (IntermediateEvent)branchAct;
		// Element content = null;
		// if ((successor != null) && !successor.equals(comp.getSinkObject())) {
		// content = mapActivity(successor, links);
		// } else {
		// content = this.document.createElement("empty");
		// }
		// if (event.getTriggerType().equals(IntermediateEvent.TRIGGER_MESSAGE))
		// {
		// branch = this.structuredFactory.createPickBranchElement(event,
		// content);
		// } else if
		// (event.getTriggerType().equals(IntermediateEvent.TRIGGER_TIMER)) {
		// branch = this.structuredFactory.createPickBranchElement(event,
		// content);
		// }
		// }
		// if (branch != null) {
		// result.appendChild(branch);
		// }
		// }

		return result;
	}

	// /**
	// * Refines a quasi component where the sink gateway has additional
	// incoming
	// * sequence flows, that do not belong to the component.
	// * As a result of the refinement the container contains another
	// well-structured
	// * component.
	// *
	// * <p>Refinement means that a new gateway is inserted before the sink
	// gateway.
	// * The sequence flows from the component activities are now leading to
	// this
	// * created gateway.</p>
	// *
	// * <p>If the sink object of the component is not a gateway, an error is
	// * added to the output and the result will be null.</p>
	// *
	// * @param comp The quasi-component to be refined.
	// */
	// private void refineQuasi(Component comp) {
	// if (!(comp.getSinkObject() instanceof Gateway)) {
	// this.output.addError("A component was not generated correctly",
	// comp.getSinkObject().getId());
	// }
	// Gateway oldGateway = (Gateway)comp.getSinkObject();
	//		
	// // create new Gateway of the type of the target object
	// Gateway newGateway = new Gateway(oldGateway.getGatewayType(),
	// oldGateway.getSplitType(), true, this.output);
	// this.container.addActivity(newGateway);
	//		
	// // create Transition from new gateway to old gateway
	// this.container.addTransition(new Transition(newGateway, oldGateway,
	// this.output));
	//		
	// // change target of transitions from activties to new gateway
	// for (Iterator<Activity> it = comp.getActivities().iterator();
	// it.hasNext();) {
	// Activity act = it.next();
	// Transition trans = act.getTransitionTo(oldGateway);
	// if (trans != null) {
	// trans.setTarget(newGateway, this.output);
	// newGateway.addTargetFor(trans, this.output);
	// oldGateway.removeTargetFor(trans);
	// }
	// }
	//		
	// // if direct transition from source to target change this one too
	// Transition trans =
	// comp.getSourceObject().getTransitionTo(comp.getSinkObject());
	// if (trans != null) {
	// trans.setTarget(newGateway, this.output);
	// newGateway.addTargetFor(trans, this.output);
	// oldGateway.removeTargetFor(trans);
	// }
	// }

	/**
	 * Checks if the activities are the only successors of the given activity.
	 * 
	 * @param source
	 *            The activity whose successors will be checked.
	 * @param successors
	 *            A list with the successors to be checked.
	 * 
	 * @return True if all successors of the activity are contained in the list,
	 *         false otherwise
	 */
	private boolean isOnlySuccessors(Activity source, List<Activity> successors) {
		// for (Iterator<Transition> it = source.getSourceFor().iterator();
		// it.hasNext();) {
		// Transition trans = it.next();
		// if (!successors.contains(trans.getTarget())) {
		// return false;
		// }
		// }
		return true;
	}

	/**
	 * Checks if the activities are the only predecessors of the given activity.
	 * 
	 * @param target
	 *            The activity whose predecessors will be checked.
	 * @param predecessors
	 *            A list with the predecessors to be checked.
	 * 
	 * @return True, if all predecessors of the activity are contained in the
	 *         list, false otherwise
	 */
	private boolean isOnlyPredecessor(Activity target,
			List<Activity> predecessors) {
		// for (Iterator<Transition> it = target.getTargetFor().iterator();
		// it.hasNext();) {
		// Transition trans = it.next();
		// if (!predecessors.contains(trans.getSource())) {
		// return false;
		// }
		// }
		return true;
	}

	/**
	 * Refines the condition of an inclusive gateway. This refinement is
	 * necessary if a quasi-special flow component is mapped. The refined
	 * condition is added to the conditional sequence flow created during the
	 * refinement.
	 * 
	 * @return The refined condition. This condition will always evaluate to
	 *         true.
	 */
	private Expression getRefinedInclCond() {
		Expression result = new Expression();
		result
				.setExpressionLanguage("urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0");
		result.setExpression("true()");
		return result;
	}

	// /**
	// * Refines a quasi (special) flow component where the source gateway has
	// * additional outgoing sequence flows and/or the sink gateway has
	// additional
	// * incoming sequence flows, that do not belong to the component.
	// * As a result of the refinement the container contains another
	// * well-structured component.
	// *
	// * <p>Refinement means that a new gateway is inserted before the
	// * source/sink gateway. The sequence flows from/to the component
	// activities
	// * are now emanating/leading to/from this created gateway.</p>
	// *
	// * <p>If the source and the sink object of the component are not gateways,
	// * an error is added to the output and the result will be null.</p>
	// *
	// * @param comp The quasi-component to be refined.
	// * @param special True if the component is a special flow component,
	// * false otherwise.
	// */
	// private void refineQuasiFlow(Component comp, boolean special) {
	// if (!(comp.getSinkObject() instanceof Gateway)) {
	// this.output.addError("A quasi flow component " +
	// "was not generated correctly.", comp.getSinkObject().getId());
	// return;
	// }
	// if (!(comp.getSourceObject() instanceof Gateway)) {
	// this.output.addError("A quasi flow component " +
	// "was not generated correctly.", comp.getSourceObject().getId());
	// return;
	// }
	//		
	// Gateway sourceGateway = (Gateway)comp.getSourceObject();
	// Gateway sinkGateway = (Gateway)comp.getSinkObject();
	//		
	// // create new Gateways of the type of the target object
	// Gateway newSourceGateway = null;
	// Transition newSourceTrans = null;
	// if (!isOnlySuccessors(sourceGateway, comp.getActivities())) {
	// newSourceGateway = new Gateway(
	// sourceGateway.getGatewayType(), sourceGateway.getSplitType(),
	// true, this.output);
	// this.container.addActivity(newSourceGateway);
	// newSourceTrans = new Transition(sourceGateway, newSourceGateway,
	// this.output);
	// if (special) {
	// newSourceTrans.setConditionType(Transition.TYPE_EXPRESSION);
	// newSourceTrans.setConditionExpression(getRefinedInclCond());
	// }
	// this.container.addTransition(newSourceTrans);
	// }
	//		
	// Gateway newSinkGateway = null;
	// if (!isOnlyPredecessor(sinkGateway, comp.getActivities())) {
	// newSinkGateway = new Gateway(
	// sinkGateway.getGatewayType(), sinkGateway.getSplitType(),
	// true, this.output);
	// this.container.addActivity(newSinkGateway);
	// this.container.addTransition(new Transition(newSinkGateway,
	// sinkGateway, this.output));
	// }
	//		
	// if ((newSourceGateway == null) && (newSinkGateway == null)) {
	// this.output.addError("A quasi flow component " +
	// "was not generated correctly.", comp.getId());
	// }
	//		
	// // change transitions for activities:
	// // activities that were a successor of source Gateway are now
	// // a successor of new source gateway
	// // activities that were a predecessor of sink Gateway are now
	// // a predecessor of new sink gateway
	// for (Iterator<Activity> it = comp.getActivities().iterator();
	// it.hasNext();) {
	// Activity act = it.next();
	//			
	// if (newSourceGateway != null) {
	// Transition transFrom = act.getTransitionFrom(sourceGateway);
	// if (transFrom != null) {
	// transFrom.setSource(newSourceGateway, this.output);
	// newSourceGateway.addSourceFor(transFrom, this.output);
	// sourceGateway.removeSourceFor(transFrom);
	// }
	// }
	//			
	// if (newSinkGateway != null) {
	// Transition transTo = act.getTransitionTo(sinkGateway);
	// if (transTo != null) {
	// transTo.setTarget(newSinkGateway, this.output);
	// newSinkGateway.addTargetFor(transTo, this.output);
	// sinkGateway.removeTargetFor(transTo);
	// }
	// }
	// }
	// }

	/**
	 * Maps a repeat component to its BPEL4Chor representation. For this purpose
	 * the activities of the component are mapped to BPEL4Chor. After that a
	 * "repeatUntil" element will be created that contains these mapped
	 * BPEL4Chor elements.
	 * 
	 * <p>
	 * If the sink object does not have conditional outgoing sequence flows an
	 * error is added to the output and the result will be null. This will also
	 * be done if there is not exaclty one activity (except source and sink
	 * object) in the component.
	 * </p>
	 * 
	 * @param comp
	 *            The repeat component to be mapped.
	 * @param links
	 *            Links that are defined in the context of the component.
	 * 
	 * @return The created "repeatUntil" element.
	 */
	private Element mapRepeat(Component comp, List<Link> links) {
		// if (comp.getActivities().size() == 1) {
		// Element result = this.document.createElement("repeatUntil");
		// Activity act = comp.getActivities().get(0);
		//			
		// Element element = mapActivity(act, links);
		// result.appendChild(element);
		//			
		// Transition trans =
		// comp.getSinkObject().getTransitionTo(comp.getSourceObject());
		//			
		// if (trans.getConditionType() == null ||
		// !trans.getConditionType().equals(Transition.TYPE_EXPRESSION)) {
		// this.output.addError("The transition " +
		// "must define a transition condition.", trans.getId());
		// } else {
		// Element condition = this.supportingFactory.createExpressionElement(
		// "condition", trans.getConditionExpression());
		// result.appendChild(condition);
		// }
		//			
		// return result;
		// }
		//		
		// this.output.addError("A repeat component was not generated correctly",
		// comp.getId());
		return null;
	}

	/**
	 * Maps a repeat-while component to its BPEL4Chor representation. For this
	 * purpose the activities of the component are mapped to BPEL4Chor. After
	 * that a "while" element will be created that contains these mapped
	 * BPEL4Chor elements. The "while" element will be contained in a "sequence"
	 * element.
	 * 
	 * <p>
	 * If the sink object does not have conditional outgoing sequence flows an
	 * error is added to the output and the result will be null. This will also
	 * be done if there are not exactly two activities (except source and sink
	 * object) in the component.
	 * </p>
	 * 
	 * @param comp
	 *            The repeat-while component to be mapped.
	 * @param links
	 *            Links that are defined in the context of the component.
	 * 
	 * @return The created "sequence" element.
	 */
	private Element mapRepeatWhile(Component comp, List<Link> links) {
		// if (comp.getActivities().size() == 2) {
		// Activity t1 = null;
		// Activity t2 = null;
		// Transition condTrans = null;
		//			
		// // determine first and second activity
		// for (Iterator<Activity> it = comp.getActivities().iterator();
		// it.hasNext();) {
		// Activity act = it.next();
		//				
		// if (comp.getSinkObject().getTransitionTo(act) != null) {
		// t2 = act;
		// condTrans = comp.getSinkObject().getTransitionTo(act);
		// } else if (comp.getSinkObject().getTransitionFrom(act) != null) {
		// t1 = act;
		// }
		// }
		// if (t1 == null || t2 == null) {
		// this.output.addError(
		// "A repeat while component was not generated correctly",
		// comp.getId());
		// } else {
		// Element result = this.document.createElement("sequence");
		// Element element = mapActivity(t1, links);
		// result.appendChild(element);
		//				
		// // create while elemnt
		// Element whileElement = this.document.createElement("while");
		// if (condTrans == null) {
		// this.output.addError(
		// "The outgoing transitions of this gateway " +
		// "must define transition conditions.",
		// comp.getSinkObject().getId());
		// } else if ((condTrans.getConditionType() == null) ||
		// !condTrans.getConditionType().equals(Transition.TYPE_EXPRESSION)) {
		// this.output.addError("The transition " +
		// "must define a transition condition.", condTrans.getId());
		// } else {
		// Element condition = this.supportingFactory.createExpressionElement(
		// "condition", condTrans.getConditionExpression());
		// whileElement.appendChild(condition);
		// }
		//				
		// Element sequence = this.document.createElement("sequence");
		// element = mapActivity(t2, links);
		// sequence.appendChild(element);
		// element = mapActivity(t1, links);
		// sequence.appendChild(element);
		// whileElement.appendChild(sequence);
		//				
		// result.appendChild(whileElement);
		// return result;
		// }
		// } else {
		// this.output.addError("A repeat-while component was not generated correctly",
		// comp.getId());
		// }
		return null;
	}

	/**
	 * Maps a while component to its BPEL4Chor representation. For this purpose
	 * the activity of the component is mapped to BPEL4Chor. After that a
	 * "while" element will be created that contains this mapped BPEL4Chor
	 * element.
	 * 
	 * <p>
	 * If the sink object does not have conditional outgoing sequence flows an
	 * error is added to the output and the result will be null. This will also
	 * be done if there is not exaclty one activity (except source and sink
	 * object) in the component.
	 * </p>
	 * 
	 * @param comp
	 *            The while component to be mapped.
	 * @param links
	 *            Links that are defined in the context of the component.
	 * 
	 * @return The created "while" element.
	 */
	private Element mapWhile(Component comp, List<Link> links) {
		// if (comp.getActivities().size() == 1) {
		// Activity act = comp.getActivities().get(0);
		// Transition condTrans =
		// comp.getSinkObject().getTransitionTo(act);
		// if (condTrans == null) {
		// this.output.addError(
		// "A while component was not generated correctly", comp.getId());
		// } else {
		// Element result = this.document.createElement("while");
		// if (condTrans.getConditionType() == null ||
		// !condTrans.getConditionType().equals(Transition.TYPE_EXPRESSION)) {
		// this.output.addError("The transition " +
		// "must define a transition condition.", condTrans.getId());
		// } else {
		// result.appendChild(this.supportingFactory.createExpressionElement(
		// "condition", condTrans.getConditionExpression()));
		// result.appendChild(mapActivity(act, links));
		// return result;
		// }
		// }
		// } else {
		// this.output.addError("A while component was not generated correctly",
		// comp.getId());
		// }
		return null;
	}

	/**
	 * Maps a special-flow component to its BPEL4Chor representation. For this
	 * purpose the activities of the component are mapped to BPEL4Chor. After
	 * that a "flow" element will be created that contains this mapped BPEL4Chor
	 * elements. The flow starts with an empty activity. This activity is the
	 * source of the links that lead to the mapped BPEL4Chor elements.
	 * 
	 * @param comp
	 *            The special-flow component to be mapped.
	 * 
	 * @return The created "flow" element.
	 */
	private Element mapSpecialFlow(Component comp) {
		Element result = this.document.createElement("flow");
		// Element links = this.document.createElement("links");
		// Element empty = this.document.createElement("empty");
		//		
		// // sources of empty element
		// Element sources = this.document.createElement("sources");
		// result.appendChild(links);
		// result.appendChild(empty);
		//		
		// //boolean defaultFound = false;
		// List<Transition> transitions = comp.getSourceObject().getSourceFor();
		//		
		// for (Iterator<Transition> it = transitions.iterator(); it.hasNext();)
		// {
		// Transition trans = it.next();
		// // create link for links element
		// Element link = this.document.createElement("link");
		// String linkName = null;
		// if (trans.getName() != null) {
		// linkName = trans.getName();
		// } else {
		// linkName = trans.getId();
		// }
		// link.setAttribute("name", linkName);
		// links.appendChild(link);
		//			
		// // create targets for target activity of transition
		// Activity act = trans.getTarget();
		// Element element = null;
		// if (act.equals(comp.getSinkObject())) {
		// // create additional empty element for direct
		// // transitions from source to sink object
		// element = this.document.createElement("empty");
		// } else {
		// element = mapActivity(act, null);
		// }
		// Element targets = this.document.createElement("targets");
		// Element target = this.document.createElement("target");
		// target.setAttribute("linkName", linkName);
		// targets.appendChild(target);
		// if (element.getFirstChild() == null) {
		// element.appendChild(targets);
		// } else {
		// element.insertBefore(targets, element.getFirstChild());
		// }
		//			
		// // create source for sources element in the empty activity
		// Element source = this.document.createElement("source");
		// source.setAttribute("linkName", linkName);
		// if (trans.getConditionType() != null) {
		// if (trans.getConditionType().equals(Transition.TYPE_EXPRESSION)) {
		// Element condition =
		// this.supportingFactory.createExpressionElement(
		// "transitionCondition", trans.getConditionExpression());
		// source.appendChild(condition);
		// } else {
		// this.output.addError(
		// "There are only conditional sequence flows " +
		// "allowed to be connected with this inclusive gateway",
		// trans.getSource().getId());
		// }
		// }
		// sources.appendChild(source);
		// result.appendChild(element);
		// }
		// empty.appendChild(sources);

		return result;
	}

	/**
	 * Folds a component to a single folded task. There is no need to define
	 * links in the mapped elements of the component (see
	 * {@link #foldComponent(Component, List)}).
	 * 
	 * @param comp
	 *            The component to be folded
	 * 
	 * @return The folded task that holds a mapped BPEL4Chor element.
	 */
	public FoldedTask foldComponent(Component comp) {
		return foldComponent(comp, null);
	}

	/**
	 * Creates and inserts a folded task into the sequnce flow of the container
	 * replacing the given component. The folded task holds the given element.
	 * The activities and transitions of the component will be removed from the
	 * container.
	 * 
	 * @param comp
	 *            The component to be folded.
	 * @param element
	 *            The BPEL4Chor element represented by the folded task.
	 * 
	 * @return The created and inserted folded task or null, if the component
	 *         could not be folded.
	 */
	public FoldedTask createAndInsertFoldedTask(Component comp, Element element) {
		if (element == null) {
			return null;
		}

		// FoldedTask task = new FoldedTask(element, this.container,
		// this.output);
		FoldedTask task = new FoldedTask(element, this.container);

		SequenceFlow toTask = comp.getEntry();
		if (toTask == null) {
			// this.output.addError("A component could not be folded.",
			// comp.getId());
			return null;
		}

		SequenceFlow fromTask = comp.getExit();
		if (fromTask == null) {
			// this.output.addError("A component could not be folded.",
			// comp.getId());
			return null;
		}

		for (Iterator<Node> it = comp.getChildNodes().iterator(); it.hasNext();) {
			this.container.removeNode(it.next());
		}
		this.container.removeNode(comp.getSourceObject());
		this.container.removeNode(comp.getSinkObject());

		this.container.addNode(task);

		// add transitions again
		toTask.setTarget(task);
		fromTask.setSource(task);


		
		
//		toTask.setTarget(task, this.output);
//		task.addTargetFor(toTask, this.output);
//		toTask.getSource().addSourceFor(toTask, this.output);
//		fromTask.setSource(task, this.output);
//		task.addSourceFor(fromTask, this.output);
//		fromTask.getTarget().addTargetFor(fromTask, this.output);
		
//		this.container.addTransition(toTask);
//		this.container.addTransition(fromTask);
		
		return task;
	}

	/**
	 * Folds a component to a single folded task. For this purpose the component
	 * is mapped to its BPEL representation. After that the activities and
	 * transitions of the component will be removed from the container. A folded
	 * task will be created that holds the mapped BPEL4Chor element and is
	 * inserted at the location of the component.
	 * 
	 * <p>
	 * If the component is a quasi-component it will only be refined. There will
	 * not be created a folded task.
	 * </p>
	 * 
	 * @param comp
	 *            The component to be folded
	 * @param links
	 *            Links that are defined in the context of the component.
	 * 
	 * @return The folded task that holds a mapped BPEL4Chor element or null, if
	 *         the component is a quasi-component or if it could not be mapped.
	 */
	public FoldedTask foldComponent(Component comp, List<Link> links) {
		Element element = null;
		if (comp.getType() == Component.TYPE_ATTACHED_EVENTS) {
			// element = mapAttachedEvents(comp, links);
			// } else if (comp.getType() == Component.TYPE_FLOW) {
			// element = mapFlow(comp, links);
			// } else if (comp.getType() == Component.TYPE_IF) {
			// element = mapIf(comp, links);
			// } else if (comp.getType() == Component.TYPE_PICK) {
			// element = mapPick(comp, links);
			// } else if (comp.getType() ==
			// Component.TYPE_QUASI_ATTACHED_EVENTS) {
			// refineQuasi(comp);
			// } else if (comp.getType() == Component.TYPE_QUASI_FLOW) {
			// refineQuasiFlow(comp, false);
			// } else if (comp.getType() == Component.TYPE_QUASI_IF) {
			// refineQuasi(comp);
			// } else if (comp.getType() == Component.TYPE_QUASI_SPECIAL_FLOW) {
			// refineQuasiFlow(comp, true);
			// } else if (comp.getType() == Component.TYPE_QUASI_PICK) {
			// refineQuasi(comp);
			// } else if (comp.getType() == Component.TYPE_REPEAT) {
			// element = mapRepeat(comp, links);
			// } else if (comp.getType() == Component.TYPE_REPEAT_WHILE) {
			// element = mapRepeatWhile(comp, links);
		} else if (comp.getType() == Component.TYPE_SEQUENCE) {
			element = mapSequence(comp, links);
			// } else if (comp.getType() == Component.TYPE_SPECIAL_FLOW) {
			// element = mapSpecialFlow(comp);
			// } else if (comp.getType() == Component.TYPE_WHILE) {
			// element = mapWhile(comp, links);
			// } else if (comp.getType() == Component.TYPE_GENERALISED_FLOW) {
			// return new GeneralizedFlowMapper(
			// this.diagram, this.document, this.container, comp, this.output).
			// mapGeneralizedFlow();
			// } else if (comp.getType() ==
			// Component.TYPE_SYNCHRONIZING_PROCESS) {
			// return new SynchronizingProcessMapper(
			// this.diagram, this.document, this.container, comp).
			// mapSynchronizingProcess(this.output);
		}

		return createAndInsertFoldedTask(comp, element);
	}

	/**
	 * Transforms the sequence flow of the container to its BPEL4Chor
	 * representation.
	 * 
	 * <p>
	 * First multiple start and end events will be combined. After that
	 * components will be identified and folded (see
	 * {@link Componentizer#getNextComponent()}). If there are still activities
	 * left but no component can be found and the sequence flow is not trivial,
	 * an error will be added to the output and the result will be null.
	 * </p>
	 * 
	 * @return The mapped element representing the sequence flow of the
	 *         container or null if the sequence flow could not be mapped.
	 */
	public Element transformSequenceFlow() {

		// EndEvent end = combineMultipleEndEvents();
		// handled by Normalizer
		List<EndEvent> endEvents = this.container.getEndEvents();
		if (endEvents.size() != 1) {
			this.output.addError("The process or sub-process "
					+ "must contain at exactly one end event.", ""); // this.container.getId());
			return null;
		}
		EndEvent end = endEvents.get(0);

		List<StartEvent> startEvents = this.container.getStartEvents();
		StartEvent start = null;
		if (isValidStartEvents(startEvents)) {
			// handled by Normalizer
			// start = combineMultipleStartEvents(startEvents);
			if (startEvents.size() != 1) {
				this.output.addError("The process or sub-process "
						+ "must contain at exactly one start event.", ""); // this.container.getId());
				return null;
			}
			start = startEvents.get(0);
		}

		if ((start == null) || (end == null)) {
			return null;
		}

		while (!isTrivial(start, end)) {
			Component component = this.componentizer.getNextComponent();
			if (component != null) {
				FoldedTask task = foldComponent(component);
				if ((task == null) && !component.isQuasi()) {
					this.output.addError("Diagram can not be transformed to "
							+ "BPEL4Chor. Component was not folded correctly.",
							"this.container.getId()");
					return null;
				}
			} else {
				this.output.addError("Diagram can not be transformed to "
						+ "BPEL4Chor. No component found",
						"this.container.getId()");
				for (Activity a : this.container.getActivities()) {
					this.output.addError("Activity could not be transformed", a
							.getId());
				}
				return null;
			}
		}

		Element sequence = mapTrivial(start, end);
		
		
		/* Assign response message content */
		
		Element responseAssign = this.document.createElement("assign");
		Element copy = this.document.createElement("copy");
		
		/* Create from part */
		Element from = this.document.createElement("from");
		Element literal = this.document.createElement("literal");
		literal.setTextContent("Process finished");
		from.appendChild(literal);
		copy.appendChild(from);
		
		/* Create to part */
		Element to = this.document.createElement("to");
		to.setAttribute("part", "payload");
		to.setAttribute("variable", "output");
		copy.appendChild(to);
		
		responseAssign.appendChild(copy);
		sequence.appendChild(responseAssign);
		
		/* Append reply for process response */
		Element reply = this.document.createElement("reply");
		reply.setAttribute("partnerLink", "InvokeProcessPartnerLink");
		reply.setAttribute("portType", "tns:InvokeProcess");
		reply.setAttribute("operation", "process");
		reply.setAttribute("variable", "output");
		sequence.appendChild(reply);
		
		return sequence;
	}
}
