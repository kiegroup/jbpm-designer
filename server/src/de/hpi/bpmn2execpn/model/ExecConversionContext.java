package de.hpi.bpmn2execpn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hpi.bpmn.Container;
import de.hpi.bpmn2pn.model.ConversionContext;

public class ExecConversionContext extends ConversionContext {
	
	public Map<Container, List<ExecTask>> subprocessToExecTasksMap = new HashMap<Container, List<ExecTask>>();
	
	public void addToSubprocessToExecTasksMap(Container key, ExecTask value) {
		List<ExecTask> taskList = this.subprocessToExecTasksMap.get(key);
		if (taskList == null) {
			taskList = new ArrayList<ExecTask>();
			this.subprocessToExecTasksMap.put(key, taskList);
		}
		taskList.add(value);
	}

}
