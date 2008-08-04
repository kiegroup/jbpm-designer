package de.iaas.bpel.models;

import java.util.ArrayList;
import java.util.List;

import de.iaas.bpel.models.Edge;



/**
 * Copyright (c) 2008 Zhen Peng
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

public abstract class Node extends DiagramObject {
	
	protected String label;
	protected Container parent;
	protected Container process;
	protected List<Edge> outgoingEdges;
	protected List<Edge> incomingEdges;
	
	public String toString() {
		return (label != null ? label : resourceId);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		if (label != null)
			label = label.replace("\n", "_").replace(" ", "_");
		this.label = label;
	}

	public Container getParent() {
		return parent;
	}

	public List<Edge> getIncomingEdges() {
		if (incomingEdges == null)
			incomingEdges = new ArrayList<Edge>();
		return incomingEdges;
	}

	public List<Edge> getOutgoingEdges() {
		if (outgoingEdges == null)
			outgoingEdges = new ArrayList<Edge>();
		return outgoingEdges;
	}
	
	public void setParent(Container parent) {
		if (this.parent != parent) {
			if (this.parent != null && this.parent != this.process)
				this.parent.getChildNodes().remove(this);
			if (parent != null && parent != this.process)
				parent.getChildNodes().add(this);
		}
		this.parent = parent;
	}

	public Container getProcess() {
		return process;
	}

	public void setProcess(Container process) {
		if (this.process != process) {
			if (this.process != null && this.process != this.parent)
				this.process.getChildNodes().remove(this);
			if (process != null && process != this.parent)
				process.getChildNodes().add(this);
		}
		this.process = process;
	}

}
