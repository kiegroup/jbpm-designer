package de.hpi.bpel4chor.transformation.factories;

import java.util.Iterator;
import java.util.List;
import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.activities.AssignTask;
import de.hpi.bpel4chor.model.activities.EmptyTask;
import de.hpi.bpel4chor.model.activities.Event;
import de.hpi.bpel4chor.model.activities.IntermediateEvent;
import de.hpi.bpel4chor.model.activities.ServiceTask;
import de.hpi.bpel4chor.model.activities.NoneTask;
import de.hpi.bpel4chor.model.activities.ReceiveTask;
import de.hpi.bpel4chor.model.activities.SendTask;
import de.hpi.bpel4chor.model.activities.ResultCompensation;
import de.hpi.bpel4chor.model.activities.ResultError;
import de.hpi.bpel4chor.model.activities.StartEvent;
import de.hpi.bpel4chor.model.activities.Task;
import de.hpi.bpel4chor.model.activities.Trigger;
import de.hpi.bpel4chor.model.activities.TriggerResultMessage;
import de.hpi.bpel4chor.model.activities.TriggerTimer;
import de.hpi.bpel4chor.model.activities.ValidateTask;
import de.hpi.bpel4chor.model.artifacts.VariableDataObject;
import de.hpi.bpel4chor.model.connections.MessageFlow;
import de.hpi.bpel4chor.model.supporting.Copy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import de.hpi.bpel4chor.util.BPELUtil;
import de.hpi.bpel4chor.util.Output;

/**
 * This factory transforms the basic BPEL4Chor activities assign, compensate, 
 * empty, invoke, opaque, receive, reply, throw, validate and wait from the diagram. 
 * Each instance of this factory can only be used for one diagram.
 */
public class BasicActivityFactory {
	
	private Diagram diagram = null;
	private Document document = null;
	private SupportingFactory supportingFactory = null;
	private StructuredElementsFactory structuredElementsFactory = null;
	private Output output;
	
	/**
	 * Constructor. Initializes the factory with the diagram and the 
	 * target document, the generated BPEL4Chor elements will be contained in. 
	 * 
	 * @param diagram  The diagram the activities are modeled in
	 * @param document The target document for the generated BPEL4Chor elements
	 * @param output   The Output to print errors to. 
	 */
	public BasicActivityFactory(Diagram diagram, Document document, Output output) {
		this.diagram = diagram;
		this.document = document;
		this.output = output;
		this.supportingFactory = new SupportingFactory(diagram, document, this.output);
		this.structuredElementsFactory = 
			new StructuredElementsFactory(diagram, document, this.output);
	}
	
	/**
	 * <p>Creates the BPEL4Chor "Copy" element from a Copy object.</p>
	 * 
	 * The attributes keepSrcElementName and ignoreMissingFromData 
	 * are taken from the Copy object. The fromSpec and toSpec elements
	 * are generated using the appropriate methods
	 * {@link SupportingFactory#createFromSpecElement(model.supporting.FromSpec)} and
	 * {@link SupportingFactory#createToSpecElement(model.supporting.ToSpec)}.
	 * 
	 * @param copy The copy object to create the element from
	 * @param task The assign task to generate the copy element for.
	 * 
	 * @return the generated BPEL4Chor "copy" element.
	 */
	private Element createCopyElement(Copy copy, AssignTask task) {
		Element result = this.document.createElement("copy");
		
		if (copy.isKeepSrcElementName() != null) {
			result.setAttribute("keepSrcElementName", 
					copy.isKeepSrcElementName());
		}
		
		if (copy.isIgnoreMissingFromData() != null) {
			result.setAttribute("ignoreMissingFromData", 
				copy.isIgnoreMissingFromData());
		}
		
		Element fromSpec = 
			this.supportingFactory.createFromSpecElement(copy.getFromSpec());
		if (fromSpec != null) {
			result.appendChild(fromSpec);
		} else {
			this.output.addError("The assign task has a copy without a specified from spec element.", task.getId());
		}
		
		Element toSpec = 
			this.supportingFactory.createToSpecElement(copy.getToSpec());
		if (toSpec != null) {
			result.appendChild(toSpec);
		} else {
			this.output.addError("The assign task has a copy without a specified to spec element.", task.getId());
		}
		
		return result;
	}
	
