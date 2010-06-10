package de.hpi.bpel4chor.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.activities.EndEvent;
import de.hpi.bpel4chor.model.activities.Gateway;
import de.hpi.bpel4chor.model.activities.Handler;
import de.hpi.bpel4chor.model.activities.IntermediateEvent;
import de.hpi.bpel4chor.model.activities.Scope;
import de.hpi.bpel4chor.model.activities.StartEvent;
import de.hpi.bpel4chor.model.activities.Task;
import de.hpi.bpel4chor.model.connections.Transition;

/**
 * 
 * This is a container that holds activities and transitions.
 * A container is the base class for the Process, SubProcess and
 * Component classes. 
 */
public class Container {
	
	private List<Activity> activities = new ArrayList<Activity>();
	private List<Transition> transitions = new ArrayList<Transition>();
	private String id = null;
	
	/**
	 * Constructor.
	 */
	protected Container() {}
	
	/**
	 * Constructor. Initializes the container with the
	 * activities and transitions that the container should hold.
	 * 
	 * The id of the container must be set using {@link #setId(String)}.
	 * 
	 * @param activities  the activities of the container
	 * @param transitions the transitions of the container
	 */
	public Container(List<Activity> activities, List<Transition> transitions) {
		this.activities = activities;
		this.transitions = transitions;
	}
	
	/**
	 * 
	 * @return The id if the container. 
	 * If the id was not set, the result is null.
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * @return The activities of the container
	 */
	public List<Activity> getActivities() {
		return this.activities;
	}

	/**
	 * @return The transitions of the container
	 */
	public List<Transition> getTransitions() {
		return this.transitions;
	}
	
	/**
	 * Sets the id of the container.
	 * 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Adds an activity to the list of the container's activities.
	 * 
	 * @param activity The activity to add.
	 */
	public void addActivity(Activity activity) {
		this.activities.add(activity);
	}
	
	/**
	 * Removes an Activity from the container's activities.
	 * The transitions with this activity as source or target will be 
	 * removed from the container's transitions as well.
	 * 
	 * @param activity The activity to remove from the container
	 */
	public void removeActivity(Activity activity) {
		this.activities.remove(activity);
		
		// remove transition with activity as source or target
		for (Iterator<Transition> it = 
			activity.getSourceFor().iterator(); it.hasNext();) {
			Transition transition = it.next();
			removeTransition(transition);
			transition.getTarget().removeTargetFor(transition);
		}
		
		for (Iterator<Transition> it = 
			activity.getTargetFor().iterator(); it.hasNext();) {
			Transition transition = it.next();
			removeTransition(transition);
			transition.getSource().removeSourceFor(transition);
		}
	}
	
	/**
	 * Removes the activities from list of the container's
	 * activities. Transitions with these activities as source or
	 * target will be removed from the container's transitions as well.
	 * 
	 * @param activities The activities to remove from the container.
	 */
	public void removeActivities(List<Activity> activities) {
		for (Iterator<Activity> it = activities.iterator(); it.hasNext();) {
			removeActivity(it.next());
		}
	}
	
	/**
	 * Adds a transition to the list of the container's transitions.
	 * 
	 * @param transition The transition to add.
	 */
	public void addTransition(Transition transition) {
		this.transitions.add(transition);
	}
	
	/**
	 * Adds the transitions to the list of the container's transitions.
	 * 
	 * @param transitions The transitions to add.
	 */
	public void addTransitions(List<Transition> transitions) {
		this.transitions.addAll(transitions);
	}
	
	/**
	 * Removes a transition from the list of the container's transitions.
	 * Activities connected to the transition will not be removed from 
	 * the container.
	 * 
	 * @param transition The transition to remove.
	 */
	public void removeTransition(Transition transition) {
		this.transitions.remove(transition);
	}
	
