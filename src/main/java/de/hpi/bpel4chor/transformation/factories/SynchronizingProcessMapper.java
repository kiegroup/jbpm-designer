package de.hpi.bpel4chor.transformation.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hpi.bpel4chor.model.Container;
import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.activities.BlockActivity;
import de.hpi.bpel4chor.model.activities.EmptyTask;
import de.hpi.bpel4chor.model.activities.Event;
import de.hpi.bpel4chor.model.activities.FoldedTask;
import de.hpi.bpel4chor.model.activities.Gateway;
import de.hpi.bpel4chor.model.activities.Task;
import de.hpi.bpel4chor.model.connections.Transition;
import de.hpi.bpel4chor.model.supporting.Expression;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import de.hpi.bpel4chor.util.Output;

/**
 * Class for mapping a generalized flow component to a folded task. The 
 * changes that are made within the components activities and transitions are
 * reflected within the container of the component as well.
 * 
 * <p>The mapping algorithm is based on the algorithm presented in the Ouyang et. 
 * al transformation paper.</p> 
 */
public class SynchronizingProcessMapper {
		
	private Diagram diagram = null;
	private Document document = null;
	private Container container = null;
	private Component component = null;
	private int linkCounter = 0;
	
	/**
	 * This class represents the holds a list with activities and a list with
	 * expressions. It is used to capture the control flow logic between every two
	 * activities (no gateways) that are directly or indirectly (via gateways) connected within
	 * the component. The expressions express the transition conditions of these control
	 * flows.
	 */
	private class TEC {
		List<Activity> activities = new ArrayList<Activity>();
		List<Expression> expressions = new ArrayList<Expression>();
		
		/**
		 * Constructor
		 */
		public TEC() {}
		
		/**
		 * Adds one activity to the list of activities of this TEC.
		 * 
		 * @param act The activit to add.
		 */
		public void addActivity(Activity act) {
			this.activities.add(act);
		}
		
		/**
		 * Adds all activities contained in the list to the activities of 
		 * this TEC.
		 * 
		 * @param act The activities to add.
		 */
		public void addActivities(List<Activity> act) {
			this.activities.addAll(act);
		}
		
		/**
		 * @return The activities of this TEC.
		 */
		public List<Activity> getActivities() {
			return this.activities;
		}
		
		/**
		 * Adds an expression to the list of expressions of this TEC.
		 * 
		 * @param expr The expression to add.
		 */
		public void addExpression(Expression expr) {
			this.expressions.add(expr);
		}
		
		/**
		 * Adds all expressions contained in the list to the expressions of 
		 * this TEC.
		 * 
		 * @param expr The expressions to add.
		 */
		public void addExpressions(List<Expression> expr) {
			this.expressions.addAll(expr);
		}
		
		/**
		 * @return The expressions of this TEC.
		 */
		public List<Expression> getExpressions() {
			return this.expressions;
		}
	}
	
	/**
	 * Constructor. Initializes the mapper.
	 * 
	 * @param diagram  			the diagram, the component belongs to
	 * @param document 			the target document, the elements of the folded
	 * 							task should be created for
	 * @param parentContainer	the container, the component is contained in
	 * @param component			the generalized flow component to map
	 */
	public SynchronizingProcessMapper(Diagram diagram, Document document,
			Container parentContainer, Component component) {
		this.diagram = diagram;
		this.document = document;
		this.container = parentContainer;
		this.component = component;
		
		// component activities also must contain the source and sink object
		this.component.addActivity(component.getSinkObject());
		this.component.addActivity(component.getSourceObject());
	}
	
	/**
	 * Combines the transition conditions of the transitions contained in the
	 * list with conjunction. Default transitions are omitted. If there is an 
	 * opaque transition all other transitions have to be opaque, too.
	 * 
	 * @param transitions The transitions whose conditions will be combined.
	 * 
	 * @return With conjunction combined transition conditions. The result is 
	 * null if there are opaque transitions conditions, but not all transition
	 * conditions are opaque. If all transition conditions are opaque, the
	 * result is an empty string.
	 */
	private String combineWithConjunction(List<Transition> transitions) {
		String result = "";		
		boolean opaque = false;
		for (Iterator<Transition> it = transitions.iterator(); it.hasNext();) {
			Transition trans = it.next();
			if (trans.getConditionType().equals(Transition.TYPE_OTHERWISE)) {
				continue;
			}
			Expression expression = trans.getConditionExpression();
			if ((expression != null) && (expression.getExpression() != null)) {
				if (opaque) {
					return null;
				}
				result = result + expression.getExpression() + " and ";  
			} else if (!result.equals("")) {
				return null;
			} else {
				opaque = true;
			}
		}
		// remove last and from result
		int index = result.lastIndexOf(" and ");
		if (index < 0) {
			return result;
		}
		return result.substring(0, index);
	}
	
