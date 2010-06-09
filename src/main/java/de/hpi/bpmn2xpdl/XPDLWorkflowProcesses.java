package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("WorkflowProcesses")
public class XPDLWorkflowProcesses extends XMLConvertible {

	@Element("WorkflowProcess")
	protected ArrayList<XPDLWorkflowProcess> workflowProcesses;

	public void add(XPDLWorkflowProcess newProcess) {
		initializeWorkflowProcesses();
		
		getWorkflowProcesses().add(newProcess);
	}
	
	public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
		if (getWorkflowProcesses() != null) {
			for (XPDLWorkflowProcess process: getWorkflowProcesses()) {
				process.createAndDistributeMapping(mapping);
			}
		}
	}
	
	public ArrayList<XPDLWorkflowProcess> getWorkflowProcesses() {
		return workflowProcesses;
	}
	
	public void readJSONworkflowprocessesunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "workflowprocessesunknowns");
	}

	public void setWorkflowProcesses(ArrayList<XPDLWorkflowProcess> newProcess) {
		this.workflowProcesses = newProcess;
	}
	
	public void writeJSONworkflowprocessunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "workflowprocessesunknowns");
	}
	
	protected void initializeWorkflowProcesses() {
		if (getWorkflowProcesses() == null) {
			setWorkflowProcesses(new ArrayList<XPDLWorkflowProcess>());
		}
	}
}
