/**
 * 
 */
package de.hpi.bpmn2bpel.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import de.hpi.bpmn.Association;
import de.hpi.bpmn.DataObject;
import de.hpi.bpmn.Edge;

/**
 * In the context of BPEL a {@link DataObject} represents either the input or 
 * output parameters of a web service's method. It is mapped to a BPEL Variable.
 * 
 * @author Sven Wagner-Boysen
 *
 */
public class BPELDataObject extends DataObject {
	
	/* The message type of the variable */
	private String messageType;
	/* The values of the web service's parameters */
	private JSONObject properties;
	/* The namespace of the related web service */
	private String namespace;
	/* The name of the related web service */
	private String serviceName;
	/* The used operation of the related web service */
	private String operation;
	/* The related port tye */
	private String portType;
	
	/**
	 * Returns the incoming {@link BPELDataObject}s that are associated with this
	 * data object.
	 * 
	 * @return
	 * 		The list of {@link BPELDataObject}
	 */
	public List<BPELDataObject> getSourceBPELDataObjects() {
		ArrayList<BPELDataObject> dataObjects = new ArrayList<BPELDataObject>();
		for(Edge e : this.getIncomingEdges()) {
			if ((e instanceof Association) && (e.getSource() instanceof BPELDataObject)) {
				dataObjects.add((BPELDataObject) e.getSource());
			}
		}
		return dataObjects;
	}
	
	/* Getters & Setters */
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public JSONObject getProperties() {
		return properties;
	}
	public void setProperties(JSONObject properties) {
		this.properties = properties;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getOperation() {
		return operation;
	}
	public void setPortType(String portType) {
		this.portType = portType;
	}
	public String getPortType() {
		return portType;
	}
}
