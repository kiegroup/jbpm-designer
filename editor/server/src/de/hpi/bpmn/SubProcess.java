package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpmn.serialization.BPMNSerialization;
import de.hpi.bpmn2bpel.model.Container4BPEL;

public class SubProcess extends Activity implements Container, Container4BPEL {
	
	protected List<Node> childNodes;
	protected boolean adhoc;
	protected boolean parallelOrdering;  
	protected String completionCondition; 

	public String getCompletionCondition() {
		return completionCondition;
	}

	public void setCompletionCondition(String completionCondition) {
		this.completionCondition = completionCondition;
	}

	public List<Node> getChildNodes() {
		if (childNodes == null)
			childNodes = new ArrayList<Node>();
		return childNodes;
	}

	public boolean isAdhoc() {
		return adhoc;
	}

	public void setAdhoc(boolean adhoc) {
		this.adhoc = adhoc;
	}

	public boolean isParallelOrdering() {
		return parallelOrdering;
	}

	public void setParallelOrdering(boolean parallelOrdering) {
		this.parallelOrdering = parallelOrdering;
	}
	
	
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}

	
	public Node getCopy() {
		SubProcess newnode = (SubProcess)super.getCopy();
		newnode.setAdhoc(this.isAdhoc());
		newnode.setParallelOrdering(this.isParallelOrdering());
		newnode.setCompletionCondition(this.getCompletionCondition());
		return newnode;
	}

	public List<EndEvent> getEndEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<StartEvent> getStartEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Task> getTasks() {
		// TODO Auto-generated method stub
		return null;
	}

}
