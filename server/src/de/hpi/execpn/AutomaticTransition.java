package de.hpi.execpn;

import de.hpi.petrinet.Node;
import de.hpi.petrinet.SilentTransition;


public class AutomaticTransition extends ExecTransition implements SilentTransition {
	
	protected String modelURL;
	private String label;
	private String action;
	private String task;
	private String xsltURL;
	
	public String getModelURL() {
		return modelURL;
	}

	public void setModelURL(String modelURL) {
		this.modelURL = modelURL;
	}
	
	public String getXsltURL() {
		return xsltURL;
	}

	public void setXsltURL(String xsltURL) {
		this.xsltURL = xsltURL;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String taskId) {
		this.task = taskId;
	}

	public boolean isSimilarTo(Node node) {
		return (node instanceof AutomaticTransition);
	}

}
