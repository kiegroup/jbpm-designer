package de.hpi.bpel4chor.transformation;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.hpi.bpel4chor.util.Output;

public class TransformationResult {
	
	public enum Type { DIAGRAM, TOPOLOGY, PROCESS };

	private Type type;
	private Output output;
	private Document document;
	private String processName;
	
	private static int processCount = 0;
	
	public Type getType() {
		return type;
	}

	public Output getOutput() {
		return output;
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
	
	public TransformationResult(Type type, Output output) {
		this.type = type;
		this.output = output;
		this.document = null;
		this.processName = null;
	}

	/**
	 * Constructor for process data including output AND a document
	 * 
	 * The document is only used to extract the name from.
	 * 
	 */
	public TransformationResult(Type type, Output output, Document document) {
		this.type = type;
		this.output = output;
		storeProcessName(type, document);
		this.document = null;
	}

	public TransformationResult(Type type, Document document) {
		this.type = type;
		this.output = null;
		this.document = document;
		storeProcessName(type, document);
	}
	
	public boolean isSuccess() {
		return (document != null);
	}
	
	public String toString() {
		String res;
		if (isSuccess()) {
			res = "success: " + this.getDocument();
		} else {
			res = "error: " + this.getOutput();
		}
		return res;
	}
}
