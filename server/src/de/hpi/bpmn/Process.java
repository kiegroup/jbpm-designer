package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

public class Process implements Container {
	
	protected List<Node> childNodes;

	public List<Node> getChildNodes() {
		if (childNodes == null)
			childNodes = new ArrayList();
		return childNodes;
	}

}
