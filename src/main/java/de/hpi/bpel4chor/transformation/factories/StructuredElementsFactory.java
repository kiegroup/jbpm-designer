package de.hpi.bpel4chor.transformation.factories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.hpi.bpel4chor.model.Container;
import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.SubProcess;
import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.activities.BlockActivity;
import de.hpi.bpel4chor.model.activities.Handler;
import de.hpi.bpel4chor.model.activities.IntermediateEvent;
import de.hpi.bpel4chor.model.activities.ReceiveTask;
import de.hpi.bpel4chor.model.activities.ResultError;
import de.hpi.bpel4chor.model.activities.Scope;
import de.hpi.bpel4chor.model.activities.StartEvent;
import de.hpi.bpel4chor.model.activities.Task;
import de.hpi.bpel4chor.model.activities.Trigger;
import de.hpi.bpel4chor.model.activities.TriggerResultMessage;
import de.hpi.bpel4chor.model.activities.TriggerTimer;
import de.hpi.bpel4chor.model.artifacts.VariableDataObject;
import de.hpi.bpel4chor.model.connections.Association;
import de.hpi.bpel4chor.model.supporting.Import;
import de.hpi.bpel4chor.model.supporting.Loop;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import de.hpi.bpel4chor.util.BPELUtil;
import de.hpi.bpel4chor.util.Output;

/**
 * This factory generates BPEL4Chor activity element that can have
 * child activities (e.g. scope, while, catch).
 * 
 * <p>An instance of this class can only be used for one diagram.</p>
 */
public class StructuredElementsFactory {

	private Diagram diagram = null;
	private Document document = null;
	private SupportingFactory supportingFactory = null;
	private Output output = null;
	
	/**
	 * Constructor. Initializes the structured elements factory with the
	 * diagram that contains the activities to be transformed.
	 *  
	 * @param diagram   The diagram to be transformed.
	 * @param document  The document to create the BPEL4Chor elements for.
	 * @param output    The {@link Output} to print errors to. 
	 */
	public StructuredElementsFactory(Diagram diagram, Document document, Output output) {
		this.diagram = diagram;
		this.document = document;
		this.output = output;
		this.supportingFactory = new SupportingFactory(diagram, document, this.output);
	}
	
	/**
	 * <p>If a handler contains standard variable data objects an
	 * additional scope has to be created for it that defines these variables. 
	 * The given content will be added to the scope. If there are no variables,
	 * the content will be returned instead of the scope element.</p>
	 * 
	 * @param handler The handler to create the scope element for
	 * @param content The content that will be contained in the scope
	 * 
	 * @return The created "scope" element or the given content element
	 * if no variables are present in the handler.
	 */
	private Element createHandlerScope(Handler handler, Element content) {
		Element scope = this.document.createElement("scope");
		Element variablesElement = this.supportingFactory.createVariablesElement(
					handler.getParentSwimlane(), handler.getSubProcess());
		
		if (variablesElement != null) {
			scope.appendChild(variablesElement);
			scope.appendChild(content);
			return scope;
		} 
		return content;
	}
	
	/**
	 * Creates a "catch" element that is represented by the given fault event and
	 * fault handler. The fault variable of the catch element is determind
	 * by a fault variable data object, that is associated with the given event.
	 * The sequence flow within the fault handler represents the activities 
	 * that will be contained in the "catch" element.
	 * 
	 * <p>If the fault handler containes standard variable data objects an
	 * additional scope will be created that defines these variables. If
	 * the fault variable is not defined correctly (e.g missing prefix or
	 * missing import for prefix in swimlane) an error is added to the output
	 * and the result will be null.</p>
	 * 
	 * @param event     The fault event the fault handler is connected with.
	 * @param handler   The fault handler that holds the sequence flow that 
	 *                  holds the sequence flow generated into the catch element.
	 * @param errorCode The fault name of the fault that is caught by the 
	 *                  element to be created. 
	 * 
	 * @return The created "catch" element or null if an error occured.
	 */
	private Element createCatchElement(IntermediateEvent event, 
			Handler handler, String errorCode) {
		Element result = this.document.createElement("catch");
		result.setAttribute("faultName", errorCode);
		
		VariableDataObject faultVar = this.diagram.getFaultVariable(event);
		if (faultVar != null) {
			result.setAttribute("faultVariable", faultVar.getName());
			
			String value = faultVar.getVariableTypeValue();
			String prefix = value.substring(0, value.indexOf(':'));
			if (value.indexOf(':') < 0) {
				this.output.addError("There is a prefix missing for the " +
						"variable type value "+ value + 
						" of this fault variable ", faultVar.getId());
				return null;
			}
			Import imp = event.getParentSwimlane().getImportForPrefix(prefix);
			if (imp == null) {
				this.output.addError(
						"There is an import element missing for the prefix " 
						+ prefix + " of this fault variable ", faultVar.getId());
				return null;
			}
			
			if (faultVar.getVariableType().equals(
					VariableDataObject.VARIABLE_TYPE_MESSAGE)) {	
				result.setAttribute("faultMessageType", value); 			
			} else if (faultVar.getVariableType().equals(
					VariableDataObject.VARIABLE_TYPE_XML_ELEMENT)) {
				result.setAttribute("faultElement", value);
			} else {
				this.output.addError(
						"The type of the fault variable defined in this handler " +
						 "cannot be determined.", handler.getId());
				return null;
			}
		}
	
		Element sequenceFlow = new SequenceFlowFactory(
				this.diagram, this.document, handler.getSubProcess(), 
					this.output).transformSequenceFlow();
		if (sequenceFlow != null) {
			// create additional scope if fault handler has
			// additional variables defined
			result.appendChild(createHandlerScope(handler, sequenceFlow));
		}
		
		return result;
	}
	
