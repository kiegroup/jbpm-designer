package de.hpi.execpn;

import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.LabeledTransitionImpl;
import de.hpi.petrinet.Node;

/**
 * @author gero.decker
 */
public class ExecLabeledTransition extends ExecTransition implements LabeledTransition {

	protected String label;
	protected String action;
	protected String task; 

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isSimilarTo(Node node) {
		if (node instanceof LabeledTransition && getLabel() != null) {
			return (getLabel().equals(((LabeledTransition)node).getLabel()));
		}
		return false;
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
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ExecLabeledTransition clone = (ExecLabeledTransition) super.clone();
		if (this.getLabel() != null)
			clone.setLabel(new String(this.getLabel()));
		if (this.getTask() != null)
			clone.setTask(new String(this.getTask()));
		if (this.getAction() != null)
			clone.setAction(new String(this.getAction()));
		return clone;
	}

}