	/**
	 * <p>Creates the BPEL4Chor "receive" element from an Event object.</p>
	 * 
	 * <p>The standard attributes are taken from the event. 
	 * The createInstance attribute is taken from the parameter.
	 * The messageExchange, correlation and fromParts attributes and elements 
	 * are taken from the trigger </p>
	 * 
	 * <p>
	 * The correlation and fromParts elements
	 * are generated using the appropriate methods
	 * {@link SupportingFactory#createCorrelationsElement(List)} and
	 * {@link SupportingFactory#createFromPartsElement(List)}.
	 * </p>
	 * 
	 * <p>
	 * The variable attribute will only be generated, 
	 * if no fromParts element was specified and if the trigger does not define
	 * an opaque variable. The variable attribute is determined from a standard
	 * variable data object, that is associated with the event. 
	 * </p>
	 * 
	 * @param event			 The event, to generate the receive element from. 
	 * @param eventTrigger   The trigger of the event (should be a TriggerResultMessage) 
	 * @param createInstance true, if the receive element has a createInstance attribute 
	 * set to "yes", false otherwise.
	 * 
	 * @return The generated BPEL4Chor "receive" element
	 */
	private Element createReceiveElement(Event event, 
			Trigger eventTrigger, boolean createInstance) {
		Element result = this.document.createElement("receive");
		
		BPELUtil.setStandardAttributes(result, event);
		
		if (createInstance)
			result.setAttribute("createInstance", BPELUtil.booleanToYesNo(createInstance));
		
		// must be determined here, because there may be no trigger defined
		VariableDataObject variable = 
			this.diagram.getStandardVariable(event, true);
		
		if ((eventTrigger != null) && 
				(eventTrigger instanceof TriggerResultMessage)) {
			TriggerResultMessage trigger = (TriggerResultMessage)eventTrigger;
			
			if (trigger.getMessageExchange() != null) {
				result.setAttribute("messageExchange", trigger.getMessageExchange());
			}
			
			Element correlations = 
				this.supportingFactory.createCorrelationsElement(
						trigger.getCorrelations());
			if (correlations != null) {
				result.appendChild(correlations);
			}
			
			if(trigger.isOpaqueOutput()) {
				result.setAttribute("variable", "##opaque");
				// the output variable is opaque so omit from parts and variable data objects
				variable = null;
			} else if (variable == null) {
				// create from part only if no variable data object found
				Element fromParts = 
					this.supportingFactory.createFromPartsElement(
							trigger.getFromParts());
				if (fromParts != null) {
					result.appendChild(fromParts);
				} else {
					this.output.addError("The message event must define an output variable.", event.getId());
				}
			} 
		}
		
		if (variable != null) {
			result.setAttribute("variable", variable.getName());
		}
		
		return result;
	}
	
	
	/**
	 * <p>Generates a BPEL4Chor "receive" element from a start event.</p>
	 * 
	 * <p>For more detail see the documentation of 
	 * {@link #createReceiveElement(Event, Trigger, boolean)}</p>
	 * 
	 * @param event			 the start event to generate the receive element from
	 * @param createInstance true, if the receive element has a createInstance attribute 
	 * set to "yes", false otherwise.
	 * 
	 * @return  The generated BPEL4Chor "receive" element.
	 * 			The result is null if the trigger of the event is not a message trigger. 
	 */
	public Element createReceiveElement(StartEvent event, boolean createInstance) {
		if (!event.getTriggerType().equals(IntermediateEvent.TRIGGER_MESSAGE)) {
			return null;
		}

		return createReceiveElement(event, event.getTrigger(), createInstance);
	}
	
