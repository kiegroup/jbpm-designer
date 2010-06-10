package de.hpi.bpel4chor.util;

import org.w3c.dom.Node;

public class OutputElement {
	private String msg;
	private String elementId;
	private Node node;
	
	public String getElementId() {
		return elementId;
	}

	public String getMsg() {
		return msg;
	}

	public Node getNode() {
		return node;
	}

	public OutputElement(String msg, Node node) {
		this.msg = msg;
		this.elementId = null;
		this.node = node;
	}
	
	public OutputElement(String msg, String elementId) {
		this.msg = msg;
		this.elementId = elementId;
		this.node = null;
	}
	
	public OutputElement(String msg) {
		this.msg = msg;
		this.elementId = null;
		this.node = null;
	}
	
	public String toString() {
		if (this.elementId == null) {
			return this.msg;
		} else {
			return this.msg + " (" + this.elementId + ")";
		}
	}
	
	public boolean hasNodeInfo() {
		return (this.node != null);
	}
	
	public boolean hasElementInfo() {
		return (this.elementId != null);
	}
	
	public boolean isGeneralInfo() {
		return ((this.elementId == null) && (this.node != null));
	}

}
