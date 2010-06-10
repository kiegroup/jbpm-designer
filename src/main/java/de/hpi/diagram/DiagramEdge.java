package de.hpi.diagram;

public class DiagramEdge extends DiagramObject {
	
	protected DiagramNode source;
	protected DiagramNode target;
	
	public DiagramNode getSource() {
		return source;
	}
	
	public void setSource(DiagramNode source) {
		if (this.source != source) {
			if (this.source != null)
				this.source.getOutgoingEdges().remove(this);
			if (source != null)
				source.getOutgoingEdges().add(this);
		}		
		this.source = source;
	}
	
	public DiagramNode getTarget() {
		return target;
	}
	
	public void setTarget(DiagramNode target) {
		if (this.target != target) {
			if (this.target != null)
				this.target.getIncomingEdges().remove(this);
			if (target != null)
				target.getIncomingEdges().add(this);
		}		
		this.target = target;
	}


}
