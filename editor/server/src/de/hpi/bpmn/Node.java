package de.hpi.bpmn;

import java.util.List;

import de.hpi.util.Bounds;


public abstract class Node extends DiagramObject {
	
	protected String label;
	protected Container parent;
	protected Container process;
	protected Bounds bounds;
	
	public String toString() {
		return (label != null ? label : resourceId);
	}

	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}

	public Container getParent() {
		return parent;
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
	
	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	/**
	 * Searches recursively all ancestors for a pool
	 * @return pool or null
	 */
    public Pool getPool(){
    	if(this instanceof Pool){
    		return (Pool)this;
    	} else if(this.getParent() instanceof Node) {
    		return ((Node)this.getParent()).getPool();
    	} else {
    		return null;
    	}
    }
	
	public Node getCopy() {
		try {
			Node newnode = (Node)this.getClass().newInstance();
			newnode.setId(this.getId());
			newnode.setLabel(this.getLabel());
			newnode.setResourceId(this.getResourceId());
			return newnode;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}

	public Node getPredecessor() {
		List<SequenceFlow> sequenceFlows = this.getIncomingSequenceFlows();
		// TODO: check assumption of only one incoming edge
		return (Node) sequenceFlows.get(0).getSource();
	}

	public Node getSuccessor() {
		List<SequenceFlow> sequenceFlows = this.getOutgoingSequenceFlows();
		// TODO: check assumption of only one outgoing edge
		return (Node) sequenceFlows.get(0).getTarget();
	}
}
