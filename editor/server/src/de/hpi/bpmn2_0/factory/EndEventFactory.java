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
import de.hpi.bpmn2_0.model.diagram.EventShape;
import de.hpi.bpmn2_0.model.event.CancelEventDefinition;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.ErrorEventDefinition;
import de.hpi.bpmn2_0.model.event.EscalationEventDefinition;
import de.hpi.bpmn2_0.model.event.MessageEventDefinition;
import de.hpi.bpmn2_0.model.event.SignalEventDefinition;
import de.hpi.bpmn2_0.model.event.TerminateEventDefinition;

/**
 * Factory to create end events
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"EndNoneEvent",
	"EndMessageEvent",
	"EndEscalationEvent",
	"EndErrorEvent",
	"EndCancelEvent",
	"EndCompensationEvent",
	"EndSignalEvent",
	"EndMultipleEvent",
	"EndTerminateEvent"
})
public class EndEventFactory extends AbstractBpmnFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent) throws BpmnConverterException {
		EventShape eventShape = (EventShape) this.createDiagramElement(shape);
		EndEvent endEvent = this.createProcessElement(shape);
		
		/* Set Reference from shape to process element */
		eventShape.setEventRef(endEvent);
		
		return new BPMNElement(eventShape, endEvent, shape.getResourceId());
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected Object createDiagramElement(Shape shape) {
		EventShape eventShape = new EventShape();
		this.setVisualAttributes(eventShape, shape);
		
		return eventShape;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected EndEvent createProcessElement(Shape shape) throws BpmnConverterException {
		try {
			EndEvent endEvent = (EndEvent) this.invokeCreatorMethod(shape);
			endEvent.setId(shape.getResourceId());
			endEvent.setName(shape.getProperty("name"));
			
			return endEvent;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
	
	/* Methods for different */
	
	@StencilId("EndNoneEvent")
	protected EndEvent createEndNoneEvent(Shape shape) {
		EndEvent endEvent = new EndEvent();
		
		return endEvent;
	}
	
	@StencilId("EndMessageEvent")
	protected EndEvent createEndMessageEvent(Shape shape) {
		EndEvent endEvent = new EndEvent();
		
		MessageEventDefinition msgEventDef = new MessageEventDefinition();
		endEvent.getEventDefinition().add(msgEventDef);
		
		return endEvent;
	}
	
	@StencilId("EndEscalationEvent")
	protected EndEvent createEndEscalationEvent(Shape shape) {
		EndEvent endEvent = new EndEvent();
		
		EscalationEventDefinition escalEventDef = new EscalationEventDefinition();
		endEvent.getEventDefinition().add(escalEventDef);
		
		return endEvent;
	}
	
	@StencilId("EndErrorEvent")
	protected EndEvent createEndErrorEvent(Shape shape) {
		EndEvent endEvent = new EndEvent();
		
		ErrorEventDefinition errorEventDef = new ErrorEventDefinition();
		endEvent.getEventDefinition().add(errorEventDef);
		
		return endEvent;
	}
	
	@StencilId("EndCancelEvent")
	protected EndEvent createEndCancelEvent(Shape shape) {
		EndEvent endEvent = new EndEvent();
		
		CancelEventDefinition cancelEventDef = new CancelEventDefinition();
		endEvent.getEventDefinition().add(cancelEventDef);
		
		return endEvent;
	}
	
	@StencilId("EndCompensationEvent")
	protected EndEvent createEndCompensateEvent(Shape shape) {
		EndEvent endEvent = new EndEvent();
		
		CompensateEventDefinition compEventDef = new CompensateEventDefinition();
		endEvent.getEventDefinition().add(compEventDef);
		
		return endEvent;
	}
	
	@StencilId("EndSignalEvent")
	protected EndEvent createEndSignalEvent(Shape shape) {
		EndEvent endEvent = new EndEvent();
		
		SignalEventDefinition signalEventDef = new SignalEventDefinition();
		endEvent.getEventDefinition().add(signalEventDef);
		
		return endEvent;
	}
	
	@StencilId("EndMultipleEvent")
	protected EndEvent createEndMultipleEvent(Shape shape) {
		EndEvent endEvent = new EndEvent();
		
		return endEvent;
	}
	
	@StencilId("EndTerminateEvent")
	protected EndEvent createEndTerminateEvent(Shape shape) {
		EndEvent endEvent = new EndEvent();
		
		TerminateEventDefinition eventDef = new TerminateEventDefinition();
		endEvent.getEventDefinition().add(eventDef);
		
		return endEvent;
	}
}
