package de.hpi.bpel4chor.transformation.factories;

import java.util.Iterator;
import java.util.List;
import de.hpi.bpel4chor.model.Container;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.connections.Transition;

/**
 * A Component can be determined in the sequence flow and represents components
 * according to the definition in the Ouyang et al. paper and the diploma
 * thesis. There are different types of components defined:<br> 
 * 
 * <b>Well-structured components:</b> 
 * <ul> 
 * 	<li>attached events 
 * <li>sequence 
 * <li>flow 
 * <li>special flow (like flow with or-split and or-join) 
 * <li>if 
 * <li>pick 
 * <li>while 
 * <li>repeat 
 * <li>repeat-while 
 * </ul> 
 * 
 * <b>Quasi-structured components:</b> 
 * <ul> 
 * 	<li>quasi attached events 
 *  <li>quasi flow 
 *  <li>quasi special flow (like flow with or-split and or-join) 
 *  <li>quasi if 
 *  <li>quasi pick 
 * </ul> 
 * 
 * <b>Generalized flow component</b>
 * <b>Synchronizing process component</b>
 *  
 * Each component has a parallel gatewsource object and a sink object (these are gateways,
 * except for the (quasi)attached events pattern). The other activities of the
 * components are contained in the activities list (does not contain the 
 * source and sink object). The list of transitions contains the transitions
 * between the activities of the component. 
 */
public class Component extends Container {
	
	public static final int TYPE_ATTACHED_EVENTS = 0;
	public static final int TYPE_QUASI_ATTACHED_EVENTS = 1;
	
	public static final int TYPE_SEQUENCE = 2;
	
	public static final int TYPE_FLOW = 3;
	public static final int TYPE_QUASI_FLOW = 4;	
	public static final int TYPE_SPECIAL_FLOW = 5;
	public static final int TYPE_QUASI_SPECIAL_FLOW = 6;
	
	public static final int TYPE_GENERALISED_FLOW = 7;
	
	public static final int TYPE_IF = 8;
	public static final int TYPE_QUASI_IF = 9;
	
	public static final int TYPE_PICK = 10;
	public static final int TYPE_QUASI_PICK = 11;
	
	public static final int TYPE_WHILE = 12;
	public static final int TYPE_REPEAT = 13;
	public static final int TYPE_REPEAT_WHILE = 14;
	
	public static final int TYPE_SYNCHRONIZING_PROCESS = 15;
	
	private int type;	
	private Activity sourceObject = null;
	private Activity sinkObject = null;
	
	/**
	 * Constructor. Creates a component of the given type, 
	 * with the activities and transitions between them. 
	 * The source and sink object should not be contained in the activities. 
	 * 
	 * @param type			the type of the component
	 * @param activities    the activities of the component (without source and sink object)
	 * @param transitions	the transitions between the activities
	 * @param source		the source object of the component (see definition in Ouyang et al.)
	 * @param sink			the sink object of the component (see definition in Ouyang et al.)
	 */
	public Component(int type, List<Activity> activities, 
			List<Transition> transitions, Activity source, Activity sink) {
		super(activities, transitions);
		this.sourceObject = source;
		this.sinkObject = sink;
		this.type = type; 
	}
	
	/**
	 * Returns the entry transition of the component.
	 * This is the first transition that leads to the components source object 
	 * that is not already part of the components transitions.
	 * 
	 * @return the transition that leads to the components source object 
	 * from outside of the component.
	 */
	public Transition getEntry() {
		if (this.sourceObject.getPredecessor() == null) {
			// multiple predecessors for source object (loop)
			List<Transition> transitions = this.sourceObject.getTargetFor();
			for (Iterator<Transition> it = 
				transitions.iterator(); it.hasNext();) {
				
				Transition trans = it.next();
				if (!getTransitions().contains(trans)) {
					return trans;
				}
			}
		} else {
			return this.sourceObject.getTransitionFrom(
					this.sourceObject.getPredecessor());
		}
		return null;
	}
	
	/**
	 * Returns the exit transition of the component.
	 * This is the first transition that originates from the components sink object 
	 * that is not already part of the components transitions.
	 * 
	 * @return the transition that originates from the components sink object 
	 * to the outside of the component.
	 */
	public Transition getExit() {
		if (this.sinkObject.getSuccessor() == null) {
			// multiple successors for source object (loop)
			List<Transition> transitions = this.sinkObject.getSourceFor();
			for (Iterator<Transition> it = 
				transitions.iterator(); it.hasNext();) {
				
				Transition trans = it.next();
				if (!getTransitions().contains(trans)) {
					return trans;
				}
			}
		} else {
			return this.sinkObject.getTransitionTo(
					this.sinkObject.getSuccessor());
		}
		return null;
	}
	
	/**
	 * Checks if the component is a quasi component.
	 * (Definition see Ouyang et al. or the diploma thesis)
	 * 
	 * @return true, if the component is a quasi component, false otherwise
	 */
	public boolean isQuasi() {
		if (this.type == TYPE_QUASI_ATTACHED_EVENTS) {
			return true;
		} 
		if (this.type == TYPE_QUASI_FLOW) {
			return true;
		}
		if (this.type == TYPE_QUASI_IF) {
			return true;
		}
		if (this.type == TYPE_QUASI_PICK) {
			return true;
		}
		if (this.type == TYPE_QUASI_SPECIAL_FLOW) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the sink object of the component.
	 * @return        The sink object
	 */
	public Activity getSinkObject() {
		return this.sinkObject;
	}

	/**
	 * Returns the source object of the component.
	 * @return        the source object
	 */
	public Activity getSourceObject() {
		return this.sourceObject;
	}

	/**
	 * Returns the type of the component.
	 * @return        The type
	 */
	public int getType() {
		return this.type;
	}
	
	/**
	 * Sets the type of the component
	 * @param type        	The type of the component
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * Sets the source object of the component.
	 * 
	 * @param source The source object to set.
	 */
	public void setSource(Activity source) {
		this.sourceObject = source;
	}
	
	/**
	 * Sets the sink object of the component.
	 * 
	 * @param sink The sink object to set.
	 */
	public void setSink(Activity sink) {
		this.sinkObject = sink;
	}
}
