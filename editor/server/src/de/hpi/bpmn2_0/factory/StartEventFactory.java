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
import de.hpi.bpmn2_0.model.event.MessageEventDefinition;
import de.hpi.bpmn2_0.model.event.StartEvent;

/**
 * The factory for start events
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"StartNoneEvent",
	"StartMessageEvent"
})
public class StartEventFactory extends AbstractBpmnFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent) throws BpmnConverterException {
		EventShape eventShape = this.createDiagramElement(shape);
		StartEvent startEvent = this.createProcessElement(shape);
		
		/* Set Reference from shape to process element */
		eventShape.setEventRef(startEvent);
		
		return new BPMNElement(eventShape, startEvent, shape.getResourceId());
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected EventShape createDiagramElement(Shape shape) {
		EventShape eventShape = new EventShape();
		this.setVisualAttributes(eventShape, shape);
		return eventShape;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected StartEvent createProcessElement(Shape shape) throws BpmnConverterException {
		StartEvent event;
		try {
			event = (StartEvent) this.invokeCreatorMethod(shape);
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
		event.setId(shape.getResourceId());
		event.setName(shape.getProperty("name"));
		return event;
	}
	
	@StencilId("StartMessageEvent")
	protected StartEvent createStartMessageEvent(Shape shape) {
		StartEvent event = new StartEvent();
		MessageEventDefinition msgEvDef = new MessageEventDefinition();
		event.getEventDefinition().add(msgEvDef);
		
		return event;
	}
	
	@StencilId("StartNoneEvent")
	protected StartEvent createStartNoneEvent(Shape shape) {
		StartEvent event = new StartEvent();
		
		return event;
	}

}
