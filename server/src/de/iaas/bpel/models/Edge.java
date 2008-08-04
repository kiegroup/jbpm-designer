package de.iaas.bpel.models;

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

public abstract class Edge extends DiagramObject {
	
	protected Node source;
	protected Node target;
	
	public String toString() {
		return (id != null ? id : resourceId);
	}

	public Node getSource() {
		return source;
	}
	
	public void setSource(Node source) {
		if (this.source != source) {
			if (this.source != null)
				this.source.getOutgoingEdges().remove(this);
			if (source != null)
				source.getOutgoingEdges().add(this);
		}		
		this.source = source;
	}
	
	public Node getTarget() {
		return target;
	}
	
	public void setTarget(Node target) {
		if (this.target != target) {
			if (this.target != null)
				this.target.getIncomingEdges().remove(this);
			if (target != null)
				target.getIncomingEdges().add(this);
		}		
		this.target = target;
	}

}
