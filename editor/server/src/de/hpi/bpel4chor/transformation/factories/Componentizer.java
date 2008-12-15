package de.hpi.bpel4chor.transformation.factories;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import de.hpi.bpel4chor.model.Container;
import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.activities.EndEvent;
import de.hpi.bpel4chor.model.activities.Gateway;
import de.hpi.bpel4chor.model.activities.Handler;
import de.hpi.bpel4chor.model.activities.IntermediateEvent;
import de.hpi.bpel4chor.model.activities.ReceiveTask;
import de.hpi.bpel4chor.model.activities.Scope;
import de.hpi.bpel4chor.model.activities.StartEvent;
import de.hpi.bpel4chor.model.activities.Task;
import de.hpi.bpel4chor.model.connections.Transition;
import de.hpi.bpel4chor.util.ListUtil;
import de.hpi.bpel4chor.util.Output;

/**
 * The Componentizer tries to find components within the activities of a 
 * container that match defined patterns. A Componentizer instance can 
 * only be used for the specified container.
 */
public class Componentizer {
	
	private Container container = null;
	private Diagram diagram = null;
	private Output output;
	
	/**
	 * Constructor. Initializes the Componentizer with container to examine
	 *  and the diagram the container belongs to.
	 *  
	 * @param diagram  	The diagram, the container belongs to.
	 * @param container The container, that may contain components
	 * @param output    The output to print errors to.
	 */
	public Componentizer(Diagram diagram, Container container, Output output) {
		this.diagram = diagram;
		this.container = container;
		this.output = output;
	}
	
	/**
	 * <p>Computes all transitions between the given activities.</p>
	 * 
	 * <p> These are transitions, that originate from the source activity, 
	 * that lead to the target activity or that have a source or target 
	 * contained in the activities list.</p>
	 * 
	 * <p>The list does not contain duplicated entries.</p>
	 * 
	 * @param activities activities that are source or target of a transition.
	 * @param source     source activity of a transition
	 * @param target	 target activitiy of a transition
	 *  
	 * @return List with transitions originating or leading to
	 * the given activities.
	 */
	private List<Transition> computeTransitions(List<Activity> activities, 
			Activity source, Activity target) {
		HashSet<Transition> set = new HashSet<Transition>();
		for (Iterator<Activity> it = activities.iterator(); it.hasNext();) {
			Activity act = it.next();
			List<Transition> sourceFor = null;
			List<Transition> targetFor = null;
			if (act.equals(source)) {
				// if source is also contained in activities
				sourceFor = act.getSourceFor();
			} else if (act.equals(target)) {
				// if target is also contained in activities
				targetFor = act.getTargetFor();
			} else {
				sourceFor = act.getSourceFor();
				targetFor = act.getTargetFor();
			}
			if (sourceFor != null) {
				set.addAll(sourceFor);
			}
			if (targetFor != null) {
				set.addAll(targetFor);
			}
		}
		
		// add transitions from the source activity
		if (source.getSourceFor() != null) {
			set.addAll(source.getSourceFor());
		}
		
		// add transitions that lead to the target activity
		if (target.getTargetFor() != null) {
			set.addAll(target.getTargetFor());
		}
		return new ArrayList<Transition>(set);
	}
	
	/**
	 * Collects all error handlers that are connected with 
	 * attached error events of the activity.
	 * 
	 * @param activity the activity to get the error handlers for
	 * 
	 * @return List of found error handlers or null if the activity
	 * has no error events attached.
	 */
	private List<Handler> getErrorHandlers(Activity activity) {
		List<Handler> result = new ArrayList<Handler>();
		List<IntermediateEvent> errorEvents = 
			activity.getAttachedEvents(IntermediateEvent.TRIGGER_ERROR);
		if (errorEvents.isEmpty()) {
			return null;
		}
		for (Iterator<IntermediateEvent> itEvents = errorEvents.iterator(); itEvents.hasNext();) {
			Handler errorHandler = 
				itEvents.next().getConnectedHandler();
			if (errorHandler != null) {
				result.add(errorHandler);
			}
		}
		return result;
	}
	
	/**
	 * <p>Determines the sink object of the attached-error-events-pattern.</p>
	 * 
	 * <p>The sink object is defined as inclusive or exclusive merge gateway
	 * every error handler of the activity is connected with.</p>
	 * 
	 * @param activity  The activity, that may have error handlers. 
	 * 
	 * @return The sink object (inclusive of exlusive merge gateway) of the
	 * attached-events-pattern or null if such a pattern could not be found.
	 */
	private Gateway getAttachedErrorEventSinkObject(Activity activity) {
		List<Handler> errorHandlers = getErrorHandlers(activity);
		if ((errorHandlers == null) || errorHandlers.isEmpty()) {
			return null;
		}
		
		Gateway gateway = null;
		int counter = 0;
		List<Activity> predecessors = new ArrayList<Activity>();
		
		// the error handler that misses a gateway
		Handler missing = null;
		
		for (Iterator<Handler> itHandler = 
			errorHandlers.iterator(); itHandler.hasNext();) {
			Handler errorHandler = itHandler.next();
			
			// check if successor of handler is a gateway
			Activity target = errorHandler.getSuccessor();
			if (target instanceof Gateway) {
				Gateway targetGateway = (Gateway)target;
				
				// check if this gateway is equals to the gateways the other event handlers lead to
				if (targetGateway.getGatewayType().equals(Gateway.TYPE_OR) || 
						targetGateway.getGatewayType().equals(Gateway.TYPE_XOR)) {
					if (gateway == null) {
						gateway = targetGateway;
					} else if (!gateway.equals(targetGateway)) {
						this.output.addError("Each error handler must lead to the same gateway.", activity.getId());
						return null;
					}
					predecessors.add(errorHandler);
					counter++;
				} else {
					missing = errorHandler;
					break;
				}
			} else {
				missing = errorHandler;
				break;
			}
		}
		
		if (missing != null) {
			this.output.addError("The outgoing transition of this error handler " +
					" must lead to an inclusive or"+
					" exclusive merge gateway.", missing.getId());
			return null;
		}
		
		if (gateway == null) {
			return null;
		}

		// check if gateway is successor of activity
		if ((activity.getSuccessor() != null) && 
			activity.getSuccessor().equals(gateway)) {
			return gateway;
		}
		
		
		this.output.addError("The successor of this activity " +
				" must be the gateway with id "+ 
				gateway.getId(), activity.getId());
		this.output.addError("This gateway has to be the successor of the acitivity with id "+activity.getId(), gateway.getId());
		return null;
	}
	
