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
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ExecTransition clone = (ExecTransition) super.clone();
		
		if (this.getModelURL() != null)
			clone.setModelURL(new String(this.getModelURL()));
		return clone;
	}

	
}
