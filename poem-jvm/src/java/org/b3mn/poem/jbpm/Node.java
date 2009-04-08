package org.b3mn.poem.jbpm;

import java.util.List;

public class Node {
	protected String name;
	protected Bounds bounds;
	protected List<Transition> outgoings;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Transition> getOutgoings() {
		return outgoings;
	}
	public void setOutgoings(List<Transition> outgoings) {
		this.outgoings = outgoings;
	}
	public Bounds getBounds() {
		return bounds;
	}
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public String toJpdl() throws InvalidModelException {
		return "";
	}
}