	/**
	 * <p>Generates a BPEL4Chor "receive" element from an intermediate event.</p>
	 * 
	 * <p>For more detail see the documentation of 
	 * {@link #createReceiveElement(Event, Trigger, boolean)}</p>
	 * 
	 * @param event			 the intermediate event to generate the receive element from
	 * 
	 * @return 	The generated BPEL4Chor "receive" element.
	 * 			The result is null if the trigger of the event is not a message trigger. 
	 */
	public Element createReceiveElement(IntermediateEvent event) {
		if (!event.getTriggerType().equals(IntermediateEvent.TRIGGER_MESSAGE)) {
			return null;
		}

		return createReceiveElement(event, event.getTrigger(), event.getCreateInstance());
	}
	
	/**
	 * <p>Creates the BPEL4Chor "receive" element from an receive task.</p>
	 * 
	 * <p>The standard, messageExchange, correlation and fromParts attributes 
	 * and elements are taken from the task. 
	 * There will be no createInstance attribute generated (only for receiving 
	 * start events).</p>
	 * 
	 * <p>
	 * The correlation and fromParts elements
	 * are generated using the appropriate methods
	 * {@link SupportingFactory#createCorrelationsElement(List)} and
	 * {@link SupportingFactory#createFromPartsElement(List)}.
	 * </p>
	 * 
	 * <p>
	 * The variable attribute will only be generated, 
	 * if no fromParts element was specified and if the task does not define
	 * an opaque variable. The variable attribute is determined from a standard
	 * variable data object, that is associated with the task. 
	 * </p>
	 * 
	 * @param task	The task, to generate the receive element from. 
	 * 
	 * @return 		The generated BPEL4Chor "receive" element
	 */
	public Element createReceiveElement(ReceiveTask task) {
		Element result = this.document.createElement("receive");
		
		BPELUtil.setStandardAttributes(result, task);
		
		if (task.getMessageExchange() != null) {
			result.setAttribute("messageExchange", task.getMessageExchange());
		}
		
		result.setAttribute("createInstance", 
				BPELUtil.booleanToYesNo(task.isInstantiate()));
		
		Element correlations = 
			this.supportingFactory.createCorrelationsElement(
					task.getCorrelations());
		if (correlations != null) {
			result.appendChild(correlations);
		}
		
		Element fromParts = 
			this.supportingFactory.createFromPartsElement(task.getFromParts());
		VariableDataObject object = 
			this.diagram.getStandardVariable(task, true);
		
		if(task.isOpaqueOutput()) {
			result.setAttribute("variable", "##opaque");
		} else if (object != null) {
			result.setAttribute("variable", object.getName());
		} else if (fromParts != null) {
			result.appendChild(fromParts);
		} else {
			this.output.addError("The receive task must define an output variable.", task.getId());
		}
		
		return createScopeForAttachedHandlers(result, task);
	}
	
