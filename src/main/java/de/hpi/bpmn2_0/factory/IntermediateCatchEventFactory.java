/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.hpi.bpmn2_0.factory;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.diagram.EventShape;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.CancelEventDefinition;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.ConditionalEventDefinition;
import de.hpi.bpmn2_0.model.event.ErrorEventDefinition;
import de.hpi.bpmn2_0.model.event.EscalationEventDefinition;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.event.IntermediateCatchEvent;
import de.hpi.bpmn2_0.model.event.LinkEventDefinition;
import de.hpi.bpmn2_0.model.event.MessageEventDefinition;
import de.hpi.bpmn2_0.model.event.SignalEventDefinition;
import de.hpi.bpmn2_0.model.event.TimerEventDefinition;

/**
 * Factory to create intermediate catching Events
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId( { "IntermediateMessageEventCatching", "IntermediateTimerEvent",
		"IntermediateEscalationEvent", "IntermediateConditionalEvent",
		"IntermediateLinkEventCatching", "IntermediateErrorEvent",
		"IntermediateCancelEvent", "IntermediateCompensationEventCatching",
		"IntermediateSignalEventCatching", "IntermediateMultipleEventCatching",
		"IntermediateParallelMultipleEventCatching" })
public class IntermediateCatchEventFactory extends AbstractBpmnFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor
	 * .server.diagram.Shape, de.hpi.bpmn2_0.factory.BPMNElement)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent)
			throws BpmnConverterException {
		EventShape icEventShape = (EventShape) this.createDiagramElement(shape);
		IntermediateCatchEvent icEvent = (IntermediateCatchEvent) this
				.createProcessElement(shape);
		icEventShape.setEventRef(icEvent);

		return new BPMNElement(icEventShape, icEvent, shape.getResourceId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected Object createDiagramElement(Shape shape) {
		EventShape intermediateEventShape = new EventShape();
		this.setVisualAttributes(intermediateEventShape, shape);

		return intermediateEventShape;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected BaseElement createProcessElement(Shape shape)
			throws BpmnConverterException {
		try {
			IntermediateCatchEvent icEvent = (IntermediateCatchEvent) this
					.invokeCreatorMethod(shape);
			icEvent.setId(shape.getResourceId());
			icEvent.setName(shape.getProperty("name"));

			icEvent.setCancelActivity(shape
					.getProperty("boundarycancelactivity"));

			return icEvent;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}

	}

	/* Creator methods for different event definitions */

	@StencilId("IntermediateCompensationEventCatching")
	protected IntermediateCatchEvent createCompensateEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();
		CompensateEventDefinition compEvDef = new CompensateEventDefinition();
		icEvent.getEventDefinition().add(compEvDef);
		return icEvent;
	}

	@StencilId("IntermediateTimerEvent")
	protected IntermediateCatchEvent createTimerEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		TimerEventDefinition timerEvDef = new TimerEventDefinition();
		icEvent.getEventDefinition().add(timerEvDef);

		return icEvent;
	}

	@StencilId("IntermediateMessageEventCatching")
	protected IntermediateCatchEvent createMessageEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		MessageEventDefinition messageEvDef = new MessageEventDefinition();
		icEvent.getEventDefinition().add(messageEvDef);

		return icEvent;
	}

	@StencilId("IntermediateEscalationEvent")
	protected IntermediateCatchEvent createEscalationEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		EscalationEventDefinition escalDef = new EscalationEventDefinition();
		icEvent.getEventDefinition().add(escalDef);

		return icEvent;
	}

	@StencilId("IntermediateConditionalEvent")
	protected IntermediateCatchEvent createConditionalEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		ConditionalEventDefinition conDef = new ConditionalEventDefinition();

		/* Set condition attribute as FormalExpression */
		String condition = shape.getProperty("condition");
		if (condition != null && !condition.isEmpty())
			conDef.setCondition(new FormalExpression(condition));

		icEvent.getEventDefinition().add(conDef);

		return icEvent;
	}

	@StencilId("IntermediateLinkEventCatching")
	protected IntermediateCatchEvent createLinkEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		LinkEventDefinition linkDef = new LinkEventDefinition();

		/* Set required name attribute */
		String name = shape.getProperty("name");
		if (name != null && !name.isEmpty())
			linkDef.setName(name);

		icEvent.getEventDefinition().add(linkDef);

		return icEvent;
	}

	@StencilId("IntermediateErrorEvent")
	protected IntermediateCatchEvent createErrorEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		ErrorEventDefinition errorDef = new ErrorEventDefinition();
		icEvent.getEventDefinition().add(errorDef);

		return icEvent;
	}

	@StencilId("IntermediateCancelEvent")
	protected IntermediateCatchEvent createCancelEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		CancelEventDefinition cancelDef = new CancelEventDefinition();
		icEvent.getEventDefinition().add(cancelDef);

		return icEvent;
	}

	@StencilId("IntermediateSignalEventCatching")
	protected IntermediateCatchEvent createSignalEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		SignalEventDefinition signalDef = new SignalEventDefinition();
		icEvent.getEventDefinition().add(signalDef);

		return icEvent;
	}

	@StencilId("IntermediateMultipleEventCatching")
	protected IntermediateCatchEvent createMultipleEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();
		icEvent.setParallelMultiple(false);

		return icEvent;
	}

	@StencilId("IntermediateParallelMultipleEventCatching")
	protected IntermediateCatchEvent createParallelMultipleEvent(Shape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();
		icEvent.setParallelMultiple(true);

		return icEvent;
	}

	public static void changeToBoundaryEvent(BPMNElement activity,
			BPMNElement event) {
		if (!(activity.getNode() instanceof Activity)
				|| !(event.getNode() instanceof IntermediateCatchEvent)) {
			return;
		}

		BoundaryEvent bEvent = new BoundaryEvent();
		bEvent.getEventDefinition().addAll(
				((Event) event.getNode()).getEventDefinition());
		
		/* Special boundary event attributes */
		bEvent.setAttachedToRef((Activity) activity.getNode());
		bEvent.setCancelActivity(((IntermediateCatchEvent) event.getNode())
				.getCancelActivity().equalsIgnoreCase("true"));
		
		// bEvent.setProcessRef(event.get);
		bEvent.setId(event.getNode().getId());
		bEvent.setName(((IntermediateCatchEvent) event.getNode()).getName());
		bEvent.setParallelMultiple(((IntermediateCatchEvent) event.getNode())
				.isParallelMultiple());
		// TODO: bEvent.setCancelActivity()

		// /* Refresh references */
		// int index = process.getFlowElement().indexOf(event.getNode());
		// if(index != -1) {
		// process.getFlowElement().remove(index);
		// process.getFlowElement().add(index, bEvent);
		// }
		IntermediateCatchEvent ice = (IntermediateCatchEvent) event.getNode();
		event.setNode(bEvent);
		((EventShape) event.getShape()).setEventRef(bEvent);
		((Activity) activity.getNode()).getBoundaryEventRefs().add(bEvent);

		/* Handle boundary events as child elements of a lane */
		if (ice.getLane() != null) {
			/* Exchange intermediate event with boundary event */
			bEvent.setLane(ice.getLane());
			int index = bEvent.getLane().getFlowElementRef().indexOf(ice);
			bEvent.getLane().getFlowElementRef().remove(ice);
			bEvent.getLane().getFlowElementRef().add(index, bEvent);
		}
	}
}
