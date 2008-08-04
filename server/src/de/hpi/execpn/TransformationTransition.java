package de.hpi.execpn;


public class TransformationTransition extends ExecLabeledTransition {

	protected String xsltURL;
	protected boolean triggerManually;
	
	public String getXsltURL() {
		return xsltURL;
	}

	public void setXsltURL(String url) {
		xsltURL = url;		
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
