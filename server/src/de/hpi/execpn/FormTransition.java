package de.hpi.execpn;

import de.hpi.petrinet.LabeledTransition;

public interface FormTransition extends LabeledTransition {
	
	String getModelURL();
	
	void setModelURL(String url);

}