	/**
	 * <p>Checks if the activity applies to an attached-error-events-pattern and creates 
	 * the appropriate component.</p>
	 * 
	 * <p>The activity applies to an attached-error-events-pattern if it has
	 * attached intermediate error events and all these events are connected
	 * with error handlers. The handlers again must lead to the same exclusive
	 * or inclusive merge gateway.</p>
	 * 
	 * <p>For a quasi pattern there can be other activities connected with the
	 * gateway. For a non-quasi pattern the handlers are the only activities
	 * connected with this gateway.</p>
	 * 
	 * @param activity	The activity that may apply to an attached-error-events
	 *  				pattern.
	 * @param quasi		True, if the pattern searched for is a quasi pattern,
	 * 					false otherwise.
	 * 
	 * @return A component of the type quasi-attached-events. The source object is 
	 * the activity that has the attached error events. The sink object is the 
	 * gateway, the error handlers are connected to. The component activities are the
	 * error events and error handlers. 
	 */
	private Component computeAttachedErrorEvents(Activity activity, boolean quasi) {
		List<IntermediateEvent> errorEvents = 
			activity.getAttachedEvents(IntermediateEvent.TRIGGER_ERROR);
		
		Gateway sinkGateway = getAttachedErrorEventSinkObject(activity);
		if (sinkGateway != null) {
			List<Activity> activities = new ArrayList<Activity>();
			activities.addAll(errorEvents);
			List<Handler> errorHandlers = getErrorHandlers(activity);
			activities.addAll(errorHandlers);
			if (quasi) {
				return new Component(
						Component.TYPE_QUASI_ATTACHED_EVENTS,activities,
						computeTransitions(activities, activity, sinkGateway),
						activity, sinkGateway);
			}
			
			// check if gateway has only incoming transitions from error handlers or the task
			List<Activity> predecessors = new ArrayList<Activity>();
			predecessors.addAll(errorHandlers);
			predecessors.add(activity);
			if (isOnlyPredecessor(sinkGateway, predecessors)) {
				return new Component(
						Component.TYPE_ATTACHED_EVENTS, activities, 
						computeTransitions(activities, activity, sinkGateway),
						activity, sinkGateway);
			}
		}
		return null;
	}
	
	/**
	 * Checks for each task and scope in the container if it is applicable 
	 * to an attached-error-events pattern using the method
	 * {@link #computeAttachedErrorEvents(Activity, boolean)}.
	 * 
	 * @param quasi True, if the pattern searched for is a quasi pattern,
	 * 				false otherwise.
	 * 
	 * @return The component matching the attached-error-events pattern or null,
	 * if no component was found that matches this pattern .
	 */
	private Component computeAttachedErrorEvents(boolean quasi) {
		List<Task> tasks = this.container.getTasks();
		for (Iterator<Task> it = tasks.iterator(); it.hasNext();) {
			Task task = it.next();
			Component comp = computeAttachedErrorEvents(task, quasi);
			if (comp != null) {
				return comp;
			}
		}
		
		List<Scope> scopes = this.container.getScopes();
		for (Iterator<Scope> it = scopes.iterator(); it.hasNext();) {
			Scope scope = it.next();
			Component comp = computeAttachedErrorEvents(scope, quasi);
			if (comp != null) {
				return comp;
			}
		}
		return null;
	}
	
