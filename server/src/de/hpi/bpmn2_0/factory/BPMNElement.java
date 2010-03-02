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

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.diagram.BpmnNode;
import de.hpi.bpmn2_0.model.diagram.BpmnShape;

public class BPMNElement {
	private BpmnShape shape;
	private BaseElement node;
	private String id;

	public BPMNElement(BpmnShape shape, BaseElement node, String id) {
		this.shape = shape;
		this.node = node;
		this.id = id;
	}
	
	/**
	 * Adds a {@link BPMNElement} as child to the current {@link BPMNElement}
	 * 
	 * @param child
	 * 		The child element
	 */
	public void addChild(BPMNElement child) {
		if(this.getNode() != null) 
			/* Set the lane reference */
			if(child.getNode() != null) {
				child.getNode().setLane(this.getNode().getLane());
			}
			this.getNode().addChild(child.getNode());
		
		if(this.getShape() instanceof BpmnNode && child.getShape() instanceof BpmnNode) {
			((BpmnNode) this.getShape()).addChild((BpmnNode) child.getShape());
		}
	}
	
	/* Getter & Setter */
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public BpmnShape getShape() {
		return shape;
	}
	public void setShape(BpmnShape shape) {
		this.shape = shape;
	}
	public BaseElement getNode() {
		return node;
	}
	public void setNode(BaseElement node) {
		this.node = node;
	}
}
