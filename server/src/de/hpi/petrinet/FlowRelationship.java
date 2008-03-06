package de.hpi.petrinet;

public interface FlowRelationship {
	
	Node getSource();
	
	void setSource(Node source);
	
	Node getTarget();
	
	void setTarget(Node target);

}
