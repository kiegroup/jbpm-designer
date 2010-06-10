package de.hpi.bpmn2xpdl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmappr.DomElement;

public class XMLUnknownsContainer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected HashMap<String,String> unknownAttributes;
	protected ArrayList<DomElement> unknownElements;
	
	public HashMap<String,String> getUnknownAttributes() {
		return unknownAttributes;
	}
	
	public ArrayList<DomElement> getUnknownElements() {
		return unknownElements;
	}
	
	public void setUnknownAttributes(HashMap<String,String> unknowns) {
		unknownAttributes = unknowns;
	}
	
	public void setUnknownElements(ArrayList<DomElement> unknowns) {
		unknownElements = unknowns;
	}
}
