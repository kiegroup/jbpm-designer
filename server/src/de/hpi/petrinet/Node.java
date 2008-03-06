package de.hpi.petrinet;

import java.util.List;

public interface Node {
	
	String getId();
	
	void setId(String id);
	
	List<FlowRelationship> getIncomingFlowRelationships();

	List<FlowRelationship> getOutgoingFlowRelationships();
	
	
	boolean isSimilarTo(Node node);

}