	/**
	 * Creates a "catchAll" element that is represented by the given fault event
	 * and fault handler. The sequence flow within the fault handler represents 
	 * the activities that will be contained in the "catch" element. The "catchAll"
	 * element should be created if the fault event does not specifiy an error code.
	 *
	 * @param handler   The fault handler that holds the sequence flow that 
	 *                  holds the sequence flow generated into the catch element.
	 * 
	 * @return The created "catchAll" element.
	 */
	private Element createCatchAllElement(Handler handler) {
		Element result = this.document.createElement("catchAll");
		
		Element sequenceFlow = 
			new SequenceFlowFactory(
					this.diagram, this.document, handler.getSubProcess(), 
					this.output).transformSequenceFlow();
		
		if (sequenceFlow != null) {
			// create additional scope if fault handler has
			// additional variables defined
			result.appendChild(createHandlerScope(handler, sequenceFlow));
		}
		return result;
	}
	
	/** 
	 * Creates the "catch" and "catchAll" elements for the fault handlers
	 * attached to the given activity. An attached error event that does
	 * not specify an error code is mapped to a "catchAll" element. The
	 * "catchAll" element is located at the end of the list. 
	 * 
	 * <p>If an error event is not connected with an error handler, 
	 * an error is added to the output and there will be no element
	 * created for this event. This will also be done if there are more
	 * than one events with no specified error code.</p> 
	 * 
	 * @param activity The activity the error events are attached to.
	 * 
	 * @return A list with the created "catch" and "catchAll" elements.
	 * If there is no error event attached to the activity the list is empty.
	 */
	public List<Element> createFaultHandlerElements(Activity activity) {
		// catchAll element must be positioned at the end of the list
		List<Element> result = new ArrayList<Element>();
		List<IntermediateEvent> attachedErrorEvents = 
			activity.getAttachedEvents(IntermediateEvent.TRIGGER_ERROR);
		
		Element catchAllElement = null;
		for (Iterator<IntermediateEvent> it = 
			attachedErrorEvents.iterator(); it.hasNext();) {
			
			IntermediateEvent event = it.next();
			ResultError trigger = null;
			if (event.getTrigger() instanceof ResultError) {
				trigger = (ResultError)event.getTrigger();
			}
			Handler errorHandler = event.getConnectedHandler(); 
			
			if (errorHandler == null) {
				this.output.addError("The error event " +
					"is not connected with an error handler.", event.getId());
				continue;
			}
			
			if ((trigger != null) && (trigger.getErrorCode() != null) && 
					!trigger.getErrorCode().equals("")) {
				
				Element element = createCatchElement(
						event, errorHandler, trigger.getErrorCode());
				if (element != null) {
					result.add(element);
				}
			} else if (catchAllElement == null){
				Element element = createCatchAllElement(errorHandler);
				if (element != null) {
					catchAllElement = element;
				}
			} else {
				this.output.addError(
					"There is more than one error event without a specified" +
					" error name attached to this activity ", activity.getId());
			}
		}
		
		if (catchAllElement != null) {
			result.add(catchAllElement);
		}
		return result;
	}
	
	/**
	 * Creates the "faultHandlers" element for the given activity containing 
	 * "catch" and "catchAll" elements for each attached error event.
	 * 
	 * @param activity The activity to create the "faultHandlers" element for.
	 * 
	 * @return The created "faultHandlers" element or null, if the activity
	 * has no attached error events.
	 */
	public Element createFaultHandlersElement(Activity activity) {
		
		List<Element> toAppend = createFaultHandlerElements(activity);
		
		if (toAppend.isEmpty()) {
			return null;
		}
		
		Element result = this.document.createElement("faultHandlers");
		for (Iterator<Element> it = toAppend.iterator(); it.hasNext();) {
			result.appendChild(it.next());
		}
		return result;
	}
	
