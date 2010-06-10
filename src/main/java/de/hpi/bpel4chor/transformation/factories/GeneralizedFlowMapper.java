package de.hpi.bpel4chor.transformation.factories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.hpi.bpel4chor.model.Container;
import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.activities.EndEvent;
import de.hpi.bpel4chor.model.activities.FoldedTask;
import de.hpi.bpel4chor.model.activities.Gateway;
import de.hpi.bpel4chor.model.activities.StartEvent;
import de.hpi.bpel4chor.model.connections.Transition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import de.hpi.bpel4chor.util.Output;

/**
 * Class for Mapping a generalized flow component to a folded task. The 
 * changes that are made within the components activities and transitions 
 * have to be reflected within the container of the component as well. 
 * In this way this class also implements the folding of a generalized 
 * flow component.
 */
public class GeneralizedFlowMapper {
		
	private Diagram diagram = null;
	private Document document = null;
	private Container container = null;
	private Component component = null;
	private Output output = null;
	private int linkCounter = 0;
	
	/**
	 * Constructor. Initializes the mapper.
	 * 
	 * @param diagram  			The diagram, the component belongs to
	 * @param document 			The target document, the elements of the folded
	 * 							task should be created for
	 * @param parentContainer	The container, the component is contained in
	 * @param component			The generalized flow component to map
	 * @param output			The output to print errors to.
	 */
	public GeneralizedFlowMapper(Diagram diagram, Document document,
			Container parentContainer, Component component, Output output) {
		this.diagram = diagram;
		this.document = document;
		this.container = parentContainer;
		this.component = component;
		this.output = output;
		
		// component activities also must contain the source and sink object
		this.component.addActivity(component.getSinkObject());
		this.component.addActivity(component.getSourceObject());
	}
	
	/**
	 * Collects the gateways contained in the component.
	 * 
	 * @param withSource True, if the source object of the component
	 * should be included in the result
	 * @param withSink True, if the sink object of the component
	 * should be included in the result
	 * 
	 * @return The gateways contained in the component
	 */
	private List<Gateway> getGateways(boolean withSource, boolean withSink) {
		List<Gateway> result = new ArrayList<Gateway>();
		for (Iterator<Activity> it = 
			this.component.getActivities().iterator(); it.hasNext();) {
			Activity act = it.next();
			if (act instanceof Gateway) {
				result.add((Gateway)act);
			}
		}
		if (!withSource) {
			result.remove(this.component.getSourceObject());
		}
		if (!withSink) {
			result.remove(this.component.getSinkObject());
		}
		return result;
	}
	
	/**
	 * Collects the fork gateways contained in the component.
	 * 
	 * @param withSource True, if the source object of the component
	 * should be included in the result
	 * 
	 * @return The fork gateways contained in the component
	 */
	private List<Gateway> getForkGateways(boolean withSource) {
		List<Gateway> result = new ArrayList<Gateway>();
		for (Iterator<Activity> it = 
			this.component.getActivities().iterator(); it.hasNext();) {
			Activity act = it.next();
			if (act instanceof Gateway) {
				Gateway gateway = (Gateway)act;
				if (isForkGateway(gateway)) {
					result.add(gateway);
				}
			}
		}
		
		if (!withSource) {
			result.remove(this.component.getSourceObject());
		}
		return result;
	}
	
	/**
	 * Collects the join gateways contained in the component.
	 * 
	 * @param withSink True, if the sink object of the component
	 * should be included in the result
	 * 
	 * @return The join gateways contained in the component
	 */
	private List<Gateway> getJoinGateways(boolean withSink) {
		List<Gateway> result = new ArrayList<Gateway>();
		for (Iterator<Activity> it = 
			this.component.getActivities().iterator(); it.hasNext();) {
			Activity act = it.next();
			if (act instanceof Gateway) {
				Gateway gateway = (Gateway)act;
				if (isJoinGateway(gateway)) {
						result.add(gateway);
				}
			}
		}

		if (!withSink) {
			result.remove(this.component.getSinkObject());
		}
		return result;
	}
	
	/**
	 * @return The first fork gateway found with another fork gateway as
	 * predecessor
	 */
	private Gateway getSequentialForkGateways() {
		List<Gateway> forkGateways = getForkGateways(false);
		for (Iterator<Gateway> it = forkGateways.iterator(); it.hasNext();) {
			Gateway gateway = it.next();
			Activity predecessor = gateway.getPredecessor();
			if ((predecessor instanceof Gateway) && 
				(isForkGateway((Gateway)gateway.getPredecessor()))) {
				return gateway;
			}
		}
		return null;
	}
	
