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
import de.hpi.bpmn2_0.model.diagram.ChoreographyParticipantShape;
import de.hpi.bpmn2_0.model.participant.Participant;

/**
 * Factory to create participants
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId("ChoreographyParticipant")
public class ParticipantFactory extends AbstractBpmnFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor.server.diagram.Shape, de.hpi.bpmn2_0.factory.BPMNElement)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent)
			throws BpmnConverterException {
		ChoreographyParticipantShape cps = this.createDiagramElement(shape);
		Participant p = this.createProcessElement(shape);
		cps.setParticipant(p);
		return new BPMNElement(cps, p, shape.getResourceId());
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected ChoreographyParticipantShape createDiagramElement(Shape shape) {
		ChoreographyParticipantShape cps = new ChoreographyParticipantShape();
		this.setVisualAttributes(cps, shape);
		return cps;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected Participant createProcessElement(Shape shape)
			throws BpmnConverterException {
		Participant p = new Participant();
		this.setCommonAttributes(p, shape);
		p.setId(shape.getResourceId());
		p.setName(shape.getProperty("name"));
		
		/* Handle initiating property */
		String initiating = shape.getProperty("initiating");
		if(initiating != null)
			p.setInitiating(initiating.equalsIgnoreCase("true"));
		else 
			p.setInitiating(false);
		return p;
	}

}