	/**
	 * Creates a "terminationHandler" element, if the activity has a 
	 * termination event attached. The sequence flow within the termination
	 * handler represents the activities that will be contained in the 
	 * "terminationHandler" element.
	 * 
	 * <p>If there are multiple termination events attached to the 
	 * activity an error is added to the output and the result will be null.
	 * This will also be done if there is no termination handler connected
	 * to the termination event.</p>
	 * 
	 * @param activity The activity to create the "terminationHandler" 
	 *                 element for
	 * 
	 * @return The created "terminationHandler" element or null if there are no
	 * termination events attached to the activity. The result will also be 
	 * null if an error occured.
	 */
	public Element createTerminationHandlerElement(Activity activity) {
		
		List<IntermediateEvent> attachedTermEvents = 
			activity.getAttachedEvents(IntermediateEvent.TRIGGER_TERMINATION);
		
		if (attachedTermEvents.size() > 1) {
			this.output.addError("The activity " +
				"is not allowed to have more than one termination event attached. ", activity.getId());
			return null;
		} 
		
		if (!attachedTermEvents.isEmpty()) {
			IntermediateEvent event = attachedTermEvents.get(0);
			Handler termHandler = event.getConnectedHandler();
			
			if (termHandler == null) {
				this.output.addError("A termination handler attached the event " +
						"could not be found.", event.getId());
				return null;
			}
			
			Element result = this.document.createElement("terminationHandler");
			Element sequenceFlow = new SequenceFlowFactory(
					this.diagram,this.document, termHandler.getSubProcess(), 
					this.output).transformSequenceFlow();
			if (sequenceFlow != null) {							
				// create additional scope if termination handler has
				// additional variables defined
				result.appendChild(createHandlerScope(termHandler, sequenceFlow));
			}
			
			return result;
		}
		return null;
	}
	
	/**
	 * Creates a "compensationHandler" element, if the activity has a 
	 * compensation event attached. The sequence flow within the compensation
	 * handler represents the activities that will be contained in the 
	 * "compensationHandler" element.
	 * 
	 * <p>If there are multiple compensation events attached to the 
	 * activity an error is added to the output and the result will be null.
	 * This will also be done if there is no compensation handler connected
	 * to the termination event.</p>
	 * 
	 * @param activity The activity to create the "compensationHandler" element
	 *                 for
	 * 
	 * @return The created "terminationHandler" element or null if there are no
	 * termination events attached to the activity. The result will also be 
	 * null if an error occured.
	 */
	public Element createCompensationHandlerElement( Activity activity) {

		List<IntermediateEvent> attachedCompEvents = 
			activity.getAttachedEvents(IntermediateEvent.TRIGGER_COMPENSATION);
		if (attachedCompEvents.size() > 1) {
			this.output.addError(
				"The activity is not allowed to " +
				"have more than one compensation event attached.",
				activity.getId());
		} 
		if (!attachedCompEvents.isEmpty()) {
			IntermediateEvent event = attachedCompEvents.get(0);
			Handler compHandler = 
				this.diagram.getAssociatedCompensationHandler(event);
			if (compHandler == null) {
				this.output.addError("A compensation handler attached to this event " +
						"could not be found.", event.getId());
				return null;
			}
			
			Element result = this.document.createElement("compensationHandler");
			Element sequenceFlow = new SequenceFlowFactory(
					this.diagram, this.document, compHandler.getSubProcess(), 
					this.output).transformSequenceFlow();
			if (sequenceFlow != null) {
				// create additional scope if compensation handler has
				// additional variables defined
				result.appendChild(createHandlerScope(compHandler, sequenceFlow));
			}
			return result;
		}
		return null;
	}
	
	/**
	 * Creates a "scope" element from the given block activity. The sequence flow
	 * within the sub-process of the block activity represents the activities 
	 * that will be contained in the created "scope" element.
	 * 
	 * @param activity       The activity to ceate the "scope" element from.
     *
	 * @return The created "scope" element.
	 */
	public Element createScopeElement(BlockActivity activity) {
		Element result = this.document.createElement("scope");
		
		// attributes
		if (activity.getExitOnStandardFault() != null) {
			result.setAttribute("exitOnStandardFault", activity.getExitOnStandardFault());
		}
		if (activity.getIsolated() != null) {
			result.setAttribute("isolated", activity.getIsolated());
		}
		
		// create elements
		Element variables = this.supportingFactory.createVariablesElement(
					activity.getParentSwimlane(), activity.getSubProcess());
		
		Element messageExchanges = null;
		Element correlationSets = null;
		if (activity instanceof Scope) {
			Scope scope = (Scope)activity;
			messageExchanges = 
				this.supportingFactory.createMessageExchangesElement(scope);
			correlationSets = 
				this.supportingFactory.createCorrelationSetsElement(scope);
		}
		
		Element faultElement = createFaultHandlersElement(activity);
		Element compElement = createCompensationHandlerElement(activity);
		Element terminationElement = createTerminationHandlerElement(activity);		
		Element eventHandlers = createEventHandlersElement(
				activity.getSubProcess());
		Element sequenceFlow = new SequenceFlowFactory(
				this.diagram, this.document, activity.getSubProcess(), 
				this.output).transformSequenceFlow();
		
		// keep order of elements
		if (messageExchanges != null) {
			result.appendChild(messageExchanges);
		}
		if (variables != null) {
			result.appendChild(variables);
		}
		if (correlationSets != null) {
			result.appendChild(correlationSets);
		}
		if (faultElement != null) {
			result.appendChild(faultElement);
		}
		if (compElement != null) {
			result.appendChild(compElement);
		}
		if (terminationElement != null) {
			result.appendChild(terminationElement);
		}
		if (eventHandlers != null) {
			result.appendChild(eventHandlers);
		}
		if (sequenceFlow != null) {
			result.appendChild(sequenceFlow);
		}
		
		return result;
	}
	
