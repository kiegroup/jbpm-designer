package de.hpi.execpn;

import de.hpi.petrinet.FlowRelationship;

public class ExecFlowRelationship extends FlowRelationship {

	public final static int RELATION_MODE_TAKETOKEN = 0;
	public final static int RELATION_MODE_READTOKEN = 1;
	
	protected String transformationURL;
	protected int mode = RELATION_MODE_TAKETOKEN;
	
	public String getTransformationURL() {
		return transformationURL;
	}

	public void setTransformationURL(String url) {
		transformationURL = url;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

}