	/**
	 * Removes all fork gateways that have another fork gateway
	 * as predecessor.
	 * The gateway is removed after the source of all outgoing transitions
	 * is changed to the predecessor fork gateway. 
	 */
	private void removeSequentialForkGateways() {
		Gateway sequentialGateway = getSequentialForkGateways();
		while (sequentialGateway != null) {
			Gateway predecessor = (Gateway)sequentialGateway.getPredecessor();
			
			// change source of outgoing transitions of gateway to predecessor
			for (Iterator<Transition> it = 
				sequentialGateway.getSourceFor().iterator(); it.hasNext();) {
				Transition trans = it.next();
				trans.setSource(predecessor, this.output);
				predecessor.addSourceFor(trans, this.output);
			}
			// remove transition between gateways
			Transition trans = predecessor.getTransitionTo(sequentialGateway);
			predecessor.removeSourceFor(trans);
			this.component.getTransitions().remove(trans);
			this.container.getTransitions().remove(trans);
			
			// remove gateway
			this.component.getActivities().remove(sequentialGateway);
			this.container.getActivities().remove(sequentialGateway);
			
			sequentialGateway = getSequentialForkGateways();
		}
	}
	
	/**
	 * Checks if the gateway is a join gateway and if this gateway type
	 * is allowed in a generalized flow pattern.
	 *  
	 * @param gateway The gateway to check
	 * 
	 * @return True, if the gateway is an inclusive or parallel join gateway.
	 * False, otherwise. 
	 */
	private boolean isJoinGateway(Gateway gateway) {
		if (gateway.getGatewayType().equals(Gateway.TYPE_AND) ||
				gateway.getGatewayType().equals(Gateway.TYPE_OR)) {
			if (gateway.getTargetFor().size() > 1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the gateway is a fork gateway and if this gateway type
	 * is allowed in a generalized flow pattern.
	 *  
	 * @param gateway The gateway to check
	 * 
	 * @return True, if the gateway is a parallel fork gateway.
	 * False, otherwise. 
	 */
	private boolean isForkGateway(Gateway gateway) {
		if (gateway.getGatewayType().equals(Gateway.TYPE_AND) && 
				gateway.getSourceFor().size() > 1) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return The first join gateway found, that has another join
	 * gateway as successor.
	 */
	private Gateway getSequentialJoinGateways() {
		List<Gateway> joinGateways = getJoinGateways(false);
		for (Iterator<Gateway> it = joinGateways.iterator(); it.hasNext();) {
			Gateway gateway = it.next();
			Activity successor = gateway.getSuccessor();
			if ((successor instanceof Gateway) && 
				(isJoinGateway((Gateway)gateway.getSuccessor()))) {				
				return gateway;
			}
		}
		return null;
	}
	
	/**
	 * Removes all join gateways, that have another join gateway as successor. 
	 * Such a join gateway can be removed after the target of all transitions to the gateway
	 * is changed to the successor gateway. 
	 * 
	 */
	private void removeSequentialJoinGateways() { 
		Gateway sequentialGateway = getSequentialJoinGateways();
		while (sequentialGateway != null) {
			Gateway successor = (Gateway)sequentialGateway.getSuccessor();
			
			// change target of incoming transitions of gateway to successor
			for (Iterator<Transition> it = 
				sequentialGateway.getTargetFor().iterator(); it.hasNext();) {
				Transition trans = it.next();
				trans.setTarget(successor, this.output);
				successor.addTargetFor(trans, this.output);
			}
			
			// remove transition between gateways
			Transition trans = successor.getTransitionFrom(sequentialGateway);
			successor.removeTargetFor(trans);
			this.component.getTransitions().remove(trans);
			this.container.getTransitions().remove(trans);
			
			// remove gateway
			this.component.getActivities().remove(sequentialGateway);
			this.container.getActivities().remove(sequentialGateway);
			
			sequentialGateway = getSequentialJoinGateways();
		}
	}
	
	/** 
	 * @return All join gateways that have an incoming transition 
	 * from a fork gateway.
	 */
	private List<Gateway> getDirectJoinGateways() {
		List<Gateway> result = new ArrayList<Gateway>();
		List<Gateway> joinGateways = getJoinGateways(true);
		for (Iterator<Gateway> it = joinGateways.iterator(); it.hasNext();) {
			Gateway gateway = it.next();
			for (Iterator<Transition> itTrans = 
				gateway.getTargetFor().iterator(); itTrans.hasNext();) {
				// check if source of one of the incoming transitions 
				// is a fork gateway
				Transition trans = itTrans.next();
				if ((trans.getSource() instanceof Gateway) && 
					(isForkGateway((Gateway)trans.getSource()))) {
					result.add(gateway);
				}
			}
		}
		return result;
	}
	
	/**
	 * Searches a fork gateway in the given list.
	 * 
	 * @param activities The list to search in.
	 *  
	 * @return The first fork gateway found in the list. 
	 */
	private Gateway getFirstFork(List<Activity> activities) {
		for (Iterator<Activity> it = activities.iterator(); it.hasNext();) {
			Activity act = it.next();
			if ((act instanceof Gateway) && (isForkGateway((Gateway)act))) {
				return (Gateway)act;
			}
		}
		return null;
	}
	
	
	/**
	 * Derives Links from transitions connecting a fork gateway
	 * directly to a join gateway and removes these transitions
	 * from the container and the component.
	 * 
	 * @return A list with the created links
	 */
	private List<Link> deriveLinks() {
		List<Link> result = new ArrayList<Link>();
		for (Iterator<Gateway> it = 
			getDirectJoinGateways().iterator(); it.hasNext();) {
			Gateway g = it.next();
			while (g.getTargetFor().size() > 1) {
				// get in(g) and select a fork gateway
				List<Activity> predecessors = g.getPredecessors();
				Gateway x = getFirstFork(predecessors);
				if (x != null) {
					// create link
					if (!x.equals(this.component.getSourceObject()) && 
							!g.equals(this.component.getSinkObject())) {
						Link link = new Link(x.getPredecessor(), 
								g.getSuccessor(), this.linkCounter);
						this.linkCounter++;
						result.add(link);
					}
					
					// remove transition from x to g
					Transition trans = x.getTransitionTo(g);
					this.component.getTransitions().remove(trans);
					this.container.getTransitions().remove(trans);
					g.removeTargetFor(trans);
					x.removeSourceFor(trans);
				} else {
					// this break is not part of the paper algorithm
					// while condition |in(g) sec G^F_C| > 1 would be better in the paper (error?)
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * Removes gateways with exactly one incoming and one outgoing transition
	 * in the component and in the container of the component.
	 * 
	 * The transitions from and to the gateway are replaced with 
	 * new transitions form the gateways successor to the gateways predecessor.
	 *
	 */
	private void reduceNeedlessGateways() {
		List<Gateway> gateways = getGateways(false, false);
		for (Iterator<Gateway> it = gateways.iterator(); it.hasNext();) {
			Gateway gateway = it.next();
			if ((gateway.getSourceFor().size() == 1) && 
				(gateway.getTargetFor().size() == 1)) {
				// remove gateway
				Activity pred = gateway.getPredecessor();
				Activity succ = gateway.getSuccessor();
				this.component.removeActivity(gateway);
				this.container.removeActivity(gateway);
				
				// create new Transition replacing the gateway
				Transition trans = new Transition(pred, succ, this.output);
				this.container.addTransition(trans);
				this.component.getTransitions().add(trans);
			}
		}
	}
	
	/**
	 * Finishes the mapping of the generalized flow component, if there is only 
	 * one task left. This task must be a folded task. A "links" element has
	 * to be added to the BPEL code belonging to this task if links were created.
	 * This "links" element is generated from the links created
	 * during the mapping.</p>
	 * 
	 * @param links The links created during the mapping
	 * 
	 * @return The folded task representing the generalized flow component. 
	 * The result is null if there is more than one activity left or the activity is
	 * not a folded task. 
	 */
	private FoldedTask finishFlow(List<Link> links) {
		if (this.component.getActivities().size() != 1) {
			this.output.addError("A generalized flow pattern could not be mapped correctly.", this.component.getId());
			return null;
		}
		
		Activity act = this.component.getActivities().get(0);
		if (!(act instanceof FoldedTask)) {
			this.output.addError("A generalized flow pattern could not be mapped correctly.", this.component.getId());
			return null;
		}
		
		Element element = ((FoldedTask)act).getBPELElement();
		if (element == null) {
			this.output.addError("An Activity could not be mapped correctly, since the BPELElement is null", act.getId());
			return null;
		}
		Element linksElement = 
			new SupportingFactory(this.diagram, 
					this.document, this.output).createLinks(links);
		if (linksElement != null) {
			Node child = element.getFirstChild();
			if (child == null) {
				element.appendChild(linksElement);
			} else {
				element.insertBefore(linksElement, child);
			}
		}
		
		return (FoldedTask)act;
	}
	
	/**
	 * <p>Does the actual mapping. Searches for components and maps them to folded tasks.
	 * If the found component is a generalized flow pattern (-> is this pattern) a link
	 * is derived from an arc connecting a task or event to a join gateway. This may result
	 * in new components so the search starts again.</p> 
	 * 
	 * <p>After the call of this methode, there should only be one folded task left in the 
	 * component that holds the BPEL representation of the generalized flow.</p>
	 * 
	 * @param links The links that were already created in the preparation of the mapping
	 */
	private void doMapping(List<Link> links) {
		// transform component into a task and attached the appropriate BPEL Code
		// and replace start and end events by the original elements
		Activity target = this.component.getSinkObject();
		SequenceFlowFactory factory = 
			new SequenceFlowFactory(this.diagram, this.document, 
					this.component, this.output);
		
		Componentizer componentizer = new Componentizer(
				this.diagram, this.component, this.output);

		List<FoldedTask> foldedTasks = new ArrayList<FoldedTask>(); 
		
		Component next = componentizer.getNextComponent();
		while (next != null) {
			if (next.getType()== Component.TYPE_GENERALISED_FLOW) {
				// derive an additional link from any arc connecting 
				// an activity(no gateway) to a join gateway
				List<Gateway> gateways = getJoinGateways(false);
				if (gateways.size() > 0) {
					Gateway g = gateways.get(0);
					Activity x = null;
					List<Activity> preds = g.getPredecessors();
					for (Iterator<Activity> it = preds.iterator(); it.hasNext();) {
						Activity act = it.next();
						if (!(act instanceof Gateway)) {
							x = act;
							break;
						} 
					}
					Activity succ = g.getSuccessor();
					if (x != null && succ != null) {
						Transition trans = x.getTransitionTo(g);
						links.add(new Link(x, succ, this.linkCounter));
						this.linkCounter++;
						trans.setTarget(target, this.output);
						g.removeTargetFor(trans);
						target.addTargetFor(trans, this.output);
					} else if ((succ != null) && (g.getTargetFor().size() == 1)) {
						// remove g
						Transition trans = g.getTargetFor().get(0);
						g.removeTargetFor(trans);
						this.component.removeActivity(g);
						trans.setTarget(succ, this.output);
						succ.addTargetFor(trans, this.output);
					}
				} else {
					this.output.addError("The generalized flow component was not generated correctly.", this.component.getId());
					break;
				}
			} else {
				foldedTasks.add(factory.foldComponent(next, links));
			}
			next = componentizer.getNextComponent();
		}
	}

	
	/**
	 * Maps the generalized flow component to a folded task containing the
	 * bpel code representing the mapped component.
	 * 
	 * @return The created BPEL "flow" element or null, if the component
	 * could not be mapped.
	 */
	public FoldedTask mapGeneralizedFlow() {		
		Transition toTask = this.component.getEntry();
		if (toTask == null) {
			this.output.addError("A component could not be folded.", this.component.getId());
			return null;
		}
		
		Transition fromTask = this.component.getExit();
		if (fromTask == null) {
			this.output.addError("A component could not be folded.", this.component.getId());
			return null;
		}
		
		// store existing activities and transitions in a new list because the original lists
		// will be changed during the mapping
		List<Activity> toRemoveAct = 
			new ArrayList<Activity>(this.component.getActivities());
		List<Transition> toRemoveTrans = 
			new ArrayList<Transition>(this.component.getTransitions());
		
		toRemoveTrans.addAll(this.component.getSourceObject().getSourceFor());
		toRemoveTrans.addAll(this.component.getSinkObject().getTargetFor());
		
		// prepare mapping
		removeSequentialForkGateways();
		removeSequentialJoinGateways();		
		
		List<Link> links = deriveLinks();
		reduceNeedlessGateways();
		
		// store incoming and outgoing element of component and the transitions
		Activity source = this.component.getSourceObject();
		Activity target = this.component.getSinkObject();
		Transition incoming = source.getTargetFor().get(0);
		Transition outgoing = target.getSourceFor().get(0);
		
		// remove the connecting to these elements in the component and
		source.removeTargetFor(incoming);
		target.removeSourceFor(outgoing);
		
		// add a start and end event
		StartEvent start = new StartEvent(StartEvent.TRIGGER_NONE, null, 
				true, this.output);
		EndEvent end = new EndEvent(this.output);
		
		Transition startTrans = new Transition(start, source, 
				this.output);
		Transition endTrans = new Transition(target, end, 
				this.output);
		
		this.component.addActivity(start);
		this.component.addActivity(end);
		
		this.component.addTransition(startTrans);
		this.component.addTransition(endTrans);
		
		// do actual mapping
		doMapping(links);
		
		// create folded task from remaining activity
		this.component.removeActivity(start);
		this.component.removeActivity(end);
		this.component.removeTransition(startTrans);
		this.component.removeTransition(endTrans);

		// remove component activities from container
		this.container.removeActivities(toRemoveAct);
		this.container.removeTransitions(toRemoveTrans);
		
		FoldedTask task = finishFlow(links);
		if (task == null) {
			return null;
		}
		
		// add transitions again
		toTask.setTarget(task, this.output);
		task.addTargetFor(toTask, this.output);
		fromTask.setSource(task, this.output);
		task.addSourceFor(fromTask, this.output);
		return task;
	}
}