	/**
	 * Sets the variable attributes for an "onEvent" element.
	 * 
	 * <p>If there is a prefix missing for the variable type or an import
	 * missing for the prefix an error is added to the output. If the
	 * type of the message variable is not defined correctly an error
	 * is added to the output, too.</p>
	 * 
	 * @param messageVar The data object representing the message variable.
	 * @param handler    The handler that represents the "onEvent" element
	 * @param result     The "onEvent" element to create the attributes for.
	 */
	private void setMessageVariable(VariableDataObject messageVar, Handler handler, Element result) {
		if (messageVar != null) {
			result.setAttribute("variable", messageVar.getName());
			
			String value = messageVar.getVariableTypeValue();
			String prefix = value.substring(0, value.indexOf(':'));
			if (value.indexOf(':') < 0) {
				this.output.addError(
					"There is a prefix missing for the variable type value " +
					value + "of this message variable", messageVar.getId());
			}
			Import imp = handler.getParentSwimlane().getImportForPrefix(prefix);
			if (imp == null) {
				this.output.addError(
					"There is an import element missing for the prefix " +
					prefix + "of this message variable", messageVar.getId());
			}
			if (messageVar.getVariableType().equals(
					VariableDataObject.VARIABLE_TYPE_MESSAGE)) {	
				result.setAttribute("messageType", value); 			
			} else if (messageVar.getVariableType().equals(
					VariableDataObject.VARIABLE_TYPE_XML_ELEMENT)) {
				result.setAttribute("element", value);
			} else {
				this.output.addError(
					"The type of the message variable defined here " +
					"cannot be determined.", handler.getId());
			}
		}
	}
	
	/**
	 * Appends a "scope" element containing the sequence flow of the
	 * handler to the given result element. If the scope could not
	 * be generated correclty an error is added to the output and
	 * there will be no element appended to the result element.
	 *
	 * @param handler The handler to create a "scope" element from.
	 * @param result  The result element to append the created "scope" element
	 *                to.
	 */
	private void appendHandlerScope(Handler handler, Element result) {
		Element scopeElement = createScopeElement(handler);
		if (scopeElement != null) {
			result.appendChild(scopeElement);
		} else {
			this.output.addError("The content of the event handler " +
					"could not be generated properly.", handler.getId());
		}
	}
	
	/**
	 * Creates an "onEvent" element from the given message event handler. The
	 * sequence flow within the event handler represents the activities that
	 * will be contained in the created "onEvent" element.
	 * 
	 * <p>If the sequence flow of the handle could not be generated correctly,
	 * an error is added to the output.</p>
	 * 
	 * @param handler The handler to create the "onEvent" element from.
	 * 
	 * @return The created "onEvent" element.
	 */
	private Element createMessageHandlerElement(Handler handler) {
		Element result = this.document.createElement("onEvent");
		StartEvent start = handler.getMessageEventHandlerStart(this.output);
		if (start == null) {
			return null;
		}
		result.setAttribute("wsu:id", start.getName());
		Trigger startTrigger = start.getTrigger();
		boolean opaqueVar = false;
		if (startTrigger instanceof TriggerResultMessage) {
			// message Exchange
			TriggerResultMessage trigger = (TriggerResultMessage)startTrigger;
			String messageExchange = trigger.getMessageExchange();
			if (messageExchange != null) {
				result.setAttribute("messageExchange", messageExchange);
			}
			
			if (trigger.isOpaqueOutput()) {
				result.setAttribute("variable", "##opaque");
				opaqueVar = true;
			}
			
			// fromParts
			Element fromPartsElement = this.supportingFactory.
				createFromPartsElement(trigger.getFromParts());
			if (fromPartsElement != null) {
				result.appendChild(fromPartsElement);
			}
			// correlations
			Element correlationsElement = this.supportingFactory.
				createCorrelationsElement(trigger.getCorrelations());
			if (correlationsElement != null) {
				result.appendChild(correlationsElement);
			}
		}
		
		if (!opaqueVar) {
			// set variable attributes
			VariableDataObject messageVar =
				this.diagram.getMessageVariable(start);
			setMessageVariable(messageVar, handler, result);
		}
		
		appendHandlerScope(handler, result);
		
		return result;
	}
	
