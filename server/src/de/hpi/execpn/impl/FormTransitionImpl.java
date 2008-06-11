package de.hpi.execpn.impl;

import de.hpi.execpn.FormTransition;
import de.hpi.petrinet.impl.LabeledTransitionImpl;

public class FormTransitionImpl extends LabeledTransitionImpl implements FormTransition {
	
	protected String modelURL;
	protected String bindingsURL;
	protected String formURL;
	
	public String getModelURL() {
		return modelURL;
	}

	public void setModelURL(String modelURL) {
		this.modelURL = modelURL;
	}

	public String getBindingsURL() {
		return bindingsURL;
	}

	public void setBindingsURL(String bindingsURL) {
		this.bindingsURL = bindingsURL;
	}
	
	public String getFormURL() {
		return formURL;
	}

	public void setFormURL(String formURL) {
		this.formURL = formURL;
	}
	
}
