package de.hpi.highpetrinet;

public class HighFlowRelationship extends de.hpi.petrinet.FlowRelationship {
	public enum ArcType {
		Reset, Inhibitor, Read, Plain;
	}
	
	protected ArcType type = ArcType.Plain;
	public ArcType getType() {
		return type;
	}
	public void setType(ArcType type) {
		this.type = type;
	}
}