package de.hpi.petrinet;

import java.util.List;

/**
 * Copyright (c) 2008 Gero Decker
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
public class FlowRelationship {
	
	protected Node source;
	protected Node target;
	protected String id;

	public Node getSource() {
		return source;
	}

	public void setSource(Node value) {
		if (source != null)
			source.getOutgoingFlowRelationships().remove(this);
		source = value;
		if (source != null)
			((List<FlowRelationship>)source.getOutgoingFlowRelationships()).add(this);
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node value) {
		if (target != null)
			target.getIncomingFlowRelationships().remove(this);
		target = value;
		if (target != null)
			((List<FlowRelationship>)target.getIncomingFlowRelationships()).add(this);
	}
	
	
	public String toString() {
		return "("+source+","+target+")";
	}

	public String getId() {
		if(id != null){
			return id;
		} else if(id == null && 
				this.getSource().getId() != null && 
				this.getTarget().getId() != null){
			return this.getSource().getId() + this.getTarget().getId();
		} else {
			return null;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

}