	/**
	 * Checks if the activity is a successor of an event-based 
	 * decision gateway.
	 * 
	 * @param act 	The activity to examine
	 * 
	 * @return 		True, if the activity is the successor of an event-based
	 * 				decision gateway, false otherwise
	 */
	private boolean isSuccessorOfXORDecision(Activity act) {
		Activity pred = act.getPredecessor();
		if (pred instanceof Gateway) {
			Gateway gateway = (Gateway)pred;
			if ((gateway.getSplitType() != null) && 
					gateway.getSplitType().equals(Gateway.SPLIT_XOREVENT)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * <p>Computes the sequence activities starting from the given activity.</p>
	 * 
	 * <p>A sequence is defined as connected activities without a gateway, 
	 * start or end event between them.</p>
	 * 
	 * @param act The activity, the sequence should start
	 * 
	 * @return The list of sequence activities inclusive the given activity.
	 */
	private List<Activity> computeSequenceActivities(Activity act) {
		List<Activity> sequence = new ArrayList<Activity>();
		while (act != null) {
			if ((act instanceof Gateway) ||
					(act instanceof StartEvent) ||
						(act instanceof EndEvent)) {
				break;
			}
			sequence.add(act);
			act = act.getSuccessor();
		}
		return sequence;
	}
	
	/**
	 * <p>Computes a component that matches the sequence pattern.</p>
	 * 
	 * <p>For each activity the sequence activities are computed using
	 * the method {@link #computeSequenceActivities(Activity)}. After that the 
	 * sequence with the maximal size is choosen.</p> 
	 * 
	 * @return The maximal component that matches the sequence pattern. 
	 * The first activity is the source object and the last activity is the sink object. 
	 * The activities in between are contained in the activities list of the component. 
	 * If no sequence component was found the result is null. 
	 */
	private Component computeSequence() {
		List<Activity> maxSequence = null;
		for (Iterator<Activity> it = 
			this.container.getActivities().iterator(); it.hasNext();) {
			Activity act = it.next();
			if (!isSuccessorOfXORDecision(act)) {
				List<Activity> sequence = computeSequenceActivities(act);
				if (sequence.size() > 1) {
					if (maxSequence == null) {
						maxSequence = sequence;
					} else if (maxSequence.size() < sequence.size()) {
						maxSequence = sequence;
					}
				}
			}
		}
		if (maxSequence != null) {
			Activity source = maxSequence.get(0);
			Activity sink = maxSequence.get(maxSequence.size() - 1);
			maxSequence.remove(sink);
			maxSequence.remove(source);
			List<Transition> transitions = computeTransitions(maxSequence, source, sink);
			return new Component(Component.TYPE_SEQUENCE, maxSequence, transitions, source, sink);
		}
		return null;
	}
	
	/**
	 * <p>Collects the elements between the source and the target activity. 
	 * These are all activities (except gateways) that have a transition from 
	 * the source activity and a transition to the target activity.</p>
	 * 
	 * @param source 	the source activity
	 * @param target 	the target activity
	 * 
	 * @return A list of activities between the source and the target activity.
	 * If there is a gateway, a start or end event between the source and the target
	 * the result will be null. If there is a path from the source, that does not lead
	 * to the target activity, null will be returned, too.
	 */
	private List<Activity> getElementsBetween(Activity source, Activity target) {
		List<Activity> successors = null;
		if (source instanceof Gateway) {
			successors = ((Gateway)source).getSuccessors();
		} else if (source.getSuccessor() != null) {
			successors = new ArrayList<Activity>();
			successors.add(source.getSuccessor());
		} else {
			return null;
		}
		
		// check if all successors are valid and return null otherwise
		for (Iterator<Activity> it = successors.iterator(); it.hasNext();) {
			Activity act = it.next();
			if (act instanceof Gateway) {
				if (!act.equals(target)) {
					// no direct path from successor to target
					return null;
				}
				continue;
			} else if ((act instanceof StartEvent) || 
				(act instanceof EndEvent)) {
				return null;
			} else if (act.getSuccessor().equals(target)) {
				continue;
			}
			return null;
		}
		return successors;	
	}
	
	/**
	 * <p>Determines the element between the source and the target activity. 
	 * This is an activity (except gateways) that has a transition from 
	 * the source activity and a transition to the target activity.</p>
	 * 
	 * @param source 	the source activity
	 * @param target 	the target activity
	 * 
	 * @return The Activity between the source and the target activity. If
	 * there are multiple such Activities the result is null.
	 */
	private Activity getElementBetween(Gateway source, Gateway target) {
		Activity result = null;
		List<Activity> successors = source.getSuccessors();
		
		// check if all successors are valid and return null otherwise
		for (Iterator<Activity> it = successors.iterator(); it.hasNext();) {
			Activity act = it.next();
			if (!(act instanceof Gateway) && 
				(act.getSuccessor() != null) &&
				act.getSuccessor().equals(target)) {
				if (result != null) {
					return null;
				}
				result = act;
			}
		}
		return result;	
	}
	
	/**
	 * Checks if the given activities are the only successors of the activity.
	 * Activities in the list, that are not a successor of
	 * the given activity do not matter.
	 * 
	 * @param source the activity to check the successors for
	 * @param successors a list of activities containing at least all
	 * successors of the activity.
	 * 
	 * @return true, if all successors of the activity are contained in the list,
	 * false otherwise 
	 */
	private boolean isOnlySuccessors(Activity source, List<Activity> successors) {
		for (Iterator<Transition> it = source.getSourceFor().iterator(); it.hasNext();) {
			Transition trans = it.next();
			if (!successors.contains(trans.getTarget())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the activities in the list are the only predecessors of 
	 * the given activity. Activities in the list, that are not a predecessor of
	 * the given activity do not matter.
	 * 
	 * @param target the activity to check the predecessors for
	 * @param predecessors a list of activities containing at least all
	 * predecessors of the activity.
	 * 
	 * @return true, if all predecessors of the activity are contained in
	 * the list, false otherwise
	 */
	private boolean isOnlyPredecessor(Activity target, List<Activity> predecessors) {
		for (Iterator<Transition> it = target.getTargetFor().iterator(); it.hasNext();) {
			Transition trans = it.next();
			if (!predecessors.contains(trans.getSource())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Computes a component that matches a quasi flow pattern for 
	 * the given source and sink gateways:
	 * <ul>
	 * 	<li> source object x is a parallel fork gateway
	 * 	<li> sink object y is a parallel join gateway
	 * 	<li> X = out(x), activities the outgoing transitions of x lead to
	 * 	<li> Y = in(y), activities the incoming transitions to y come from
	 *  <li> Z = X intersect Y, X <> Y and Z containes more than one task or event
	 * </ul>
	 * 
	 * @param source the source object x of the component
	 * @param sink the sink object y of the component
	 * 
	 * @return The component that matches the quasi flow pattern with the given
	 * source and sink object. If no component was found the result is null.
	 */
	private Component computeQuasiFlow(Gateway source, Gateway sink) {
		List<Activity> X = source.getSuccessors();
		List<Activity> Y = sink.getPredecessors();
		
		// compute intersection
		List<Activity> Z = ListUtil.intersect(X, Y);
		
		if (!ListUtil.isEqual(X, Y)) {
			int counter = 0;
			// count the activities that are not gateways
			for (Iterator it = Z.iterator(); it.hasNext();) {
				if (!(it.next() instanceof Gateway)) {
					counter++;
				}
			}
			
			if (counter > 1) {
				List<Activity> quasiActivities = new ArrayList<Activity>(X);
				quasiActivities.addAll(Y);
				quasiActivities.remove(Z);
				return new Component(
						Component.TYPE_QUASI_FLOW, 
						Z, computeTransitions(Z, source, sink),
						source, sink);
			}
		}
		return null;
	}
	
	/**
	 * Computes a component that matches a flow pattern for 
	 * the given source and sink gateways:
	 * <ul>
	 * <li> source object is a parallel split gateway
	 * <li> target object is a parralel join gateway
	 * <li> all inner objects of the component are tasks or intermediate events
     * <li> all inner objects have source has source and sink as target.
	 *</ul>
	 * 
	 * @param source the source object of the component
	 * @param sink the sink object of the component
	 * 
	 * @return The component that matches the flow pattern with the given
	 * source and sink object. If no component was found the result is null.
	 */
	private Component computeFlow(Gateway source, Gateway sink) {
		List<Activity> result = getElementsBetween(source, sink);
		if (result != null) {
			List<Activity> activities = new ArrayList<Activity>();
			activities.addAll(result);
			
			// this condition must be fulfilled too
			if(isOnlyPredecessor(sink, result) && 
					(isOnlySuccessors(source, result))) {
				return new Component(
						Component.TYPE_FLOW, 
						activities, 
						computeTransitions(activities, source, sink),
						source, sink);
			}
		 }
		 return null;
	}
	
	/**
	 * Checks for each parallel split and parallel or inclusive join gateway, if 
	 * there exists a component matching a (quasi) flow pattern. With these 
	 * gateways as source and sink object.
	 * 
	 * @param quasi True, if the component that should be searched for 
	 * is a quasi component.
	 *   
	 * @return A component that matches a (quasi) flow pattern.
	 */
	private Component computeFlow(boolean quasi) {
		 List<Gateway> andSplits = 
			 this.container.getSplitGateways(Gateway.TYPE_AND);
		 List<Gateway> andJoins = 
			 this.container.getJoinGateways(Gateway.TYPE_AND);
		 List<Gateway> orJoins = 
			 this.container.getJoinGateways(Gateway.TYPE_OR);
		 
		 if (andSplits.size()>0 && 
				 (andJoins.size()>0 || orJoins.size()>0)) {
			 for(Iterator<Gateway> itSplit = 
				 andSplits.iterator(); itSplit.hasNext();) {
				 
				 Gateway andSplit = itSplit.next();
				 
				 for(Iterator<Gateway> itAnd = andJoins.iterator(); itAnd.hasNext();) {
					 Gateway andJoin = itAnd.next();
					 Component comp = null;
					 if (quasi) {
						 comp = computeQuasiFlow(andSplit, andJoin);
					 } else {
						 comp = computeFlow(andSplit, andJoin);
					 }
					 if (comp != null) {
						 return comp;
					 }
				 }
				 
				 for(Iterator<Gateway> itOR = orJoins.iterator(); itOR.hasNext();) {
					 Gateway orJoin = itOR.next();
					 Component comp = null;
					 if (quasi) {
						 comp = computeQuasiFlow(andSplit, orJoin);
					 } else {
						 comp = computeFlow(andSplit, orJoin);
					 }
					 if (comp != null) {
						 return comp;
					 }
				 }
			 }
		 }
		 return null;
	}

	/**
	 * Checks for each inclusive split and inclusive join gateway, if 
	 * there exists a component matching a (quasi) special flow pattern. With these 
	 * gateways as source and sink object.
	 * 
	 * @param quasi True, if the component that should be searched for 
	 * is a quasi component.
	 *   
	 * @return A component that matches a (quasi)special flow pattern.
	 */
	private Component computeSpecialFlow(boolean quasi) {
		 List<Gateway> orSplits = 
			 this.container.getSplitGateways(Gateway.TYPE_OR);
		 List<Gateway> orJoins = 
			 this.container.getJoinGateways(Gateway.TYPE_OR);
		 
		 if ((orSplits.size()>0) && (orJoins.size()>0)) {
			 for(Iterator<Gateway> itSplit = 
				 orSplits.iterator(); itSplit.hasNext();) {
				 
				 Gateway orSplit = itSplit.next();
				 
				 for(Iterator<Gateway> itOr = 
					 orJoins.iterator(); itOr.hasNext();) {
					 
					 Gateway orJoin = itOr.next();
					 Component comp = null;
					 if (quasi) {
						 comp = computeQuasiFlow(orSplit, orJoin);
					 } else {
						 comp = computeFlow(orSplit, orJoin);
					 }
					 if (comp != null) {
						 if (quasi) {
							 comp.setType(Component.TYPE_QUASI_SPECIAL_FLOW);
						 } else {
							 comp.setType(Component.TYPE_SPECIAL_FLOW);
						 }
						 return comp;
					 }
				 }
			 }
		 }
		 return null;
	}
	
	/**
	 * Computes a component that matches an if pattern for 
	 * the given source and sink gateways:
	 * <ul>
	 * <li> source object is an exclusive split gateway
	 * <li> target object is an inclusive or exclusive merge gateway
	 * <li> all inner objects of the component are tasks or intermediate events
     * <li> all inner objects have source has source and sink as target.
	 *</ul>
	 *
	 * The component matches a quasi-pattern, if there are other activities
	 * from outside the component leading to sink object.
	 * 
	 * @param source the source object of the component
	 * @param sink the sink object of the component
	 * @param quasi True, if the component that should be searched for 
	 * is a quasi component.
	 * 
	 * @return The component that matches the if pattern with the given
	 * source and sink object. If no component was found the result is null.
	 */
	private Component computeIf(Gateway source, Gateway sink, boolean quasi) {
		List<Activity> result = getElementsBetween(source, sink);
		if (result != null) {
			List<Activity> activities = new ArrayList<Activity>();
			activities.addAll(result);
			if (quasi) {
				// this condition is enough
				// add all activities, that have a transition to the target gateway				
				return new Component(
						Component.TYPE_QUASI_IF, 
						activities, computeTransitions(activities, source, sink),
						source, sink);
			} else {
				// this condition must be fulfilled too
				activities.add(sink);
				if (isOnlySuccessors(source, activities)) {
					activities.remove(sink);
					activities.add(source);
					if (isOnlyPredecessor(sink, activities)) {
						return new Component(
								Component.TYPE_IF, 
								result, 
								computeTransitions(result, source, sink),
								source, sink);
					}
				}
			}
		 }
		 return null;
	}
	
	/**
	 * Checks for each exclusive split and exclusive or inclusive join gateway, if 
	 * there exists a component matching a (quasi) if pattern. With these 
	 * gateways as source and sink object.
	 * 
	 * @param quasi True, if the component that should be searched for 
	 * is a quasi component.
	 *   
	 * @return A component that matches a (quasi) if pattern.
	 */
	private Component computeIf(boolean quasi) {
		List<Gateway> xorSplits = 
			this.container.getSplitGateways(Gateway.TYPE_XOR);
		List<Gateway> xorJoins = 
			this.container.getJoinGateways(Gateway.TYPE_XOR);
		List<Gateway> orJoins = 
			this.container.getJoinGateways(Gateway.TYPE_OR);
		
		if (xorSplits.size()>0 && (xorJoins.size()>0 || orJoins.size()>0)) {
			 for(Iterator<Gateway> itSplit = xorSplits.iterator(); itSplit.hasNext();) {
				 Gateway xorSplit = itSplit.next();
				 if ((xorSplit.getSplitType() != null) && 
						 xorSplit.getSplitType().equals(Gateway.SPLIT_XOREVENT)) {
					 continue;
				 }
				 
				 for(Iterator<Gateway> itAnd = xorJoins.iterator(); itAnd.hasNext();) {
					 Gateway xorJoin = itAnd.next();
					 Component comp = computeIf(xorSplit, xorJoin, quasi);
					 if (comp != null) {
						 return comp;
					 }
				 }
				 
				 for(Iterator<Gateway> itOR = orJoins.iterator(); itOR.hasNext();) {
					 Gateway orJoin = itOR.next();
					 Component comp = computeIf(xorSplit, orJoin, quasi);
					 if (comp != null) {
						 return comp;
					 }
				 }
			 }
		}
		return null;
	}
	
	/**
	 * Checks if the xorEventGateway matches to a source gateway for a pick pattern.
	 * This means that all its outgoing transitions lead to intermediate message events, 
	 * intermediate timer events or non-looping receive tasks.
	 * 
	 * @param xorEventGateway the gateway that should be checked 
	 * 
	 * @return The events and tasks, that are successors of the given gateway, 
	 * if the gateway matches the source gateway of a pick pattern.
	 * Null, otherwise.   
	 */
	private List<Activity> getPickBranches(Gateway xorEventGateway) {
		List<Activity> result = new ArrayList<Activity>();
		for (Iterator<Transition> it = xorEventGateway.getSourceFor().iterator(); it.hasNext();) {
			Activity act = it.next().getTarget();
			if (act instanceof IntermediateEvent) {
				IntermediateEvent event = (IntermediateEvent)act;
				if (event.getTriggerType().equals(IntermediateEvent.TRIGGER_MESSAGE) || 
						(event.getTriggerType().equals(IntermediateEvent.TRIGGER_TIMER))) {
					result.add(act);
				}
			} else if (act instanceof ReceiveTask) {
				if (act.getLoop() == null) {
					result.add(act);
				}
			} else {
				return null;
			}
		}
		return result;
	}
	
	/**
	 * Computes a component that matches a pick pattern for 
	 * the given source and sink gateways:
	 * <ul>
	 * <li> source object is an exclusive split gateway
	 * <li> target object is an inclusive or exclusive merge gateway
	 * <li> all inner objects of the component are tasks or intermediate events
     * <li> all objects X immediately following the source object are intermediate message events,
     * intermediate timer events or receive tasks (pick branches).
     * <li> the objects following these pick branches lead to the sink object
	 *</ul>
	 *
	 * The component matches a pick-pattern, if there are other activities
	 * from outside the component leading to sink object.
	 * 
	 * @param source the source object of the component
	 * @param sink the sink object of the component
	 * @param quasi True, if the component that should be searched for 
	 * is a quasi component.
	 * 
	 * @return The component that matches the pick-pattern with the given
	 * source and sink object. If no component was found the result is null.
	 */
	private Component computePick(Gateway source, Gateway sink, boolean quasi) {
		List<Activity> pickBranches = getPickBranches(source);
		if (pickBranches == null) {
			return null;
		}
		
		List<Activity> between = new ArrayList<Activity>();
		for (Iterator<Activity> itPick = pickBranches.iterator(); itPick.hasNext();) {
			Activity act = itPick.next();
			
			// compute elements between pick elements and join gateway
			List<Activity> result = getElementsBetween(act, sink);
			if (result != null) {
				between.addAll(result);
			} else {
				return null;
			}
		}
		
		// join gateway can be reached from all pick gateways
		// elements in between represent the elements between 
		// the pick branches and the join gateway
		List<Activity> predecessors = new ArrayList<Activity>();
		predecessors.addAll(pickBranches);
		predecessors.addAll(between);
		if (quasi) {
			return new Component(
					Component.TYPE_QUASI_PICK, predecessors,
					computeTransitions(predecessors, source, sink), 
					source, sink);
		}
		if (isOnlyPredecessor(sink, predecessors)) {
			return new Component(
					Component.TYPE_PICK, predecessors, 
					computeTransitions(predecessors, source, sink), 
					source, sink);
		}
		return null;
	}
	
	/**
	 * Checks for each event-based exclusive split and exclusive 
	 * or inclusive join gateway, if there exists a component 
	 * matching a (quasi) pick-pattern. With these gateways as source
	 * and sink object.
	 * 
	 * @param quasi True, if the component that should be searched for 
	 * is a quasi component.
	 *   
	 * @return A component that matches a (quasi) pick-pattern.
	 */
	private Component computePick(boolean quasi) {
		List<Gateway> xorSplits = 
			this.container.getEventBasedExclusiveDecisionGateways();
		List<Gateway> joins = this.container.getJoinGateways(Gateway.TYPE_XOR);
		joins.addAll(this.container.getJoinGateways(Gateway.TYPE_OR));
		
		if (xorSplits.size()>0 && (joins.size()>0)) {
			for (Iterator<Gateway> it = xorSplits.iterator(); it.hasNext();) {
				Gateway xorSplit = it.next();
				for (Iterator<Gateway> itJoin = 
					joins.iterator(); itJoin.hasNext();) {
					
					Gateway join = itJoin.next();
					Component comp = computePick(xorSplit, join, quasi);
					if (comp != null) {
						return comp;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks for exclusive split and exclusive join gateway, if there exists
	 * a component matching a while-pattern. With these gateways as source
	 * and sink object.
	 * 
	 * A while pattern is defined as:
	 * <ul>
	 * <li> source object is an exclusive split gateway
	 * <li> target object is an exclusive merge gateway
	 * <li> there are only three transitions:
	 * <ul>
	 * 	<li> from source to sink object
	 *  <li> from sink object to a task or event
	 *  <li> from this task or event to the source object</li>
	 * </ul>
	 * </ul>
	 *   
	 * @return A component that matches a while-pattern.
	 */
	private Component computeWhile() {
		List<Gateway> xorSplits = 
			this.container.getDataBasedExclusiveDecisionGateways();
		List<Gateway> joins = this.container.getJoinGateways(Gateway.TYPE_XOR);
		joins.addAll(this.container.getJoinGateways(Gateway.TYPE_OR));
		
		for (Iterator<Gateway> itSplits = xorSplits.iterator(); itSplits.hasNext();) {
			Gateway split = itSplits.next();
			for (Iterator<Gateway> itJoin = joins.iterator(); itJoin.hasNext();) {
				Gateway join = itJoin.next();
				
				// direct transition between join and split
				if (join.getTransitionTo(split) == null) {
					continue;
				}
				
				// one element between split and join
				Activity result = getElementBetween(split, join);
				if (result == null) {
					continue;
				}
				
				// XOR-Split has only two outgoing transitions and
				// XOR-Merge has only two incoming transitions
				if ((split.getSourceFor().size() == 2) && 
						(join.getTargetFor().size() == 2)) {
					// check if transition from XOR-Merge to XOR-Split
					Transition trans = split.getTransitionTo(result);
					if ((trans == null) ||
							(trans.getConditionType() == null) || 
							(!trans.getConditionType().equals(Transition.TYPE_EXPRESSION))) {
						this.output.addError("The outgoing transitions should define a transition condition.", split.getId());
						break;
					}
					
					List<Activity> act = new ArrayList<Activity>();
					act.add(result);
					return new Component(Component.TYPE_WHILE, act,
							computeTransitions(act, join, split),
							join, split);
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks for exclusive split and exclusive join gateway, if there exists
	 * a component matching a repeat-pattern. With these gateways as source
	 * and sink object.
	 * 
	 * A repeat-pattern is defined as:
	 * <ul>
	 * <li> source object is an exclusive split gateway
	 * <li> target object is an exclusive merge gateway
	 * <li> there are only three transitions:
	 * <ul>
	 * 	<li> from source object to a task or event
	 *  <li> from this task or event to the sink object
	 *  <li> from sink object to source object
	 * </ul>
	 * </ul>
	 *   
	 * @return A component that matches a repeat-pattern.
	 */
	private Component computeRepeat() {
		List<Gateway> xorSplits = 
			this.container.getDataBasedExclusiveDecisionGateways();
		List<Gateway> joins = this.container.getJoinGateways(Gateway.TYPE_XOR);
		joins.addAll(this.container.getJoinGateways(Gateway.TYPE_OR));
		
		for (Iterator<Gateway> itSplits = xorSplits.iterator(); itSplits.hasNext();) {
			Gateway split = itSplits.next();
			for (Iterator<Gateway> itJoin = joins.iterator(); itJoin.hasNext();) {
				Gateway join = itJoin.next();
				
				// direct transition between split and join
				Transition trans = split.getTransitionTo(join);
				if (trans == null) {
					continue;
				}
				
				// one activity between join and split
				Activity result = getElementBetween(join, split);
				if (result == null) {
					continue;
				}
				
				// XOR-Split has only two outgoing transitions and
				// XOR-Merge has only two incoming transitions
				if ((split.getSourceFor().size() == 2) && 
						(join.getTargetFor().size() == 2)) {
					
					// check if transition from XOR-split to XOR-merge with condition expression
					if ((trans.getConditionType() == null) || 
							(!trans.getConditionType().equals(Transition.TYPE_EXPRESSION))) {
						this.output.addError("The transition should define a transition condition.", trans.getId());
						break;
					}
					
					List<Activity> act = new ArrayList<Activity>();
					act.add(result);
					
					List<Transition> transitions = computeTransitions(act, join, split);
					// add transition from split to join too
					transitions.add(trans);
					return new Component(Component.TYPE_REPEAT, act,
							transitions,
							join, split);
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks for exclusive split and exclusive join gateway, if there exists
	 * a component matching a repeat-while-pattern. With these gateways as source
	 * and sink object.
	 * 
	 * A repeat-while-pattern is defined as:
	 * <ul>
	 * <li> source object is an exclusive split gateway
	 * <li> target object is an exclusive merge gateway
	 * <li> there are only four transitions:
	 * <ul>
	 * 	<li> from source object to a task or event x
	 *  <li> from x to the sink object
	 *  <li> from the sink object to another task or event y
	 *  <li> from y to the source object
	 * </ul>
	 * </ul>
	 * 
	 * The elements between source and sink object can 
	 * not be communication activities. (unique naming gets lost during mapping)
	 *   
	 * @return A component that matches a repeat-while-pattern.
	 */
	private Component computeRepeatWhile() {
		List<Gateway> xorSplits = 
			this.container.getDataBasedExclusiveDecisionGateways();
		List<Gateway> joins = this.container.getJoinGateways(Gateway.TYPE_XOR);
		joins.addAll(this.container.getJoinGateways(Gateway.TYPE_OR));
		
		for (Iterator<Gateway> itSplits = xorSplits.iterator(); itSplits.hasNext();) {
			Gateway split = itSplits.next();
			for (Iterator<Gateway> itJoin = joins.iterator(); itJoin.hasNext();) {
				Gateway join = itJoin.next();
				
				Activity resultFrom = getElementBetween(join, split);
				if (resultFrom == null) {
					// no path from XOR-merge to XOR-split with one element between
					// continue search
					continue;
				}

				// pattern is not applicable to communicating activities
				// connected with message flow
				if ((this.diagram.getMessageFlowsWithTarget(resultFrom.getId()).size() > 0) || 
						(this.diagram.getMessageFlowWithSource(resultFrom.getId()) != null)) {
					return null;
				}
				
				Activity resultTo = getElementBetween(split, join);
				if (resultTo == null) {
					// no path from XOR-split to XOR-merge with one element between
					// continue search
					continue;
				}
				
				// XOR-Split has only two outgoing transitions and
				// XOR-Merge has only two incoming transitions
				if ((split.getSourceFor().size() == 2) && 
						(join.getTargetFor().size() == 2)) {
					
					Transition trans = split.getTransitionTo(resultTo);
					if ((trans == null) ||
							(trans.getConditionType() == null) || 
							(!trans.getConditionType().equals(Transition.TYPE_EXPRESSION))) {
						this.output.addError("The outgoing transitions should define a transition condition.", split.getId());
						break;
					}
					
					List<Activity> act = new ArrayList<Activity>();
					act.add(resultTo);
					act.add(resultFrom);
					return new Component(Component.TYPE_REPEAT_WHILE, act,
							computeTransitions(act, join, split),
							join, split);
				}
			}
		}
		return null;
	}
	
	/**
	 * Collects all activities between source (exclude) and target (exclude).
	 * 
	 * @param source The source activity
	 * @param target The target activity
	 * 
	 * @return A Set with all activities between the source and target activity.
	 * Null, if there is a path that does not lead to the target or if a 
	 * path contains a cycle. 
	 */
	private Set<Activity> getActivitiesFromTo(Activity source, Activity target) {
		Set<Activity> activities = new HashSet<Activity>();
		
		List<Activity> path = new ArrayList<Activity>();
		path.add(source);
		Set<Activity> pathsFound = getActivitiesFromTo(source, target, path);
		if (pathsFound != null) {
			pathsFound.remove(source);
			activities.addAll(pathsFound);
		} else {
			return null;
		}
		return activities;
	}

	/**
	 * Calculates the activities on all the paths (list of activities)
	 * form source to target. 
	 * Only returns the activities if every path from the source leads
	 * to the target and the path does not contain a cycle.
	 * 
	 * @param source The source activity
	 * @param target The target activity
	 * @param path   An already initialized list containing the activities that were already
	 * visited on this path
	 * 
	 * @return All activities on all the paths (list of activities)
	 * form source to target. Null, if there is a path that does not lead to
	 * the target or if a path contains a cycle.   
	 */
	private Set<Activity> getActivitiesFromTo(
			Activity source, Activity target, final List<Activity> path) {
		Set<Activity> activities = new HashSet<Activity>();
		
		for (Iterator<Transition> it = source.getSourceFor().iterator(); it.hasNext();) {
			Activity nextSource = it.next().getTarget();
			if ((nextSource instanceof EndEvent) ||
					(nextSource == null) || 
					path.contains(nextSource)) {
				// path does not lead to the target activity
				// or contains a cycle
				return null;
			} else if (nextSource.equals(target)){
				// path does lead to the target activity
				activities.addAll(path);
			} else {
				// add element to the path that is examined in this step
				// and search for the next element
				path.add(nextSource);
				Set<Activity> pathsFound = getActivitiesFromTo(nextSource, target, path);
				if (pathsFound != null) {
					activities.addAll(pathsFound);
					path.remove(nextSource);
				} else {
					return null;
				}
			}
		}
		return activities;
	}
	
	/**
	 * Checks if all successors of the gateway are contained
	 * in the given Set.
	 * 
	 * @param gateway 		The gateway to check
	 * @param containedIn   The Set of activities the successors
	 * should be contained in.
	 *  
	 * @return True, if all successors of the gateway
	 * are contained in the given Set, false otherwise.
	 */
	private boolean gatewaySuccessorsContainedIn(
			Gateway gateway, Set<Activity> containedIn) {
		List<Activity> successors = gateway.getSuccessors();
		if (successors.size() > 1) {
			for (Iterator<Activity> it = successors.iterator(); it.hasNext();) {
				if (!containedIn.contains(it.next())) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Checks if all predecessors of the gateway are contained
	 * in the given Set.
	 * 
	 * @param gateway 		The gateway to check
	 * @param containedIn   The Set of activities the
	 * predecessors should be contained in.
	 *  
	 * @return True, if all predecessors of the gateway
	 * are contained in the given Set, false otherwise.
	 */
	private boolean gatewayPredecessorsContainedIn(
			Gateway gateway, Set<Activity> containedIn) {
		List<Activity> predecessors = gateway.getPredecessors();
		if (predecessors.size() > 1) {
			for (Iterator<Activity> it = predecessors.iterator(); it.hasNext();) {
				if (!containedIn.contains(it.next())) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Checks if all predecessors and successors of the gateway are contained
	 * in the given Set. If they are not contained in the set they must be 
	 * the source of the target gateway.
	 * 
	 * @param gateway 	  The gateway to be checked
	 * @param containedIn The Set of activities the successors and
	 *                    predecessors should be contained in.
	 * @param source      The source gateway of the component to be checked
	 * @param target      The target gateway of the componenet to be checked  
	 *  
	 * @return True, if all predecessors and successors of the gateway
	 * are contained in the given Set, false otherwise.
	 */
	private boolean completeGatewayContainedIn(
			Gateway gateway, Set<Activity> containedIn, Gateway source, Gateway target) {
		List<Activity> predecessors = gateway.getPredecessors();
		for (Iterator<Activity> it = predecessors.iterator(); it.hasNext();) {
			Activity act = it.next();
			if (!(containedIn.contains(act) || act.equals(source))) {
				return false;
			}
		}
		
		List<Activity> successors = gateway.getSuccessors();
		for (Iterator<Activity> it = successors.iterator(); it.hasNext();) {
			Activity act = it.next();
			if (!(containedIn.contains(act) || act.equals(target))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the activities contained in the set are allowed in 
	 * a generalized flow pattern. A generalized flow pattern only allows
	 * parallel gateways and inclusive merge gateways. Moreover all sources
	 * and targets of a gateway must be contained in the set.
	 * 
	 * @param set    The activities to be checked
	 * @param source The source gateway of the generalized flow pattern.
	 * @param target The target gateway of the generalized flow pattern.
	 * 
	 * @return True, if the activities contained in the set are allowed
	 *  in a generalized flow pattern, false otherwise.
	 */
	private boolean checkGeneralizedFlowAct(
			Set<Activity> set, Gateway source, Gateway target) {
		
		for (Iterator<Activity> it = set.iterator(); it.hasNext();) {
			Activity act = it.next();
			if (act instanceof Gateway) {
				Gateway gateway = (Gateway) act;				
				if (gateway.getGatewayType().equals(Gateway.TYPE_XOR)) {
					return false;
				} else if (gateway.getGatewayType().equals(Gateway.TYPE_OR) && 
					(gateway.getSourceFor().size() > 1)) {
						return false;
				} else if (!completeGatewayContainedIn(
						gateway, set, source, target)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Checks if the activities contained in the set are allowed in 
	 * a synchronzing process pattern. A synchronzing process pattern does not
	 * allow event-based decision gateways. Moreover all sources 
	 * and targets of a gateway must be contained in the set.
	 * 
	 * @param set    The activities to be checked
	 * @param source The source gateway of the synchronizing process component.
	 * @param target The target gateway of the synchronizing process component.
	 * 
	 * @return True, if the activities contained in the set are allowed
	 *  in a synchronzing process pattern, false otherwise.
	 */
	private boolean checkSynchronizingProcessAct(Set<Activity> set, Gateway source, Gateway target) {
		for (Iterator<Activity> it = set.iterator(); it.hasNext();) {
			Activity act = it.next();
			if (act instanceof Gateway) {
				Gateway gateway = (Gateway) act;				
				if (gateway.getGatewayType().equals(Gateway.TYPE_XOR) && 
						(gateway.getSplitType().equals(Gateway.SPLIT_XOREVENT))) {
					return false;
				} else if (!completeGatewayContainedIn(gateway, set, source, target)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Computes a minimal general flow pattern, which is defined as follows:
	 * <ul>
	 * 	<li> The component contains no cycles
	 * 	<li> All gateways in the component are either parallel fork, 
	 * parallel join or inclusive join gateways.
	 * <li> There is no other component contained in this component 
	 * </ul>
	 * 
	 * @return A generalized flow component or null if no generalized flow
	 * component was found.
	 */
	private Component computeGeneralizedFlow() { 
		List<Gateway> andSplits = 
			this.container.getSplitGateways(Gateway.TYPE_AND);
		List<Gateway> andJoins = 
			this.container.getJoinGateways(Gateway.TYPE_AND);
		andJoins.addAll(this.container.getJoinGateways(Gateway.TYPE_OR));
		
		int minCount = -1;
		Set<Activity> minActivities = null;
		Activity minStart = null;
		Activity minEnd = null;
		for (Iterator<Gateway> itSplit = 
			andSplits.iterator(); itSplit.hasNext(); ) {
			
			Gateway andSplit = itSplit.next(); 
			
			for (Iterator<Gateway> itJoin = 
				andJoins.iterator(); itJoin.hasNext();) {
				
				Gateway join = itJoin.next();
				Set<Activity> set = getActivitiesFromTo(andSplit, join);
				if (set != null) {
					int i = set.size();
					if (i <= 0) {
						// no component found for this join
						continue;
					}
					boolean found = checkGeneralizedFlowAct(set, andSplit, join);
					if (found) {
						found = gatewaySuccessorsContainedIn(andSplit,set);
					}
					if (found) {
						found = gatewayPredecessorsContainedIn(join, set);
					}
					
					if (found && ((minCount == -1) || (i < minCount))) {
						minCount = i;
						minActivities = set;
						minStart = andSplit;
						minEnd = join;
					}
				}
			}
		}
		
		// create component from activities
		if (minActivities == null) {
			return null;
		}
		List<Activity> activities = new ArrayList<Activity>(minActivities);
		activities.remove(minStart);
		activities.remove(minEnd);
		return new Component(
				Component.TYPE_GENERALISED_FLOW, activities,
				computeTransitions(activities, minStart, minEnd),
				minStart, minEnd);
	}
	
	/**
	 * Computes a minimal synchronizing process component.
	 * <ul>
	 * 	<li> The component contains no cycles
	 * 	<li> The component does not contain event-based decision gateways
	 * 	<li> The component is sound and safe (is not checked using petri-net semantics) 
	 * </ul>
	 * 
	 * @return A minimal process component or null if no minimal process component
	 * was found.
	 */
	private Component computeSynchronizingProcessComponent() { 
		List<Gateway> splits = 
			this.container.getSplitGateways(Gateway.TYPE_AND);
		splits.addAll(this.container.getSplitGateways(Gateway.TYPE_OR));
		splits.addAll(this.container.getDataBasedExclusiveDecisionGateways());
		List<Gateway> joins = this.container.getJoinGateways(null);	
		
		int minCount = -1;
		Set<Activity> minActivities = null;
		Activity minStart = null;
		Activity minEnd = null;
		for (Iterator<Gateway> itSplit = splits.iterator(); itSplit.hasNext(); ) {
			Gateway split = itSplit.next(); 
			
			for (Iterator<Gateway> itJoin = joins.iterator(); itJoin.hasNext();) {
				Gateway join = itJoin.next();
				Set<Activity> set = getActivitiesFromTo(split, join);
				if (set != null) {
					int i = set.size();
					if (i <= 0) {
						// no component found for this join
						continue;
					}
					boolean found = checkSynchronizingProcessAct(set, split, join);				
					if (found) {
						found = gatewaySuccessorsContainedIn(split,set);
					}
					if (found) {
						found = gatewayPredecessorsContainedIn(join, set);
					}
					
					if (found && ((minCount == -1) || (i < minCount))) {
						minCount = i;
						minActivities = set;
						minStart = split;
						minEnd = join;
					}
				}
			}
		}
		
		// create component from activities
		if (minActivities == null) {
			return null;
		}
		List<Activity> activities = new ArrayList<Activity>(minActivities);
		activities.remove(minStart);
		activities.remove(minEnd);
		return new Component(
				Component.TYPE_SYNCHRONIZING_PROCESS, activities,
				computeTransitions(activities, minStart, minEnd),
				minStart, minEnd);
	}
	
	/**
	 * Computes a quasi-structured component in the container.
	 * 
	 * @return the first quasi-structured component found or null
	 *  if no quasi component was found
	 */
	private Component computeQuasiStructured() {
		Component result = computeAttachedErrorEvents(true);
		if (result == null)
			result = computeFlow(true);
		if (result == null) {
			result = computeSpecialFlow(true);
		}
		if (result == null) {
			result = computeIf(true);
		}
		if (result == null) {
			result = computePick(true);
		}
		if (result == null) {
			result = computeSpecialFlow(true);
		}
		return result;
	}
	
	/**
	 * Computes a well-structured component in the container.
	 * 
	 * @return the first well-structured component found or null
	 *  if no well-structured component was found
	 */
	private Component computeWellStructured() {
		Component result = computeFlow(false);
		if (result == null) {
			result = computeSpecialFlow(false);
		}
		if (result == null) {
			result = computeIf(false);
		}
		if (result == null) {
			result = computePick(false);
		}
		if (result == null) {
			result = computeWhile();
		}
		if (result == null) {
			result = computeRepeat();
		}
		if (result == null) {
			result = computeRepeatWhile();
		}
		return result;
	}
	
	/**
	 * Returns the next component, that can be found in the container.
	 * 
	 * The search for components has a special order:
	 * <ul>
	 * 	<li> attached-events-pattern
	 * 	<li> sequence-pattern
	 * 	<li> other well-structured patterns
	 * 	<li> quasi-structured patterns
	 * 	<li> generalized flow pattern
	 * </ul>
	 * 
	 * @return The first pattern that was found regarding the search order or
	 *  null if no pattern was found.
	 */
	public Component getNextComponent() {
		Component result = computeAttachedErrorEvents(false);
		if (result == null) {
			result = computeSequence();
		}
		if (result == null) {
			// compute other well-structured components
			result = computeWellStructured();
		}
		
		if (result == null) {
			result = computeQuasiStructured();
		}
		
		if (result == null) {
			result = computeGeneralizedFlow();
		}
		
		if (result == null) {
			result = computeSynchronizingProcessComponent();
		}
		
		return result;
	}

}
