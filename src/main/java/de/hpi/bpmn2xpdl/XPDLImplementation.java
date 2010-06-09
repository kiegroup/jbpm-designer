package de.hpi.bpmn2xpdl;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Implementation")
public class XPDLImplementation extends XMLConvertible {
	
	@Element("No")
	protected XPDLNo no;
	@Element("Task")
	protected XPDLTask task;
	
	public static boolean handlesTaskType(String taskType) {
		String[] types = {
				"Service",
				"Receive",
				"User",
				"Script",
				"Manual",
				"Reference",
				"Send"};
		return Arrays.asList(types).contains(taskType);
	}
	
	public XPDLNo getNo() {
		return no;
	}
	
	public XPDLTask getTask() {
		return task;
	}
	
	public void readJSONimplementation(JSONObject modelElement) {
	}
	
	public void readJSONimplementationunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "implementationunknowns");
	}
	
	public void readJSONinstantiate(JSONObject modelElement) {
	}
	
	public void readJSONnounknowns(JSONObject modelElement) throws JSONException {
		String taskType = modelElement.optString("tasktype");
		if (!XPDLImplementation.handlesTaskType(taskType)) {
			initializeNo();
			
			JSONObject passObject = new JSONObject();
			passObject.put("nounknowns", modelElement.optString("nounknowns"));
			getNo().parse(passObject);
		}
	}
	
	public void readJSONtaskref(JSONObject modelElement) {
	}
	
	public void readJSONtasktype(JSONObject modelElement) throws JSONException {
		String taskType = modelElement.optString("tasktype");
		if (XPDLImplementation.handlesTaskType(taskType)) {
			passInformationToTask(modelElement, "tasktype");
		} else {
			initializeNo();
		}
	}
	
	public void readJSONtasktypeunknowns(JSONObject modelElement) throws JSONException {
		String taskType = modelElement.optString("tasktype");
		if (XPDLImplementation.handlesTaskType(taskType)) {
			passInformationToTask(modelElement, "tasktypeunknowns");
		}
	}
	
	public void readJSONtaskunknowns(JSONObject modelElement) throws JSONException {
		String taskType = modelElement.optString("tasktype");
		if (XPDLImplementation.handlesTaskType(taskType)) {
			passInformationToTask(modelElement, "taskunknowns");
		}
	}
	
	public void setNo(XPDLNo no) {
		this.no = no;
	}
	
	public void setTask(XPDLTask task) {
		this.task = task;
	}
	
	public void writeJSONimplementationunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "implementationunknowns");
	}
	
	public void writeJSONno(JSONObject modelElement) {
		if (getNo() != null) {
			getNo().write(modelElement);
		}
	}
	
	public void writeJSONtask(JSONObject modelElement) {
		if (getTask() != null) {
			getTask().write(modelElement);
		}
	}
	
	protected void initializeNo() {
		if (getNo() == null) {
			setNo(new XPDLNo());
		}
	}
	
	protected void initializeTask() {
		if (getTask() == null) {
			setTask(new XPDLTask());
		}
	}
	
	protected void passInformationToTask(JSONObject modelElement, String key) throws JSONException {
		initializeTask();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		passObject.put("taskref", modelElement.optString("taskref"));
		passObject.put("tasktype", modelElement.optString("tasktype"));
		passObject.put("implementation", modelElement.optString("implementation"));
		passObject.put("instantiate", modelElement.optString("instantiate"));
		
		getTask().parse(passObject);
	}
}
