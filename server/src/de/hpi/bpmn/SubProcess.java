package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

public class SubProcess extends Activity implements Container {
	
	protected List<Node> childNodes;
	protected boolean adhoc;

	public List<Node> getChildNodes() {
		if (childNodes == null)
			childNodes = new ArrayList();
		return childNodes;
	}

	public boolean isAdhoc() {
		return adhoc;
	}

	public void setAdhoc(boolean adhoc) {
		this.adhoc = adhoc;
	}
	
}
