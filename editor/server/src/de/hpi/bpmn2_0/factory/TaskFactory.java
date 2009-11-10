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

import de.hpi.bpmn2_0.annotations.Property;
import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.model.activity.BusinessRuleTask;
import de.hpi.bpmn2_0.model.activity.ManualTask;
import de.hpi.bpmn2_0.model.activity.ReceiveTask;
import de.hpi.bpmn2_0.model.activity.ScriptTask;
import de.hpi.bpmn2_0.model.activity.SendTask;
import de.hpi.bpmn2_0.model.activity.ServiceTask;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.activity.UserTask;
import de.hpi.bpmn2_0.model.diagram.activity.ActivityShape;

/**
 * Concrete class to create any kind of task objects from a {@link Shape} with 
 * the stencil id "http://b3mn.org/stencilset/bpmn2.0#Task"
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 *
 */
@StencilId("Task")
public class TaskFactory extends AbstractBpmnFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected ActivityShape createDiagramElement(Shape shape) {
		ActivityShape actShape = new ActivityShape();
		this.setVisualAttributes(actShape, shape);
		
		return actShape;		
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected Task createProcessElement(Shape shape) throws BpmnConverterException {
		try {
			Task task = (Task) this.invokeCreatorMethodAfterProperty(shape);
			return task;
		} catch (Exception e) {
			throw new BpmnConverterException(
					"Error while creating the process element of " 
					+ shape.getStencilId(), e);
		}
	}

	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent) throws BpmnConverterException {
		Task task = this.createProcessElement(shape);
		ActivityShape activity = this.createDiagramElement(shape);
		
		activity.setActivityRef(task);
		
		return new BPMNElement(activity, task, shape.getResourceId());
	}

	@Property(name = "tasktype", value = "None") 
	protected Task createTask(Shape shape) {
		Task task = new Task();
		
		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		
		return task;
	}
	
	@Property(name = "tasktype", value = "User")
	protected UserTask createUserTask(Shape shape) {
		UserTask task = new UserTask();
		
		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		
		return task;
	}
	
	@Property(name = "tasktype", value = "Receive")
	protected ReceiveTask createReceiveTask(Shape shape) {
		ReceiveTask task = new ReceiveTask();
		
		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		
		return task;
	}
	
	@Property(name = "tasktype", value = "Send")
	protected SendTask createSendTask(Shape shape) {
		SendTask task = new SendTask();
		
		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		
		return task;
	}
	
	@Property(name = "tasktype", value = "Script")
	protected ScriptTask createScriptTask(Shape shape) {
		ScriptTask task = new ScriptTask();
		
		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		
		return task;
	}
	
	@Property(name = "tasktype", value = "Business Rule")
	protected BusinessRuleTask createBusinessRuleTask(Shape shape) {
		BusinessRuleTask task = new BusinessRuleTask();
		
		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		
		return task;
	}
	
	@Property(name = "tasktype", value = "Service")
	protected ServiceTask createServiceTask(Shape shape) {
		ServiceTask task = new ServiceTask();
		
		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		
		return task;
	}
	
	@Property(name = "tasktype", value = "Manual")
	protected ManualTask createManualTask(Shape shape) {
		ManualTask task = new ManualTask();
		
		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		
		return task;
	}
}