	/**
	 * Creates an "onAlarm" element from the given timer event handler. The
	 * sequence flow within the event handler represents the activities that
	 * will be contained in the created "onAlarm" element.
	 * 
	 * <p>If the sequence flow of the handle could not be generated correctly,
	 * an error is added to the output.</p>
	 * 
	 * @param handler The handler to create the "onAlarm" element from.
	 * 
	 * @return The created "onAlarm" element.
	 */
	private Element createTimerHandlerElement(Handler handler) {
		Element result = this.document.createElement("onAlarm");
		
		StartEvent start = handler.getTimerEventHandlerStart(this.output);
		if (start == null) {
			return null;
		}
		Trigger startTrigger = start.getTrigger();
		if (startTrigger instanceof TriggerTimer) {
			
			TriggerTimer trigger = (TriggerTimer)startTrigger;
			
			if (trigger.getTimeDeadlineExpression() != null) {
				Element expression = this.supportingFactory.createExpressionElement(
					"until", trigger.getTimeDeadlineExpression());
				result.appendChild(expression);
			} else if (trigger.getTimeDurationExpression() != null) {
				Element expression = this.supportingFactory.createExpressionElement(
					"for", trigger.getTimeDurationExpression());
				result.appendChild(expression);
			}
			
			if (trigger.getRepeatEveryExpression() != null) {
				Element expression = this.supportingFactory.createExpressionElement(
						"repeatEvery", trigger.getRepeatEveryExpression());
					result.appendChild(expression);
			}
		}
		
		appendHandlerScope(handler, result);
		
		return result;
	}
	
	/**
	 * Creates an "eventHandlers" element containing the "onEvent" and "onAlarm"
	 * elements represented by the event handlers in the container. 
	 * 
	 * @param container The container that contains the event handlers to be created
	 *                  in the "eventHandlers" element.
	 * 
	 * @return The created "eventHandlers" element.
	 */
	public Element createEventHandlersElement(Container container) {
		List<Handler> messageHandlers = container.getHandlers(Handler.TYPE_MESSAGE);
		List<Handler> timerHandlers = container.getHandlers(Handler.TYPE_TIMER);
		if (messageHandlers.isEmpty() && timerHandlers.isEmpty()) {
			return null;
		}
		
		Element result = this.document.createElement("eventHandlers");
		for (Iterator<Handler> it = messageHandlers.iterator(); it.hasNext();) {
			Element element = createMessageHandlerElement(it.next());
			if (element != null) {
				result.appendChild(element);
			}
		}
		
		for (Iterator<Handler> it = timerHandlers.iterator(); it.hasNext();) {
			Element element = createTimerHandlerElement(it.next());
			if (element != null) {
				result.appendChild(element);
			}
		}
		
		return result;
	}
	
	/**
	 * Generates a counter variable data object that acts as loop counter for
	 * the given looping task. The counter variable data object has a generated
	 * name using the pattern: loopName + "_generatedCounter". Since the loop 
	 * counter can not be located in the task, it will be located in the parent
	 * container of the task.
	 * 
	 * <p>In addition to the data object an association will be created that
	 * association the counter with the looping task.</p>
	 * 
	 * @param task     The looping task to generate the loop counter for.
	 * @param loopName The name of the loop, created from the task.
	 * 
	 * @return The create counter variable data object that acts as loop 
	 *         counter for the task.
	 */
	private VariableDataObject generateLoopCounter(Task task, String loopName) {
		VariableDataObject counter = new VariableDataObject(this.output);
		counter.setType(VariableDataObject.TYPE_COUNTER);
		counter.setId(task.getId() + "_generatedCounter");
		counter.setName(loopName + "_generatedCounter");
		counter.setContainer(task.getParentContainer());
		this.diagram.addVariableDataObject(counter);
		Association assoc = new Association(this.output);
		assoc.setDirection(Association.DIRECTION_TO);
		assoc.setSource(task);
		assoc.setTarget(counter);
		assoc.setId(task.getId() + "generatedCounterAssoc");
		this.diagram.addAssociation(assoc);
		return counter;
	}
	
	/**
	 * Appends the scope content to the given result element. Of the scope content
	 * is null and the activity is a block activity the scope content will be created
	 * using the {@link SequenceFlowFactory}. If the scope content is already a 
	 * "scope" element it will be appended as it is. Otherwise an additional
	 * "scope" element will be created that contains the scope content.
	 * 
	 * @param activity     The activity that represents the loop.
	 * @param scopeContent The element that represents the sequence flow of the loop. 
	 * @param result       The element to add the scope content to.
	 */
	private void appendLoopScope(Activity activity, Element scopeContent, Element result) {
		if (scopeContent != null) {
			if (!scopeContent.getNodeName().equals("scope")) {
				Element scope = this.document.createElement("scope");
				scope.appendChild(scopeContent);
				result.appendChild(scope);
			} else {
				result.appendChild(scopeContent);
			}
		} else if (activity instanceof BlockActivity){
			// generate sequence flow
			SubProcess subProcess = ((BlockActivity)activity).getSubProcess();
			Element sequenceFlow = new SequenceFlowFactory(
					this.diagram, this.document,subProcess, 
					this.output).transformSequenceFlow();
			if (sequenceFlow != null) {
				appendLoopScope(activity, sequenceFlow, result);
			}
		}
	}
	
