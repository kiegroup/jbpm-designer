package de.hpi.execpn;


public class FormTransition extends ExecLabeledTransition {
	
	protected String bindingsURL;
	protected String formURL;
	
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
