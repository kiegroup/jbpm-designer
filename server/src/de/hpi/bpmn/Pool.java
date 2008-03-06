package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

public class Pool extends Node implements Container {
	
	protected List<Node> childNodes;
	
	public List<Node> getChildNodes() {
		if (childNodes == null)
			childNodes = new ArrayList();
		return childNodes;
	}

}
