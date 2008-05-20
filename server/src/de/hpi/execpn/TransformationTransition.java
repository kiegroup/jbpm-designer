package de.hpi.execpn;

import de.hpi.petrinet.LabeledTransition;

public interface TransformationTransition extends LabeledTransition {

	String getAction();

	String getXsltURL();

	String getLabel();

	void setAction(String action);

	void setXsltURL(String url);

	void setLabel(String label);
}
