package de.hpi.execpn;

import de.hpi.petrinet.Transition;

public abstract class ExecTransition extends ExecNode implements Transition {

	protected String modelURL;

	public String getModelURL() {
		return modelURL;
	}

	public void setModelURL(String modelURL) {
		this.modelURL = modelURL;
	}
	
}
