package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class CollapsedSubprocess extends Activity {
	protected boolean skippable;
	protected String rolename;
	protected String rightInitProcess;
	protected String rightExecuteTask; 
	protected String rightSkipTask;
	protected String rightDelegateTask;
	protected String form;
	protected String color;
	protected String subprocessRef;
	
	/* The input message type of the operation */
	private String inMessageType;
	/* The output message type of the operation */
	private String outMessageType;
	/* The values of the web service's parameters */
	private JSONObject inputSets;
	/* The namespace of the related web service */
	private String namespace;
	/* The name of the related web service */
	private String serviceName;
	/* The used operation of the related web service */
	private String operation;
	/* The related port type */
	private String portType;
	
	
	/**
	 * Returns all input {@link DataObject}s
	 * 
	 * @return
	 * 		A list of input data objects
	 */
	public List<DataObject> getInputDataObjects() {
		ArrayList<DataObject> dataObjects = new ArrayList<DataObject>();
		
		for (Edge e : this.getIncomingEdges()) {
			if ((e instanceof Association) && (e.source instanceof DataObject) ) {
				dataObjects.add((DataObject) e.source);
			}
		}
		return dataObjects;
	}
	
	/**
	 * Checks if task describes the same web service as this task.
	 * Two tasks are equal concerning their services, if and only if 
	 * <ul>
	 * 	<li>the namespace</li>
	 * 	<li>the service name</li>
	 * 	<li>and the operation are equal</li>
	 * </ul>
	 * 
	 * @param task
	 * 		The task to compare with
	 * @return
	 * 		The check result
	 */
	public boolean describesEqualService(Task task) {
		if(this.namespace.equals(task.getNamespace())
				&& this.serviceName.equals(task.getServiceName())
				&& this.operation.equals(task.getOperation())) {
			
			return true;
		}
		
		return false;
	}
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	public String getSubprocessRef() {
		return subprocessRef;
	}

	public void setSubprocessRef(String subprocessRef) {
		this.subprocessRef = subprocessRef;
	}

	public String getInMessageType() {
		return inMessageType;
	}

	public void setInMessageType(String inMessageType) {
		this.inMessageType = inMessageType;
	}

	public void setOutMessageType(String outMessageType) {
		this.outMessageType = outMessageType;
	}

	public String getOutMessageType() {
		return outMessageType;
	}

	public JSONObject getInputSets() {
		return inputSets;
	}

	public void setInputSets(JSONObject inputSets) {
		this.inputSets = inputSets;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getPortType() {
		return portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public boolean isSkippable() {
		return skippable;
	}

	public void setSkippable(boolean skippable) {
		this.skippable = skippable;
	}
	
	public String getRolename() {
		return rolename;
	}
	
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
}
