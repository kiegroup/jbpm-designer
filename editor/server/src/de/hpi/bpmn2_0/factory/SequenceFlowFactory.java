package de.hpi.bpmn2_0.factory;

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

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Expression;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.diagram.SequenceFlowConnector;

/**
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId("SequenceFlow")
public class SequenceFlowFactory extends AbstractEdgesFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor
	 * .server.diagram.Shape)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent) {
		SequenceFlowConnector seqConnector = (SequenceFlowConnector) this
				.createDiagramElement(shape);
		SequenceFlow seqFlow = (SequenceFlow) this.createProcessElement(shape);

		seqConnector.setSequenceFlowRef(seqFlow);

		return new BPMNElement(seqConnector, seqFlow, shape.getResourceId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected Object createDiagramElement(Shape shape) {
		SequenceFlowConnector sequenceFlowConnector = new SequenceFlowConnector();
		sequenceFlowConnector.setId(shape.getResourceId() + "_gui");

		// TODO: Gedanken machen zu Label-Positioning
		sequenceFlowConnector.setLabel(shape.getProperty("name"));

		this.setBendpoints(sequenceFlowConnector, shape);

		return sequenceFlowConnector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected BaseElement createProcessElement(Shape shape) {
		SequenceFlow seqFlow = new SequenceFlow();
		seqFlow.setId(shape.getResourceId());
		seqFlow.setName(shape.getProperty("name"));

		String conditionType = shape.getProperty("conditiontype");
		String conditionExpression = shape.getProperty("conditionexpression");
		
		if (!(conditionType == null || conditionType.equals("Default"))
				&& !(conditionExpression == null || conditionExpression
						.equals(""))) {
			seqFlow.setConditionExpression(new Expression(conditionExpression));
		}

		if (conditionType != null && conditionType.equals("Default")) {
			seqFlow.setDefaultSequenceFlow(true);
		}

		return seqFlow;
	}

}