	/**
	 * Appends the attributes and child elements that are related with the
	 * loop counter to the given result element.
	 *  
	 * @param counter The loop counter to create the attributes and child 
	 *                elements from
	 * @param loop    The loop the counter belongs to.
	 * @param result  The result element to add the attributes and child
	 *                elements to
	 */
	private void appendLoopCounterAttrEl(VariableDataObject counter, Loop loop, Element result) {
		if (counter == null) {
			return;
		}
		
		result.setAttribute("counterName", counter.getName());
		
		if (loop.getStartCounterValue() != null) {
			result.appendChild(this.supportingFactory.createExpressionElement(
					"startCounterValue", loop.getStartCounterValue()));
		}
		
		if (loop.getFinalCounterValue() != null) {
			result.appendChild(this.supportingFactory.createExpressionElement(
					"finalCounterValue", loop.getFinalCounterValue()));
		}
	}
	
	/**
	 * Creates a "forEach" element for the given looping task. In the "forEach"
	 * element a "scope" will be created the given content is added to. If
	 * the looping task is not associated with a counter variable data object,
	 * it will be created automatically.
	 * 
	 * <p>The "forEach" element gets a generated name using the pattern: 
	 * "forEach_" + taskName.</p> 
	 * 
	 * @param task         The task to create the "forEach" element from.
	 * @param scopeContent The content within the "forEach" element (should
	 *                     not be null).
	 * 
	 * @return The created "forEach" element.
	 */
	private Element createMultipleLoop(Task task, Element scopeContent) {
		Element result = this.document.createElement("forEach");
		Loop loop = task.getLoop();
		BPELUtil.setStandardAttributes(result, task);
		String name = "forEach_" + task.getName();
		result.setAttribute("name", name);
		
		if (loop.getOrdering() != null) {
			if (loop.getOrdering().equals(Loop.ORDERING_PARALLEL)) {
				result.setAttribute("parallel", "yes");
			} else {
				result.setAttribute("parallel", "no");
			}
		}
		
		VariableDataObject counter = this.diagram.getCounterVariable(task);
		if ((counter == null) && (this.diagram.getLoopCounterSet(task) == null )) {
			// create loop counter automatically
			counter = generateLoopCounter(task, name);
		}
		appendLoopCounterAttrEl(counter, loop, result);
		
		Element condition = 
			this.supportingFactory.createCompletionCondition(loop);
		if (condition != null) {
			result.appendChild(condition);
		}
		
		appendLoopScope(task, scopeContent, result);
		
		return result;
	}
	
	/**
	 * Creates a "forEach" element for the given looping block activity. In 
	 * the "forEach" element a "scope" will be created the given content is 
	 * added to. If the looping activity does not contain a counter variable 
	 * data object, an error is added to the output.
	 * 
	 * @param loopingActivity The activity to create the "forEach" element from
	 * @param scopeContent    The content within the "forEach" element. If the
	 *                        paramter is null, the scope content will be 
	 *                        generated.
	 * 
	 * @return The created "forEach" element.
	 */
	private Element createMultipleLoop(BlockActivity loopingActivity, Element scopeContent) {
		Element result = this.document.createElement("forEach");
		Loop loop = loopingActivity.getLoop();
		
		BPELUtil.setStandardAttributes(result, loopingActivity);
		if (loop.getOrdering() != null) {
			if (loop.getOrdering().equals(Loop.ORDERING_PARALLEL)) {
				result.setAttribute("parallel", "yes");
			} else {
				result.setAttribute("parallel", "no");
			}
		}
		
		VariableDataObject counter = 
			this.diagram.getCounterVariable(loopingActivity);
		if ((counter == null) && 
				(this.diagram.getLoopCounterSet(loopingActivity) == null )) {
			this.output.addError("Loop counter is missing.", loopingActivity.getId());
		} else {
			appendLoopCounterAttrEl(counter, loop, result);
		}
		
		Element condition =
			this.supportingFactory.createCompletionCondition(loop);
		if (condition != null) {
			result.appendChild(condition);
		}
		
		appendLoopScope(loopingActivity, scopeContent, result);
		
		return result;
	}
	
	/**
	 * Creates a "forEach" element from the given looping activity (see
	 * {@link #createMultipleLoop(Task, Element)} or 
	 * {@link #createMultipleLoop(BlockActivity, Element)})
	 * 
	 * @param loopingActivity The activity to create the "forEach" element from.
	 * @param scopeContent    The content within the "forEach" element.
	 * 
	 * @return The created "forEach" element. The result is null if a "forEach"
	 * element could not be created for the given activity.
	 */
	private Element createMultipleLoop(Activity loopingActivity, Element scopeContent) {
		if (loopingActivity instanceof BlockActivity) {
			return createMultipleLoop((BlockActivity)loopingActivity, scopeContent);
		} else if (loopingActivity instanceof Task) {
			return createMultipleLoop((Task)loopingActivity, scopeContent);
		}
		return null;
	}
	
