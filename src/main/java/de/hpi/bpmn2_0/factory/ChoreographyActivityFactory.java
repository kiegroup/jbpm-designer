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
import de.hpi.bpmn2_0.model.choreography.ChoreographyActivity;
import de.hpi.bpmn2_0.model.choreography.ChoreographySubProcess;
import de.hpi.bpmn2_0.model.choreography.ChoreographyTask;
import de.hpi.bpmn2_0.model.diagram.ChoreographyActivityShape;

/**
 * Factory that creates elements of a choreography diagram.
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"ChoreographyTask",
	"ChoreographySubprocessCollapsed",
	"ChoreographySubprocessExpanded"
})
public class ChoreographyActivityFactory extends AbstractBpmnFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor.server.diagram.Shape, de.hpi.bpmn2_0.factory.BPMNElement)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent)
			throws BpmnConverterException {
		ChoreographyActivityShape choreoShape = this.createDiagramElement(shape);
		ChoreographyActivity choreoAct = this.createProcessElement(shape);
		choreoShape.setActivityRef(choreoAct);
		return new BPMNElement(choreoShape, choreoAct, shape.getResourceId());
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected ChoreographyActivityShape createDiagramElement(Shape shape) {
		ChoreographyActivityShape choreoShape = new ChoreographyActivityShape();
		this.setVisualAttributes(choreoShape, shape);
		return choreoShape;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected ChoreographyActivity createProcessElement(Shape shape)
			throws BpmnConverterException {
		try {
			ChoreographyActivity activity = (ChoreographyActivity) this.invokeCreatorMethod(shape);
			activity.setId(shape.getResourceId());
			activity.setName(shape.getProperty("name"));
			return activity;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
	
	/**
	 * Creator method for a choreography task.
	 * 
	 * @param shape
	 * 		The resource shape
	 * @return
	 * 		the {@link ChoreographyTask}
	 */
	@StencilId("ChoreographyTask")
	protected ChoreographyTask createChoreographyTask(Shape shape) {
		return new ChoreographyTask();
	}
	
	/**
	 * Creator method for a collapsed choreography subprocess.
	 * 
	 * @param shape
	 * 		The resource shape
	 * @return
	 * 		the {@link ChoreographySubprocess}
	 */
	@StencilId({
		"ChoreographySubprocessCollapsed",
		"ChoreographySubprocessExpanded"
	})
	protected ChoreographySubProcess createChoreographySubprocessCollapsed(Shape shape) {
		return new ChoreographySubProcess();
	}
}
