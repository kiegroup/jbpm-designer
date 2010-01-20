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

package de.hpi.bpmn2_0.model.data_object;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.connector.Edge;

/**
 * The AbstractDataObject abstracts from data related elements, like 
 * {@link DataObject}, {@link DataInput}, {@link DataOutput}, {@link DataStore}.
 * 
 * @author Sven Wagner-Boysen
 */
public abstract class AbstractDataObject extends FlowNode {
	
	/**
	 * Find an appropriate {@link Process} container for the data object.
	 * 
	 * The algorithm checks the source and target neighborhood nodes of the 
	 * data object and the takes the referenced process of one of the neighbors.
	 * 
	 * Navigates into both directions.
	 */
	public void findRelatedProcess() {
		Process process = this.findRelatedProcessRecursivly(this);
		if(process != null) {
			this.setProcess(process);
			process.addChild(this);
		}
	}
	
	/**
	 * Navigates into both directions.
	 * 
	 * @param flowElement
	 * 		The {@link FlowElement} to investigate.
	 */
	private Process findRelatedProcessRecursivly(FlowElement flowElement) {
		
		/* Check if one of the neighbors is assigned to a Process, 
		 * otherwise continue with the after next. */
		
		for(Edge edge : this.getIncoming()) {
			Process process = edge.getSourceRef().getProcess();
			if(process != null)
				return process;
		}
		
		for(Edge edge : this.getOutgoing()) {
			Process process = edge.getTargetRef().getProcess();
			if(process != null)
				return process;
		}
		
		/* Continue with the after next nodes */
		
		for(Edge edge : this.getIncoming()) {
			Process process = this.findRelatedProcessRecursivly(edge.getSourceRef());
			if(process != null)
				return process;
		}
		
		for(Edge edge : this.getOutgoing()) {
			Process process = this.findRelatedProcessRecursivly(edge.getTargetRef());
			if(process != null)
				return process;
		}
		
		return null;
	}
}