	/**
	 * Removes a transitions from the list of the container's transitions.
	 * Activities connected to the transitions will not be removed from 
	 * the container.
	 * 
	 * @param transitions The transitions to remove.
	 */
	public void removeTransitions(List<Transition> transitions) {
		this.transitions.removeAll(transitions);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Container) {
			if (((Container)obj).getId().equals(this.id)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Collects the handlers of the given handler type,
	 * that are contained in the container.
	 * 
	 * @param handlerType The type of the handlers to collect.
	 * @return A list with the handlers of the container.
	 */
	public List<Handler> getHandlers(String handlerType) {
		List<Handler> result = new ArrayList<Handler>();
		for (Iterator<Activity> it = getActivities().iterator(); it.hasNext();) {
			Activity act = it.next();
			if (act instanceof Handler) {
				String type = ((Handler)act).getHandlerType();
				if (type.equals(handlerType)) {
					result.add((Handler)act);
				}
			}
		}
		return result;
	}
	
	/**
	 * Collects the start events that are contained in the container.
	 * 
	 * @return A list with start events of the container. 
	 */
	public List<StartEvent> getStartEvents() {
		List<StartEvent> result = new ArrayList<StartEvent>();
		
		for (Iterator<Activity> it = 
			this.activities.iterator(); it.hasNext();) {
			
			Activity activity = it.next();
			if (activity instanceof StartEvent) {
				result.add((StartEvent)activity);
			}
		}
		return result;
	}
	
	/**
	 * Collects the intermediate events that are contained in the container.
	 * 
	 * @param triggerType The trigger type of the events to collect. If null,
	 * all intermediate events are collected.
	 * 
	 * @return A list with intermediate events of the container. 
	 */
	public List<IntermediateEvent> getIntermediateEvents(String triggerType) {
		List<IntermediateEvent> result = new ArrayList<IntermediateEvent>();
		
		for (Iterator<Activity> it = 
			this.activities.iterator(); it.hasNext();) {
			
			Activity activity = it.next();
			if (activity instanceof IntermediateEvent) {
				IntermediateEvent event = (IntermediateEvent)activity;
				if (triggerType != null) {
					if (triggerType.equals((event.getTriggerType()))) {
						result.add(event);
					}
				} else {
					result.add(event);
				}
			}
		}
		return result;
	}
	
	/**
	 * Collects the end events that are contained in the container.
	 * 
	 * @return A list with end events of the container. 
	 */
	public List<EndEvent> getEndEvents() {
		List<EndEvent> result = new ArrayList<EndEvent>();
		
		for (Iterator<Activity> it = 
			this.activities.iterator(); it.hasNext();) {
			
			Activity activity = it.next();
			if (activity instanceof EndEvent) {
				result.add((EndEvent)activity);
			}
		}
		return result;
	}
	
	/**
	 * Collects the scopes that are contained in the container.
	 * 
	 * @return A list with scopes of the container. 
	 */
	public List<Scope> getScopes() {
		List<Scope> result = new ArrayList<Scope>();
		
		for (Iterator<Activity> it = 
			this.activities.iterator(); it.hasNext();) {
			
			Activity activity = it.next();
			if (activity instanceof Scope) {
				result.add((Scope)activity);
			}
		}
		return result;
	}
	
	/**
	 * Collects the tasks that are contained in the container.
	 * 
	 * @return A list with tasks of the container. 
	 */
	public List<Task> getTasks() {
		List<Task> result = new ArrayList<Task>();
		
		for (Iterator<Activity> it = 
			this.activities.iterator(); it.hasNext();) {
			
			Activity activity = it.next();
			if (activity instanceof Task) {
				result.add((Task)activity);
			}
		}
		return result;
	}
	
	/**
	 * Collects the split gateways of the given type that are contained in 
	 * the container. A split gateway is a gateway that has more than one 
	 * outgoing transition.
	 * 
	 * @param type The type of the split gateway ({@link Gateway#TYPE_AND},
	 * {@link Gateway#TYPE_OR} or {@link Gateway#TYPE_XOR})
	 * 
	 * @return A list with split gateways of the given type. 
	 */
	public List<Gateway> getSplitGateways(String type) {
		List<Gateway> result = new ArrayList<Gateway>();
		
		for (Iterator<Activity> it = 
			this.activities.iterator(); it.hasNext();) {
			
			Activity activity = it.next();
			if (activity instanceof Gateway) {
				Gateway gateway = (Gateway)activity;
				if (gateway.getSourceFor().size() > 1) {
					if (type == null) {
						result.add(gateway);
					} else if (gateway.getGatewayType().equals(type)) {
						result.add(gateway);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Collects the join gateways of the given type that are contained in 
	 * the container. A join gateway is a gateway that has more than one 
	 * incoming transition.
	 * 
	 * @param type The type of the split gateway ({@link Gateway#TYPE_AND},
	 * {@link Gateway#TYPE_OR} or {@link Gateway#TYPE_XOR})
	 * 
	 * @return A list with join gateways of the given type. 
	 */
	public List<Gateway> getJoinGateways(String type) {
		List<Gateway> result = new ArrayList<Gateway>();
		
		for (Iterator<Activity> it = 
			this.activities.iterator(); it.hasNext();) {
			
			Activity activity = it.next();
			if (activity instanceof Gateway) {
				Gateway gateway = (Gateway)activity;
				if (gateway.getTargetFor().size() > 1) {
					if (type == null) {
						result.add(gateway);
					} else if (gateway.getGatewayType().equals(type)) {
						result.add(gateway);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Collects the event-based exclusive gateways that are contained in 
	 * the container. An event-based exclusive gateway must have more than one 
	 * outgoing transition.
	 * 
	 * @return A list with event-based exclusive gateways. 
	 */
	public List<Gateway> getEventBasedExclusiveDecisionGateways() {
		List<Gateway> result = new ArrayList<Gateway>();
		
		for (Iterator<Activity> it = 
			this.activities.iterator(); it.hasNext();) {
			
			Activity activity = it.next();
			if (activity instanceof Gateway) {
				Gateway gateway = (Gateway)activity;
				if (gateway.getSourceFor().size() > 1) {
					if (gateway.getGatewayType().equals(Gateway.TYPE_XOR) && 
							(gateway.getSplitType().equals(Gateway.SPLIT_XOREVENT))) {
						result.add(gateway);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Collects the data-based exclusive gateways that are contained in 
	 * the container. An event-based exclusive gateway must have more than one 
	 * outgoing transition.
	 * 
	 * @return A list with data-based exclusive gateways. 
	 */
	public List<Gateway> getDataBasedExclusiveDecisionGateways() {
		List<Gateway> result = new ArrayList<Gateway>();
		
		for (Iterator<Activity> it = 
			this.activities.iterator(); it.hasNext();) {
			
			Activity activity = it.next();
			if (activity instanceof Gateway) {
				Gateway gateway = (Gateway)activity;
				if (gateway.getSourceFor().size() > 1) {
					if (gateway.getGatewayType().equals(Gateway.TYPE_XOR) && 
							(gateway.getSplitType().equals(Gateway.SPLIT_XORDATA))) {
						result.add(gateway);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Collects the gateways that are contained in the container.
	 * These are all gateways independent from the number of 
	 * incoming or outgoing transitions.
	 * 
	 * @return A list with the gateways of the container.
	 */
	public List<Gateway> getGateways() {
		List<Gateway> result = new ArrayList<Gateway>();
		
		for (Iterator<Activity> it = 
			this.activities.iterator(); it.hasNext();) {
			
			Activity activity = it.next();
			if (activity instanceof Gateway) {
				result.add((Gateway)activity);
			}
		}
		return result;
	}
}
