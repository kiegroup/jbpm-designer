package de.hpi.execpn.impl;

import de.hpi.execpn.ExecFlowRelationship;
import de.hpi.petrinet.impl.FlowRelationshipImpl;

public class ExecFlowRelationshipImpl extends FlowRelationshipImpl implements
		ExecFlowRelationship {

	protected String transformationURL;
	
	public String getTransformationURL() {
		return transformationURL;
	}

	public void setTransformationURL(String url) {
		transformationURL = url;
	}

}