	/**
	 * <p>Creates the BPEL4Chor "invoke" element from an invoke task.</p>
	 * 
	 * <p>The standard, correlation and fromParts and toParts attributes 
	 * and elements are taken from the task.</p>
	 * 
	 * <p>
	 * The correlation, fromParts and toParts elements
	 * are generated using the appropriate methods
	 * {@link SupportingFactory#createCorrelationsElement(List)}, 
	 * {@link SupportingFactory#createFromPartsElement(List)} and 
	 * {@link SupportingFactory#createToPartsElement(List)}.
	 * </p>
	 * 
	 * <p>
	 * The inputVariable and outputVariable attribute will only be generated, 
	 * if no fromParts element was specified and if the task does not define
	 * an opaque input or output variable. 
	 * The variable attributes are determined from standard
	 * variable data objects, that are associated with the task. 
	 * </p>
	 * 
	 * <p>
	 * Fault and compensation Handlers that are present for the invoke task
	 * will be generated using the appropriate methods
	 * {@link StructuredElementsFactory#createFaultHandlerElements(model.activities.Activity)} and  
	 * {@link StructuredElementsFactory#createCompensationHandlerElement(model.activities.Activity)}. 
	 * </p>
	 * 
	 * @param task	The task, to generate the invoke element from. 
	 * 
	 * @return 		The generated BPEL4Chor "invoke" element
	 */
	public Element createInvokeElement(ServiceTask task) {
		Element invoke = this.document.createElement("invoke");
		
		BPELUtil.setStandardAttributes(invoke, task);
		
		Element correlations = 
			this.supportingFactory.createCorrelationsElement(
					task.getCorrelations());
		if (correlations != null) {
			invoke.appendChild(correlations);
		}
		
		// create fault and compensation handlers		
		List<Element> faultHandlers = 
			this.structuredElementsFactory.createFaultHandlerElements(task);
		for (Iterator<Element> it = faultHandlers.iterator(); it.hasNext();) {
			invoke.appendChild(it.next());
		}
		
		Element compensationHandler = 
			this.structuredElementsFactory.createCompensationHandlerElement(task);
		if (compensationHandler != null) {
			invoke.appendChild(compensationHandler);
		}
		
		Element toParts = 
			this.supportingFactory.createToPartsElement(task.getToParts());
		VariableDataObject object = 
			this.diagram.getStandardVariable(task, false);
		if(task.isOpaqueInput()) {
			invoke.setAttribute("inputVariable", "##opaque");
		} else if (object != null) {
			invoke.setAttribute("inputVariable", object.getName());
		} else if (toParts != null) {
				invoke.appendChild(toParts);
		} else {
			this.output.addError("The service task must define an input variable.", task.getId());
		}
		
		Element fromParts = 
			this.supportingFactory.createFromPartsElement(task.getFromParts());
		object = this.diagram.getStandardVariable(task, true);
		if(task.isOpaqueOutput()) {
			invoke.setAttribute("outputVariable", "##opaque");
		} else if (object != null) {
			invoke.setAttribute("outputVariable", object.getName());
		} else if (fromParts != null) {
			invoke.appendChild(fromParts);
		} else {
			this.output.addError("The service task must define an output variable.", task.getId());
		}
		
		Element terminationHandler = 
			this.structuredElementsFactory.createTerminationHandlerElement(task);
		if (terminationHandler != null) {
			Element scope = this.document.createElement("scope");
			scope.appendChild(terminationHandler);
			scope.appendChild(invoke);
			return scope;
		}
		return invoke;
	}
	
	/**
	 * <p>Creates the BPEL4Chor "invoke" element from a send task.</p>
	 * 
	 * <p>The standard, correlation and fromParts attributes 
	 * and elements are taken from the task.</p>
	 * 
	 * <p>
	 * The correlation and fromParts elements
	 * are generated using the appropriate methods
	 * {@link SupportingFactory#createCorrelationsElement(List)} and 
	 * {@link SupportingFactory#createFromPartsElement(List)}.
	 * </p>
	 * 
	 * <p>
	 * The inputVariable attribute will only be generated, 
	 * if no fromParts element was specified and if the task does not define
	 * an opaque input variable. 
	 * The variable attributes are determined from standard
	 * variable data objects, that are associated with the task. 
	 * </p>
	 * 
	 * <p>
	 * Fault and compensation Handlers that are present for the invoke task
	 * will be generated using the appropriate methods
	 * {@link StructuredElementsFactory#createFaultHandlerElements(model.activities.Activity)} and  
	 * {@link StructuredElementsFactory#createCompensationHandlerElement(model.activities.Activity)}. 
	 * </p>
	 * 
	 * @param task	The task, to generate the invoke element from. 
	 * 
	 * @return 		The generated BPEL4Chor "invoke" element
	 */
	public Element createInvokeElement(SendTask task) {
		Element invoke = this.document.createElement("invoke");
		
		BPELUtil.setStandardAttributes(invoke, task);
		
		Element correlations = 
			this.supportingFactory.createCorrelationsElement(
					task.getCorrelations());
		if (correlations != null) {
			invoke.appendChild(correlations);
		}
		
		Element compensationHandler = 
			this.structuredElementsFactory.createCompensationHandlerElement(task);
		if (compensationHandler != null) {
			invoke.appendChild(compensationHandler);
		}
		
		// create fault termination handlers but they must be located in an additional scope 
		List<Element> faultHandlers = 
			this.structuredElementsFactory.createFaultHandlerElements(task);
		Element terminationHandler = 
			this.structuredElementsFactory.createTerminationHandlerElement(task);
		if (faultHandlers.size() > 0 || (terminationHandler != null)) {
			Element scope = this.document.createElement("scope");
			
			if (faultHandlers.size() > 0) {
				Element faultHandlersElement = 
					this.document.createElement("faultHandlers");
				for (Iterator<Element> it = faultHandlers.iterator(); it.hasNext();) {
					faultHandlersElement.appendChild(it.next());
				}
				scope.appendChild(faultHandlersElement);
			}
			
			if (terminationHandler != null) {
				scope.appendChild(terminationHandler);
			}
			scope.appendChild(invoke);
			return scope;
		}
		
		Element toParts = 
			this.supportingFactory.createToPartsElement(task.getToParts());
		VariableDataObject object = 
			this.diagram.getStandardVariable(task, false);
		if(task.isOpaqueInput()) {
			invoke.setAttribute("inputVariable", "##opaque");
		} else if (object != null) {
			invoke.setAttribute("inputVariable", object.getName());
		} else if (toParts != null) {
			invoke.appendChild(toParts);
		} else {
			this.output.addError("The send task must define an input variable.", task.getId());
		}
		
		return invoke;
	}
	