	/**
	 * Creates a "while" element for the given looping activity. The "while"
	 * element will contain the given content element. If the content is null
	 * and the looping is a block activity, the content will be generated from
	 * the sequence flow within the sub-process of the block activity.
	 * If the content could not be generated, an error is added to the output.
	 * If there is no loop condition defined in the Loop object of the activiy,
	 * an error is added to the output, too. 
	 *  
	 * @param loopingActivity The activity representing the "while" element.
	 * @param content         The element that will be added as content to the 
	 *                        created "while element.
	 * @return The created "while" element.
	 */
	private Element createWhileLoop(Activity loopingActivity, Element content) {
		Element result = this.document.createElement("while");
		
		if (loopingActivity.getLoop().getLoopCondition() != null) {
			result.appendChild(this.supportingFactory.createExpressionElement(
				"condition", loopingActivity.getLoop().getLoopCondition()));
		} else {
			this.output.addError("The loop " +
					"must define a loop condition.", loopingActivity.getId());
		}
		
		BPELUtil.setStandardAttributes(result, loopingActivity);
		
		if (content != null) {
			result.appendChild(content);
		} else if (loopingActivity instanceof BlockActivity){
			BlockActivity activity = (BlockActivity)loopingActivity;
			Element sequenceFlow = new SequenceFlowFactory(
					this.diagram, this.document, activity.getSubProcess(), 
					this.output).transformSequenceFlow();
			if (sequenceFlow != null) {
				result.appendChild(sequenceFlow);
			}
		} else {
			this.output.addError("Content of looping activity " +
				"could not be generated.", loopingActivity.getId());
		}
		
		return result;
	}
	
	/**
	 * Creates a "repeatUntil" element for the given looping activity. The 
	 * "repeatUntil" element will contain the given content element. If the
	 * content is null and the looping is a block activity, the content will be
	 * generated from the sequence flow within the sub-process of the block 
	 * activity. If the content could not be generated, an error is added to
	 * the output. If there is no loop condition defined in the Loop object of
	 * the activiy, an error is added to the output, too. 
	 *  
	 * @param loopingActivity The activity representing the "reoeatUntil" element.
	 * @param content         The element that will be added as content to the 
	 *                        created "repeatUntil element.
	 * @return The created "repeatUntil" element.
	 */
	private Element createRepeatUntilLoop(Activity loopingActivity, Element content) {
		Element result = this.document.createElement("repeatUntil");
		
		BPELUtil.setStandardAttributes(result, loopingActivity);
				
		if (content != null) {
			result.appendChild(content);
		} else if (loopingActivity instanceof BlockActivity){
			BlockActivity activity = (BlockActivity)loopingActivity;
			Element sequenceFlow = new SequenceFlowFactory(
					this.diagram, this.document, activity.getSubProcess(), 
					this.output).transformSequenceFlow();
			if (sequenceFlow != null) {
				result.appendChild(sequenceFlow);
			}
		} else {
			this.output.addError("Content of looping activity " +
					"could not be generated.", loopingActivity.getId());
		}
		
		if (loopingActivity.getLoop().getLoopCondition() != null) {
			result.appendChild(this.supportingFactory.createExpressionElement(
				"condition", loopingActivity.getLoop().getLoopCondition()));
		} else {
			this.output.addError("The loop " +
					"must define a loop condition.", loopingActivity.getId());
		}
		
		return result;
	}
	
	/**
	 * Creates a "while" or "repeatUntil" element from the given activity
	 * depending on the defined test time in the Loop object of the activity.
	 * 
	 * @param loopingActivity The activity to create the loop element from.
	 * @param content         The element that will be contained in the loop
	 *                        element.
	 * @return The created "while" or "repeatUntil" loop element.
	 */
	private Element createStandardLoop(Activity loopingActivity, Element content) {
		if (loopingActivity.getLoop().getTestTime().equals(Loop.TEST_TIME_BEFORE)) {
			return createWhileLoop(loopingActivity, content);
		}
		return createRepeatUntilLoop(loopingActivity, content);
	}
	
	/**
	 * Creates an "while", "repeatUntil" or "forEach" element depending on 
	 * the Loop object of the given activity. The scope within the loop will
	 * not be created. This method should be used, if the content of the loop is
	 * already known and should be appended as child of the loop element.
	 * 
	 * @param loopingActivity The activity to create the loop element from.
	 * @param content         The element that will be contained in the loop
	 *                        element.
	 *                        
	 * @return The created "while", "repeatUntil" or "forEach" element. The
	 * result is null, if the activity is not a looping activity.
	 */
	public Element createLoopElement(Activity loopingActivity, Element content) {
		if (loopingActivity.getLoop() == null) {
			return null;
		}
		if (loopingActivity.getLoop().getLoopType().equals(Loop.TYPE_MULITPLE)) {
			return createMultipleLoop(loopingActivity, content);
		}
		return createStandardLoop(loopingActivity, content);
	}
	
