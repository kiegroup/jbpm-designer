package de.hpi.execpn.impl;

import de.hpi.execpn.FormTransition;
import de.hpi.petrinet.impl.LabeledTransitionImpl;

public class FormTransitionImpl extends LabeledTransitionImpl implements FormTransition {
	
	protected String modelURL;

	public String getModelURL() {
		return modelURL;
	}

	public void setModelURL(String modelURL) {
		this.modelURL = modelURL;
	}

}
