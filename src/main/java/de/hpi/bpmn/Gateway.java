package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

public abstract class Gateway extends Node {

	protected List<Assignment> assignments;
	
	/**
	 * the assignments getter
	 * @return List of Assignment
	 */
	public List<Assignment> getAssignments() {
		if (assignments == null)
			assignments = new ArrayList<Assignment>();
		return assignments;
	}
}
