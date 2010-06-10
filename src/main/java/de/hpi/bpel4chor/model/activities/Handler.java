package de.hpi.bpel4chor.model.activities;

import java.util.List;
import de.hpi.bpel4chor.model.connections.Transition;
import de.hpi.bpel4chor.util.Output;

/**
 * A Handler handles specified events in a process or sub-process.
 * There can be fault, message, timer, termination or compensation handlers. 
 * Each handler has special restrictions regarding the connected transitions.
 */
public class Handler extends BlockActivity {

	public static final String TYPE_FAULT = "Fault";
	public static final String TYPE_MESSAGE = "Message";
	public static final String TYPE_TIMER = "Timer";
	public static final String TYPE_TERMINATION = "Termination";
	public static final String TYPE_COMPENSATION = "Compensation";	
	
	private String handlerType = null; 
	
	/**
	 * Constructor. Initializes the handler and generates a unique id for it.
	 * 
	 * @param output The output to print errors to.
	 */
	public Handler(Output output) {
		super(output);
	}

	/**
	 * @return The type of the handler ({@link #TYPE_FAULT}, 
	 * {@link #TYPE_MESSAGE}, {@link #TYPE_TIMER}, {@link #TYPE_TERMINATION},
	 * {@link #TYPE_COMPENSATION}). 
	 */
	public String getHandlerType() {
		return this.handlerType;
	}
	
	@Override
	/**
	 * Message, timer, compensation and termination handlers are not allowed to
	 * have outgoing transitions. Moreover, all handlers are not allowed to have
	 * multiple outgoing transitions. In this case an error is added to the output.
	 * In any other case the outgoing transition is added to the list of outgoing
	 * transitions.
	 */
	public void addSourceFor(Transition transition, Output output) {
		if (this.handlerType.equals(TYPE_MESSAGE) ||
				this.handlerType.equals(TYPE_TIMER) ||
				this.handlerType.equals(TYPE_COMPENSATION) ||
				this.handlerType.equals(TYPE_TERMINATION)) {
			output.addError("This handler" +  
					" is not allowed to have outgoing transitions.", getId());
		} else if (!this.sourceFor.isEmpty()) {
			output.addError("This handler " +
					"is not allowed to have multiple outgoing transitions.", getId());
		} else {
			super.addSourceFor(transition, output);
		}
	}
	
	@Override
	/**
	 * Message, timer and compensation handlers are not allowed to
	 * have incoming transitions. The resulting handlers can only be connected
	 * with the appropriate attached intermediate events. Moreover, all
	 * handlers are not allowed to have multiple incoming transitions.
	 * In these cases an error is added to the output.
	 * In any other case the outgoing transition is added to the
	 * list of outgoing transitions.
	 */
	public void addTargetFor(Transition transition, Output output) {
		if (this.handlerType.equals(TYPE_MESSAGE) ||
				this.handlerType.equals(TYPE_TIMER) ||
				this.handlerType.equals(TYPE_COMPENSATION)) {
			output.addError("This handler" +  
					" is not allowed to have incoming transitions.", getId());
		} else if (!this.targetFor.isEmpty()) {
			output.addError("This handler " +
					"is not allowed to have multiple incoming transitions.", getId());
		} else if (!(transition.getSource() instanceof IntermediateEvent) || 
				((IntermediateEvent)transition.getSource()).getTarget() == null){
			output.addError("This handler " +
					" can only be connected with a transition from"+
					" an attached intermediate event.", getId());
		} else {
			IntermediateEvent sourceEvent = (IntermediateEvent)transition.getSource();
			if (this.handlerType.equals(TYPE_FAULT)) {
				if (!sourceEvent.getTriggerType().equals(IntermediateEvent.TRIGGER_ERROR)) {
					output.addError("This handler " +
							" must be connected with a transition from"+
							" an attached fault intermediate event.", getId());
					return;
				}
			} else if (this.handlerType.equals(TYPE_TERMINATION)) {
				if (!sourceEvent.getTriggerType().equals(IntermediateEvent.TRIGGER_TERMINATION)) {
					output.addError("This handler " +
							" must be connected with a transition from"+
							" an attached termination intermediate event.", getId());
					return;
				}
			}
			super.addTargetFor(transition, output);
		}
	}
	
	/**
	 * Determines the message start event located in the handler. If 
	 * there is no start event, if there are multiple start events or if
	 * the start event is not a message event, an error is added to the output.
	 *  
	 * @param output The output to print errors to.
	 * 
	 * @return The message start event of the handler or null if an error
	 * occured.
	 */
	public StartEvent getMessageEventHandlerStart(Output output) {
		List<StartEvent> startEvents = getSubProcess().getStartEvents();
		StartEvent start = null;
		if (startEvents.isEmpty()) {
			output.addError("Message event handler " +
					"does not contain a message start event.", getId());
			return null;
		} else if (startEvents.size() > 1) {
			output.addError("Message event handler " +
					"is not allowed to have multiple start events.", getId());
			return null;
		} else {
			start = startEvents.get(0);
			if (!start.getTriggerType().equals(StartEvent.TRIGGER_MESSAGE)) {
				output.addError("Message event handler " +
						"must contain a start event with a message trigger.", getId());
				return null;
			}
		}
		return start;
	}
	
	/**
	 * Determines the timer start event located in the handler. If 
	 * there is no start event, if there are multiple start events or if
	 * the start event is not a timer event, an error is added to the output.
	 *  
	 * @param output The output to print errors to.
	 * 
	 * @return The timer start event of the handler or null if an error occured.
	 */
	public StartEvent getTimerEventHandlerStart(Output output) {
		List<StartEvent> startEvents = getSubProcess().getStartEvents();
		StartEvent start = null;
		if (startEvents.isEmpty()) {
			output.addError("Timer event handler " +
					"does not contain a timer start event.", getId());
			return null;
		} else if (startEvents.size() > 1) {
			output.addError("Timer event handler " +
					"is not allowed to have multiple start events.", getId());
			return null;
		} else {
			start = startEvents.get(0);
			if (!start.getTriggerType().equals(StartEvent.TRIGGER_TIMER)) {
				output.addError("Timer event handler " +
						"must contain a start event with a timer trigger.", getId());
				return null;
			}
		}
		return start;
	}

	/**
	 * Sets the type of the handler
	 * 
	 * @param handlerType The new handler type ({@link #TYPE_FAULT},
	 * {@link #TYPE_MESSAGE}, {@link #TYPE_TIMER}, {@link #TYPE_TERMINATION},
	 * {@link #TYPE_COMPENSATION}). 
	 */
	public void setHandlerType(String handlerType) {
		this.handlerType = handlerType;
	}
}
