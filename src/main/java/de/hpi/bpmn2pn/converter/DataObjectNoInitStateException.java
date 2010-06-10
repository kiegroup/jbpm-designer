package de.hpi.bpmn2pn.converter;

public class DataObjectNoInitStateException extends Exception {
	static final long serialVersionUID= 1979;
	
	public String getMessage()
	{
		
		return "A Data Object does not have an initial state";
	}
	
	
}
