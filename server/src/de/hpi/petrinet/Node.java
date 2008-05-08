package de.hpi.petrinet;

import java.util.List;
import java.util.Vector;

import de.hpi.execpn.pnml.Locator;

public interface Node {
	
	String getId();
	
	void setId(String id);
	
	List<FlowRelationship> getIncomingFlowRelationships();

	List<FlowRelationship> getOutgoingFlowRelationships();
	String getGuard();
	void setGuard(String guard);
	
	String getRolename();
	void setRolename(String rolename);

	boolean isSimilarTo(Node node);
	
	Vector<Locator> getLocators();
	void addLocator(Locator locator);
	
}
