package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class SubProcess extends Activity implements Container {
	
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
	
	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