	/**
	 * Checks if the given refined condition and the transition expression are
	 * valid. The given transition expression has to be defined for this 
	 * checking. If this expression is not defined, an error is added to the 
	 * output.
	 * 
	 * <p>The refined condition is not valid if it is undefined. If the 
	 * refined condition is an empty string, this means that it is opaque.
	 * In this case the given transition expression has to be opaque, too 
	 * (not defined).
	 * 
	 * @param refinedExpression The refined expression to check.
	 * @param transExpr         The transition expression to use for the checking.
	 * @param gateway           The gateway that has the transitions as outgoing transitions.
	 * @param output            The Output to print errors to.
	 * 
	 * @return True, if the refined expression and the transition expression
	 * are valid, false otherwise.
	 */
	private boolean isValid(String refinedExpression, Expression transExpr, 
			Gateway gateway, Output output) {
		if (transExpr == null) {
			output.addError(
					"The gateway " +
					"must define conditions for its outgoing transitions.",  gateway.getId());
			return false;
		}
		if (refinedExpression == null) {
			// error
			output.addError(
					"The conditions of the outgoing transitions " +
					"from this gateway "+ 
					" must be all opaque or all non-opaque.", gateway.getId());
			return false;
		} else if (refinedExpression.equals("")) {
			// opaque expression
			if (transExpr.getExpression() != null) {
				output.addError(
						"The conditions of the outgoing transitions " +
						"from this gateway "+  
						" must be all opaque or all non-opaque.", gateway.getId());
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Refines the transition conditions of the outgoing transitions of the
	 * given gateway. Refinement means:
	 * If the transition is a default transition its refined condition negates
	 * the conditions of all other outgoing transitions.
	 * If the transition is a conditional transition then the transition 
	 * conditions before get negated and conjuncted with this transition 
	 * condition.
	 * 
	 * @param gateway The gateway whose outgoing transition conditions will be
	 *                refined.
	 * @param output  The output to print errors to.
	 * 
	 * @return A map that maps the transitions to their refined transition 
	 * conditions. The actual transition conditions of the transitions will
	 * not be changed.
	 */
	private Map<Transition, String> getRefinedConditions(Gateway gateway, Output output) {
		Map<Transition, String> result = new HashMap<Transition, String>();
		List<Transition> transitions = gateway.determineEvaluationOrder();
		for (int i = 1; i < transitions.size(); i++) {
			Transition trans = transitions.get(i);
			if (trans.getConditionType().equals(Transition.TYPE_OTHERWISE)) {
				
				// all other transitions are negated
				String refinedExpression = 
					combineWithConjunction(transitions.subList(0, transitions.size()));
				
				// check refined expression
				if (refinedExpression == null) {
					output.addError(
							"The conditions of the outgoing transitions " +
							"from this gateway "+ 
							" must be all opaque or all non-opaque.",
							gateway.getId());
					return null;
				} else if (refinedExpression.equals("")) {
					// opaque expression can not be negated
					result.put(trans, refinedExpression);
				} else {
					refinedExpression = "not(" + refinedExpression + ")";				
					result.put(trans, refinedExpression);
				}
				
			} else {
				String refinedExpression = 
					combineWithConjunction(transitions.subList(0,i));
				Expression expression = trans.getConditionExpression();
				
				// check if refined expression and this expression can be combined
				if (isValid(refinedExpression, expression, gateway, output)) {
					// refine expression
					if ((expression != null) && 
							(expression.getExpression() != null)) {
						
						refinedExpression = "not(" + refinedExpression + 
							") and " + expression.getExpression();						
						result.put(trans, refinedExpression);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Refines the condition of each data-based exclusive gateway contained
	 * in the component. First of all the refined conditions are determined,
	 * but they will not be changed in the actual transition. This has to be
	 * done because the original transition conditions are used for multiple
	 * refinements. After all transition conditions have been refined, the
	 * actual transition conditions can be replaced by the refined ones. 
	 * 
	 * @param output The output to add errors to.
	 */
	private void refineConditions(Output output) {
		List<Gateway> splits = 
			this.component.getDataBasedExclusiveDecisionGateways();
		for (Iterator<Gateway> it = splits.iterator(); it.hasNext();) {
			Gateway gateway = it.next();
			Map<Transition, String> map = getRefinedConditions(gateway, output);
			if (map == null) {
				// error occurred
				return;
			}
			for (Iterator<Transition> itKey = 
					map.keySet().iterator(); itKey.hasNext();) {
				Transition trans = itKey.next();
				String refinedCondition = map.get(trans);
				if (trans.getConditionExpression() != null) {
					trans.getConditionExpression().setExpression(refinedCondition);
				} else {
					Expression expr = new Expression();
					expr.setExpression(refinedCondition);
					expr.setExpressionLanguage(
							"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0");
					trans.setConditionExpression(expr);
				}
			}
		}
	}
	
	/**
	 * Adds the condition expression of the transition to each TEC in the list.
	 * 
	 * @param trans The transition that holds the condition expression.
	 * @param list  The TECs the expressions will be added to.
	 */
	private void addCond(Transition trans, List<TEC> list) {
		for (Iterator<TEC> it = list.iterator(); it.hasNext();) {
			it.next().addExpression(trans.getConditionExpression());
		}
	}
	
	/**
	 * Builds the cartesian product for the given lists of TECs.
	 * This means that for all pairs of TECs the activities and 
	 * expressions are merged into a new TEC that is added to 
	 * the result list.
	 * 
	 * @param list1 The first list for the cartesian product.
	 * @param list2 The second list for the cartesian product.
	 * 
	 * @return The cartesian product for the given TECs.
	 */
	private List<TEC> cartesianProduct(List<TEC> list1, List<TEC> list2) {
		if ((list1 == null) || list1.isEmpty()){
			return list2;
		} else if ((list2 == null) || list2.isEmpty()) {
			return list1;
		}
		
		List<TEC> result = new ArrayList<TEC>();
		for (Iterator<TEC> it = list1.iterator(); it.hasNext();) {
			TEC tec1 = it.next();
			for (Iterator<TEC> it2 = list2.iterator(); it2.hasNext();) {
				TEC tec2 = it2.next();
				TEC tec = new TEC();
				tec.addActivities(tec1.getActivities());
				tec.addActivities(tec2.getActivities());
				tec.addExpressions(tec1.getExpressions());
				tec.addExpressions(tec2.getExpressions());
				result.add(tec);
			}
		}
		return result;
	}
	
	/**
	 * This method implements the PreTEC-SetsFlow method of the Ouyang et.
	 * al transformation. It generates a list of TECs each containing the
	 * preceding tasks and events and/or expressions for the transition
	 * that has y as source and x as target.
	 * 
	 * @param y The source object of the transition
	 * @param x The target object of the transition
	 * 
	 * @return The list with TECs containing the preceding tasks, events
	 * and/or expressions.
	 */
	private List<TEC> preTECSetsFlow(Activity y, Activity x) {
		List<TEC> result = new ArrayList<TEC>();
		if ((y instanceof Task) || (y instanceof Event) || (y instanceof BlockActivity)) {
			TEC tec = new TEC();
			tec.addActivity(y);
			result.add(tec);
		} else if (y instanceof Gateway) {
			Gateway gateway = (Gateway)y;
			if (gateway.getGatewayType().equals(Gateway.TYPE_AND)) {
				if	(gateway.getSourceFor().size() > 1) {
					// parallel fork gateway
					result = preTECSets(gateway);
				} else {
					// parallel join gateway
					for (Iterator<Activity> it = y.getPredecessors().iterator(); it.hasNext();) {
						List<TEC> list = preTECSetsFlow(it.next(),gateway);
						result = cartesianProduct(result, list);
					}
				}
			} else if (gateway.getGatewayType().equals(Gateway.TYPE_XOR)) {
				if (gateway.getSplitType().equals(Gateway.SPLIT_XORDATA) &&
					(gateway.getSourceFor().size() > 1)) {
					// exclusive data-based decision gateway
					result = preTECSets(gateway);
					Transition trans = y.getTransitionTo(x);
					addCond(trans, result);
				} else if (gateway.getTargetFor().size() > 1) {
					// exclusive merge gateway
					result = new ArrayList<TEC>();
					for (Iterator<Activity> it = y.getPredecessors().iterator(); it.hasNext();) {
						List<TEC> list = preTECSetsFlow(it.next(),gateway);
						result.addAll(list);
					}
				}
			} else if (gateway.getGatewayType().equals(Gateway.TYPE_OR)) {
				if (gateway.getSourceFor().size() > 1) {
					// inclusive split gateway
					result = preTECSets(gateway);
					Transition trans = y.getTransitionTo(x);
					addCond(trans, result);
				} else {
					// inclusive merge gateway
					// no join Condition needed because by default or-join
					result = preTECSets(gateway);
				}
			}
		}
		return result;
	}
	
	/**
	 * This method implements the PreTEC-Sets method of the Ouyang et.
	 * al transformation. It generates a list of TECs each containing the
	 * preceding tasks and events and/or expressions for the activity x.
	 * 
	 * @param x The activity to get the predecessors and expressions for.
	 * 
	 * @return The list with TECs containing the preceding tasks, events
	 * and/or expressions.
	 */
	private List<TEC> preTECSets(Activity x) {
		List<TEC> result = new ArrayList<TEC>();
		for (Iterator<Activity> it = x.getPredecessors().iterator(); it.hasNext();) {
			Activity pred = it.next();
			if (this.component.getActivities().contains(pred)) {
				List<TEC> list = preTECSetsFlow(pred,x);
				result.addAll(list);
			}
		}
		return result;
	}
	
	/**
	 * Checks if the list of Links contains a link from the given source
	 * activity to the given target activity.
	 * 
	 * @param links  The links to be checked
	 * @param source The source activity of the link to be searched
	 * @param target The target activity of the link to be searched
	 * 
	 * @return The determined link with the source and the target activitiy 
	 * or null if the link does not exist in the list.
	 */
	private Link getLink(List<Link> links, Activity source, Activity target) {
		for (Iterator<Link> it = links.iterator(); it.hasNext();) {
			Link link = it.next();
			if (link.getSource().equals(source) && (link.getTarget().equals(target))) {
				return link;
			}
		}
		return null;
	}
	
	/**
	 * Derives the set of control links with their associated transition
	 * conditions for each of the tasks, events and block activities within
	 * the component.
	 * 
	 * @return The list with the created control links.
	 */
	private List<Link> calcTransCond() {		
		List<Link> links = new ArrayList<Link>();
		for (Iterator<Activity> it = 
			this.component.getActivities().iterator(); it.hasNext();) {
			
			Activity target = it.next();
			if ((target instanceof Task) || (target instanceof Event) || 
					(target instanceof BlockActivity)) {
				
				// generate link for each source TE of the target activity
				List<TEC> tecs = preTECSets(target);
				for (Iterator<TEC> itTEC = tecs.iterator(); itTEC.hasNext();) {
					TEC next = itTEC.next();
					Expression transCond = null;
					for (Iterator<Expression> itExp = 
						next.expressions.iterator(); itExp.hasNext();) {
						
						Expression exp = itExp.next();
						if ((exp == null) || (exp.getExpression() == null)) {
							// opaque expression will not be combined
							transCond = exp;
						} else {
							// combine expressions in TEC with conjunction
							String expStr = exp.getExpression();
							if (transCond == null) {
								transCond = new Expression();
								transCond.setExpression(expStr);
							} else {
								transCond.setExpression(transCond.getExpression() + " and " + expStr);
							}
							if (exp.getExpressionLanguage() != null) {
								transCond.setExpressionLanguage(exp.getExpressionLanguage());
							}
						}
					}

					for (Iterator<Activity> itAct = next.activities.iterator(); itAct.hasNext();) {
						Activity source = itAct.next();
						// check if link already exists
						if (getLink(links, source, target) == null) {
							// create link
							Link link = new Link(source, target, transCond, this.linkCounter);
							this.linkCounter++;
							links.add(link);
						}
					}
				}
			}
		}
		return links;
	}
	
	/**
	 * Derives the join conditions for each activity from the set of control links.
	 * The join condition is build up of all control links that lead to this activity.#
	 * 
	 * @param links  The links that are defined in the context of the component.
	 * @param output The output to print erros to.
	 * 
	 * @return A map that maps the activity to its determined join condition. 
	 */
	private Map<Activity, Expression> calcJoinCond(List<Link> links, Output output) {
		Map<Activity, Expression> joinConds = new HashMap<Activity, Expression>();
		for (Iterator<Activity> it = this.component.getActivities().iterator(); it.hasNext();) {
			Activity target = it.next();
			
			if ((target instanceof Task) || (target instanceof Event) || 
					(target instanceof BlockActivity)) {
				
				List<TEC> tecs = preTECSets(target);
				Expression joinCond = new Expression();
				joinCond.setExpressionLanguage(
						"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0");
				
				for (Iterator<TEC> itTEC = tecs.iterator(); itTEC.hasNext();) {
					TEC next = itTEC.next();
					String joinPart = "";
					for (Iterator<Activity> itAct = next.activities.iterator();
							itAct.hasNext();) {
						Activity source = itAct.next();
						// determine link name for link with source and target and 
						// combine it in the appropriate way
						Link link = getLink(links, source, target);
						if (link != null) {
							joinPart = joinPart + "$" + link.getName();
							if (itAct.hasNext()) {
								joinPart = joinPart + " and ";
							}
						} else {
							output.addError("A link could not be generated " +
									"for a synchronizing process pattern.", source.getId());
						}
					}
					if (!joinPart.equals("")) {
						if (joinCond.getExpression() == null) {
							joinCond.setExpression("(" + joinPart + ")");
						} else {
							joinCond.setExpression(joinCond.getExpression() + " or (" + joinPart + ")");
						}
					}
					
				}
				joinConds.put(target, joinCond);
			}
		}
		return joinConds;
	}
	
	/**
	 * Finishes the mapping by mapping the component to a "flow" element.
	 * The control links of the "flow" will be created and the remaining 
	 * tasks, events and block activities will be mapped to its BPEL4Chor
	 * representations. 
	 * 
	 * <p>A folded task will be created that holds the mapped "flow" 
	 * element". The remaining activities will not be removed since the
	 * folding of the task is done in the {@link SequenceFlowFactory}.</p>
	 * 
	 * @param output The output to add errors to.
	 * 
	 * @return The created folded task.
	 */
	private FoldedTask finishMapping(Output output) {
		Element flowElement = this.document.createElement("flow");
		List<Link> links = calcTransCond();
		Map<Activity, Expression> joinConds = calcJoinCond(links, output);
		Element linksElement = 
			new SupportingFactory(this.diagram, this.document, output).createLinks(links);
		
		flowElement.appendChild(linksElement);
		
		SequenceFlowFactory factory = 
			new SequenceFlowFactory(this.diagram, this.document, this.component, output); 
		for (Iterator<Activity> it = this.component.getActivities().iterator(); it.hasNext();) {
			Activity act = it.next();
			if ((act instanceof Task) || (act instanceof Event) || 
					(act instanceof BlockActivity)) {
				Element element = factory.mapActivity(act, links, joinConds.get(act));
				if (element != null) {
					flowElement.appendChild(element);
				}
			}
		}
		
		FoldedTask task = new FoldedTask(flowElement, this.container, output);
		return task;
	}
	
	/**
	 * Does the actual mapping of the synchronzing process component according
	 * to the algorithm defined in the Ouyang et. al transformation.
	 * 
	 * @param output The Output to print errors to.
	 * 
	 * @return The folded task that was created during the mapping.
	 */
	public FoldedTask mapSynchronizingProcess(Output output) {
		Activity source = this.component.getSourceObject();
		Activity sink = this.component.getSinkObject();
		
		// transition to the source object
		Transition toAh = this.component.getEntry();
		if (toAh == null) {
			output.addError("A component could not be folded.", this.component.getId());
			return null;
		}
		
		// transition to the sink object
		Transition fromIc = this.component.getExit();
		if (fromIc == null) {
			output.addError("A component could not be folded.", this.component.getId());
			return null;
		}
		
		// insert empty task before source
		EmptyTask ah = new EmptyTask(output);
		ah.setParentContainer(source.getParentContainer());
		source.removeTargetFor(toAh);
		Transition fromAh = new Transition(ah, source, output);
		toAh.setTarget(ah, output);
		ah.addTargetFor(toAh, output);
		this.component.addTransition(fromAh);
		this.component.setSource(ah);
		this.component.getActivities().add(0, ah);
		
		// insert empty task after sink object
		EmptyTask ic = new EmptyTask(output);
		ic.setParentContainer(sink.getParentContainer());
		sink.removeSourceFor(fromIc);
		Transition toIc = new Transition(sink, ic, output);
		fromIc.setSource(ic, output);
		ic.addSourceFor(fromIc, output);
		this.component.addTransition(toIc);
		this.component.setSink(ic);
		this.component.addActivity(ic);
		
		// refine conditions emanating from data-based exlusive gateway
		refineConditions(output);
		
		// map to BPEL and create folded task
		FoldedTask task = finishMapping(output);

		// remove component activities from container
		this.container.removeActivities(this.component.getActivities());
		this.container.removeTransitions(this.component.getTransitions());
		
		// add transitions again
		toAh.setTarget(task, output);
		toAh.getSource().addSourceFor(toAh, output);
		task.addTargetFor(toAh, output);
		fromIc.setSource(task, output);
		fromIc.getTarget().addTargetFor(fromIc, output);
		task.addSourceFor(fromIc, output);
		this.container.addTransition(toAh);
		this.container.addTransition(fromIc);
		this.container.addActivity(task);
		return task;
	}
}
