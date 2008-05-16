package de.hpi.petrinet;

public interface FlowRelationship {
	
	public final static int RELATION_MODE_TAKETOKEN = 0;
	
	public final static int RELATION_MODE_READTOKEN = 1;
	
	Node getSource();
	
	void setSource(Node source);
	
	Node getTarget();
	
	void setTarget(Node target);
	
	int getMode();
	
	void setMode(int mode);

}
