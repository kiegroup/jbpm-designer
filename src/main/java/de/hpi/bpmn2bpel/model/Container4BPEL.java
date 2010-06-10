package de.hpi.bpmn2bpel.model;

import java.util.List;

import de.hpi.bpmn.Activity;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.Task;


public interface Container4BPEL extends Container {

//	/**
//	 * 
//	 * @return The id if the container. 
//	 * If the id was not set, the result is null.
//	 */
//	public String getId();

	/**
	 * @return The activities of the container
	 */
	public List<Activity> getActivities();

	/**
	 * @return The transitions of the container
	 */
	public List<SequenceFlow> getTransitions();

//	/**
//	 * Sets the id of the container.
//	 * 
//	 * @param id the id to set
//	 */
//	public void setId(String id);
//
	/**
	 * Adds a node to the list of the container's nodes.
	 * 
	 * @param node The node to add.
	 */
	public void addNode(Node node);

	/**
	 * Removes a {@link Node} from the container's child nodes.
	 * The transitions with this node as source or target will be 
	 * removed from the container's transitions as well.
	 * 
	 * @param node The {@link Node} to remove from the container
	 */
	public void removeNode(Node node);
	
	/**
	 * Connects two nodes with each other and returns the connecting sequence flow.
	 * 
	 * @param source
	 * 			The source node
	 * @param target
	 * 			The target node
	 * @return
	 * 			The connecting sequence flow
	 */
	public SequenceFlow connectNodes(Node source, Node target);

//	/**
//	 * Removes the activities from list of the container's
//	 * activities. Transitions with these activities as source or
//	 * target will be removed from the container's transitions as well.
//	 * 
//	 * @param activities The activities to remove from the container.
//	 */
//	public void removeActivities(List<Activity> activities);
//
//	/**
//	 * Adds a transition to the list of the container's transitions.
//	 * 
//	 * @param transition The transition to add.
//	 */
//	public void addTransition(Transition transition);
//
//	/**
//	 * Adds the transitions to the list of the container's transitions.
//	 * 
//	 * @param transitions The transitions to add.
//	 */
//	public void addTransitions(List<Transition> transitions);
//
//	/**
//	 * Removes a transition from the list of the container's transitions.
//	 * Activities connected to the transition will not be removed from 
//	 * the container.
//	 * 
//	 * @param transition The transition to remove.
//	 */
//	public void removeTransition(Transition transition);
//
//	/**
//	 * Removes a transitions from the list of the container's transitions.
//	 * Activities connected to the transitions will not be removed from 
//	 * the container.
//	 * 
//	 * @param transitions The transitions to remove.
//	 */
//	public void removeTransitions(List<Transition> transitions);
//
//	public boolean equals(Object obj);
//
//	/**
//	 * Collects the handlers of the given handler type,
//	 * that are contained in the container.
//	 * 
//	 * @param handlerType The type of the handlers to collect.
//	 * @return A list with the handlers of the container.
//	 */
//	public List<Handler> getHandlers(String handlerType);

	/**
	 * Collects the start events that are contained in the container.
	 * 
	 * @return A list with start events of the container. 
	 */
	public List<StartEvent> getStartEvents();

//	/**
//	 * Collects the intermediate events that are contained in the container.
//	 * 
//	 * @param triggerType The trigger type of the events to collect. If null,
//	 * all intermediate events are collected.
//	 * 
//	 * @return A list with intermediate events of the container. 
//	 */
//	public List<IntermediateEvent> getIntermediateEvents(String triggerType);

	/**
	 * Collects the end events that are contained in the container.
	 * 
	 * @return A list with end events of the container. 
	 */
	public List<EndEvent> getEndEvents();

//	/**
//	 * Collects the scopes that are contained in the container.
//	 * 
//	 * @return A list with scopes of the container. 
//	 */
//	public List<Scope> getScopes();

	/**
	 * Collects the tasks that are contained in the container.
	 * 
	 * @return A list with tasks of the container. 
	 */
	public List<Task> getTasks();

//	/**
//	 * Collects the split gateways of the given type that are contained in 
//	 * the container. A split gateway is a gateway that has more than one 
//	 * outgoing transition.
//	 * 
//	 * @param type The type of the split gateway ({@link Gateway#TYPE_AND},
//	 * {@link Gateway#TYPE_OR} or {@link Gateway#TYPE_XOR})
//	 * 
//	 * @return A list with split gateways of the given type. 
//	 */
//	public List<Gateway> getSplitGateways(String type);
//
//	/**
//	 * Collects the join gateways of the given type that are contained in 
//	 * the container. A join gateway is a gateway that has more than one 
//	 * incoming transition.
//	 * 
//	 * @param type The type of the split gateway ({@link Gateway#TYPE_AND},
//	 * {@link Gateway#TYPE_OR} or {@link Gateway#TYPE_XOR})
//	 * 
//	 * @return A list with join gateways of the given type. 
//	 */
//	public List<Gateway> getJoinGateways(String type);
//
//	/**
//	 * Collects the event-based exclusive gateways that are contained in 
//	 * the container. An event-based exclusive gateway must have more than one 
//	 * outgoing transition.
//	 * 
//	 * @return A list with event-based exclusive gateways. 
//	 */
//	public List<Gateway> getEventBasedExclusiveDecisionGateways();
//
//	/**
//	 * Collects the data-based exclusive gateways that are contained in 
//	 * the container. An event-based exclusive gateway must have more than one 
//	 * outgoing transition.
//	 * 
//	 * @return A list with data-based exclusive gateways. 
//	 */
//	public List<Gateway> getDataBasedExclusiveDecisionGateways();
//
//	/**
//	 * Collects the gateways that are contained in the container.
//	 * These are all gateways independent from the number of 
//	 * incoming or outgoing transitions.
//	 * 
//	 * @return A list with the gateways of the container.
//	 */
//	public List<Gateway> getGateways();

}