	/**
	 * Creates the "onMessage" element of a "pick" from a receive task. The 
	 * given content is added as child element to the "onMessage" element. 
	 *  
	 * @param task    The receive task to create the "onMessage" element from.
	 * @param content The element that will be contained in the "onMessage" 
	 *                element.
	 * 
	 * @return The created "onMessage" element.
	 */
	public Element createOnMessageBranch(ReceiveTask task, Element content) {
		Element result = this.document.createElement("onMessage");
		result.setAttribute("wsu:id" , task.getName());
		
		if (task.getMessageExchange() != null) {
			result.setAttribute("messageExchange", task.getMessageExchange());
		}
		
		Element correlations = this.supportingFactory.
			createCorrelationsElement(task.getCorrelations());
		if (correlations != null) {
			result.appendChild(correlations);
		}
		
		VariableDataObject object = 
			this.diagram.getStandardVariable(task, true);
		if(task.isOpaqueOutput()) {
			result.setAttribute("variable", "##opaque");
		} else if (object != null) {
			result.setAttribute("variable", object.getName());
		} else {
			Element fromParts = this.supportingFactory.
				createFromPartsElement(task.getFromParts());
			if (fromParts != null) {
				result.appendChild(fromParts);
			}
		}
		
		if (content != null) {
			result.appendChild(content);
		}
		return result;
		
	}
	
	/**
	 * Creates the "onMessage" element of a "pick" from a message intermediate
	 * event. The given content is added as child element to the "onMessage" 
	 * element. 
	 *  
	 * @param event   The intermediate message event to create the "onMessage"
	 *                element from.
	 * @param content The element that will be contained in the "onMessage" 
	 *                element.
	 * 
	 * @return The created "onMessage" element.
	 */
	private Element createOnMessageBranch(
			IntermediateEvent event, Element content) {
		
		Element result = this.document.createElement("onMessage");
		result.setAttribute("wsu:id" , event.getName());
		
		VariableDataObject object = 
			this.diagram.getStandardVariable(event, true);
		if ((event.getTrigger() != null) && 
				(event.getTrigger() instanceof TriggerResultMessage)) {
			TriggerResultMessage trigger = 
				(TriggerResultMessage)event.getTrigger();
			
			if (trigger.getMessageExchange() != null) {
				result.setAttribute(
					"messageExchange", trigger.getMessageExchange());
			}
			
			Element correlations = 
				this.supportingFactory.createCorrelationsElement(
					trigger.getCorrelations());
			if (correlations != null) {
				result.appendChild(correlations);
			}
			
			if(trigger.isOpaqueOutput()) {
				result.setAttribute("variable", "##opaque");
			} else if (object != null) {
				result.setAttribute("variable", object.getName());
			} else {
				Element fromParts = 
					this.supportingFactory.createFromPartsElement(
						trigger.getFromParts());
				if (fromParts != null) {
					result.appendChild(fromParts);
				}
			}
		} else if (object != null) {
			result.setAttribute("variable", object.getName());
		}
		
		if (content != null) {
			result.appendChild(content);
		}

		return result;
	}
	
	/**
	 * Creates the "onAlarm" element of a "pick" from a timer intermediate
	 * event. The given content is added as child element to the "onAlarm" 
	 * element. 
	 * 
	 * <p>If the timer trigger of the event does not define a duration or
	 * deadline, an error is added to the output. If the event does not
	 * define a trigger at all an error is added to the output, too.</p>
	 *  
	 * @param event   The intermediate timer event to create the "onAlarm"
	 *                element from.
	 * @param content The element that will be contained in the "onAlarm" 
	 *                element.
	 * 
	 * @return The created "onAlarm" element.
	 */
	private Element createOnAlarmBranch(
			IntermediateEvent event, Element content) {
		Element result = this.document.createElement("onAlarm");
		
		if ((event.getTrigger() != null) && 
				(event.getTrigger() instanceof TriggerTimer)) {
			TriggerTimer trigger = (TriggerTimer)event.getTrigger();
			
			if (trigger.getTimeDeadlineExpression() != null) {
				result.appendChild(
						this.supportingFactory.createExpressionElement(
						"until", trigger.getTimeDeadlineExpression()));
			} else if (trigger.getTimeDurationExpression() != null) {
				result.appendChild(
						this.supportingFactory.createExpressionElement(
						"for", trigger.getTimeDurationExpression()));
			} else {
				this.output.addError("The duration or deadline " +
						"expression of the wait activity "+  
						" coult not be generated.", event.getId() );
			}
		} else {
			// event must define trigger to identify which expression
			// should be used (deadline or duration)
			this.output.addError("The event " +
					"must define a timer trigger element.", event.getId());
		}
		
		if (content != null) {
			result.appendChild(content);
		}

		return result;
	}
	
	/**
	 * Creates the "onAlarm" or "onMessage" element of a "pick" depending on 
	 * the trigger type of the event. An "onMessag" element will be created
	 * for a message intermediate event and a "onAlarm" element will be created
	 * for a timer intermediate event. 
	 * 
	 * @param event   The event to create the pick branch from.
	 * @param content The content that will be contained in the pick branch 
	 *                element.
	 * @return The created pick branch element "onMessage" or "onAlarm".
	 */
	public Element createPickBranchElement(IntermediateEvent event, Element content) {
		if (event.getTriggerType().equals(IntermediateEvent.TRIGGER_MESSAGE)) { 
			return createOnMessageBranch(event, content);
		} else if (event.getTriggerType().equals(IntermediateEvent.TRIGGER_TIMER)) {
			return createOnAlarmBranch(event, content);
		}
		return null;
	}
}
