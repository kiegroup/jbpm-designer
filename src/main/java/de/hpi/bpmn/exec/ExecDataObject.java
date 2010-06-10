package de.hpi.bpmn.exec;

import de.hpi.bpmn.DataObject;

public class ExecDataObject extends DataObject {

	// Constructors
	public ExecDataObject (){
		this.type = Type.SEQ;
	}
	
	// Define types
	public enum Type {
		DATA, CONTEXT, SEQ
	}
	
	// Attributes
	protected String model;
	protected Type type;
	
	// Accessors
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	public Type getType () {
		return this.type;
	}
	
	public void setType (Type type) {
		this.type = type;
	}
	
}
