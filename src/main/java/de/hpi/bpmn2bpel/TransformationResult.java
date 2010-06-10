package de.hpi.bpmn2bpel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TransformationResult {
	
	public enum Type { 
		DEPLOYMENT_DESCRIPTOR, 
		PROCESS, 
		PROCESS_WSDL,
		SERVICE_NAME
	};

	private Type type;
	private Document document;
	private Object object;
	private String processName;
	
	private static int processCount = 0;
	
	public Type getType() {
		return type;
	}

	public Document getDocument() {
		return document;
	}
	
	public String getProcessname() {
		return processName;
	}
	
	private void storeProcessName(Type type, Document document) {
		if ((type == Type.PROCESS) && (document != null)) {
			processCount++;
			Node node = document.getDocumentElement().getAttributes().getNamedItem("name");
			if ((node == null) || (node.getTextContent() == null)) {
				this.processName = "process " + Integer.toString(processCount);
			} else {
				this.processName = node.getTextContent();
			}
		} else {
			this.processName = null;
		}
	}
	
	public TransformationResult(Type type) {
		this.type = type;
		this.document = null;
		this.processName = null;
	}

	/**
	 * Constructor for process data including output AND a document
	 * 
	 * The document is only used to extract the name from.
	 * 
	 */
	public TransformationResult(Type type, Document document) {
		this.type = type;
		this.document = document;
		storeProcessName(type, document);
	}
	
	public TransformationResult(Type type, Object object) {
		this.type = type;
		this.object = object;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	public boolean isSuccess() {
		return (document != null);
	}
	
	public String toString() {
		String res;
		if (isSuccess()) {
			res = "success: " + this.getDocument();
		} else {
			res = "error: "; //+ this.getOutput();
		}
		return res;
	}
}