	/**
	 * Checks if all message flows emanating from the service task 
	 * lead to the swimlane the given send task is located in.
     *
	 * @param task    The send task to determine the target swimlane.
	 * @param service The service task to check the message flows for.
	 * 
	 * @return True, if all message flow lead to the swimlane of
	 * the send task. False otherwise.
	 */
	private boolean isReplyServiceTask(SendTask task, ServiceTask service) {
		List<MessageFlow> flows = 
			this.diagram.getMessageFlowsWithSource(service.getId());
		for (Iterator<MessageFlow> it = flows.iterator(); it.hasNext();) {
			MessageFlow flow = it.next();
			if (flow.getTarget().getParentSwimlane().getId() != 
				task.getParentSwimlane().getId()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Creates an invoke or a reply element for the given send task.
	 * 
	 * If the sending task sends a message to a service task that has sent a
	 * message to swimlane of the send task before, a reply element will be
	 * created (see {@link #createReplyElement(SendTask)}). 
	 * Otherwise an invoke element is created 
	 * (see {@link #createInvokeElement(SendTask)}).
	 * 
	 * @param task The send task to create an invoke or reply element for
	 * 
	 * @return The created invoke or reply element.
	 */
	public Element createSendingElement(SendTask task) {
		List<MessageFlow> flows = this.diagram.getMessageFlowsWithSource(task.getId());
		// all flows must lead to a service task to create a reply element from the send task
		boolean reply = false;
		for (Iterator<MessageFlow> it = flows.iterator(); it.hasNext();) {
			MessageFlow flow = it.next();
			if ((flow.getTarget() != null) && (flow.getTarget() instanceof ServiceTask)) {
				if (isReplyServiceTask(task, (ServiceTask)flow.getTarget())) {
					reply = true;
				}
			} 
		}
		
		if (reply) {
			return createReplyElement(task);
		}
		return createInvokeElement(task);
	}
	
	/**
	 * Creates an scope element containing the given content. Moreover,
	 * fault handlers, a compensation handler and a termination handler
	 * is created in this scope from handlers modeled for the given task.
	 * 
	 * If the task does not define any handlers, there is no need to 
	 * create an additional scope. In this case the given content
	 * is returned.
	 * 
	 * @param content The content enclosed by the scope.
	 * @param task    The task that provides the handlers to be created
	 *                in the scope.
	 *                
	 * @return The created scope enclosing the created handlers and the given
	 * content. If a scope is not necessary, the given content is returned.
	 */
	private Element createScopeForAttachedHandlers(Element content, Task task) {
		// create fault, compensation and termination handlers but they must 
		// be located in an additional scope 
		List<Element> faultHandlers = this.structuredElementsFactory.
			createFaultHandlerElements(task);
		Element compensationHandler = this.structuredElementsFactory.
			createCompensationHandlerElement(task);
		Element terminationHandler = 
			this.structuredElementsFactory.createTerminationHandlerElement(task);
		
		if ((faultHandlers.size() > 0) || (compensationHandler != null) || 
				(terminationHandler != null)) {
			
			Element scope = this.document.createElement("scope");
			scope.setAttribute("name", BPELUtil.generateScopeName(task));
			if (faultHandlers.size() > 0) {
				Element faultHandlersElement = 
					this.document.createElement("faultHandlers");
				for (Iterator<Element> it = faultHandlers.iterator(); it.hasNext();) {
					faultHandlersElement.appendChild(it.next());
				}
				scope.appendChild(faultHandlersElement);
			}
			
			if (compensationHandler != null) {
				scope.appendChild(compensationHandler);
			}
			
			if (terminationHandler != null) {
				scope.appendChild(terminationHandler);
			}
			scope.appendChild(content);
			return scope;
		}
		return content;
	}
	
	/**
	 * <p>Creates the BPEL4Chor "reply" element from an reply task.</p>
	 * 
	 * <p>The standard, messageExchange, faultName, correlation and ToParts
	 * attributes and elements are taken from the task.</p>
	 * 
	 * <p>
	 * The correlation and toParts elements
	 * are generated using the appropriate methods
	 * {@link SupportingFactory#createCorrelationsElement(List)} and 
	 * {@link SupportingFactory#createToPartsElement(List)}.
	 * </p>
	 * 
	 * <p>
	 * The variable attribute will only be generated, 
	 * if no toParts element was specified and if the task does not define
	 * an opaque variable. 
	 * The variable attributes are determined from standard
	 * variable data objects, that are associated with the task. 
	 * </p>
	 * 
	 * @param task	The task, to generate the reply element from. 
	 * 
	 * @return 		The generated BPEL4Chor "reply" element
	 */
	private Element createReplyElement(SendTask task) {
		Element reply = this.document.createElement("reply");
		
		BPELUtil.setStandardAttributes(reply, task);
		
		if (task.getMessageExchange() != null) {
			reply.setAttribute("messageExchange", task.getMessageExchange());
		}
		
		if (task.getFaultName() != null) {
			reply.setAttribute("faultName", task.getFaultName());
		}
		
		Element correlations = 
			this.supportingFactory.createCorrelationsElement(
					task.getCorrelations());
		if (correlations != null) {
			reply.appendChild(correlations);
		}
		
		Element toParts = 
			this.supportingFactory.createToPartsElement(task.getToParts());
		VariableDataObject object = 
			this.diagram.getStandardVariable(task, false);
		if(task.isOpaqueInput()) {
			reply.setAttribute("variable", "##opaque");
		} else if (object != null) {
			reply.setAttribute("variable", object.getName());
		} else if (toParts != null) {
			reply.appendChild(toParts);
		} else {
			this.output.addError("The send task must define an input variable.", task.getId());
		}
		
		return createScopeForAttachedHandlers(reply, task);
	}
	
	/**
	 * <p>Creates the BPEL4Chor "assign" element from an assign task.</p>
	 * 
	 * <p>The standard, validate and copy 
	 * attributes and elements are taken from the task.</p>
	 * 
	 * <p>
	 * The copy elements are generated using the appropriate method
	 * {@link #createCopyElement(Copy, AssignTask)}.
	 * </p>
	 * 
	 * @param task	The task, to generate the assign element from. 
	 * 
	 * @return 		The generated BPEL4Chor "assign" element
	 */
	public Element createAssignElement(AssignTask task) {
		Element assign = this.document.createElement("assign");
		
		BPELUtil.setStandardAttributes(assign, task);
		
		if (task.getValidate() != null) {
			assign.setAttribute("validate", task.getValidate());
		}
		
		List<Copy> copy = task.getCopyElements();
		if (copy.isEmpty()) {
			this.output.addError("The assign task must define at least one copy statement.", task.getId());
			return assign;
		}
		for (Iterator<Copy> it = copy.iterator(); it.hasNext();) {
			Element copyElement = createCopyElement(it.next(), task);
			if (copyElement != null) {
				assign.appendChild(copyElement);
			}
		}
		
		return createScopeForAttachedHandlers(assign, task);
	}
	
	/**
	 * <p>Creates the BPEL4Chor "empty" element from an empty task.</p>
	 * 
	 * <p>The standard attributes are taken from the task.</p>
	 * 
	 * @param task	The task, to generate the empty element from. 
	 * 
	 * @return 		The generated BPEL4Chor "empty" element
	 */
	public Element createEmptyElement(EmptyTask task) {
		Element empty = this.document.createElement("empty");
		
		BPELUtil.setStandardAttributes(empty, task);
		
		return createScopeForAttachedHandlers(empty, task);
		
	}
	
	/**
	 * <p>Creates the BPEL4Chor "opaqueActivity" element from an opaque task.</p>
	 * 
	 * <p>The standard attributes are taken from the task.</p>
	 * 
	 * @param task	The task, to generate the "opaqueActivity" element from. 
	 * 
	 * @return 		The generated BPEL4Chor "opaqueActivity" element
	 */
	public Element createOpaqueElement(NoneTask task) {
		Element opaque = this.document.createElement("opaqueActivity");
		
		BPELUtil.setStandardAttributes(opaque, task);
		
		return createScopeForAttachedHandlers(opaque, task);
	}
	
	/**
	 * <p>Creates the BPEL4Chor "compensate" or "compensateScope" element 
	 * from an intermediate event.</p>
	 * 
	 * <p>The standard attributes are taken from the event.
	 * If the trigger defines an activity to compensate, an "compensateScope" 
	 * element will be generated with the target attribute set to this activity.</p>
	 * 
	 * @param event The event, to generate the compensate of compensateScope element from. 
	 * 
	 * @return 		The generated BPEL4Chor "compensate" or "compensateScope" element
	 */
	public Element createCompensateElement(IntermediateEvent event) {
		Element result = null;
		if ((event.getTrigger() != null) && (event.getTrigger() instanceof ResultCompensation)) {
			ResultCompensation trigger = (ResultCompensation)event.getTrigger();
			if (trigger.getActivity() != null) {
				Activity act = trigger.getActivity();
				String name = null;
				if (act instanceof Task) {
					if (act instanceof ServiceTask) {
						name = act.getName();
					} else {
						// task must have an attached compensation event
						if (act.getAttachedEvents(
							IntermediateEvent.TRIGGER_COMPENSATION).isEmpty()) {
							
							this.output.addError("The task must have an attached compensation event to be compensated.", act.getId());
							return null;
						}
						// scope created around task is compensated
						name = BPELUtil.generateScopeName(act);
					}
				} else {
					name = act.getName();
				}
				result = this.document.createElement("compensateScope");
				result.setAttribute("target", name);
				return result;
			}
		} 
		result = this.document.createElement("compensate");
		BPELUtil.setStandardAttributes(result, event);
		return result;
	}
	
	/**
	 * <p>Creates the BPEL4Chor "throw" or "rethrow" element 
	 * from an intermediate event.</p>
	 * 
	 * <p>The standard attributes are taken from the event.
	 * If the trigger defines an error code a "throw" 
	 * element will be generated with the faultName attribute set 
	 * to the defined error code. In this case also the faultVariable 
	 * attribute may be generated if a standard variable is associated with the event. 
	 * </p>
	 * 
	 * @param event 			The event, to generate the throw of rethrow 
	 * 							element from. 
	 * @param rethrowAllowed 	True, if a rethrow element is allowed in the 
	 * 							context of the event, false otherwise.
	 * 
	 * @return 		The generated BPEL4Chor "throw" or "rethrow" element. 
	 * 				The result is null, if the trigger type of the event is not
	 * 				an error trigger or if a rethrow should be generated 
	 * 				although it is not allowed in this context.
	 */
	public Element createThrowElement(IntermediateEvent event, boolean rethrowAllowed) {
		Element result = null;
		if (!event.getTriggerType().equals(IntermediateEvent.TRIGGER_ERROR)) {
			return result;
		}
		if (event.getTrigger() == null) {
			if (rethrowAllowed) {
				result = this.document.createElement("rethrow");
				BPELUtil.setStandardAttributes(result, event);
			} else {
				this.output.addError("The activity must define an error code, because a rethrow is not allowed in this context.", event.getId());
				return null;
			}
		} else if (event.getTrigger() instanceof ResultError) {
			ResultError trigger = (ResultError)event.getTrigger();
			if (trigger.getErrorCode() == null || trigger.getErrorCode().equals("")) {
				if (rethrowAllowed) {
					result = this.document.createElement("rethrow");
					BPELUtil.setStandardAttributes(result, event);
				} else {
					this.output.addError("The activity must define an error code because a rethrow is not allowed in this context.", event.getId());
					return null;
				}
			} else {
				result = this.document.createElement("throw");
				BPELUtil.setStandardAttributes(result, event);
				result.setAttribute("faultName", trigger.getErrorCode());
				VariableDataObject faultVariable = 
					this.diagram.getStandardVariable(event, false);
				if (faultVariable != null) {
					result.setAttribute("faultVariable", faultVariable.getName());
				}
			}
		}
		return result;
	}
	
	/**
	 * <p>Creates the BPEL4Chor "wait" element from an intermediate event.</p>
	 * 
	 * <p>The standard attributes are taken from the event.</p>
	 * 
	 * <p>The triger of the event must either define a time deadline of a time duration.
	 * The until of for attributes will be generated from this using the method 
	 * {@link SupportingFactory#createExpressionElement(String, model.supporting.Expression)}.</p>
	 * 
	 * @param event 			The event, to generate the wait element from. 
	 * 
	 * @return 		The generated BPEL4Chor wait element. 
	 * 				The result is null, if the trigger type of the event is not
	 * 				a time trigger or if the event trigger does not define 
	 * 				a time duration or a time deadline.
	 */
	public Element createWaitElement(IntermediateEvent event) {
		Element result = null;
		if ((event.getTrigger() != null) && 
				(event.getTrigger() instanceof TriggerTimer)) {
			
			TriggerTimer trigger = (TriggerTimer)event.getTrigger();
			result = this.document.createElement("wait");
			BPELUtil.setStandardAttributes(result, event);
			
			if (trigger.getTimeDeadlineExpression() != null) {
				result.appendChild(this.supportingFactory.createExpressionElement(
						"until", trigger.getTimeDeadlineExpression()));
			} else if (trigger.getTimeDurationExpression() != null) {
				result.appendChild(this.supportingFactory.createExpressionElement(
						"for", trigger.getTimeDurationExpression()));
			} else {
				this.output.addError("The duration or deadline expression of the wait activity could not be generated.", event.getId());
				return null;
			}
		} else {
			this.output.addError("The event must define a timer trigger element.", event.getId());
			return null;
		}
		return result;
	}
	
	/**
	 * <p>Creates the BPEL4Chor "validate" element from a validate task.</p>
	 * 
	 * <p>The standard attributes are taken from the task.</p>
	 * 
	 * <p>The variables to validate are generated from the standard variable 
	 * data objects associated with the validate element.</p>
	 * 
	 * @param task The task, to generate the validate element from. 
	 * 
	 * @return 		The generated BPEL4Chor validate element. 				
	 */
	public Element createValidateElement(ValidateTask task) {
		Element result = this.document.createElement("validate");
		BPELUtil.setStandardAttributes(result, task);
		
		List<VariableDataObject> variables = 
			this.diagram.getStandardVariables(task, false);
		String variablesStr = "";
		for (Iterator<VariableDataObject> it = variables.iterator(); it.hasNext();) {
			if (variablesStr.equals("")) {
				variablesStr += it.next().getName();
			} else {
				variablesStr += " " + it.next().getName();
			}
		}
		
		if (variablesStr.equals("")) {
			this.output.addError("The variables for this validate task could not be determined.", task.getId() );
		} else {
			result.setAttribute("variables", variablesStr);
		}
		
		return createScopeForAttachedHandlers(result, task);
	}
}
