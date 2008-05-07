package de.hpi.execpn;

import de.hpi.petrinet.FlowRelationship;

public interface ExecFlowRelationship extends FlowRelationship {

	String getTransformationURL();
	
	void setTransformationURL(String url);
}
