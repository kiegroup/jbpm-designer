package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Task")
public class XPDLTask extends XMLConvertible {
	
	@Element("TaskManual")
	protected XPDLTaskManual taskManual;
	@Element("TaskReceive")
	protected XPDLTaskReceive taskReceive;
	@Element("TaskReference")
	protected XPDLTaskReference taskReference;
	@Element("TaskScript")
	protected XPDLTaskScript taskScript;
	@Element("TaskSend")
	protected XPDLTaskSend taskSend;
	@Element("TaskService")
	protected XPDLTaskService taskService;
	@Element("TaskUser")
	protected XPDLTaskUser taskUser;

	public XPDLTaskManual getTaskManual() {
		return taskManual;
	}

	public XPDLTaskReceive getTaskReceive() {
		return taskReceive;
	}

	public XPDLTaskReference getTaskReference() {
		return taskReference;
	}

	public XPDLTaskScript getTaskScript() {
		return taskScript;
	}

	public XPDLTaskSend getTaskSend() {
		return taskSend;
	}

	public XPDLTaskService getTaskService() {
		return taskService;
	}

	public XPDLTaskUser getTaskUser() {
		return taskUser;
	}
	
	public void readJSONimplementation(JSONObject modelElement) {
	}
	
	public void readJSONinstantiate(JSONObject modelElement) {
	}
	
	public void readJSONtaskref(JSONObject modelElement) {
	}
	
	public void readJSONtasktype(JSONObject modelElement) throws JSONException {
		String taskType = modelElement.optString("tasktype");
		if ("Service".equals(taskType)) {
			setTaskService(new XPDLTaskService());
			getTaskService().parse(createPassObject(modelElement));
		} else if ("Receive".equals(taskType)){
			setTaskReceive(new XPDLTaskReceive());
			getTaskReceive().parse(createPassObject(modelElement));
		} else if ("User".equals(taskType)) {
			setTaskUser(new XPDLTaskUser());
			getTaskUser().parse(createPassObject(modelElement));
		} else if ("Script".equals(taskType)) {
			setTaskScript(new XPDLTaskScript());
			getTaskScript().parse(createPassObject(modelElement));
		} else if ("Manual".equals(taskType)) {
			setTaskManual(new XPDLTaskManual());
			getTaskManual().parse(createPassObject(modelElement));
		} else if ("Reference".equals(taskType)) {
			setTaskReference(new XPDLTaskReference());
			getTaskReference().parse(createPassObject(modelElement));
		} else {
			setTaskSend(new XPDLTaskSend());
			getTaskSend().parse(createPassObject(modelElement));
		}
	}
	
	public void readJSONtaskunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "taskunknowns");
	}

	public void setTaskManual(XPDLTaskManual taskManual) {
		this.taskManual = taskManual;
	}

	public void setTaskReceive(XPDLTaskReceive taskReceive) {
		this.taskReceive = taskReceive;
	}

	public void setTaskReference(XPDLTaskReference taskReference) {
		this.taskReference = taskReference;
	}

	public void setTaskScript(XPDLTaskScript taskScript) {
		this.taskScript = taskScript;
	}

	public void setTaskSend(XPDLTaskSend taskSend) {
		this.taskSend = taskSend;
	}

	public void setTaskService(XPDLTaskService taskService) {
		this.taskService = taskService;
	}

	public void setTaskUser(XPDLTaskUser taskUser) {
		this.taskUser = taskUser;
	}
	
	public void writeJSONtasktypeobject(JSONObject modelElement) {
		if (getTaskManual() != null) {
			getTaskManual().write(modelElement);
		} else if (getTaskReceive() != null) {
			getTaskReceive().write(modelElement);
		} else if (getTaskReference() != null) {
			getTaskReference().write(modelElement);
		} else if (getTaskScript() != null) {
			getTaskScript().write(modelElement);
		} else if (getTaskSend() != null) {
			getTaskSend().write(modelElement);
		} else if (getTaskService() != null) {
			getTaskService().write(modelElement);
		} else if (getTaskUser() != null) {
			getTaskUser().write(modelElement);
		}
	}
	
	public void writeJSONtaskunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "taskunknowns");
	}
	
	protected JSONObject createPassObject(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		
		passObject.put("taskref", modelElement.optString("taskref"));
		passObject.put("implementation", modelElement.optString("implementation"));
		passObject.put("instantiate", modelElement.optString("instantiate"));
		passObject.put("tasktypeunknowns", modelElement.optString("tasktypeunknowns"));
		return passObject;
	}
}