package de.hpi.execpn.impl;

import de.hpi.execpn.AutomaticTransition;
import de.hpi.petrinet.impl.TauTransitionImpl;

public class AutomaticTransitionImpl extends TauTransitionImpl implements AutomaticTransition {

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

	public boolean isManuallyTriggered() {
		return triggerManually;
	}

	public void setManuallyTriggered(boolean triggerManually) {
		this.triggerManually = triggerManually;
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
