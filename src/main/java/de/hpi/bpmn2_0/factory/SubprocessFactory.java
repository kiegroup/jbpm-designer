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

import de.hpi.bpmn2_0.annotations.Property;
import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.model.AdHocOrdering;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.AdHocSubProcess;
import de.hpi.bpmn2_0.model.activity.CallActivity;
import de.hpi.bpmn2_0.model.activity.SubProcess;
import de.hpi.bpmn2_0.model.activity.Transaction;
import de.hpi.bpmn2_0.model.activity.TransactionMethod;
import de.hpi.bpmn2_0.model.diagram.activity.CalledSubprocessShape;
import de.hpi.bpmn2_0.model.diagram.activity.EmbeddedSubprocessShape;
import de.hpi.bpmn2_0.model.diagram.activity.SubprocessShape;

/**
 * Factory to handle all types subprocesses in a process diagram
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"CollapsedSubprocess",
	"Subprocess",
	"CollapsedEventSubprocess",
	"EventSubprocess"
})
public class SubprocessFactory extends AbstractActivityFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor.server.diagram.Shape, de.hpi.bpmn2_0.factory.BPMNElement)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent)
			throws BpmnConverterException {
		
		return this.createSubprocess(shape);
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected Object createDiagramElement(Shape shape) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected BaseElement createProcessElement(Shape shape)
			throws BpmnConverterException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Property(name = "callacitivity", value = "true")
	protected CallActivity createCallActivity(Shape shape) {
		CallActivity callAct = new CallActivity();
		this.setStandardAttributes(callAct, shape);
		return callAct;
	}
	
	@Property(name = "isatransaction", value = "true")
	protected Transaction createTransaction(Shape shape) {
		Transaction transaction = new Transaction();
		transaction.setMethod(TransactionMethod.fromValue(shape.getProperty("transactionMethod")));
		return transaction;
	}
	
	@Property(name = "isadhoc", value = "true")
	protected AdHocSubProcess createAdhocSubprocess(Shape shape) {
		AdHocSubProcess adhocSub = new AdHocSubProcess();
		/* Mapping of properties */
		String condition = shape.getProperty("adhoccompletioncondition");
		if(condition != null && ! condition.isEmpty()) 
			adhocSub.setCompletionCondition(new FormalExpression(condition));
		
		String ordering = shape.getProperty("adhocordering");
		if(ordering != null) {
			adhocSub.setOrdering(AdHocOrdering.fromValue(shape.getProperty("adhocordering")));
		}
		
		String cancelRemIns = shape.getProperty("adhoccancelremaininginstances");
		if(cancelRemIns != null)
			adhocSub.setCancelRemainingInstances(!cancelRemIns.equalsIgnoreCase("false"));
		
		return adhocSub;
	}
	
	protected BPMNElement createSubprocess(Shape shape) throws BpmnConverterException {
		Activity subprocess = null;
		try {
			subprocess = (Activity) this.invokeCreatorMethodAfterProperty(shape);
			this.createLoopCharacteristics(subprocess, shape);
		} catch (Exception e) {
//			throw new BpmnConverterException("Error creating subprocess elements.", e);
		} 
		
		if(subprocess == null) 
			subprocess = new SubProcess();
		
		this.setStandardAttributes(subprocess, shape);
		
		SubprocessShape subproShape = (subprocess instanceof CallActivity ? new CalledSubprocessShape() : new EmbeddedSubprocessShape());
		this.setVisualAttributes(subproShape, shape);
		
		subproShape.setDiagramLink(shape.getProperty("entry"));
		
		/* Mark as collapsed or expanded */
		if(shape.getStencilId().matches(".*Collapsed.*")) {
			subproShape.setIsExpanded(false);
		} else {
			subproShape.setIsExpanded(true);
		}
		
		/* Mark as event subprocess */
		if(!(subprocess instanceof CallActivity)) {
			if(shape.getStencilId().matches(".*EventSubprocess.*")) {
				((SubProcess) subprocess).setTriggeredByEvent(true);
			} else {
				((SubProcess) subprocess).setTriggeredByEvent(false);
			}
		}
		
		subprocess.setId(shape.getResourceId());
		subprocess.setName(shape.getProperty("name"));
		
		return new BPMNElement(subproShape, subprocess, shape.getResourceId());
	}

}
