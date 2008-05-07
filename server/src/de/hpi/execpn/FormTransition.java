package de.hpi.execpn;

import de.hpi.petrinet.LabeledTransition;

public interface FormTransition extends LabeledTransition {
	
	String getModelURL();
	
	void setModelURL(String url);
	
	String getBindingsURL();
	
	void setBindingsURL(String url);
	
	String getFormURL();
	
	void setFormURL(String url);

}
