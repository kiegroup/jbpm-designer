package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class Task extends Activity {
	protected boolean skippable;
	protected String rolename;
	protected String rightInitProcess;
	protected String rightExecuteTask; 
	protected String rightSkipTask;
	protected String rightDelegateTask;
	protected String form;
	protected String color;
	
	/*Start of BPMN EXTENSION for the mapping to YAWL */
	/** specifies if the offering of this task is done by users or the YAWL engine */
	protected String yawl_offeredBy = "";
	/** specifies if users or the YAWL engine allocate this task */
	protected String yawl_allocatedBy = "";
	/** specifies if users or the YAWL engine start this task */
	protected String yawl_startedBy = "";
	
	/* End of BPMN EXTENSION for the mapping to YAWL */
	
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
	/* The WSDL-URL of the related web service */
	private String wsdlUrl;
	
	
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

	public void setWsdlUrl(String wsdlUrl) {
		this.wsdlUrl = wsdlUrl;
	}

	public String getWsdlUrl() {
		return wsdlUrl;
	}

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}

	/**
	 * the offeredBy getter for YAWL
	 * @return yawl_offeredBy
	 */
	public String getYawl_offeredBy() {
		return yawl_offeredBy;
	}

	/**
	 * the offeredBy setter for YAWL
	 * @param yawl_offeredBy
	 */
	public void setYawl_offeredBy(String yawl_offeredBy) {
		this.yawl_offeredBy = yawl_offeredBy;
	}

	/**
	 * the allocatedBy getter for YAWL
	 * @return yawl_allocatedBy
	 */
	public String getYawl_allocatedBy() {
		return yawl_allocatedBy;
	}

	/**
	 * the allocatedBy setter for YAWL
	 * @param yawl_allocatedBy
	 */
	public void setYawl_allocatedBy(String yawl_allocatedBy) {
		this.yawl_allocatedBy = yawl_allocatedBy;
	}

	/**
	 * the startedBy getter for YAWL
	 * @return yawl_startedBy
	 */
	public String getYawl_startedBy() {
		return yawl_startedBy;
	}

	/**
	 * the startedBy setter for YAWL
	 * @param yawl_startedBy
	 */
	public void setYawl_startedBy(String yawl_startedBy) {
		this.yawl_startedBy = yawl_startedBy;
	}
}
