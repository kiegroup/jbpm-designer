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
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.EscalationEventDefinition;
import de.hpi.bpmn2_0.model.event.IntermediateThrowEvent;
import de.hpi.bpmn2_0.model.event.LinkEventDefinition;
import de.hpi.bpmn2_0.model.event.MessageEventDefinition;
import de.hpi.bpmn2_0.model.event.SignalEventDefinition;

/**
 * Factory to create intermediate throwing events
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"IntermediateEvent",
	"IntermediateMessageEventThrowing",
	"IntermediateEscalationEventThrowing",
	"IntermediateLinkEventThrowing",
	"IntermediateCompensationEventThrowing",
	"IntermediateSignalEventThrowing",
	"IntermediateMultipleEventThrowing"
})
public class IntermediateThrowEventFactory extends AbstractBpmnFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor.server.diagram.Shape, de.hpi.bpmn2_0.factory.BPMNElement)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent)
			throws BpmnConverterException {
		EventShape itEventShape = this.createDiagramElement(shape);
		IntermediateThrowEvent itEvent = this.createProcessElement(shape);
		itEventShape.setEventRef(itEvent);
		
		return new BPMNElement(itEventShape, itEvent, shape.getResourceId());
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected EventShape createDiagramElement(Shape shape) {
		EventShape intermediateEventShape = new EventShape();
		this.setVisualAttributes(intermediateEventShape, shape);
		
		return intermediateEventShape;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected IntermediateThrowEvent createProcessElement(Shape shape)
			throws BpmnConverterException {
		try {
			IntermediateThrowEvent itEvent = (IntermediateThrowEvent) this.invokeCreatorMethod(shape);
			itEvent.setId(shape.getResourceId());
			itEvent.setName(shape.getProperty("name"));
			
			return itEvent;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
	
	/* Creator methods for different throwing intermediate event definitions */
	
	@StencilId("IntermediateEvent")
	protected IntermediateThrowEvent createIntermediateNoneEvent(Shape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();
		return itEvent;
	}
	
	@StencilId("IntermediateMessageEventThrowing")
	protected IntermediateThrowEvent createIntermediateMessageEvent(Shape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();

		MessageEventDefinition msgDef = new MessageEventDefinition();
		itEvent.getEventDefinition().add(msgDef);
		
		return itEvent;
	}
	
	@StencilId("IntermediateEscalationEventThrowing")
	protected IntermediateThrowEvent createIntermediateEscalationEvent(Shape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();

		EscalationEventDefinition escalDef = new EscalationEventDefinition();
		itEvent.getEventDefinition().add(escalDef);
		
		return itEvent;
	}
	
	@StencilId("IntermediateLinkEventThrowing")
	protected IntermediateThrowEvent createIntermediateLinkEvent(Shape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();

		LinkEventDefinition linkDef = new LinkEventDefinition();
		
		/* Set required name attribute */
		String name = shape.getProperty("name");
		if(name != null && !name.isEmpty())
			linkDef.setName(name);
		
		itEvent.getEventDefinition().add(linkDef);
		
		return itEvent;
	}
	
	@StencilId("IntermediateCompensationEventThrowing")
	protected IntermediateThrowEvent createIntermediateCompensationEvent(Shape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();

		CompensateEventDefinition compDef = new CompensateEventDefinition();
		itEvent.getEventDefinition().add(compDef);
		
		return itEvent;
	}
	
	
	@StencilId("IntermediateSignalEventThrowing")
	protected IntermediateThrowEvent createIntermediateSignalEvent(Shape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();

		SignalEventDefinition sigDef = new SignalEventDefinition();
		itEvent.getEventDefinition().add(sigDef);
		
		return itEvent;
	}
	
	@StencilId("IntermediateMultipleEventThrowing")
	protected IntermediateThrowEvent createIntermediateMultipleEvent(Shape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();
		
		return itEvent;
	}
}
