
package de.hpi.treeGraph;

import java.util.ArrayList;
import java.util.Collection;

public class Shape {
	
	final protected String id;
	
	protected Collection<Shape> incomingShapes = new ArrayList<Shape>();
	protected Collection<Shape> outgoingShapes = new ArrayList<Shape>();
	
	// Factory method: creates edges or nodes depending on the given stencil URI
	public static Shape getInstance(String id, String type) {
		if (type.equals("http://b3mn.org/stencilset/treeGraph#Edge")) {
			return new Edge(id);
		}
		if (type.equals("http://b3mn.org/stencilset/treeGraph#Node")) {
			return new Node(id);
		}
		return null;
	}
	
	protected Shape(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public Collection<Shape> getIncomingShapes() {
		return this.incomingShapes;
	}

	public void addIncomingShape(Shape incomingShape) {
		this.incomingShapes.add(incomingShape);
	}

	public Collection<Shape> getOutgoingShapes() {
		return outgoingShapes;
	}
	
	public void addOutgoingShape(Shape outgoingShape) {
		this.outgoingShapes.add(outgoingShape);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Shape) {
			return ((Shape)o).getId().equals(this.id);
		} else return false;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
}
