package de.hpi.execpn.impl;

import de.hpi.execpn.TransformationTransition;
import de.hpi.petrinet.impl.TauTransitionImpl;

public class TransformationTransitionImpl extends TauTransitionImpl implements TransformationTransition {

	protected String xsltURL;
	protected boolean triggerManually;
	protected String action;
	protected String label;
	
	public String getXsltURL() {
		return xsltURL;
	}

	public void setXsltURL(String url) {
		xsltURL = url;		
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
}
