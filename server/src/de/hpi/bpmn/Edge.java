package de.hpi.bpmn;

/**
 * Why does Edge need incoming and outgoing edges (inherited from DiagramObject)?
 * Answer: undirected associations can be attached to ControlFlow and MessageFlow 
 * 
 * @author Gero.Decker
 *
 */
public abstract class Edge extends DiagramObject {
	
	protected DiagramObject source;
	protected DiagramObject target;
	
	public String toString() {
		return (id != null ? id : resourceId);
	}

	public DiagramObject getSource() {
		return source;
	}
	
	public void setSource(DiagramObject source) {
		if (this.source != source) {
			if (this.source != null)
				this.source.getOutgoingEdges().remove(this);
			if (source != null)
				source.getOutgoingEdges().add(this);
		}		
		this.source = source;
	}
	
	public DiagramObject getTarget() {
		return target;
	}
	
	public void setTarget(DiagramObject target) {
		if (this.target != target) {
			if (this.target != null)
				this.target.getIncomingEdges().remove(this);
			if (target != null)
				target.getIncomingEdges().add(this);
		}		
		this.target = target;
	}

}
